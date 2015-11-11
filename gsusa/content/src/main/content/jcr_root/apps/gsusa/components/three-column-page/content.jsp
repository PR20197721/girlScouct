<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%

    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String eyebrowNavPath = headerPath + "/eyebrow-nav";
    String headerSearchPath = headerPath + "/search";
    String cookieHeaderPath = headerPath + "/cookie-header";

    %>
<!-- content -->
<div id="main" class="three-cols">
    <cq:include path="content/top/par" resourceType="girlscouts/components/styled-parsys" />
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="right-wrapper">
        <% if (isCookiePage(currentPage)) { %>
                <cq:include path="<%= cookieHeaderPath %>" resourceType="gsusa/components/cookie-header" />
        <% } %>
        <div class="hero-section">
            <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
            <cq:include path="content/hero/par" resourceType="girlscouts/components/styled-parsys" />
            <cq:include path="./video-slider0" resourceType="gsusa/components/video-slider" />
        </div>
        <div class="middle-col">
            <cq:include script="main.jsp"/>
            <cq:include path="./video-slider1" resourceType="gsusa/components/video-slider" />
        </div>
        <div class="right-col">
            <cq:include script="right.jsp"/>
        </div>
    <cq:include script="cookie-footer.jsp"/>
    </div>
    <cq:include path="content/bottom/par" resourceType="girlscouts/components/styled-parsys" />
</div>
<!-- END of content -->
