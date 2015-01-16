


<% 
EmailMeetingReminder emr = troop.getSendingEmail();
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










