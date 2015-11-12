<%@include file="/libs/foundation/global.jsp" %>

<%
	String label = properties.get("label", "Get Updates");
	String desc = properties.get("desc", "Sign up for cookie email updates");
%>
<i class="icon-mail"></i>
<div class="wrapper clearfix">
    <p><%= label %></p>
    <% if(!desc.isEmpty()){ %>
    <p><%= desc %></p>
    <% } %>
  </div>
  <form id="emailSignupNav" name="emailSignupNav" action="#" novalidate="novalidate">
      <div class="wrapper clearfix">
          <input name="email" type="text" class="required email" id="newsletter" placeholder="email address" maxlength="30" />
          <input name="zipcode" type="text" class="required zipcode" id="newsletterzipcode" placeholder="zip code" maxlength="5" />
      </div>
      <div class="wrapper clearfix">
          <input name="alumna" id="alumna" type="checkbox" value="alumna" /> <label for="alumna">I'm a Girl Scout Alumna.</label>
          <input type="submit" name="go" class="submit button" value="Submit" />
      </div>
      <p class="error hide">Invalid email or zipcode</p>
</form>