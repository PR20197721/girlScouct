<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="../session.jsp"%>


<div class="modal_agenda_edit">
	<div class="header clearfix">
		<h3 class="columns large-22">
		<%
		  String act="";
			if (request.getParameter("isOverview") != null) {
					out.println("Overview");
					act="isOverview";
			} else if (request.getParameter("isActivity") != null) {
					out.println("Activity Plan");
					act="isActivity";
			} else if (request.getParameter("isMaterials") != null) {
					out.println("Materials List");
					act="isMaterials";
			} else if (request.getParameter("isAgenda") != null) {
				out.println("Activity");
			}
		%>
		</h3>
		<a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
	</div>

	<div class="scroll content" style="">


	<% if(!act.isEmpty()) { %>
		<a href="/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=<%=act%>&mid=<%=request.getParameter("mid") %>" target="_blank" class="icon-download right" download="<%=act%>"></a>
<a href="/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=<%=act%>&mid=<%=request.getParameter("mid") %>" target="_blank" class="icon-printer right"></a>

        <% } %>

		<div class="setupCalendar row">
		<%
			MeetingE meeting = null;
			java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
			for (int i = 0; i < meetings.size(); i++)
				if (meetings.get(i).getUid()
						.equals(request.getParameter("mid"))) {
					meeting = meetings.get(i);
					break;
				}

			if( meeting==null ){
			    java.util.List<MeetingCanceled> cmeetings = troop.getYearPlan().getMeetingCanceled();
	            for (int i = 0; i < cmeetings.size(); i++)
	                if (cmeetings.get(i).getUid()
	                        .equals(request.getParameter("mid"))) {
	                    meeting = cmeetings.get(i);
	                    break;
	                }

			}


			Meeting meetingInfo = yearPlanUtil.getMeeting(user,troop,
					meeting.getRefId());
			java.util.List<Activity> _activities = meetingInfo.getActivities();
			java.util.Map<String, JcrCollectionHoldString> meetingInfoItems = meetingInfo
					.getMeetingInfo();
			Activity _activity = null;
		%>

		<%
			if (request.getParameter("isOverview") != null) {
		%>
		<div class="editable-textarea column small-20 small-centered" id="editMeetingOverview">
			


			<h5><%=meetingInfo.getName()%>: introduction</h5>
			<%=meetingInfoItems.get("overview").getStr()%>
		</div>
		<%
			} else if (request.getParameter("isActivity") != null) {

   				%> <%@include file="modal_activity_view.jsp"%><%
			} else if (request.getParameter("isMaterials") != null) {
		%>
			<div class="editable-textarea column small-20 small-centered" id="editMeetingMaterials">




			<%
            	java.util.List<Activity>activities =  meetingInfo.getActivities();

            //TODO sort
            Collections.sort(activities, new Comparator<Activity>() {
					public int compare(Activity activity1, Activity activity2) {
						return activity1.getActivityNumber() - activity2.getActivityNumber();
					}
				});

            	for(Activity activity: activities){

					java.util.List<Activity> subActivities = activity.getMultiactivities();
                    Activity selectedActivity = VtkUtil.findSelectedActivity( subActivities );

                    if( selectedActivity ==null && (subActivities!=null &&  subActivities.size()>1)){
                        %>
              				 <p style="color:red; font-size:18px; font-weight:bold;">Activity <%= activity.getActivityNumber() %> :
                			Select Your Activity</p>
                		<%
                    }else{
                    	String materials = selectedActivity==null ? 
                        		subActivities.get(0).getMaterials()
                    			: selectedActivity.getMaterials();
						%>
                		<p style="font-size:18px; font-weight:bold;">Activity <%= activity.getActivityNumber() %> 
                		<%if(subActivities.size()!=1 ){%> - Choice <%=selectedActivity.getActivityNumber()%> <%}%>
                		: <%=subActivities.size()==1 ? activity.getName(): selectedActivity.getName()%>
                        <br/><%=materials ==null ? "" : materials%></p>
                		<%
                    }
            	}
			%>



            </div><%
			} else if (request.getParameter("isAgenda") != null) {
					try {
						meetingUtil.sortActivity(_activities);
					} catch (Exception e) {
						e.printStackTrace();
					}
					for (int ii = 0; ii < _activities.size(); ii++) {
						_activity = _activities.get(ii);
						if (ii == Integer
								.parseInt(request.getParameter("isAgenda"))) {
						%>
					<%
						break;
							}
						}
					}
				%>
