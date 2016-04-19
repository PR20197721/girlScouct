<%@ page

import="org.girlscouts.web.councilrollout.CouncilCreator,
com.day.cq.wcm.api.Page,
com.day.cq.tagging.Tag,
java.util.ArrayList,
java.util.Iterator"%>

<%@include file="/libs/foundation/global.jsp"%>

<%
String action = request.getParameter("action");
final String contentPath = "/content";

if (action == null || action.isEmpty()) {
    %>Action is null. Abort<%
    return;
}

if (action.equals("create")) {
    String councilTitle = request.getParameter("councilTitle");
    if (councilTitle == null || councilTitle.isEmpty()) {
        %>HomePage Title is empty. Abort.<%
        return;
    }

    String councilName = request.getParameter("councilName");
    if (councilName == null || councilName.isEmpty()) {
        %>HomePage Name is empty. Abort<%
        return;
    }

Session session = (Session) resourceResolver.adaptTo(Session.class);
Node contentNode = session.getNode(contentPath);

//Checks if the council node has already been created. if it has, then abort process.
    if (contentNode.hasNode(councilName)) {   
        %>
        <cq:include script="form.jsp" />
        Council Already Exists. Abort.
        <% 
    } 
    else {
        %><br>PAGES:<br><%
        //Create a service that will generate the Pages, Scaffolding, Assets, Tags and Groups
        CouncilCreator creator = sling.getService(CouncilCreator.class);
        ArrayList<Page> pageList = new ArrayList<Page>(creator.generateSite(session, resourceResolver, contentPath, councilName, councilTitle));
            for (Page p : pageList) {
            %>"<%= p.getTitle()%>" created under path:
            <%= p.getPath()%>
            <br>
            <%
            }
       
        %><br>SCAFFOLDING:<br><%
        ArrayList<Node> scaffoldingList = new ArrayList<Node>(creator.generateScaffolding(session, resourceResolver, councilName));
        for (Node s : scaffoldingList) { 
            %>"<%= s.getName() %>" scaffolding created under path:
            <%= s.getPath() %>
            <br>
            <%
        }

        %><br>ASSETS:<br><%
        ArrayList<Node> folderList = new ArrayList<Node>(creator.generateDAMFolders(session, contentPath, councilName, councilTitle));
        for (Node n : folderList) { 
            %>"<%= n.getName() %>" folder created under path:
            <%= n.getPath() %>
            <br>
            <%
        }

        %><br>TAGS:<br><%
        ArrayList<Tag> tagList = new ArrayList<Tag>(creator.generateTags(session, resourceResolver, councilName, councilTitle));
        for (Tag t : tagList) { 
            %>"<%= t.getTitle() %>" tag created under path:
            <%= t.getPath() %>
            <br>
            <%
        }
        
        %><br>DESIGN:<br><%
    	ArrayList<Node> designList = new ArrayList<Node>(creator.generateDesign(session, resourceResolver, councilName, councilTitle));
        for (Node d : designList) { 
            %>"<%= d.getName() %>" design created under path:
            <%= d.getPath() %>
            <br>
            <%
        }
        
        %><br>GROUPS:<br><%
        ArrayList<String> groupList = new ArrayList<String>(creator.generateGroups(session, councilName, councilTitle));
        if(groupList != null){
        	for (String g : groupList) { 
            	%><%= g %><br><%
        	}
        }
	}

	session.save();
}

%>