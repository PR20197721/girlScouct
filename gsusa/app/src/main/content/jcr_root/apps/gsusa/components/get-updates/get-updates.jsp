<%@include file="/libs/foundation/global.jsp" %>

<%
	String label = properties.get("label", "Get Updates");
	String desc = properties.get("desc", "Sign up for cookie email updates");
%>
<section>
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
            <input name="zipcode" type="tel" pattern="[0-9]{5}" class="required zipcode" id="newsletterzipcode" placeholder="Zip Code" maxlength="5" />
            <p class="error hide">Invalid email or zipcode</p>
        </div>
        <div class="wrapper clearfix">
            <input name="alumna" id="alumna" type="checkbox" /> <label for="alumna">I'm a Girl Scout Alumna.</label>
            <input type="submit" name="go" class="submit button" value="Submit" />
        </div>
  </form>
</section>
<p class="success hide">Thank you for subscribing!</p>