<% if (request.getParameter("isAgenda") != null) {%>
	<div class="row" style="background-color: white;">
   	<form onsubmit="return false;">
		<dl class="tabs agenda-tabs">
			<%
				Activity _subActivity_selected = _activity;
				java.util.List<Activity> subActivities = _activity.getMultiactivities();
				if( subActivities !=null ) {
					for(int sa =0; sa<subActivities.size();sa++){
						Activity _subActivity = subActivities.get(sa);
						if( _subActivity.getUid().equals(request.getParameter("uid")) ) //.getIsSelected() )
						_subActivity_selected =_subActivity;

						if( _subActivity.getIsSelected() ) {
							_subActivity_selected =_subActivity;
			%>
				<dd id="tab_<%= sa %>" data-uid="<%= _subActivity.getUid() %>"><i class="dot on"></i><a href="#panel_<%= sa %>" ><%=_subActivity.getName() %></a></dd>
			<%
						} else {
			%>
				<dd id="tab_<%= sa %>" data-uid="<%= _subActivity.getUid() %>"><i class="dot"></i><a href="#panel_<%= sa %>" ><%=_subActivity.getName() %></a></dd>
			<%
						}
					}
				}else{
				%><dd id="tab_0" data-uid="<%= _subActivity_selected.getUid() %>"><i class="dot on"></i><a href="#panel_0" ><%=_subActivity_selected.getName() %></a></dd><%
				}
			%>
		</dl>

		<br />

		<div class="columns small-6">
		<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID )) {%>




			<select id="_vtk_select_multiple_agenda"  class="<%=(subActivities !=null &&  subActivities.size()>0) ? "multi-agenda-time-options" : "" %>" onchange="durEditActiv(this.options[this.selectedIndex].value, '<%=_activity.getPath()%>', '<%=meeting.getPath()%>')">



				<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID )) {%>

				<option value="0" selected>Time Allotment</option>
				<option value="5"
					<%=(_subActivity_selected.getDuration() == 5)  ? "SELECTED" : ""%>>5</option>
				<option value="10"
					<%=(_subActivity_selected.getDuration() == 10) ? "SELECTED" : ""%>>10</option>
				<option value="15"
					<%=(_subActivity_selected.getDuration() == 15) ? "SELECTED" : ""%>>15</option>
				<option value="20"
					<%=(_subActivity_selected.getDuration() == 20) ? "SELECTED" : ""%>>20</option>
				<option value="25"
					<%=(_subActivity_selected.getDuration() == 25) ? "SELECTED" : ""%>>25</option>
				<option value="30"
					<%=(_subActivity_selected.getDuration() == 30) ? "SELECTED" : ""%>>30</option>
					<%}else{ %>
					<option value=""><%=_subActivity_selected.getDuration() %></option>
					<%} %>
			</select>
		<%

			}// end if
		%>
		</div>

		<% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID )) {%>
			<div class="columns small-18">



					<%if(subActivities==null || subActivities.size()==0){ %>
						<button onclick="location.reload();" class="btn button">Save and Back to meeting</button>
				    <button class="btn button" onclick="return rmAgenda('<%=_subActivity_selected.getPath()%>', '<%=meeting.getPath()%>')">Delete This Agenda Item</button>
			    <%}//edn if %>
			</div>
		 <%} %>
		</form>
		</div>

		<section class="row agenda-panelwrap tabs-content" style="background-color: white; margin:0px -5px;">
