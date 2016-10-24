<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/calendarElem.jsp -->
<%
java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());

String elem = request.getParameter("elem");
java.util.Date date = new java.util.Date( Long.parseLong(elem));
MeetingE meeting = (MeetingE)sched.get(date);
String AP = "AM";
if( VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, date).toUpperCase().equals("PM")){
	AP="PM";
}
boolean isCancelMeeting= false;
if( meeting != null && meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
	isCancelMeeting=true;
}
java.util.List <MeetingE>meetingsToCancel = meetingUtil.getMeetingToCancel(user, troop);

%>

<h5><strong><%=yearPlanUtil.getMeeting(user, troop, meeting.getRefId() ).getName() %></strong></h5>

<div id="locMsg"></div>

<div class="modifyCalendarDate clearfix">
	<div class="vtk-meeting-calendar-head column small-24">
		<div class="row">
		    <div class="column small-24 medium-12 large-8">
                <input type="radio" value="change" id="cngRadio" CHECKED onchange="tabsVtk.goto('calendar-meeting')" name="goto" /><label for="cngRadio"><p>Change Date / Time</p></label>
                </div>

                <div class="column small-24 medium-12 large-8">
                    <input type="radio" value="cancel" id="cclRadio" onchange="tabsVtk.goto('cancel-meeting')" name="goto" /><label for="cclRadio"><p>Cancel Meeting</p></label>
                </div>

                <div class="column small-24 medium-12 large-8 end">
                    <input type="radio" value="combine" id="cmlRadio"  onchange="tabsVtk.goto('combine-meeting')" name="goto" /><label for="cmlRadio"><p>Combine Meetings</p></label>
                </div>
			</div>
		</div>
	</div>


		<div data-parent="main" data-name="calendar-meeting" data-default="true"  data-title="" data-fetch="" class="vtk-meeting-calendar-body column small-24">
			 <div class="row">
			 	<div class="small-24 medium-8 column">
			 		<p>
			 			Current Date: <%= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,date) %><br />
			 			<strong  id="change-new-time" class="hide">New Date: <span></span></strong>
			 		</p>

			 		<p>
			 			Select a <strong>new date or time</strong> for this meeting and "Save" your choice
			 		</p>
			 	</div>

			 	<div class="small-24 medium-12 column end">
			 		<form id="frmCalElem">
						<!-- <p><strong>Change Date:</strong></p>
						<span>Select today's date or any future date</span> -->
						<div id="datepicker"></div>

						 <div class="alert-error-display-cancel hide">
                                Enter a valid time
                         </div>
						<input type="hidden" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,date) %>" id="cngDate0"  name="cngDate0" class="date calendarField" />
						<p><strong>Change Time:</strong></p>
						<section class='row clearfix'>
							<div class="column small-6">
								<input type="text" id="cngTime0" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm,date) %>" name="cngDate0"  />
							</div>
							<div class="columm small-6 left">
								<select id="cngAP0" name="cngAP0" class="ampm">
									<option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option>
									<option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
								</select>
							</div>
						</section>
					</form>
					<span  id="cngDate0ErrMsg"></span>
			 	</div>


			 	<div class="vtk-meeting-calendar-foot column small-24 column">
					<div class="row">
						<input type="button" value="save" id="saveCalElem" onclick="saveCalElem()" class="button btn right">  <input type="button" value="cancel" id="cancelCalElem" onclick="cancelModal()" class="button btn right">
					</div>
				</div>


				<div id="dialog-confirm"></div>

			 </div>
		</div>

		<div data-parent="main" data-name="cancel-meeting" data-title="Cancel Meeting" data-default="false" data-fetch=""  class="vtk-meeting-calendar-body column small-24">
			<div class="row">
			 	<div class="small-24 column">
					<%@include file="include/cancelMeeting.jsp"%>
				</div>
			</div>
		</div>

		<div data-parent="main" data-name="cancel-meeting" data-title="Cancel Meeting" data-default="false" data-fetch=""  class="vtk-meeting-calendar-body column small-24">
            <div class="row">
                <div class="small-24 column">
                    <%@include file="include/cancelMeeting.jsp"%>
                </div>
            </div>
        </div>

		<div data-parent="main" data-name="combine-meeting" data-title="Combine Meeting Dates" data-default="false" data-fetch=""  class="vtk-meeting-calendar-body column small-24">
			<div class="row">
					<div class="small-24 column">
						 <%@include file="include/combineMeetings.jsp"%>
					</div>


				</div>
			</div>
		</div>

		<div data-parent="combine-meeting" data-name="combine-meeting-time" data-title="Combine Meeting Dates" data-default="false" data-fetch=""  class="vtk-meeting-calendar-body column small-24">
            <div class="row">
                <div class="small-24 column">
                    <%@include file="include/combineMeetingsAddCal.jsp"%>
                </div>
            </div>
        </div>

		<div data-parent="combine-meeting" data-name="select-meeting" data-title="" data-default="false" data-fetch="http//localhost:4503"   class="vtk-meeting-calendar-body column small-24">
			<div class="row">
			</div>
		</div>






