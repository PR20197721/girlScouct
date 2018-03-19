<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
	String pathIndexStr = (String)request.getAttribute("gsusa-component-booth-finder-index");

	String uniqueID = "" + System.currentTimeMillis();

	String header = (String)request.getAttribute("gsusa-share-model-header");
	if (header == null) {
		header = properties.get("header","");
	}
	String button = properties.get("btn","SHARE WITH YOUR FRIENDS");
	String desc = (String)request.getAttribute("gsusa-share-modal-description");
	if(desc == null){
		desc = properties.get("desc","");
	}
	String text1 = properties.get("text1","Share on Facebook");
	String fbtitle = (String)request.getAttribute("gsusa-share-model-header");
	if(fbtitle == null){
		fbtitle = properties.get("fbtitle","");
	}
	String fbdesc = properties.get("fbdesc",desc);
	String icon1 = properties.get("icon1","icon-social-facebook");
	String text2 = properties.get("text2","Share on Twitter");
	String tweet = (String)request.getAttribute("gsusa-share-modal-tweet");
	if(tweet == null){
		tweet = properties.get("tweet",desc);
	}
	String icon2 = properties.get("icon2", "icon-social-twitter-tweet-bird");
	String hashTags = properties.get("hashtags","");

	// Get the URL
	String url = properties.get("url", currentPage.getPath());
	url = resourceResolver.map(currentPage.getPath());
	if (!url.contains(".html")) {
	    url += ".html";
	}

	String facebookId = currentSite.get("facebookId", "");

	Resource modimg = resource.getChild("modimage");
	String modFilePath = (String)request.getAttribute("gsusa-share-modal-mod-img-path");
	if(modFilePath == null){
		if(modimg != null) {
			modFilePath = ((ValueMap)modimg.adaptTo(ValueMap.class)).get("fileReference", "");
		} else{
			modFilePath = "";
		}
	}

	Resource shareimg = resource.getChild("shareimage");
	String shareFilePath = (String)request.getAttribute("gsusa-share-modal-share-img-path");
	if(shareFilePath == null){
		if(shareimg != null) {
			shareFilePath = ((ValueMap)shareimg.adaptTo(ValueMap.class)).get("fileReference", "");
		}
	}

	if (button.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%> ****Please configure the modal button text **** <%
	} else {
		if(WCMMode.fromRequest(request) == WCMMode.EDIT){
			%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
		}

%>
<div class="share-modal">
	<a href="#" data-reveal-id="shareModalpath<%=pathIndexStr%>" class="button" style="display:none"><%= button %></a>
	<!-- Reveal Modals begin -->
	<div id="shareModalpath<%=pathIndexStr%>" class="reveal-modal share-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
	  <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
	  <div class="float-left">
	    <% if(!modFilePath.equals("")){ %>
	    <img src="<%= modFilePath %>" alt="" />
	    <% } %>
	  </div>
	  <div class="float-right">
	      <h4><%= header %></h4>
	      <p><%= desc %></p>
	      <% if(!fbtitle.equals("") && !fbdesc.equals("") && !text1.equals("")){ %>
	      <a class="button" onclick="postToFeed<%= uniqueID %>(); return false;"><%= text1 %> <% if(!icon1.equals("")){ %><i class="<%= icon1 %>"></i><% } %></a>
	      <% }
	      if(!text2.equals("") && !tweet.equals("")){
	      	tweet = tweet.replace("\"","%quot;").replace(" ","%20").replace("#","%23");
	      	hashTags = hashTags.replace("\"","%quot;").replace(" ","%20").replace("#","%23");
	      	%>
	      <a class="button" target="_blank" href="https://twitter.com/share?text=<%=tweet%>&hashtags=<%=hashTags%>"><%= text2 %> <% if(!icon2.equals("")){ %><i class="<%= icon2 %>"></i><% } %></a>
	      <% } %>
	  </div>
	</div>
</div>
<% } %>

<script type="text/javascript">
	$(document).ready(function() {
		var scriptTag = document.createElement("script");
		scriptTag.type = "text/javascript"
		scriptTag.src="//connect.facebook.net/en_US/all.js";
		scriptTag.async = true;
		document.getElementsByTagName("head")[0].appendChild(scriptTag);

		scriptTag.onload=initFB;
		scriptTag.onreadystatechange = function () {
		  if (this.readyState == 'complete' || this.readyState == 'loaded') initFB();
		}
	});
	function initFB() {
		FB.init({appId: "<%= facebookId %>", status: true, cookie: true});
	}

      function postToFeed<%= uniqueID %>() {

        // calling the API ...
        var obj = {
          method: 'feed',
          link: '<%= url %>',
          name: '<%= fbtitle.replaceAll("\\'","\\\\'") %>',
          picture: location.host + '<%= shareFilePath %>',
          caption: 'WWW.GIRLSCOUTS.ORG',
          description: '<%= fbdesc.replaceAll("\\'","\\\\'") %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }
</script>