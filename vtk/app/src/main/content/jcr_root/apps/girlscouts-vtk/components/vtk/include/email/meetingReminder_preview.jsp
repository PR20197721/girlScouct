<%@page import="java.util.Iterator,org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 
EmailMeetingReminder emr = user.getSendingEmail();
%>
<br/>PREVIEW: sending NOW


<div style="background-color:gray">Address List</div>

<br/>Girls /Parents <%= emr.getEmailToGirlParent() !=null ? "CHECKED" : "" %>
<br/>Self <%= emr.getEmailToSelf() !=null ? "CHECKED" : "" %>
<br/>Troop Volunteers <%= emr.getEmailToTroopVolunteer() !=null ? "CHECKED" : "" %>


<br/>Enter your own:<%=emr.getCc() %>



<br/>Subject: <%=emr.getSubj() %>



<%= emr.getHtml() %>

<div>
	Aid(s) Included:
	<%
	
	if( emr!=null ){
		java.util.List<Asset> eAssets = emr.getAssets();
		if( eAssets!=null)
			for(int i=0;i<eAssets.size();i++){
				%><li><%=eAssets.get(i).getRefId() %></li><% 
			}
	}
	%>

</div>

<br/><br/><input type="button" value="Send" onclick="sendMeetingReminderEmail()"/>









