<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
  String[] links = properties.get("links", String[].class);
  Boolean centerLinks = (Boolean) request.getAttribute("centerLinks");
%>
<style>
#footer div.footer-navigation {
  padding: 0;
}
#footer a {
  color:#fff;
}
#footer ul {
  margin: 0;
  padding: 0;
}
#footer .footer-navigation div:last-child ul {
  text-align: right;
}
#footer li {
  display: inline;
  list-style: none;
}
#footer .footer-navigation div:last-child ul a,
#footer .footer-navigation div:last-child ul a {
  margin: 0;
}
[class^="icon-"], [class*=" icon-"] {
  color:white;
  font-size: 32px;
}
</style>
<div class="columns large-18 medium-18">
  <ul>
    <li><a href="/content/gateway/en/website/privacy-policy.html">Privacy Policy</a></li>
    <li><a href="/content/gateway/en/website/terms-and-conditions.html">Terms and Conditions</a></li>
    <li><a href="/content/gateway/en/website/terms-and-conditions.html">Content Monitoring</a></li>
    <li><a href="http://www.gsnetx.org/en/for-volunteers/spanish-website.html">En Espanol</a></li>
    <li><a href="https://gsusa.ebiz.uapps.net/vp/Home.aspx?pid=4">eBiz</a></li>
  </ul>
</div>
<div class="columns large-6 medium-6">
  <ul>
    <li><a><i class="icon-social-twitter-tweet-bird"></i></a></li>
    <li><a><i class="icon-social-facebook"></i></a></li>
    <li><a><i class="icon-social-instagram"></i></a></li>
    <li><a><i class="icon-video-movie"></i></a></li>
  </ul>
</div>
