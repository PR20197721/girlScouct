<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<!-- apps/girlscouts/components/page/body.jsp -->
<%
    String headerPath = homepage.getContentResource().getPath() + "/header";
    Boolean displayPageBanner = Boolean.FALSE;
    try {
        ValueMap globalNavProps = resourceResolver.getResource(headerPath + "/global-nav").adaptTo(ValueMap.class);
        if (globalNavProps != null) {
            displayPageBanner = globalNavProps.get("./displayPageBanner", Boolean.FALSE);
        }
    } catch (Exception e) {
    }
%>
<body data-grid-framework="f4" data-grid-color="darksalmon" data-grid-opacity="0.5" data-grid-zindex="10" data-grid-gutterwidth="10px" data-grid-nbcols="24">
<%-- <GSWP-2256> --%>
<!-- Google Tag Manager (noscript) -->
<noscript>
    <iframe src="https://www.googletagmanager.com/ns.html?id=GTM-TGGWNNL" height="0" width="0" style="display:none;visibility:hidden"></iframe>
</noscript>
<!-- End Google Tag Manager (noscript) -->
<%-- </GSWP-2256> --%>
<cq:include script="background.jsp"/>
<cq:include script="google-tag-manager.jsp"/>
<div class="off-canvas-wrap">
    <div class="inner-wrap<%if(displayPageBanner){%> round_corners<%}%>">
        <cq:include script="header.jsp"/>
        <cq:include script="content.jsp"/>
        <cq:include script="footer.jsp"/>
    </div>
</div>
<div id="gsModal"></div>
</body>