<script>


	function cancelModal(){
		$('#gsModal').children('.header').children('a').children('i').trigger('click');
	}

	var tabsVtk = (function(){

		var ObjTree = {};
		var tree =[];

		$('[data-title=""]').data('title', $('#calMng').children('h5').children('strong').text());


		$(function(){
			//Create object checker
			 tree = $.map($('[data-parent]'),function(value,idx){
				var parent, name, defaultD, fetch, $element, title;

				parent = $(value).data('parent');
				name = $(value).data('name');
				defaultD = $(value).data('default');
				fetch = $(value).data('fetch');
				$element = $(value);
				title = $(value).data('title');

				return {
					parent: parent,
					visible:defaultD,
					fetch: fetch,
					idx:idx,
					name:name,
					$el:$element,
					child:[],
					title:title
				}
			});

			tree.forEach(function(o){
				if(ObjTree.hasOwnProperty(o.parent) ){
					ObjTree[o.parent]['child'].push(o);
				}else{
					ObjTree[o.name] =o;
				}
			});


		});



		function goto(id){
			$('[data-parent]').attr('data-default',false);

			var el = tree.filter(function(item){ return item.name == id})

			$('#calMng').children('h5').children('strong').text(el[0].title);

			if(el[0].fetch !== '' || el[0].fetch ){
				el[0].$el.children('.row').load(el[0].fetch)
			}
			el[0].$el.attr('data-default',true);
		}

		function goBack(){
			goto($('[data-default="true"]').data('parent'));
		}


		return {
			goto:goto,
			goBack: goBack
		}
	})();
</script>



<script>

var sTimeCancel = selectedTime()

$(function() {
	$( "#datepicker" ).datepicker({
		  defaultDate: new Date ('<%=date%>'),
		  minDate: 0,
		  onSelect: function(dateText, inst) {
		      var dateAsString = dateText; //the first parameter of this function
		      var dateAsObject = $(this).datepicker( 'getDate' ); //the getDate method

		      $('#change-new-time').show().children('span').text(dateAsString);

		      	sTimeCancel.set(new Date(dateAsString).getTime())

		      document.getElementById("cngDate0").value =dateAsString;

		   }
	});


});



function doChkSubmitValid(){
	if ($('#frmCalElem').valid()) {

		document.getElementById("newCustActivity").disabled=false;
		}
}

$(function() {
		$("#cngDate0").inputmask("mm/dd/yyyy", {});
		$('#cngDate0').datepicker({minDate: 0});

		$("#newCustActivity_startTime").inputmask("h:s", {});
		$("#newCustActivity_endTime").inputmask("h:s", {});

});

$.validator.addMethod('time', function(value, element, param) {
		return value == '' || value.match(/^([01][0-9]|2[0-3]):[0-5][0-9]$/);
		}, 'Enter a valid time: hh:mm');



$().ready(function() {
	$('#cclRadio').change(function(){
		if($('#cclRadio').prop('checked')){
			$("#cngRadio").prop('checked', false);
			}
	});
	$('#cngRadio').change(function(){
		if($('#cngRadio').prop('checked')){
			$("#cclRadio").prop('checked', false);
			}
	});

});


function saveCalElem() {
	if($('#cclRadio').prop('checked')){
	   fnOpenNormalDialog();
	}else if($("#cngRadio").prop("checked")){
		var hour = $('#cngTime0').val() +' '+  $('#cngAP0').val();
        var x = moment.tz(sTimeCancel.get(), "America/New_York");
        var x1 = moment.tz( (x.format('MM/DD/YYYY')+' '+hour), "America/New_York");

        if (x1.isValid()) {
			updSched1('0','<%=meeting.getPath()%>','<%=date.getTime()%>');
		} else {

			$('.alert-error-display-cancel').show();
			// showError("The form has one or more errors.  Please update and try again.", "#createActivitySection .errorMsg");
		}
	}
};

if (navigator.userAgent.match(/(msie\ [0-9]{1})/i)) {
	if (navigator.userAgent.match(/(msie\ [0-9]{1})/i)[0].split(" ")[1] == 9) {
	  $('select').css('background-image', 'none');
	}
}




function fnOpenNormalDialog() {

    $("#dialog-confirm").html("Are you sure you want to cancel the meeting? This will remove the meeting from the calendar and you will have <%=(sched.size()-1)%> meetings instead of <%=sched.size()%> meetings this year.");





    // Define the Dialog and its properties.
    $("#dialog-confirm").dialog({
        resizable: false,
        modal: true,
        title: false,
        height: 250,
        width: 400,
        "buttons": {
    	    "Go ahead, cancel the meeting": function () {
	            $(this).dialog('close');
	            var r = document.querySelector('input[name = "_tag_t"]:checked').value;
	            rmMeeting('<%=date.getTime()%>',r);
 	    	},
           	"Return to Specify Dates and Locations": function () {
	            $(this).dialog('close');
	            newLocCal();

        	}
        },

        "create": function (event, ui) {

        	$(".ui-dialog-titlebar.ui-widget-header").hide();
    	}
    });
}



</script>
