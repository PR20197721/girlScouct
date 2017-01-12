<%@page import="java.util.List,
               java.util.ArrayList,
               javax.jcr.Node,
               javax.jcr.NodeIterator" %><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
	<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
<%
	final String MEETING_BASE = "/content/girlscouts-vtk/meetings/myyearplan2016";
	final String YEAR_PLAN_BASE = "/content/girlscouts-vtk/yearPlanTemplates/yearplan2016";
	final String MEETING_SCAFFOLDING = "/etc/scaffolding/girlscouts-vtk/meeting.html";
	final String YEAR_PLAN_SCAFFOLDING = "/etc/scaffolding/girlscouts-vtk/year-plan.html";
	final Session session = resourceResolver.adaptTo(Session.class);
%>	
    <p><h1>Girl Scout VTk 2016 - 2017 Year Plan</h1></p>
	<p><h1>New</h1></p>
		<p>
			<input type="button" value="New Year Plan" onclick="window.location.href='<%= YEAR_PLAN_SCAFFOLDING %>'"></input>
			<input type="button" value="New Meeting" onclick="window.location.href='<%= MEETING_SCAFFOLDING %>'"></input>
		</p>
	<p><h1>Year Plans</h1></p>
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
            if (!yearPlan.getPrimaryNodeType().getName().equals("nt:unstructured")) {
                continue;
            }
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
	<p><h1>Meetings</h1></p>
<%
	iter = session.getNode(MEETING_BASE).getNodes();
	while (iter.hasNext()) {
	    Node level = iter.nextNode();
        if (!level.getPrimaryNodeType().getName().equals("nt:unstructured")) {
            continue;
        }
	    String levelName = level.getName();
	    levelName = levelName.substring(0, 1).toUpperCase() + levelName.substring(1);
	    %><p><h2><%= levelName %></h2></p><%
	    
	    NodeIterator iter1 = level.getNodes();
	    while (iter1.hasNext()) {
	        Node meeting = iter1.nextNode();
            if (!meeting.getPrimaryNodeType().getName().equals("nt:unstructured")) {
                continue;
            }
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
