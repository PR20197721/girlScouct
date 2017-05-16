<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String text = properties.get("text", "");
String[] images = properties.get("images", String[].class);

Resource 

// Mobile
String backgroundColor = properties.get("backgroundcolor", "FFFFFF");
String backgroundImage = properties.get("", "");
String backgroundColorOpacity = properties.get("", "");
String boxLogoMobile = properties.get("boximagemobile", "");


if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == null || images.length == 0)) {
    %>GIRL Join Redirect Component. Double click here to edit.<%
} else {
%>
    <div class="join-redirect-hero hide-for-small">
      <div class="welcome-video-slider">
<%	    for (String image : images) {
			int lastDotPos = image.lastIndexOf(".");
			String img2x = image.substring(0, lastDotPos) + "@2x" + image.substring(lastDotPos);
%>
      		<div><img src="<%= image %>" data-at2x="<%= img2x %>" alt="" /></div>
<%    } %>
      </div>
      <div class="cookie-header">
        <div class="wrapper clearfix">
          <div class="wrapper-inner clearfix">
            <form class="find-cookies" name="find-cookies">
              <label for="zip-code"><%= text %></label>
              <div class="form-wrapper clearfix">
                <input type="text" placeholder="ZIP Code" maxlength="5" pattern="[0-9]{5}" title="5 number zip code" class="zip-code" name="zip-code">
                <input type="submit" class="link-arrow" value="Go >"/>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
<%
    }
%>