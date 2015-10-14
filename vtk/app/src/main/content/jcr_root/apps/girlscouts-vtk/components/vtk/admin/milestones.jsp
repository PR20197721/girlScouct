<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../include/session.jsp"%>

<script type="text/javascript"
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<%
String activeTab = "admin_milestones";

int councilCode = apiConfig.getTroops().get(0).getCouncilCode();
String councilId= request.getParameter("cid")==null? Integer.toString(councilCode):request.getParameter("cid");
%>
<div id="panelWrapper" class="row content milestones meeting-detail">
	<div class="columns small-20 small-centered">
		<p>Edit milestones, add dates, create new milestones, and set to
			show in plans.</p>
		<div class="row">
			<p class="column large-7 large-push-1 medium-7 medium-push-1">Message</p>
			<p class="column large-4 large-push-1 medium-4 medium-push-3">Date</p>
			<p class="column large-4 large-pull-4 medium-5 medium-pull-3">Show in Plans</p>
		</div>
		<form class="clearfix"
			action="/content/girlscouts-vtk/controllers/vtk.controller.html"
			method="POST" id="MileStoneForm">
			<input type="hidden" name="cid" value="<%=councilId%>" />
			<%int i=0;
    		java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(councilId) ;
    		for(; i<milestones.size(); i++ ) { %>
			<section id="ms-section" class="row">

				<div class="column large-1 medium-2">
					<a id="delete" title="remove" class="icon-button-circle-cross"></a>
				</div>
				<div class="column large-7 medium-7">
					<input type="text" id="blurb<%=i %>" name="ms_blurb[]"
						value="<%=milestones.get(i).getBlurb()%>" />
				</div>
				<div class="column large-4 large-push-1 medium-4 medium-push-1">
					<input type="text" id="date<%=i %>" class="datepicker"
						name="ms_date[]"
						value="<%=milestones.get(i).getDate()==null?"": VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, milestones.get(i).getDate())%>" />
				</div>
				<div class="column large-1 large-push-1 medium-1 medium-push-1">
					<label for="date<%=i %>"><a class="icon-calendar"></a></label>
				</div>
				<div class="column large-2 large-pull-5 medium-2 medium-pull-5">
					<input type="checkbox" id="ch_<%=i %>" name="show_ch[]"
						<%=milestones.get(i).getShow()?"checked":"unchecked"%> /> <label
						for="ch_<%=i %>"></label>
				</div>
			</section>
			<%}%>
			<section id="ms-section" class="row">
				<!-- empty entries -->
				<div class="column large-1">
					<a id="remove-entry" title="remove"
						class="icon-button-circle-cross"></a>
				</div>
				<div class="column large-7 large-push-1">
					<input type="text" id="blurb<%=i %>" name="ms_blurb[]"
						placeholder="Enter a Milestone" />
				</div>
				<div class="column large-4 large-push-2">
					<input type="text" id="date<%=i %>" class="datepicker"
						name="ms_date[]" />
				</div>
				<div class="column large-1 large-push-2">
					<label for="date<%=i %>"><a class="icon-calendar"></a></label>
				</div>
				<div class="column large-2 large-pull-5">
					<input type="checkbox" id="ch_<%=i %>" name="show_ch[]" unchecked />
					<label for="ch_<%=i %>"></label>
				</div>
			</section>
			<section id="ms-section" class="row">
				<div class="column large-1">
					<a id="remove-entry" title="remove"><i
						class="icon-button-circle-cross"></i></a>
				</div>
				<div class="column large-7 large-push-1">
					<input type="text" id="blurb<%=i+1 %>" name="ms_blurb[]"
						placeholder="Enter a Milestone" />
				</div>
				<div class="column large-4 large-push-2">
					<input type="text" id="date<%=i+1 %>" class="datepicker"
						name="ms_date[]" />
				</div>
				<div class="column large-1 large-push-2">
					<label for="date<%=i+1 %>"><a class="icon-calendar"></a></label>
				</div>
				<div class="column large-2 large-pull-5">
					<input type="checkbox" id="ch_<%=i+1 %>" name="show_ch[]" unchecked />
					<label for="ch_<%=i+1 %>"></label>
				</div>
			</section>

			<section class="row">
				<div class="column large-2">
					<a onclick="newEntry()" title="add-entry"><i
						class="icon-button-circle-plus"></i></a>
				</div>
				<p>Add a Milestone</p>

			</section>
			<section class="row">
				<input type="submit" name="saveCouncilMilestones"
					value="Save To Plans" class="btn right button" />
			</section>

		</form>
	</div>
<!-- 	<div id="saved">
		<p>Milestones saved.</p>
	</div> -->
</div>

