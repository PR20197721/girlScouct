<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="include/session.jsp"%>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>

<%
String activeTab = "milestones";
boolean showVtkNav = true;
int councilCode = apiConfig.getTroops().get(0).getCouncilCode();
String councilId= request.getParameter("cid")==null? Integer.toString(councilCode):request.getParameter("cid");
String sectionClassDefinition = "milestones";
%>

<%@include file="include/bodyTop.jsp" %>
	<div class="columns medium-20 small-centered">
		<p>Edit milestones, add dates, create new milestones, and set to
			show in plans.</p>
		<div class="row centered">
			<p class="column large-7 large-push-1 medium-7 medium-push-1 small-7">Message</p>
			<p class="column large-4 large-push-1 medium-4 medium-push-3 small-5 small-push-2">Date</p>
			<p class="column large-4 large-pull-4 medium-5 medium-pull-3 small-8">Show in Plans</p>
		</div>
		<form class="clearfix" action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="POST" id="MileStoneForm">
			<input type="hidden" id="cid" name="cid" value="<%=councilId%>" />
			<%
				//If there are milestones show them in the input fields to view/edit
    		try{
    		java.util.List<Milestone> milestones = yearPlanUtil.getCouncilMilestones(user,councilId) ;

    		for(int i=0; i<milestones.size(); i++ ) { %>
				<section class="row">
						<div class="column large-1 medium-2 small-2">
							<a title="remove" class="icon-button-circle-cross delete"></a>
						</div>
						<div class="column large-7 medium-7 small-7">
							<input type="text" id="blurb<%=i %>" name="ms_blurb[]" value="<%=milestones.get(i).getBlurb()%>" />
						</div>
						<div class="column large-4 large-push-1 medium-4 medium-push-1 small-8">
							<input type="text" id="date<%=i %>" class="datepicker" placeholder="mm/dd/yyyy" name="ms_date[]" value="<%=milestones.get(i).getDate()==null?"": VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,milestones.get(i).getDate())%>" />
						</div>
						<div class="column large-1 large-push-1 medium-1 medium-push-1 small-1">
							<label for="date<%=i %>"><a class="icon-calendar"></a></label>
						</div>
						<div class="column large-2 large-pull-5 medium-2 medium-pull-5 small-1 small-pull-2">
							<input type="checkbox" id="ch_<%=i %>" name="show_ch[]"<%=milestones.get(i).getShow()!=null && milestones.get(i).getShow().booleanValue()? "checked" : "unchecked" %> /><label for="ch_<%=i %>"></label>
						</div>
				</section>
				<%}
			}catch(Exception e){
    			e.printStackTrace();
    		}%>
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
<%@include file="include/bodyBottom.jsp" %>

<script>
	var n;
	$(document).ready(function(){
		$( ".datepicker" ).datepicker();
		n=$('#MileStoneForm section').length-2;

	    $('input[type="submit"]').attr('disabled',true);

	});


	$("#MileStoneForm").submit(function( event ) {
		/*can not validate fields with same name*/
/* 		$("#MileStoneForm").validate({
			rules:{
				'ms_blurb[]':{
					required:true,
				},
				'ms_date[]':{
					date: true,
					required:true
				},

			},
	    	messages:{
	    		'ms_blurb[]':{
	    			required:"Message is required."
	    		},
	    		'ms_date[]':{
	    			date:"Date format: MM/DD/YYYY."
	    		}
	    	}

		}); */

		$('input[type=checkbox]').each(function () {
        	$("#MileStoneForm #cid").before("<input type='hidden' name='ms_show[]' value='"+this.checked+"'/>");
		});
		return (confirm('You are about to save the milestones.'))

 	});


	$("#MileStoneForm").on('click', '.delete', function() {
		if (confirm('Are you sure you want to delete this milestone?')) {
		    $(this).parent().parent().remove();
		      $('input[type="submit"]').attr('disabled',false);

		}
		return false;
	});

	$("#MileStoneForm").on('click', '.remove-entry', function() {
		$(this).parent().parent().remove();
		return false;
	});
	$("#MileStoneForm").on('change', 'input', function(event){
	    	$('input[type="submit"]').attr('disabled',false);
	});


	//to add a new milestone.
	function newEntry() {
  		var section = $('#MileStoneForm section:nth-last-child(2)');
  		var newSection = '<section class="row"><div class="column large-1"><a title="remove" class="remove-entry icon-button-circle-cross"></a></div><div class="column large-7"><input type="text" id="blurb'+n+'" name="ms_blurb[]" placeholder="Enter a Milestone"/></div><div class="column large-4 large-push-1"><input type="text" id="date'+n+'" class="datepicker" placeholder="mm/dd/yyyy" name="ms_date[]" /></div><div class="column large-1 large-push-1"><label for="date'+n+'"><a class="icon-calendar"></a></label></div><div class="column large-2 large-pull-5"><input type="checkbox" id="ch_'+n+'" name="show_ch[]" unchecked/><label for="ch_'+n+'"></label></div></section>'
  		section.before(newSection);
	  	n++;
	  	$( ".datepicker" ).datepicker();
 	};


 	loadNav('milestones');
</script>
