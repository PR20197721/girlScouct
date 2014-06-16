<%

Activity activity = (Activity) _comp;
%>
<div style=" background-color:#FFF;">

<div style="float:left; width:100px; height:100px;background-color:blue; color:#fff;">
	
	
	<%
	System.err.println( "tatat: "+ searchDate );
	if( prevDate!=0 ){ %>
		<a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"> << PREV </a>
	<%} %>
	
	<br/><%=fmt.format(searchDate) %>
	
	
	<%if( nextDate!=0 ){ %>
		<br/><a href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>">NEXT>></a>
	<%} %>
	
	

</div>

<h1>Activity</h1>
Activity: <%= activity.getName()%>


<%if( activity.getDate().after( new java.util.Date())){ %>
<input type="button" value="delete this activity" onclick="rmCustActivity12('<%=activity.getPath()%>')"/>
<%} %>

<br/><br/>Date: <%=fmtDate.format(activity.getDate()) %>
<br/><br/>Time: <%=fmtHr.format(activity.getDate()) %> - <%= fmtHr.format(activity.getEndDate()) %> 
<br/><br/>Age range: <%= user.getApiConfig().getUser().getAgeLevel()%>
<br/><br/>Location: 
<%= activity.getLocationName() %>
<%=activity.getLocationAddress()%>


<br/><br/>Cost:

<div style="background-color:#efefef"><%=activity.getContent()%></div>