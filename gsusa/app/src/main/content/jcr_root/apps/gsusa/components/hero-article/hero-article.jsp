<%--
  blockquote component.
--%><%
%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode,
     			com.day.cq.search.result.Hit,
     			com.adobe.granite.ui.components.Config,
     			com.adobe.granite.ui.components.ComponentHelper,
				java.util.List, 
				java.util.ArrayList,
				java.lang.StringBuilder,
				com.day.cq.search.QueryBuilder" %>
<%@page session="false" %>
<%
	String title = properties.get("title", "");
	String articlePath = properties.get("articlepath", "");
	Boolean titleLink = properties.get("titlelink", false);
	
if(title.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT){
%> 
<% }else{
    if (titleLink == true) {
        %>  <h4><a href="<%= articlePath %>.html"><%=title%></a></h4><%
    } else {
		%> <h4><%=title%></h4> <%
    }
} %>

	<%
    if(articlePath.length() > 0) { 
    	request.setAttribute("articlePath", articlePath);%>
	<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />

    <%} else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
	<div class="article-tile"><h3>LARGE ARTICLE</h3></div>
	<%} else{%>
	<div class="article-tile"></div>
	<%}%>
	<div class="block-grid">
    	<cq:text property="text" tagClass="text" escapeXml="true" />
    </div>
 