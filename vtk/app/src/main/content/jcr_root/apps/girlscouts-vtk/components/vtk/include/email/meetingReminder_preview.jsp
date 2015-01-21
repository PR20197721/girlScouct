<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="/apps/girlscouts-vtk/components/vtk/include/session.jsp"%>
<% EmailMeetingReminder emr = troop.getSendingEmail(); %>
<br/>PREVIEW: sending NOW
<div style="background-color:gray">Address List</div>

<br/>Girls /Parents <%= emr.getEmailToGirlParent()!=null ? "CHECKED\n"+emr.getEmailToGirlParent() : "" %>
<br/>Self <%= emr.getEmailToSelf()!=null ? "CHECKED\n"+emr.getEmailToSelf() : "" %>
<br/>Troop Volunteers <%= emr.getEmailToTroopVolunteer()!=null ? "CHECKED\n"+emr.getEmailToTroopVolunteer() : "" %>


<br/>Enter your own:<%=emr.getCc() %>


<br/>To: <%= emr.getTo()%>
<br/>Subject: <%=emr.getSubj() %>



<br/><%= emr.getHtml() %>
<!--  
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

-->








