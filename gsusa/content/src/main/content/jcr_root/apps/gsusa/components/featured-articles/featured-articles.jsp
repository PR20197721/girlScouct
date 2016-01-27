<%--
  blockquote component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@page session="false" %>

<%


	String title = properties.get("title", "");
	String largeArticle = properties.get("largeArticle", "");
	String smallArticle1 = properties.get("smallArticle1", "");
	String smallArticle2 = properties.get("smallArticle2", "");
	String smallArticle3 = properties.get("smallArticle3", "");
	String smallArticle4 = properties.get("smallArticle4", "");

if(title.isEmpty() && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>
<h4>##Configure Featured Article Title##</h4>
<% }else{%>
<h4><%=title%></h4>
<% } %>
<div>
	<%
    if(!largeArticle.isEmpty()){
    	request.setAttribute("articlePath", largeArticle);%>
	<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
</div>
    <%} else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
    <div><section>LARGE ARTICLE</section></div>
	<%} else{%>
	<div><section></section></div>
	<%}%>
	<div class="block-grid">
    	<ul>
    		<li>
                <%
        		if(!smallArticle1.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle1);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
    			<div><section>ARTICLE 1</section></div>
				<%} else{%>
				<div><section></section></div>
				<%}%>
            </li>
            <li>
                <%
        		if(!smallArticle2.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle2);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
   				<div><section>ARTICLE 2</section></div>
				<%} else{%>
				<div><section></section></div>
				<%}%>
            </li>
    		<li>
                <%
        		if(!smallArticle3.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle3);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
    			<div><section>ARTICLE 3</section></div>
				<%} else{%>
				<div><section></section></div>
				<%}%>
            </li>
            <li>
                <%
        		if(!smallArticle4.isEmpty()){
        			request.setAttribute("articlePath", smallArticle4);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else if(WCMMode.fromRequest(request) == WCMMode.EDIT){%>
    			<div><section>ARTICLE 4</section></div>
				<%} else{%>
				<div><section></section></div>
				<%}%>
            </li>
        </ul>
    </div>

