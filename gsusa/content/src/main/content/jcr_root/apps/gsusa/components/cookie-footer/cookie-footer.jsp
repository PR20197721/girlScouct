<%--

  Cookie Footer component.



--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>
<div class="install-app">
    <i class="icon-download"></i>
    <div class="wrapper clearfix">
        <p>Install</p>
        <p>The official Girl Scout Cookie Finder App</p>
    </div>
    <div class="wrapper clearfix">
        <a href="https://itunes.apple.com/us/app/girl-scout-cookie-finder/id593932097?mt=8" target="_blank" class="apple-download">
        <img src="/etc/designs/gsusa/clientlibs/images/itunes-app-store-logo.png" alt="app store"/><span>Girl Scouts iPhone App</span></a>
        <a href="https://play.google.com/store/apps/details?id=com.gsa.gscookiefinder" target="_blank" class="google-play-download"><img src="/etc/designs/gsusa/clientlibs/images/android_google_play_logo.png" alt="google play store"/><span>Girl Scouts Android App</span></a>
    </div>
</div>

<div class="get-updates">
    <i class="icon-mail"></i>
    <div class="wrapper clearfix">
        <p>Get Updates</p>
        <p>Sign up for cookie email updates</p>
    </div>
    <form id="emailSignupNav" name="emailSignupNav" action="#" novalidate="novalidate">
        <div class="wrapper clearfix">
            <input name="email" type="text" class="required email" id="newsletter" placeholder="email address" maxlength="30">
            <input name="zipcode" type="text" class="required zipcode" id="newsletterzipcode" placeholder="Zip Code" maxlength="5">
        </div>
        <div class="wrapper clearfix">
            <input name="alumna" id="alumna" type="checkbox" value="alumna" /> <label for="alumna">I'm a Girl Scout Alumna.</label>
            <input type="submit" name="go" class="submit button" value="Submit" />
        </div>
        <p class="error hide">Invalid email or zipcode</p>
  </form>
</div>
