<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>

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
			<p class="column large-7 large-push-1">Message</p>
			<p class="column large-4 large-push-1">Date</p>
			<p class="column large-4 large-pull-4">Show in Plans</p>
		</div>
		<form class="clearfix" action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" id="MileStoneForm">
			<input type="hidden" name="cid" value="<%=councilId%>" />
			<% int i=0;
				//If there are milestones show them in the input fields to view/edit
    		java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(councilId) ;
    		for(; i<milestones.size(); i++ ) { %>
					<section class="row">
						<div class="column large-1">
							<a title="remove" class="icon-button-circle-cross delete"></a>
						</div>
						<div class="column large-7">
							<input type="text" id="blurb<%=i %>" name="ms_blurb[]"
								value="<%=milestones.get(i).getBlurb()%>" />
						</div>
						<div class="column large-4 large-push-1">
							<input type="text" id="date<%=i %>" class="datepicker" name="ms_date[]" value="<%=milestones.get(i).getDate()==null?"":FORMAT_MMddYYYY.format(milestones.get(i).getDate())%>" />
						</div>
						<div class="column large-1 large-push-1">
							<label for="date<%=i %>"><a class="icon-calendar"></a></label>
						</div>
						<div class="column large-2 large-pull-5">
							<input type="checkbox" id="ch_<%=i %>" name="show_ch[]"<%=milestones.get(i).getShow() ? "checked" : "unchecked" %> /><label for="ch_<%=i %>"></label>
						</div>
				</section>
				<%}%>
				<section class="row">
					<div class="column large-10">
						<a onclick="newEntry()" class="add-btn" title="add-entry"><i class="icon-button-circle-plus"></i>Add a Milestone</a>
					</div>
				</section>
				<section class="row">
					<input type="submit" name="saveCouncilMilestones" value="Save To Plans" class="btn right button" />
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
		n=$('#MileStoneForm section').length-1;
		
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

	});
		
	$(document).on('click', '.delete', function() {
		if (confirm('Are you sure you want to delete this milestone?')) {
		    $(this).parent().parent().remove();
		} 
		return false;
	});
	
	$(document).on('click', '.remove-entry', function() {
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

	//to add a new milestone.
  function newEntry() {
  	var section = $('#MileStoneForm section:nth-last-child(2)');
  		section.before('<section class="row"><div class="column large-1"><a title="remove" class="remove-entry icon-button-circle-cross"></a></div><div class="column large-7"><input type="text" id="blurb'+n+'" name="ms_blurb[]" placeholder="Enter a Milestone"/></div><div class="column large-4 large-push-1"><input type="text" id="date'+n+'" class="datepicker" name="ms_date[]" /></div><div class="column large-1 large-push-1"><label for="date'+n+'"><a class="icon-calendar"></a></label></div><div class="column large-2 large-pull-5"><input type="checkbox" id="ch_'+n+'" name="show_ch[]" unchecked/><label for="ch_'+n+'"></label></div></section>');
	  n++; 
	  $( ".datepicker" ).datepicker();
  }; 
</script>
