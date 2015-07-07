<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
%>
<section class="clearfix">
    <form class="email-signin" action="#">
    	<input name="CORPHOME" type="hidden" value="Yes" />
        <label>Keep in touch</label>
        <input type="email" name="email" placeholder="Email address" />
    </form>
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
    <div class="social-links">
        <ul class="inline-list">
            <li><a href="https://www.facebook.com/gsgcfl"><img src="/etc/designs/girlscouts-usa-green/images/facebook_30_white.png"></a></li>
            <li><a href="https://twitter.com/gsgc"><img src="/etc/designs/girlscouts-usa-green/images/twitter_30_white.png"></a></li>
            <li><a href="https://www.flickr.com/photos/gsgc/"><img src="/etc/designs/girlscouts-usa-green/images/flickr_30_white.png"></a></li>
            <li><a href="http://www.youtube.com/user/gsgcouncil?feature=watch"><img src="/etc/designs/girlscouts-usa-green/images/youtube_30_white.png"></a></li>
        </ul>
    </div>
</section>
<!-- END of footer -->
