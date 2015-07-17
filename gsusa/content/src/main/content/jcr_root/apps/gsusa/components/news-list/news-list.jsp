<%@page import="com.day.cq.wcm.api.WCMMode,
                javax.jcr.NodeIterator,
                java.text.SimpleDateFormat,
                java.text.DateFormat,
                java.util.Locale,
                java.util.Date" %>
<%@include file="/libs/foundation/global.jsp" %>
<%
String rootPath = properties.get("rootPath", "");
if (rootPath.isEmpty()) {
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	    %>< Please choose the news path. ><%
	} else {
	    log.warn("Root Path for news is not set.");
	}
} else {
    Node rootNode = resourceResolver.resolve(rootPath).adaptTo(Node.class);
    NodeIterator yearIter = rootNode.getNodes();
    while (yearIter.hasNext()) {
        Node year = yearIter.nextNode();
        if (!year.getPrimaryNodeType().isNodeType("cq:Page")) continue;
%>
        <p class="news-year"><%= year.getName() %> News Releases</p>
        <ul>
<%
        NodeIterator newsIter = year.getNodes();
        while (newsIter.hasNext()) {
            Node news = newsIter.nextNode();
            if (!news.getPrimaryNodeType().isNodeType("cq:Page")) continue;
            ValueMap newsProps = resourceResolver.resolve(news.getPath() + "/jcr:content").adaptTo(ValueMap.class);
            String title = newsProps.get("articleTitle", "");
            if (title.isEmpty()) {
                title = newsProps.get("jcr:title", "");
            }

            String link = news.getPath() + ".html";

            DateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
            Date date = newsProps.get("date", Date.class);
            String dateStr = "";
            try {
            	dateStr = format.format(date);
            	dateStr = "(" + dateStr + ")";
            } catch (Exception e) {
                log.warn("Error date: " + news.getPath());
            }
%>
            <li>
                <a href="<%= link %>"><%= title %></a>
                <div class="date"><%= dateStr %></div>
            </li>
<%            
        }
%>
        </ul>
<%
    }
}
%>