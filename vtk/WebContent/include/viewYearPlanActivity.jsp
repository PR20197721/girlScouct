<%

Activity activity = (Activity) _comp;
%>
<div style=" background-color:#FFF;">

<div style="float:left; width:100px; height:100px;background-color:blue; color:#fff;">
	
	
	<%
	System.err.println( "tatat: "+ searchDate );
	if( prevDate!=0 ){ %>
		<a href="planView.jsp?elem=<%=prevDate%>"> << PREV </a>
	<%} %>
	
	<br/><%=fmt.format(searchDate) %>
	
	
	<%if( nextDate!=0 ){ %>
		<br/><a href="planView.jsp?elem=<%=nextDate%>">NEXT>></a>
	<%} %>
	
	

</div>

<h1>Activity</h1>
Activity: <%= activity.getName()%>

<input type="button" value="delete this activity" onclick="rmCustActivity('<%=activity.getPath()%>')"/>


<br/><br/>Date: <%=fmtDate.format(activity.getDate()) %>
<br/><br/>Time: <%=fmtHr.format(activity.getDate()) %> - <%= fmtHr.format(activity.getEndDate()) %> 
<br/><br/>Age range:
<br/><br/>Location: <%=activity.getLocationRef() %>
<%
   if( activity.getLocationRef()!=null && user.getYearPlan().getLocations()!=null )
	for(int k=0;k<user.getYearPlan().getLocations().size();k++){
		if( user.getYearPlan().getLocations().get(k).getPath().equals( activity.getLocationRef() ) )
			%>
				<br/><%=user.getYearPlan().getLocations().get(k).getName() %>
				<br/><%=user.getYearPlan().getLocations().get(k).getAddress() %>
				<%=user.getYearPlan().getLocations().get(k).getCity() %>
				<%=user.getYearPlan().getLocations().get(k).getState() %>
				<%=user.getYearPlan().getLocations().get(k).getZip() %>
			<% 
	}
%>

<br/><br/>Cost:

<div style="background-color:#efefef"><%=activity.getContent()%></div>