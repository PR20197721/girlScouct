<%@ page import="org.girlscouts.vtk.ejb.EmailMeetingReminder" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../session.jsp" %>

<%
    EmailMeetingReminder emr = selectedTroop.getSendingEmail();
%>
<br/>PREVIEW: sending NOW


<div style="background-color:gray">Address List</div>
<br/>To:
(<%= emr.getEmailToGirlParent() != null ? "Girls /Parents " : "" %>
<%= emr.getEmailToSelf() != null ? "Self " : "" %>
<%= emr.getEmailToTroopVolunteer() != null ? "Troop Volunteers" : "" %>)
<br/><%= emr.getTo()%>
<br/>Enter your own:<%=emr.getCc() %>

<br/><br/>
<div style="background-color:gray">Compose Email</div>
Subject: <%=emr.getSubj() %>
<br/><br/><%= emr.getHtml() %>

<input type="button" value="Send" onclick="sendMeetingReminderEmail()"/>








