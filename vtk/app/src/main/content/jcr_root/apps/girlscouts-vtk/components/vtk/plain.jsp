<li>{"<%if(hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID )) {%>"}
            <a href="#" 
            {"<%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date(\"1/1/1977\") )) {%>"}
                 data-reveal-id="modal-meeting-reminder" 
            {"<%} else{%>"}
                onclick="javascript:alert('You have not yet scheduled your meeting calendar.\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')"
            {"<%} %>"}
            
             title="Meeting Reminder Email">Edit/Sent Meeting Reminder Email</a>
             {"<%} else{ %>"}"
            <a>Meeting Reminder email</a>
            {"<%}%>"}
        </li>
        <li>
        {"<%if (planView.getMeeting().getSentEmails()!=null && !planView.getMeeting().getSentEmails().isEmpty()) {%>"}
            ({"<%=planView.getMeeting().getSentEmails().size() %>"} sent - 
            <a href="#" title="view sent emails" className="view" data-reveal-id="modal_view_sent_emails">view</a>)
            {"<%} %>"}
        </li>
        
         {"<%if(planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING &&
            hasPermission(troop, Permission.PERMISSION_VIEW_ATTENDANCE_ID )) {%>"}
        <li>        
            <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_attendance.html?mid=<%=planView.getYearPlanComponent().getUid() %>&isAch=<%=(planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING) ? ((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getIsAchievement() : "false" %>&mName=<%= (planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING) ? ((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() : ((Activity)planView.getYearPlanComponent()).getName()%>"}>Record Attendance &amp; Achievements</a>
        </li>
        <li>(
            {"<%if( pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") ==null || pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL").equals("")){ %>"}
            none present, no achievements
            {"<%}else{ %>"}
            {"<% if(pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT") ==null || ((Integer)pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT")) ==0 ){ %>"}
              none present, 
            {"<%}else{%>"}
              <%= pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT") %> of <%= pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") %> present,
            {"<%}%>"}
            
            <% if( pageContext.getAttribute("MEETING_achievement_CURRENT") ==null ||  ((Integer)pageContext.getAttribute("MEETING_achievement_CURRENT")) ==0){ %>
              no achievements
            <% }else{%>
              <%= pageContext.getAttribute("MEETING_achievement_CURRENT") %> of <%= pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") %> achievement(s)
            <%} %>
             )
        </li>