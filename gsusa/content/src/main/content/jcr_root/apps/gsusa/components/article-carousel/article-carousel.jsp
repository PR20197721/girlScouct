<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="org.apache.sling.commons.json.*, java.io.*, java.util.regex.*, java.net.*, org.apache.sling.commons.json.*, org.apache.sling.api.request.RequestDispatcherOptions, com.day.cq.wcm.api.components.IncludeOptions, org.apache.sling.jcr.api.SlingRepository" %>
<%@page session="false" %>
<div class="article-slider">
    <div>
        <%request.setAttribute("articlePath", "/content/gsusa/en/press-room/news-releases/article_name");%>
        <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    </div>
    <div>
        <%request.setAttribute("articlePath", "/content/gsusa/en/press-room/news-releases/article_name");%>
        <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    </div>
    <div>
        <%request.setAttribute("articlePath", "/content/gsusa/en/press-room/news-releases/article_name");%>
        <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    </div>
    <div>
        <%request.setAttribute("articlePath", "/content/gsusa/en/press-room/news-releases/article_name");%>
        <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    </div>
    <div>
        <%request.setAttribute("articlePath", "/content/gsusa/en/press-room/news-releases/article_name");%>
        <cq:include path="article-tile" resourceType="gsusa/components/article-tile" />
    </div>
</div>
