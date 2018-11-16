<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/meeting_react2.jsp -->
<%
String mid = planView.getYearPlanComponent().getUid();
MeetingE meeting = planView.getMeeting();
pageContext.setAttribute("MEETING_PATH", meeting.getPath());
pageContext.setAttribute("PLANVIEW_TIME", Long.valueOf(planView.getSearchDate().getTime()));
pageContext.setAttribute("DETAIL_TYPE", "meeting");

String readonlyModeStr = VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) &&
    VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID) ? "false" : "true";

Cookie cookie = new Cookie("VTKReadonlyMode", readonlyModeStr);
cookie.setPath("/");
response.addCookie(cookie);

String elemParam = request.getParameter("elem");
if (elemParam == null) {
    elemParam = "first";
}
String meetingDataUrl = "meeting." + elemParam + ".json";
%>

<script>
    //GLOBALS
  var thisMeetingPath = "";
  var thisMeetingDate = '';
  var thisMeetingNotes = '';
</script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>

<% String sectionClassDefinition = ""; %>
<%@include file="include/bodyTop.jsp"%>
<%@include file="include/modals/modal_help.jsp"%>



<%@include file="include/modals/modal_agenda.jsp"%>
<%@include file="include/modals/modal_meeting_reminder.jsp" %>
<%@include file="include/modals/modal_view_sent_emails.jsp"%>

<div id="theMeeting"></div>

<div id="vtk-mtg-plan"></div>

<div id="modal_popup" class="reveal-modal" data-reveal></div>
<div id="modal_popup_video" class="reveal-modal" data-reveal></div>

<div id="data-log"  
  data-permission_view_activity_plan_id="<%= Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID%>"
  data-permission_send_email_mt_id="<%= Permission.PERMISSION_SEND_EMAIL_MT_ID %>"
  data-permission_send_email_mt_id="<%= Permission.PERMISSION_SEND_EMAIL_MT_ID %>"
  data-permission_edit_attendace_id="<%= Permission.PERMISSION_EDIT_ATTENDANCE_ID%>"
  data-permission_edit_meeting_id = "<%= Permission.PERMISSION_EDIT_MEETING_ID %>"
  data-user_current_year="<%= user.getCurrentYear() %>"
  data-vtk_current_year="<%= VtkUtil.getCurrentGSYear() %>"
  data-vtk_server_url='<%= meetingDataUrl %>'
  data-vtk_troop_hasPermision_edit_yearplan_id = "<%= VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) %>"
%>
</div>



<% if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID) ){ %>
  <%@include file="include/notes.jsp"%>
<% } %>

<%@include file="include/bodyBottom.jsp" %>
<script>


  var vtkNotesInstances = false;

  //Print
  var printModal = new ModalVtk('print-modal', true);
  printModal.init();
  
  //To be use in the  REACT APP
  var popup = new ModalVtk('print-modal', true);
  popup.init();

  var cll = '<form class="print-modal" style="font-size:14px;padding:10px 20px 10px 20px;">'+
              '<p><b>What would you like to print?</b></p>'+
                '<div style="" class="column small-24">'+
                  '<input type="radio" name="whatToPrint" value="meeting"> <span>Meeting Overview </span><br />'+
                  '<input type="radio" name="whatToPrint" value="activity"> <span>Activity Plan </span> <br />'+
                  '<input type="radio" name="whatToPrint" value="material"> <span>Material List </span><br />'+
                '</div>'+
                '<div style="text-align:center"><div class="vtk-js-modal_button_action vtk-js-modal_cancel_action  button tiny" style="margin-top:20px;min-width:100px">Cancel</div><div class="vtk-js-modal_button_action vtk-js-modal_ok_action button tiny" style="margin-top:20px;min-width:100px">Print</div></div></form>';

  function callPrintModal(){
    printModal.fillWith('PRINT',cll, function(){

      var listPrintAdress = {
        activity:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isAgenda&mid=<%=mid%>',
        meeting:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isOverview&mid=<%=mid%>',
        material:'/content/girlscouts-vtk/controllers/vtk.pdfPrint.html?act=isMaterials&mid=<%=mid%>'
      }

      function openWindow(open){
          if(open.length){
            window.open(listPrintAdress[open[0].value]);
            printModal.close();
          }
      }


      function printCallBack(){
        openWindow($('.print-modal').serializeArray());
      }

      function cancelCallBack(){
          printModal.close();
      }

      $('.vtk-js-modal_ok_action').on('click', printCallBack);
      $('.vtk-js-modal_cancel_action, .vtk-js-modal-x').on('click', cancelCallBack);
    })
  }

  $(function(){


    //React App 
    if($('#vtk-mtg-plan')){
      startMtgPlanApp('<%= meetingDataUrl %>');
    }

    //Notes
    if(!vtkNotesInstances){ 
      appVTK = initNotes;

      appVTK.getNotes('<%=meeting.getUid()%>','<%=user.getApiConfig().getUser().getSfUserId()%>').done(function(json){
            appVTK.interateNotes(json);
      });

      vtkNotesInstances = true;
    }
  })

  //Other components in the page
  vtkTrackerPushAction('ViewMeetingDetail');
  loadNav('planView',true);

</script>