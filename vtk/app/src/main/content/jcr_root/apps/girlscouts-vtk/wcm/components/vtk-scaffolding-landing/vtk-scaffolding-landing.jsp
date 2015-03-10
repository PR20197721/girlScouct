<%@page import="java.util.List,
               java.util.ArrayList,
               javax.jcr.Node,
               javax.jcr.NodeIterator" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
	<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
<%
	final String MEETING_BASE = "/content/girlscouts-vtk/meetings/myyearplan";
	final String YEAR_PLAN_BASE = "/content/girlscouts-vtk/yearPlanTemplates/yearplan2014";
	final Session session = resourceResolver.adaptTo(Session.class);
%>	
	<h1>Year Plans</h1>
<%
	NodeIterator iter = session.getNode(YEAR_PLAN_BASE).getNodes();
	while (iter.hasNext()) {
	    Node level = iter.nextNode();
	    String levelName = level.getName();
	    levelName = levelName.substring(0, 1).toUpperCase() + levelName.substring(1);
	    %><p><h2><%= levelName %></h2></p><%
	    
	    NodeIterator iter1 = level.getNodes();
	    while (iter1.hasNext()) {
	        Node yearPlan = iter1.nextNode();
			String yearPlanName = yearPlan.getProperty("name").getString();
			String editPath = yearPlan.getPath() + ".scaffolding.html";
%>
			<p>
				<input type="button" value="Edit" onclick="window.location.href='<%=editPath%>';"></input> <%= yearPlanName %>
			</p>
<%	        
	    }
	}
%>

<!--  TODO: Do not repeat code -->
	<h1>Meetings</h1>
<%
	iter = session.getNode(MEETING_BASE).getNodes();
	while (iter.hasNext()) {
	    Node level = iter.nextNode();
	    String levelName = level.getName();
	    levelName = levelName.substring(0, 1).toUpperCase() + levelName.substring(1);
	    %><p><h2><%= levelName %></h2></p><%
	    
	    NodeIterator iter1 = level.getNodes();
	    while (iter1.hasNext()) {
	        Node meeting = iter1.nextNode();
			String meetingName = meeting.getProperty("name").getString();
			String editPath = meeting.getPath() + ".scaffolding.html";
%>
			<p>
				<input type="button" value="Edit" onclick="window.location.href='<%=editPath%>';"></input> <%= meetingName %>
			</p>
<%	        
	    }
	}
%>