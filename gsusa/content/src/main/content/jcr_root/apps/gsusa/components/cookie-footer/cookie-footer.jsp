<%--

  Cookie Footer component.



--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>
<div class="row">
    <div class="install-app">
        <p>Install App</p>
        <p>The official Girl Scout Cookie Finder App</p>
        <i class="icon-platform-apple"></i>
        <i class="icon-platform-android"></i>
    </div>
    <div class="get-updates">
        <p>Get Updates</p>
        <p>Sign up for cookie email updates</p>
        <form id="emailSignupNav" name="emailSignupNav" action="#" novalidate="novalidate">
          <input name="email" type="text" class="required email" id="newsletter" placeholder="email address" maxlength="30"><br>
          <input name="zipcode" type="text" class="required zipcode" id="newsletterzipcode" placeholder="zip code" maxlength="5"><br>
            <span class="alumniTxt"><input name="alumna" id="alumna" type="checkbox" value="alumna">&nbsp; I'm a Girl Scout Alumna.</span>
          <input type="submit" name="go" class="submit" value="Submit">
          <div class="newslettererror" style="display:none;"><p>Invalid email or zipcode</p></div>
      </form>
    </div>
</div>