<script>
	var n;
	$(document).ready(function(){
		$( ".datepicker" ).datepicker();
		n=$('#MileStoneForm #ms-section').length-1;;

		$("#MileStoneForm").submit(function( event ) {
			if (confirm('You are about to save the milestones.')) {
				$('input[type=checkbox]').each(function () {
		        	$("#MileStoneForm").append("<input type='hidden' name='ms_show[]' value='"+this.checked+"'/>");
	 			});
				return true;
			}else{
				return false;
			}

	 	});
		 //$('#saved').dialog({ autoOpen: false, zIndex: 200 });


	});


	$(document).on('click', '#delete', function() {
		if (confirm('Are you sure you want to delete this milestone?')) {
		    $(this).parent().parent().remove();
		}
		return false;
	});

	$(document).on('click', '#remove-entry', function() {

		$(this).parent().parent().remove();
		return false;

	});



/* 	$("#MileStoneForm").submit(function( event ) {
		alert("aaa");
        event.preventDefault();
        if_show = [];
		var i=0;
		$('input[type=checkbox]').each(function () {
/* 		    if_show[i++] = (this.checked ? "true" : "false");*/
			//this.attr( "value", );
	       /*  $("#MileStoneForm").append("<input type="hidden" name='show[]' value='this.checked'/>");

 		}); */
	    // If .required's value's length is zero
	    /* if ( $( ".required" ).val().length === 0 ) {

	        // Usually show some kind of error message here

	        // Prevent the form from submitting
	        event.preventDefault();
	    }  else {*/
/* 	    	$.ajax({
				   type: "POST",
				   data:$( "#myForm" ).serialize()
					    ,
				   url: "/content/girlscouts-vtk/controllers/vtk.controller.html",
				   success: function(msg){

				   }
			}
	    	); */

	    //}
/* 	}); */


  function newEntry(){
	  $('#MileStoneForm section#ms-section').last().after('<section id="ms-section" class="row"></section>');
	  $('#MileStoneForm section#ms-section').last().append('<div class="column large-1"><a id="remove-entry" title="remove" class="icon-button-circle-cross"></a></div>');
	  $('#MileStoneForm section#ms-section').last().append('<div class="column large-7 large-push-1"><input type="text" id="blurb'+n+'" name="ms_blurb[]" placeholder="Enter a Milestone"/></div>');
	  $('#MileStoneForm section#ms-section').last().append('<div class="column large-4 large-push-2"><input type="text" id="date'+n+'" class="datepicker" name="ms_date[]" /></div>');
	  $('#MileStoneForm section#ms-section').last().append('<div class="column large-1 large-push-2"><label for="date'+n+'"><a class="icon-calendar"></a></label></div>');
	  $('#MileStoneForm section#ms-section').last().append('<div class="column large-2 large-pull-5"><input type="checkbox" id="ch_'+n+'" name="show_ch[]" unchecked/><label for="ch_'+n+'"></label></div>');
	  n++;
  };

</script>




<%--

<%
String councilId= request.getParameter("cid");


%>


<h1>Milestones council <%= councilId %></h1>

<div style="border: 5px solid gray;">
	<form action="/content/girlscouts-vtk/en/vtk.admin.milestones.html" method="get" >
	Change council:
	<input type="text" name="cid" value="<%=councilId%>"/>
	<input type="submit" value="Change council"/>
	</form>
</div>

<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" >
<input type="hidden" name="cid" value="<%=councilId%>"/>
<table>

<tr>
	<th>Num</th>
	<th>Date</th>
	<th>Name</th>
	<th></th>
</tr>

<%

java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(councilId ) ;
for(int i=0;i<milestones.size();i++){

%>
<tr>
	<td><%= (i+1) %></td>
	<td><input type="text" id="date<%=i %>" name="date<%=i %>" value="<%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, milestones.get(i).getDate())%>"/></td>
	<td><input type="text" name="blurb<%=i %>" value="<%=milestones.get(i).getBlurb()%>"/></td>
	<td>
	<!--
		<a href="/content/girlscouts-vtk/controllers/vtk.controller.html?removeCouncilMilestones=<%=milestones.get(i).getUid() %>" style="color:red;">remove</a></td>
	-->
</tr>

	<script>
  $(function() {
    $( "#date<%=i%>" ).datepicker();

  });
  </script>
<%} %>






<tr>
	<td colspan="4" style="text-align:center;"><input type="submit" name="updateCouncilMilestones" value="Edit"/></td>
</tr>

</table>
</form>


<script>
  $(function() {
    $( "#date" ).datepicker();

  });
  </script>


<form action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" style="display:none;" >
<input type="hidden" name="cid" value="<%=councilId%>"/>
<a href="javascript:void(0)" onclick="document.getElementById('newMil').style.display='block'">create new </a>
<div style="display:none;" id="newMil">
<table>
<tr>
	<td></td>
	<td><input type="text" id="date" name="date" value=""/></td>
	<td><input type="text" name="blurb" value=""/></td>
	<td></td>
</tr>
<tr>
	<td colspan="4" style="text-align:center;">
	<input type="submit" name="createCouncilMilestones" value="Create"/>
	<input type="button" value="Close" onclick="document.getElementById('newMil').style.display='none'"/>
	</td>

</tr>
</table>
</form>
</div> --%>