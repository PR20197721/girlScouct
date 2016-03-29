<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, java.util.Random"  %>
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

<%
final String text = properties.get("text", "");
final String resultsPath = properties.get("results", "");
Resource image = resource.getChild("image");
String filePath = "";
if(image == null){
	if(WCMMode.fromRequest(request) == WCMMode.EDIT){
		%> *** Please select an image *** <%
	}
}
else{
	if(image != null) {
		filePath = ((ValueMap)image.adaptTo(ValueMap.class)).get("fileReference", "");
	}
	String id = generateId();
	final String resPath = resource.getPath();
	%>
	<script>
	//variable to be passed to app.js function
	var loc = "<%= resourceResolver.map(resultsPath) %>.html";

	document.styleSheets[0].insertRule("#<%= id%> { background: url('<%= getImageRenditionSrc(resourceResolver, filePath, getResourceLocation(resource))%>') no-repeat 0% 0%/100% 100% transparent; }", 0);
	</script>

	<div class="row">
	  <div class="wrapper clearfix" id="<%= id %>" data-at2x="<%= get2xPath(getImageRenditionSrc(resourceResolver, filePath, getResourceLocation(resource))) %>">
	    <div class="wrapper-inner clearfix">
	        <form class="find-camp clearfix" name="find-camp">
	        <label for="zip-code"><%= text %></label>
	        <div class="form-wrapper clearfix">
	          <input type="text" pattern="[0-9]*" maxlength="5" placeholder="ZIP Code" title="5 number zip code" name="zip-code">
	          <input type="submit" class="link-arrow" value="Go >"/>
	          <!-- <span>Please enter a valid zip code</span> -->
	        </div>
	      </form>
	    </div>
	  </div>
	</div>
<% } %>

