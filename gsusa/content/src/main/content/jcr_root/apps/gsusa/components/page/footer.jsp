<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
    String keepInTouchPath = footerPath + "/keepInTouch";
    String footerNavPath = footerPath + "/footerNav";
    String footerSharePath = footerPath + "/footerShare";
    String copyrightPath = footerPath + "/copyright";
%>
<section class="clearfix">
  <div class="float-right">
	<cq:include path="<%= keepInTouchPath %>" resourceType="gsusa/components/keep-in-touch" />
  </div>
	<cq:include path="<%= footerNavPath %>" resourceType="gsusa/components/footer-nav" />
</section>
<section class="clearfix">
    <cq:include path="<%= copyrightPath %>" resourceType="girlscouts/components/text" />
    <div class="social-links clearfix">
        <div>
            <cq:include path="<%= footerSharePath %>" resourceType="gsusa/components/footer-share" />
            <!-- <ul class="inline-list">
                <li><a href="https://www.facebook.com/gsgcfl"><img src="/etc/designs/girlscouts-usa-green/images/facebook_30_white.png"></a></li>
                <li><a href="https://twitter.com/gsgc"><img src="/etc/designs/girlscouts-usa-green/images/twitter_30_white.png"></a></li>
                <li><a href="https://www.flickr.com/photos/gsgc/"><img src="/etc/designs/girlscouts-usa-green/images/flickr_30_white.png"></a></li>
                <li><a href="http://www.youtube.com/user/gsgcouncil?feature=watch"><img src="/etc/designs/girlscouts-usa-green/images/youtube_30_white.png"></a></li>
            </ul> -->
        </div>
    </div>
</section>
<section class="clearfix show-for-small-only footer-logo">
    <img src="/etc/designs/gsusa/clientlibs/images/footer_logo.png" alt="footer logo" />
</section>
<!-- END of footer -->
