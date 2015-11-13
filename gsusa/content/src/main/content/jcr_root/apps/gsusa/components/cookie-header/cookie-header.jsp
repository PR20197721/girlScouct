<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
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
	%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
}
final String bgcolor = properties.get("bgcolor", "6e298d"); //the default purple color
final String mainText = properties.get("maintext", "");
final boolean hasRightShareSection = properties.get("shareSection", false);
final String shareSectionIcon = properties.get("icon", "");
final String shareSectionText = properties.get("sharetext", "");
final String shareSectionLink = properties.get("sharelink", "");
final String id = generateId();
Resource thumbnail = resource.getChild("thumbnail");
String filePath = "";
if(thumbnail != null) {
	filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
}
Resource mobileImage = resource.getChild("mobileimage");
String mobileImagePath = "";
if(mobileImage != null) {
	mobileImagePath = ((ValueMap)mobileImage.adaptTo(ValueMap.class)).get("fileReference", "");
}
//medium up query: @media only screen and (min-width: 48.1225em)
//small-only: @media only screen and (max-width: 48em)
%>

<script>
document.styleSheets[0].insertRule("@media only screen and (min-width: 48.1225em) { #<%= id%>:before { background: url('<%= filePath%>') no-repeat 0% 0% transparent; } }");
document.styleSheets[0].insertRule("@media only screen and (max-width: 48em) { #<%= id%>:before { background: url('<%= mobileImagePath%>') no-repeat 0% 0% transparent; } }");
</script>

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
          <input type="text" placeholder="ZIP Code" class="zip-code" name="zip-code">
          <input type="submit" class="link-arrow" value="Go >"/>
        </div>
      </form>
      <%if (hasRightShareSection)  { %>
	      <div class="share">
	        <a href="<%=shareSectionLink %>" title="cookies on facebook"><span><%= shareSectionText %></span> <i class="<%= shareSectionIcon %>"></i></a>
	      </div>
      <%} %> 
    </div>
  </div>
</div>


