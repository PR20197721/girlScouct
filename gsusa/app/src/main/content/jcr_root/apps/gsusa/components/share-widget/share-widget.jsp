<%@page import="com.day.cq.wcm.api.WCMMode,
                javax.jcr.Session,
                java.lang.StringBuilder,
                java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
// Get the URL
String url = properties.get("url", currentPage.getPath());
url = resourceResolver.map(currentPage.getPath());
if (!url.contains(".html")) {
    url += ".html";
}

// Get the title
String title = properties.get("title","");
ValueMap pageProps = currentPage.getProperties();
if (title.isEmpty()) {
    title = pageProps.get("articleTitle", "");
}
if (title.isEmpty()) {
    title = pageProps.get("jcr:title", "");
}

// Get the Image URL
String imageUrl = "";
if (currentNode.hasNode("image")) {
    imageUrl = resourceResolver.map(currentNode.getPath() + "/image.img.png");
}
if (imageUrl.isEmpty()) {
    String pageImagePath = currentPage.getPath() + "/jcr:content/image";
    Session session = (Session)resourceResolver.adaptTo(Session.class);
    if (session.nodeExists(pageImagePath)) {
    	imageUrl = resourceResolver.map(currentPage.getPath() + "/jcr:content.img.png");
    }
}

// Options to show
boolean hideFacebook = properties.get("hideFacebook", false);
boolean hideTwitter = properties.get("hideTwitter", false);
boolean hidePinterest = properties.get("hidePinterest", false);

// Texts
String description = pageProps.get("description", "");

String facebookText = properties.get("facebookText", description);
String twitterText = properties.get("twitterText", description);
String pinterestText = properties.get("pinterestText", description);


// IDs
String facebookId = currentSite.get("facebookId", "");

long uniqueId = System.currentTimeMillis();

if(hideFacebook && hideTwitter && hidePinterest && WCMMode.fromRequest(request) == WCMMode.EDIT){
    %> **Please check at least one social network to share <%
} else {
    %> <div id="addthisToolbox_<%= uniqueId %>" class="addthis_toolbox addthis_default_style addthis_32x32_style" style="margin:2px"> <%
    if (!hideFacebook) {
    %>
    <a class="facebook-icon" onclick="postToFeed(); return false;" />

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

      function postToFeed() {

        // calling the API ...
        var obj = {
          method: 'feed',
          link: '<%= url %>',
          picture: '<%= imageUrl %>',
          name: '<%= title.replaceAll("\\'","\\\\'") %>',
          description: '<%= facebookText.replaceAll("\\'","\\\\'") %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>
    <%
    }

    StringBuilder sb = new StringBuilder();
    if(!hideTwitter){
        sb.append("<a id=\"addthis_button_twitter" + uniqueId + "\" class=\"addthis_button_twitter\" addthis:url=\"" + url + "\"");
        if(!title.equals("")){
            sb.append(" addthis:title=\"" + title + "\"");
        }
        if(!twitterText.equals("")){
            sb.append(" addthis:description=\"" + twitterText + "\"");
        }
        sb.append("></a>");
    }

    if(!hidePinterest){
        sb.append("<a id=\"addthis_button_pinterest_share" + uniqueId + "\" class=\"addthis_button_pinterest_share\" addthis:url=\"" + url + "\"");
        if(!title.equals("")){
            sb.append(" addthis:title=\"" + title + "\"");
        }
        if(!pinterestText.equals("")){
            sb.append(" addthis:description=\"" + pinterestText + "\"");
        }
        sb.append("></a>");
    }

    %> <%= sb.toString() %> <%
    %> </div> <%
}
%>
