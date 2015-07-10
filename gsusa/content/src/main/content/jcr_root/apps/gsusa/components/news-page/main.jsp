<%@include file="/libs/foundation/global.jsp" %>
<%
    String title = properties.get("jcr:title", "");
    String description = properties.get("description", "");
    String text = properties.get("text", "");
%>

<div id="mainContent">
    <div itemscope itemtype="http://schema.org/NewsArticle">
        <h1><%= title %></h1>
        <p itemprop="description" class="dec"><%= description %></p>
        <p itemprop="text" class="text"><%= text %></p>
    </div>
</div>
