<%@include file="/libs/foundation/global.jsp" %>
<!-- footer -->
<%
    // All pages share the same footer from the site root.
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
%>
<section>
    <ul class="inline-list">
        <li><a href="#" title="Donations">Donations</a></li>
        <li><a href="#" title="Sponsorships">Sponsorships</a></li>
        <li><a href="#" title="Newsletter">Newsletter</a></li>
        <li><a href="#" title="Privacy Policy">Privacy Policy</a></li>
        <li><a href="#" title="Terms and Conditions">Terms and Conditions</a></li>
    </ul>
    <form class="email-signin" action="#">
        <label>Keep in touch</label>
        <input type="email" name="email" placeholder="Email address" />
    </form>
</section>
<section>
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
