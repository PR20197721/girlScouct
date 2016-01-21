<%--
  blockquote component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>

<%

    String tileModalDivId = (String)request.getAttribute("tileModalDivId");
	if(tileModalDivId == null){
		tileModalDivId = "videoModal_0";
    } else{
		String[] idSplit = tileModalDivId.split("_");
        int modalNumber = Integer.parseInt(idSplit[1]);
        modalNumber ++;
        tileModalDivId = "videoModal_" + modalNumber;
    }
	request.setAttribute("tileModalDivId", tileModalDivId);

	String largeArticle = properties.get("largeArticle", "");
	String smallArticle1 = properties.get("smallArticle1", "");
	String smallArticle2 = properties.get("smallArticle2", "");
	String smallArticle3 = properties.get("smallArticle3", "");
	String smallArticle4 = properties.get("smallArticle4", "");
%>
<div>
	<%
    if(!largeArticle.isEmpty()){
    	request.setAttribute("articlePath", largeArticle);%>
	<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
</div>
    <%} else{%>
    <p>Configure article</p>
    <%}%>
	<div class="block-grid">
    	<ul>
    		<li>
                <%
        		if(!smallArticle1.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle1);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else { %>
        			<p>Configure article</p>
        		<% } %>
            </li>
            <li>
                <%
        		if(!smallArticle2.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle2);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else { %>
        			<p>Configure article</p>
        		<%}%>
            </li>
    		<li>
                <%
        		if(!smallArticle3.isEmpty()) {
        			request.setAttribute("articlePath", smallArticle3);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else { %>
        			<p>Configure article</p>
        		<% } %>
            </li>
            <li>
                <%
        		if(!smallArticle4.isEmpty()){
        			request.setAttribute("articlePath", smallArticle4);%>
				<cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
       			<% } else { %>
        			<p>Configure article</p>
        		<% } %>
            </li>
        </ul>
    </div>
    <div id="<%=tileModalDivId%>" class="reveal-modal large" data-reveal aria-labelledby="videoModalTitle" aria-hidden="true" role="dialog">
        <div class="close"><a class="close-reveal-modal" aria-label="Close">&#215;</a></div>
        <div class="video-popup"></div>
    </div>