<%@ page session="false" import="com.day.cq.wcm.api.Page" import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@include file="/libs/foundation/global.jsp"%>
<%

    Page listItem = (Page)request.getAttribute("listitem");
    
%><li>
    <p>
        This should work<a href="<%= listItem.getPath() %>.html" title="<%= StringEscapeUtils.escapeHtml4(listItem.getTitle()) %>"
           onclick="CQ_Analytics.record({event: 'listItemClicked', values: { listItemPath: '<%= listItem.getPath() %>' }, collect:  false, options: { obj: this }, componentPath: '<%=resource.getResourceType()%>'})"><%= StringEscapeUtils.escapeHtml4(listItem.getTitle()) %></a>
    </p>
</li>