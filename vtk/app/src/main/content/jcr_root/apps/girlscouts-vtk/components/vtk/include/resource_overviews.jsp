<%@ page
  import="java.util.*, 
  		org.girlscouts.vtk.auth.models.ApiConfig, 
  		org.girlscouts.vtk.models.*,
  		org.girlscouts.vtk.dao.*,
  		org.girlscouts.vtk.ejb.*,
        org.apache.sling.api.request.RequestPathInfo
  		"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="session.jsp"%>



<div class="header clearfix">
		<i style="position:absolute; top:5px; right:5px;" class="icon-button-circle-cross" onclick="(function(){$('#gsModal').dialog('close')})()"></i>
</div>
<div class="scroll" style="max-height:601px">

<div  id="resource_overview">
	<div class="columns small-24">

<%

	String level = request.getParameter("level");
	if (null == level || "".equals(level)) {
		String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
		if (selectors.length > 0) {
			level = selectors[selectors.length - 1];
			if (level.length() >= 2) {
				level = level.substring(0, 1).toUpperCase() + level.substring(1);
			}
		}
	}


	List<Meeting> raw_meetings = null;
	try{
		raw_meetings = meetingUtil.getMeetings( user, troop, level );
		
	}catch( IllegalAccessException illegalUserEx){
		//TODO 
	}catch(Exception genException){
		//TODO

	}
	
	List<String> meetingPlanTypes = null;
	if( raw_meetings!=null){
		meetingPlanTypes = VtkUtil.getDistinctMeetingPlanTypes(raw_meetings);
		Map<String, List<Meeting>> sortMeetingByMeetingType = VtkUtil.sortMeetingByMeetingType( raw_meetings,  meetingPlanTypes);
		%><%@include file="resource_overview_types.jsp"%><%
	}

	VtkUtil.sortMeetingsByName(raw_meetings);
	for( Meeting meeting: raw_meetings){ 
			%><%@include file="resource_overview.jsp"%><%
		}
	
	
%>


	</div>	

	<div class="__view-all row">
		<div style="text-align:center"  class="columns small-24">
			<p>Show the other <span class="_number"></span> </p>
		</div>
	</div>



</div>
</div>



<script>
	var meeTingTypeStatus;

    function showMeetingTypes( meetingType ){
		$('.__view_meeting_details:visible').slideUp();
		$(".arrow.open").removeClass('open').addClass('close');
		hideMeetingItem();
		meetingType = ('' + meetingType).replace(new RegExp(':', 'g'), '\\:');
		var items = $("[data-value= "+meetingType+"]" );
		showFirstSix(items);
		meeTingTypeStatus = meetingType;
	}

	function hideMeetingItem(){
		$('.__resource-item').hide();
	}

	function viewMeetingDetails(domElement, meeting){
		$('.__view_meeting_details:visible').not(domElement.find('.__view_meeting_details')).slideUp();
		$(".arrow.open").not(domElement.find('.arrow.open')).removeClass('open').addClass('close');
		retrieveMeetingDetail(meeting.path,meeting.id );
	}

	function retrieveMeetingDetail(meetingPath, meetingId){

			$('#view_meeting_details_'+meetingId).slideToggle();
			$('[data-id="'+meetingId+'"] .__top .__title .arrow').toggleClass('close open');	
	}

	function showAllMeetings(){
		$('.__title .arrow.open').removeClass('open').addClass('close');
		$('.__view_meeting_details:visible').hide();
		meeTingTypeStatus= undefined;
		var  items = $('.__resource-item');
		$('.__view-all').hide();

		items.show();
		showFirstSix(items);
	}


	function showFirstSix(items, isAll){
		$('.__title .arrow.open').removeClass('open').addClass('close');
		$('.__view_meeting_details:visible').hide();
		$('.__resource-item:visible').hide();
		$('.__view-all ._number').html('');
		$('.__view-all').hide();
		
		if(items.length>6){
			items.slice(0,6).show();
			$('.__view-all ._number').html(items.length-6);
			$('.__view-all').show()
		}else{
			items.show();
		}

		items.slice(0, 6).show();
	}

	function afterClick(element){
		var domElement = $(element).parents('.__resource-item');
		var meeting =  domElement.data();
		viewMeetingDetails(domElement,meeting);
	}
	
	$(function(){
		$('.__top .__title').click(function(e){
			afterClick(this)
		})

		$('.__view-all').click(function(e){
			var items;
			if(meeTingTypeStatus){
				items = $('.__resource-item[data-value="'+meeTingTypeStatus+'"]')
			}else{
				items = $('.__resource-item');
			}
			 
			items.show();
			$(this).hide();
		})
	})
</script>