<%

	java.util.List<Activity> _subActivities = _activity.getMultiactivities();
    if( _subActivities !=null ) { //multi
        for(int sa =0; sa< _subActivities.size();sa++){
            Activity _subActivity = _subActivities.get(sa);
%>
		 	<div class="content panel__xyz" id="panel_<%= sa %>">
				<div class="row">


					<div class="columns small-6">


											<p style="margin:0px">
													<strong>
														Time to Complete
													</strong>
											</p>

											<div  id="put-here-time-selection" >

											</div>








					</div>

					<div class="columns small-18">
   					<div class="select_multiple_agenda" style="margin-top:20px"><input data-id="<%= _subActivity.getPath().substring(_subActivity.getPath().lastIndexOf("/")+1).replace(".","") %>" id="<%= _subActivity.getPath().substring(_subActivity.getPath().lastIndexOf("/")+1).replace(".","")  %>" class="act-select" name="act-select" data-uid="<%= _subActivity.getUid() %>" type="radio" data-meeting='<%=meeting.getPath()%>' data-activity='<%=_activity.getPath()%>' data-subactivity='<%=_subActivity_selected.getPath()%>' onclick="onChangeHandler(event)"
							<%= (_subActivity.getIsSelected()) ? "checked" : "" %>> <label for="<%= _subActivity.getPath().substring(_subActivity.getPath().lastIndexOf("/")+1).replace(".","")  %>"><span></span> <p>Select this activity</p></div>
					</div>
									</div>
		 		<div class="row"><%=_subActivity.getActivityDescription() %></div>
		 	</div>
<%
        }
    } else {  //single
%>
			<div class="content" id="panel_0">
				<div><%=_activity.getActivityDescription() %></div>
			</div>
<%
    }
%>

		</section>
		<%}%>
		</div>
	</div>

	<script type="text/javascript">

		function onChangeHandler(e) {

			var $this = $(e.target);

			$('[data-path="'+$this.data('id')+'"]').trigger('click');

			var selectedPanel = $('.act-select').index($this);

			$('.agenda-tabs dd .dot').removeClass('on');
			$('#tab_' + selectedPanel + ' .dot').addClass('on');
		};


	$(document).ready(function(){


		var agendaTabs, agendaTabsClass, activePanel;

		// convert a number to its corresponding word, valid for up to 5
		var toWords = function(num) {
			switch(num) {
				case 1:
					agendaTabsClass = 'one';
				break;
				case 2:
					agendaTabsClass = 'two';
				break;
				case 3:
					agendaTabsClass = 'three';
				break;
				case 4:
					agendaTabsClass = 'four';
				break;
				case 5:
					agendaTabsClass = 'five';
				break;
				default:
					agendaTabsClass = 'other';
			};
			return agendaTabsClass;
		};

		agendaTabs = parseInt($('.agenda-tabs dd').length);
		agendaTabsClass = toWords(agendaTabs);

		// class to style tabs by number of tabs present
		$('.agenda-tabs').addClass(agendaTabsClass);

		// if none previously set, set the first tab to active
		if (! $('.agenda-tabs dd').hasClass('active') ) {
			$('.agenda-tabs dd').eq(0).addClass('active');
		}

		if (agendaTabs || agendaTabs > 0) {
			// activate the selected panel
			activePanel = $('.agenda-tabs dd.active a').attr('href').toString();

			$('.agenda-panelwrap .content' + activePanel).addClass('active');

			// event listener
			$('.agenda-tabs dd a').click(function(e) {
				e.preventDefault();

				// get the id of the panel to activate
				activePanel = $(this).attr('href').toString();

				//reset tabs
				$('.agenda-tabs dd').removeClass('active');

				//set the active tab
				$(this).parent().addClass('active');

				//reset panels
				$('.agenda-panelwrap .content').removeClass('active');

				//set the active panel
				$('.agenda-panelwrap .content' + activePanel).addClass('active');
					if($('.panel__xyz').length){
						var xlone = $('#_vtk_select_multiple_agenda').clone();
						$('#_vtk_select_multiple_agenda').remove();
						$(activePanel).find('#put-here-time-selection').append(xlone);
					}



			});

			var idToCLick = '<%= request.getParameter("uid") %>';

			$(function(){
					if($('.panel__xyz').length){
				var xlone = $('#_vtk_select_multiple_agenda').clone();
				$('#_vtk_select_multiple_agenda').remove();
				$('.panel__xyz').eq(0).find('#put-here-time-selection').append(xlone);
					}

					$('dd[data-uid="'+idToCLick+'"]').children('a').trigger('click');


			})




			//$('.act-select').click(function() {
			//	var selectedPanel = $('.act-select').index(this);

			//	$('.agenda-tabs dd .dot').removeClass('on');

			//	$('#tab_' + selectedPanel + ' .dot').addClass('on');
			//});
		}
	});
	</script>
