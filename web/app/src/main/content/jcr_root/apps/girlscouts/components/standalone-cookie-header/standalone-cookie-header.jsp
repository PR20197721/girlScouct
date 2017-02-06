<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder, java.util.Random"  %>
<%@page session="false" %>
<%!
public String generateId() {
	Random rand=new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 6; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
}
%>


<%if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.girlscouts.authoring" /><%
}
final String bgcolor = properties.get("bgcolor", "6e298d"); //the default purple color
final String mainText = properties.get("maintext", "");
final boolean hasRightShareSection = properties.get("shareSection", false);
final boolean disableInMobile = properties.get("disableinmobile", false);
final boolean disableInDesktop = properties.get("disableindesktop", false);
final String shareSectionIcon = properties.get("icon", "");
final String shareSectionText = properties.get("sharetext", "");
String shareSectionLink = properties.get("sharelink", "");
final String cookieBoothLink = properties.get("cookieboothlink", "");
final String id = generateId();
Resource thumbnail = resource.getChild("thumbnail");
String filePath = "";
if(thumbnail != null) {
	filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
}
Resource mobileImage = resource.getChild("mobileimage");
String mobileImagePath = "";
if (mobileImage != null) {
	mobileImagePath = ((ValueMap)mobileImage.adaptTo(ValueMap.class)).get("fileReference", "");
}

Page shareSectionLinkPage = resourceResolver.resolve(shareSectionLink).adaptTo(Page.class);
if (shareSectionLinkPage != null && !shareSectionLink.contains(".html")) {
	shareSectionLink += ".html";
}
%>
<script>

document.styleSheets[0].insertRule("@media only screen and (min-width: 48.1225em) { #<%= id%>:before { background: url('<%= filePath%>') no-repeat 0% 0%/contain transparent; } }", 0);
<% if(currentPage.getPath().equals(currentPage.getAbsoluteParent(2).getPath())){ %>

document.styleSheets[0].insertRule("@media only screen and (max-width: 48em) { #<%= id%> form label:before { background: url('<%= mobileImagePath%>') no-repeat 0% 0%/contain transparent; } }", 0);
<% } else {%>
    document.styleSheets[0].insertRule("@media only screen and (max-width: 48em) { #<%= id%>:before { background: url('<%= mobileImagePath%>') no-repeat 0% 0%/contain transparent; } }", 0);
<% } %>
//$('.find-cookies-share, .find-cookies-noshare').attr("action", "content/gsusa/en/booth-result.10036.html");
$(document).ready(function(){
	cookieFormSubmitted = false;
	$('.find-cookies-share, .find-cookies-noshare').submit(function(event){
		if(event.preventDefault){
			event.preventDefault()
		} else {
			event.stop()
		}
		event.returnValue = false;
		event.stopPropagation();

		if (cookieFormSubmitted) {
			return;
		}

	    var zip = $(this).find('input[name="zip-code"]').val(),
	    	loc = "<%=resourceResolver.map(cookieBoothLink)%>.html";
	    var redirectUrl = loc;
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
	    cookieFormSubmitted = true;
	});
});
</script>

<%
if(currentPage.getPath().equals(currentPage.getAbsoluteParent(2).getPath())){
	%>
	<!-- HOMEPAGE VERSION -->
<div class="row homepage">
  <!--img src="/etc/designs/gsusa/clientlibs/images/zip-cookie-bg.png" alt="cookie zip code image" /-->
  <div class="wrapper clearfix" style="background: #<%= bgcolor%>">
    <div class="wrapper-inner clearfix" id="<%= id %>">
    <%if (hasRightShareSection) { %>
      <form class="find-cookies-share" name="find-cookies">
    <%} else {%>
        <form class="find-cookies-noshare" name="find-cookies">
    <% }%>
        <label for="zip-code"><%= mainText %></label>
        <div class="form-wrapper clearfix">
          <input type="text" placeholder="ZIP Code" maxlength="5" pattern="[0-9]*" title="5 number zip code" class="zip-code" name="zip-code">
          <input type="submit" class="link-arrow" value="Go >"/>
        </div>
      </form>
      <%if (hasRightShareSection)  { %>
	      <div class="share">
	        <a href="<%=shareSectionLink %>" title="cookies on facebook" target="_blank"><span><%= shareSectionText %><i class="<%= shareSectionIcon %>"></i></span></a>
	      </div>
      <%} %>
    </div>
  </div>
</div>

	<%
}else{
%>
	<!-- NON-HOMEPAGE VERSION -->
    <div class="row">
      <!--img src="/etc/designs/gsusa/clientlibs/images/zip-cookie-bg.png" alt="cookie zip code image" /-->
      <div class="wrapper clearfix" style="background: #<%= bgcolor%>">
        <div class="wrapper-inner clearfix" id="<%= id %>">
        <%if (hasRightShareSection) { %>
          <form class="find-cookies-share" name="find-cookies">
        <%} else {%>
            <form class="find-cookies-noshare" name="find-cookies">
        <% }%>
            <label for="zip-code"><%= mainText %></label>
            <div class="form-wrapper clearfix">
              <input type="text" placeholder="ZIP Code" maxlength="5" pattern="[0-9]*" title="5 number zip code" class="zip-code" name="zip-code">
              <input type="submit" class="link-arrow" value="Go >"/>
            </div>
          </form>
          <%if (hasRightShareSection)  { %>
              <div class="share">
                <a href="<%=shareSectionLink %>" title="cookies on facebook" target="_blank"><span><%= shareSectionText %><i class="<%= shareSectionIcon %>"></i></span></a>
              </div>
          <%} %>
        </div>
      </div>
    </div>
<%
}
%>




