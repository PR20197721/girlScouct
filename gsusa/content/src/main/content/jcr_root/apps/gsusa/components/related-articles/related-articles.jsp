<%--

  Related Articles component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%@page import="com.day.cq.wcm.api.WCMMode"%>
<%
	String article1 = properties.get("article1", "");
	String article2 = properties.get("article2", "");
	String article3 = properties.get("article3", "");
	String title = properties.get("title", "");
	if(title.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT){
	%>
		<h4>##Configure Featured Article Title##</h4>
	<% }else{%>
		<h4><%=title%></h4>
	<% } %>

	<div class="block-grid">
    	<ul>
    		<li>
                <%
        		if(!article1.isEmpty()) {
        			request.setAttribute("articlePath", article1);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 1</p>
                <% } else { %>
					<p></p>
                <%}%>
            </li>
            <li>
                <%
        		if(!article2.isEmpty()) {
        			request.setAttribute("articlePath", article2);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 2</p>
                <% } else { %>
					<p></p>
                <%}%>
            </li>
            <li>
                <%
        		if(!article3.isEmpty()) {
        			request.setAttribute("articlePath", article3);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT) { %>
        			<p>ARTICLE 3</p>
                <% } else { %>
                	<p></p>
                <%}%>
            </li>
        </ul>
	</div>
