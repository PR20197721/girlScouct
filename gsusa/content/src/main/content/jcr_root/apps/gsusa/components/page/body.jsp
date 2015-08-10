<%@include file="/libs/foundation/global.jsp" %>
<!-- body -->
<body data-grid-framework="f4" data-grid-color="salmon" data-grid-opacity="0.5" data-grid-zindex="10" data-grid-gutterwidth="10px" data-grid-nbcols="24">
	   <!-- Google Tag Manager -->
	<noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-K6Z8DS"
	height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
	<script type="text/javascript">(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
	new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
	j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
	'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
	})(window,document,'script','dataLayer','GTM-K6Z8DS');</script>
	<!-- End Google Tag Manager -->
  <div id="fb-root"></div> <!-- Allows Facebook Share buttons -->
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
  <cq:includeClientLib js="apps.gsusa" />
</body>
<!-- END of body -->
