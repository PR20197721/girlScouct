<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
    String keepInTouchPath = footerPath + "/keepInTouch";
    String footerNavPath = footerPath + "/footerNav";
    String footerSharePath = footerPath + "/footerShare";
%>
<section class="clearfix">
	<cq:include path="<%= keepInTouchPath %>" resourceType="gsusa/components/keep-in-touch" />
	<cq:include path="<%= footerNavPath %>" resourceType="gsusa/components/footer-nav" />
</section>
<section class="clearfix">
    <span class="copyright">&#64;2015 Girl Scouts of America</span>
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
<!-- END of footer -->

