<%@include file="/libs/foundation/global.jsp" %>
<!-- body -->
<%
    // All pages share the same header from the site root.
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
%>
<body>
    <div class="off-canvas-wrap">

      <div class="inner-wrap">
        <section class="main-section">
            <div class="header row">
              <cq:include script="header.jsp"/>
            </div>
            <div class="content row">
              <cq:include script="content.jsp"/>
            </div>
            <footer class="row">
              <cq:include script="footer.jsp"/>
            </footer>
            <cq:include script="bodylibs.jsp"/>
        </section>
        <!-- close the off-canvas menu -->
         <a class="exit-off-canvas"></a>
      </div>
    </div>
</body>
<!-- END of body -->
