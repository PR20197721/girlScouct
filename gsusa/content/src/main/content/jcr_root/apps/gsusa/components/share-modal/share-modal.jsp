<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
	String header = (String)request.getAttribute("gsusa-share-model-header");
	if (header == null) {
		header = properties.get("header","");
	}
	String button = properties.get("btn","");
	String desc = properties.get("desc","");
	String text1 = properties.get("text1","");
	String fbtitle = properties.get("fbtitle","");
	String fbdesc = properties.get("fbdesc","#");
	String icon1 = properties.get("icon1","");
	String text2 = properties.get("text2","");
	String tweet = properties.get("tweet","");
	String icon2 = properties.get("icon2", "");
	String hashTags = properties.get("hashtags","");

	// Get the URL
	String url = properties.get("url", currentPage.getPath());
	url = resourceResolver.map(currentPage.getPath());
	if (!url.contains(".html")) {
	    url += ".html";
	}

	String facebookId = currentSite.get("facebookId", "");

	Resource img = resource.getChild("image");
	String filePath = "";
	if(img != null) {
		filePath = ((ValueMap)img.adaptTo(ValueMap.class)).get("fileReference", "");
	}

	if (button.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT) {
		%> ****Please configure the modal button text **** <%
	} else {
		if(WCMMode.fromRequest(request) == WCMMode.EDIT){
			%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
		}
%>
<div class="share-modal">
	<a href="#" data-reveal-id="shareModal" class="button"><%= button %></a>
	<!-- Reveal Modals begin -->
	<div id="shareModal" class="reveal-modal share-modal" data-reveal aria-labelledby="firstModalTitle" aria-hidden="true" role="dialog">
	  <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
	  <div class="float-left">
	    <% if(!filePath.equals("")){ %>
	    <img src="<%= filePath %>" alt="" />
	    <% } %>
	  </div>
	  <div class="float-right">
	      <h4><%= header %></h4>
	      <p><%= desc %></p>
	      <% if(!fbtitle.equals("") && !fbdesc.equals("") && !text1.equals("")){ %>
	      <a class="button" onclick="postToFeed(); return false;"><%= text1 %> <% if(!icon1.equals("")){ %><i class="<%= icon1 %>"></i><% } %></a>
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
		scriptTag.src="http://connect.facebook.net/en_US/all.js";
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

      function postToFeed() {

        // calling the API ...
        var obj = {
          method: 'feed',
          link: '<%= url %>',
          name: '<%= fbtitle %>',
          description: '<%= fbdesc.replace("\'","\\'") %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>