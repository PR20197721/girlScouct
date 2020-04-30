<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp"%>

<%
String cookieClass = "";
if (isCookiePage(currentPage)) {
   cookieClass = " cookie-page";
}
String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
String headerNavPath = headerPath + "/header-nav";   
ValueMap headerNavProps = resourceResolver.resolve(headerNavPath).adaptTo(ValueMap.class);
Boolean sticky = headerNavProps.get("displayStickyNav", false);
String stickyClass = sticky ? "sticky-nav" : "";
%>

<!-- body -->
<body data-grid-framework="f4" data-grid-color="salmon" data-grid-opacity="0.5" data-grid-zindex="10" data-grid-gutterwidth="10px" data-grid-nbcols="24">
    <!-- Google Tag Manager -->
    <noscript>
        <iframe src="//www.googletagmanager.com/ns.html?id=GTM-K6Z8DS" height="0" width="0" style="display:none;visibility:hidden"></iframe>
    </noscript>
    <!-- End Google Tag Manager -->
    <div id="fb-root"></div>
    <!-- Allows Facebook Share buttons -->
    <div class="off-canvas-wrap" data-offcanvas>
        <div class="inner-wrap<%= cookieClass%>">
            <section class="main-section">
                <%-- below print image logo is hard-coded since we cannot incorporate within existing components --%>
                <img src="/content/dam/girlscouts-gsusa/images/logo/logo_print.png" data-at2x="/content/dam/girlscouts-gsusa/images/logo/logo_print@2x.png" id="mainGSLogoPrint" width="168px" style="display:none;" />
                <div class="header <%=stickyClass%>">
                    <cq:include script="header.jsp" />
                </div>
                <div class="header-placeholder"></div>
                <div class="main-content row">
                    <cq:include script="content.jsp" />
                </div>
                <footer>
                    <cq:include script="footer.jsp" />
                </footer>
                <cq:include script="bodylibs.jsp" />
            </section>
            <!-- close the off-canvas menu -->
            <a class="exit-off-canvas-el"></a>
        </div>
    </div>
    <cq:includeClientLib js="apps.gsusa" />
    <script type="text/javascript">
        _satellite.pageBottom();
    </script>
</body>
<!-- END of body -->

<!-- GSDO-1030-Fix for Anchor Scrolling with base tag -->
<cq:includeClientLib categories="common.components.base" /> 