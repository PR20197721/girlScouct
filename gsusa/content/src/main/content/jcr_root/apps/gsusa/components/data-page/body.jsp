<%@page import="java.util.Map,
                java.util.Iterator,
                com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
if (WCMMode.fromRequest(request) != WCMMode.EDIT) {
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
} else {
    String scafLink = currentPage.getPath() + ".scaffolding.html";
%>
    <p>Welcome to the data page.</p>
	<p>Properties of this page are listed below. Click <a href="<%= scafLink %>">here</a> to edit.</p>
	<p>This page only displays in authoring mode.</p>
    <table>
<%    
    String[] toSkip = {
        "hideInNav"
    }; // Plus, any propery that begins with "jcr:", "sling:" or "cq:" is skipped, except for "jcr:title"

    Iterator iter = properties.entrySet().iterator();
    while (iter.hasNext()) {
        Map.Entry pair = (Map.Entry) iter.next();
        String key = (String)pair.getKey();
        if ((key.startsWith("jcr:") && !key.equals("jcr:title")) || 
                key.startsWith("cq:") ||
                key.startsWith("sling:")) {
            continue;
        }

        boolean skipped = false;
        for (int i = 0; i < toSkip.length; i++) {
            if (toSkip[i].equals(key)) {
                skipped = true;
                break;
            }
        }
        if (skipped) continue;
    
        String value = (String)pair.getValue();
%>
	        <tr>
		        <td><%= key %></td>
		        <td><%= value %></td>
	        </tr>
<%
    }
%>
    </table>
<%
}
%>