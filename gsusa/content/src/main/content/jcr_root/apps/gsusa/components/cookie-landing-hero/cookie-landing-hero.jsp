<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String text = properties.get("text", "");
String facebookLink = properties.get("facebookLink", "");
String[] images = properties.get("images", String[].class);
if (images == null || images.length == 0) {
    %>Cookie Landing Component. Double click here to edit.<%
} else {
%>
    <div class="cookie-landing-hero hide-for-small">
      <div class="welcome-video-slider">
<%    for (String image : images) { %>
      	<div><img src="<%= image %>" alt="" /></div>
<%    } %>
      </div>
      <div class="cookie-header">
        <div class="wrapper clearfix">
          <div class="wrapper-inner clearfix">
            <form class="find-cookies" name="find-cookies">
              <label for="zip-code"><%= text %> %></label>
              <div class="form-wrapper clearfix">
                <input type="text" placeholder="ZIP Code" class="zip-code" name="zip-code">
                <input type="submit" class="link-arrow" value="Go >"/>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="facebook-image">
        <a href="<%= facebookLink %>" title="follow on facebook"><img src="/etc/designs/gsusa/images/facebook-image.png" alt="facebook" /></a>
      </div>
    </div>
<%
    }
%>