<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String text = properties.get("text", "");
String facebookLink = properties.get("facebookLink", "");
String resultPage = properties.get("resultPage", currentPage.getPath());
String[] images = properties.get("images", String[].class);
if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == null || images.length == 0)) {
    %>Cookie Landing Component. Double click here to edit.<%
} else {
%>
    <div class="cookie-landing-hero hide-for-small">
      <div class="welcome-video-slider">
<%    for (String image : images) {
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
      <div class="facebook-image">
        <a href="<%= facebookLink %>" title="follow on facebook"><img src="/etc/designs/gsusa/images/facebook-image.png" alt="facebook" /></a>
      </div>
    </div>
    <script>
    	$(document).ready(function(){
    		heroFormSubmitted = false;
		    $('.cookie-landing-hero form[name="find-cookies"]').submit(function(){
		    	if (heroFormSubmitted) {
		    		return false;
		    	}

		        var zip = $(this).find('input[name="zip-code"]').val();
			    var redirectUrl = '<%= resourceResolver.map(resultPage) %>.' + zip + '.html'; 
			    var currentUrl = window.location.href;
			    var isSameUrl = currentUrl.substring(0, currentUrl.indexOf('.html')) == redirectUrl.substring(0, redirectUrl.indexOf('.html'));
			    var queryPos = currentUrl.indexOf('?');
			    if (queryPos != -1) {
			    	var queryStr = currentUrl.substring(queryPos);
			    	var hashPos = queryStr.indexOf('#');
			    	if (hashPos != -1) {
			    		queryStr = queryStr.substr(0, hashPos);
			    	}
			    	redirectUrl += queryStr;
			    }
			    redirectUrl = redirectUrl + '#' + zip;
			    window.location.href = redirectUrl;
			    if (isSameUrl) {
			    	window.location.reload();
			    }
		    
			    heroFormSubmitted = true;
		        return false;
		    });
    	});
    </script>
<%
    }
%>