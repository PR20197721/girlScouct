<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp"%>

<%

String ageLevel=  troop.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
java.util.List<Meeting> meetings =yearPlanUtil.getAllMeetings(user, ageLevel);
request.setAttribute("meetings", meetings);
%>
<style>
#sortable2{min-height: <%=(85*meetings.size())%>px;}
</style>
<script>
  $(function() {
    $(".close").on('click', function(){
      $('#modal_custom_year_plan').foundation('reveal', 'close');
    })
    $( "#sortable1, #sortable2" ).sortable({
      connectWith: ".connectedSortable",
      revert: true,
      placeholder: "sortable-placeholder",
      grid: [ 20, 10 ],
      forcePlaceholderSize: true,
      dropOnEmpty: true,
      helper: 'clone',
      start : function(event, ui) {
    	  //var sortedIDs = $( "#sortable2" ).sortable( "toArray" );
    	  //console.log("New rpt: "+sortedIDs); 
      },
      activate: function(e,ui){
        // console.log($('#sortable1').height());
    	  var min_height = $('#sortable1').height()-71;
          $("#sortable2").css('min-height', min_height);
         //console.log($("#sortable1").height() );
    	 // $("#sortable1, #sortable2").css('minHeight', $("#sortable1").height() + "px");
    	  
      }
    }).disableSelection();
  });

  function clearCart() {
    $('#sortable2 li').each(function(){
        $(this).appendTo('#sortable1');
        $("#sortable2").css('min-height', 'auto'); 
    });
  }
 
  function rmMeeting(meetingId){
    $('#sortable2').click(function(){
      $(this).remove();
    });
  }
  
  function createPlan() {
    //console.log($( "#sortable2" ));
    var sortedIDs = $( "#sortable2" ).sortable( "toArray" );
    //alert( sortedIDs);
    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomYearPlan&mids="+ sortedIDs,
        cache: false
    }).done(function( html ) {
  	  //$('#modal_popup').foundation('reveal', 'close');
  	  vtkTrackerPushAction('CreateCustomYearPlan');
  	  location.reload();
    });
  }
 /* 
  $(document).ready(function(){
	
      var min_height = $('#sortable1').height()-71;
      $("#sortable2").css('min-height', min_height);
      alert(1);  
  }
  */
  
  
 
  </script>


  <div class="header clearfix">
    <h3 class="columns large-22">CREATE your own year plan</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">
    <p>Drag your selected meetings into your year plan.</p>
      <div class="clearfix" data-equalizer="foo">
        <div class="columns large-12 medium-12 small-24">
          <p class="title">MEETINGS LIBRARY</p>
    			<ul id="sortable1" class="connectedSortable" data-equalizer-watch="foo">
    			
    			  <c:forEach var="meeting" items="${meetings}">
    			   <li class="ui-state-default" id="${meeting.path}"><p>${meeting.name}</p><p>${meeting.cat}</p></li>
    			  </c:forEach>
    			
    			</ul> 
        </div>
        <div class="columns large-12 medium-12 small-24">
          <p class="title">YOUR YEAR PLAN</p>
    			<ul id="sortable2" class="connectedSortable" data-equalizer-watch="foo">
    			
    			</ul>
        </div>
      </div>
      <div class="clearfix right btn-wrap">
        <input type="button" value="Cancel" class="btn button close" aria-label="Close"/> <!--  close modal -->
        <input type="button" value="Clear" onclick="clearCart()" class="btn button" />  <!-- clear cart -->
        <input type="button" value="Create Plan" onclick="createPlan()" class="btn button" /> <!-- submit -->
      </div>
    </div>
  </div>

