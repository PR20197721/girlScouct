<%@include file="/libs/foundation/global.jsp" %>
<!-- body -->
<body>
    <div class="off-canvas-wrap" data-offcanvas>

        <div class="inner-wrap">
            <section class="main-section">
                <div class="header">
                  <cq:include script="header.jsp"/>
                </div>
                <div class="main-content row">
                  <cq:include script="content.jsp"/>
                </div>
                <footer>
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
