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

 <link rel="stylesheet" href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
  <link rel="stylesheet" href="/resources/demos/style.css">
  <style>
  #sortable1, #sortable2 {
    border: 1px solid #eee;
    width: 142px;
    min-height: 20px;
    list-style-type: none;
    margin: 0;
    padding: 5px 0 0 0;
    float: left;
    margin-right: 10px;
  }
  #sortable1 li, #sortable2 li {
    margin: 0 5px 5px 5px;
    padding: 5px;
    font-size: 1.2em;
    width: 120px;
  }
  </style>
  <script>
  $(function() {
    $( "#sortable1, #sortable2" ).sortable({
      connectWith: ".connectedSortable",
      revert: true,
      stop : function(event, ui) {   	 
    	  var sortedIDs = $( "#sortable2" ).sortable( "toArray" );
    	  console.log("New rpt: "+sortedIDs); 
      }
    }).disableSelection();
  });
  
  
  function clearCart(){
		        $('#sortable2 li').each(function(){
		            $(this).appendTo('#sortable1');
		        });
		        }
 
  
  function rmMeeting(meetingId){
    $('#sortable2 ').click(function(){
      $(this).remove();
    });
  }
  
  function createPlan(){
	  var sortedIDs = $( "#sortable2" ).sortable( "toArray" );
	  $.ajax({
          url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomYearPlan&mids="+ sortedIDs,
          cache: false
      }).done(function( html ) {
    	  //$('#modal_popup').foundation('reveal', 'close');
    	  location.reload();
      });
  }
 
  
  </script>
</head>

 

  <div class="header clearfix">
    <h3 class="columns large-22">CREATE your own year plan</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">

			<ul id="sortable1" class="connectedSortable">
			  <c:forEach var="meeting" items="${meetings}" >
			   <li class="ui-state-default" id="${meeting.path }">${meeting.name } </li>
			  </c:forEach>
			</ul>
			 
			<ul id="sortable2" class="connectedSortable">
			</ul>
			 
			<input type="button" value="Cancel"/> <!--  close modal -->
			<input type="button" value="Clear" onclick="clearCart()"/>  <!-- clear cart -->
			<input type="button" value="Create Plan" onclick="createPlan()"/> <!-- submit -->
			 
       
    </div>
  </div>

