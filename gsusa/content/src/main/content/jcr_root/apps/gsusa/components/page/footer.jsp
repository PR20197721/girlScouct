<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
    String keepInTouchPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/keepInTouch";
%>
<section class="clearfix">
	
	<cq:include path="<%= keepInTouchPath %>" resourceType="gsusa/components/keep-in-touch" />
    
    <ul class="inline-list">
        <li><a href="/content/gsusa/en/contact-us.html" title="Contact Us">Contact Us</a></li>
        <li><a href="#" title="Visit Us">Visit Us</a></li>
        <li><a href="#" title="Careers">Careers</a></li>
        <li><a href="#" title="Blog">Blog</a></li>
        <li><a href="#" title="Press Room">Press Room</a></li>
        <li><a href="#" title="FAQ">FAQ</a></li>
        <li><a href="#" title="Investors">Investors</a></li>
        <li><a href="#" title="Subscribe to Our Newsletter">Subscribe to Our Newsletter</a></li>
        <li><a href="#" title="Help">Help</a></li>
        <li><a href="#" title="Follow Us">Follow Us</a></li>
        <li><a href="#" title="Trademark Statement">Trademark Statement</a></li>
    </ul>
</section>
<section class="clearfix">
    <span class="copyright">&#64;2015 Girl Scouts of America</span>
    <div class="social-links clearfix">
        <div class="small-centered columns small-13 large-14 large-uncentered medium-uncentered medium-14">
            <ul class="inline-list">
                <li><a href="https://www.facebook.com/gsgcfl"><img src="/etc/designs/girlscouts-usa-green/images/facebook_30_white.png"></a></li>
                <li><a href="https://twitter.com/gsgc"><img src="/etc/designs/girlscouts-usa-green/images/twitter_30_white.png"></a></li>
                <li><a href="https://www.flickr.com/photos/gsgc/"><img src="/etc/designs/girlscouts-usa-green/images/flickr_30_white.png"></a></li>
                <li><a href="http://www.youtube.com/user/gsgcouncil?feature=watch"><img src="/etc/designs/girlscouts-usa-green/images/youtube_30_white.png"></a></li>
            </ul>
        </div>
    </div>
</section>
<!-- END of footer -->





<!-- header -->
<%
    // All pages share the same header from the site root, except Join and Volunteer!
    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String eyebrowNavPath = headerPath + "/eyebrow-nav";
    String headerSearchPath = headerPath + "/search";
%>
<div class="top-header row">
    <section class="logo-section">
        <a href="/content/gsusa/en.html" title="girscouts.com home">
            <cq:include path="<%= logoPath %>" resourceType="gsusa/components/logo" />
        </a>
    </section>
    <section class="utility show-for-medium-up">
    	<div class="clearfix">
	        <cq:include path="<%=eyebrowNavPath %>" resourceType="gsusa/components/eyebrow-nav" />
            <div class="join-buttons">
    	        <cq:include path="header/join" resourceType="gsusa/components/header-join-now"/>
    	        <cq:include path="header/volunteer" resourceType="gsusa/components/header-volunteer-now"/>
            </div>
        </div>
        <div class="clearfix">
    	    <cq:include path="<%= headerSearchPath %>" resourceType="gsusa/components/search-box" />
        </div>
    </section>
</div>
<cq:include path="<%= headerNavPath %>" resourceType="gsusa/components/header-nav" />
<!--/header -->
