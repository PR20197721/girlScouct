<%@page import="java.net.URLEncoder,
				org.jsoup.Jsoup" %>
<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%
    String url = properties.get("url", currentPage.getPath());
		url = resourceResolver.map(currentPage.getPath());
	if (!url.contains(".html")) {
    	url += ".html";
	}
	ValueMap pageProps = currentPage.getProperties();
	String title = pageProps.get("jcr:title", "");
	String facebookText = pageProps.get("articleText", "");
	String tweetText = URLEncoder.encode(title,"UTF-8").replace("+", "%20");
	String tweetUrl = url;

	String uniqueID = "" + System.currentTimeMillis();
	String facebookId = currentSite.get("facebookId", "");
	String imageUrl = "";
	String pageImagePath = currentPage.getPath() + "/jcr:content/image";
    Session session = (Session)resourceResolver.adaptTo(Session.class);
    if (session.nodeExists(pageImagePath)) {
    	imageUrl = resourceResolver.map(currentPage.getPath() + "/jcr:content.img.png");
    }

%>

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

      function postToFeed<%= uniqueID %>() {

        // calling the API ...
        var obj = {
          method: 'feed',
          link: '<%= url %>',
          picture: '<%= imageUrl %>',
          name: '<%= title %>',
          description: '<%= Jsoup.parse(facebookText).text().replace("\n","\\n") %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>
<ul class="inline-list">
    <li id="toolbox_1" class="addthis_toolbox">
      <a class="icon-social-facebook" onclick="postToFeed<%= uniqueID %>(); return false;"></a>
    </li>
    <li id="toolbox_2" class="addthis_toolbox">
      <a class="icon-social-twitter-tweet-bird" target="_blank" href="https://twitter.com/share?text=<%=tweetText%>&url=<%=tweetUrl%>"></a>
    </li>
    <li id="toolbox_3" class="addthis_toolbox">
          <a class="addthis_button_email icon-mail"><!-- <span></span> --></a>
    </li>
    <li id="toolbox_3" class="addthis_toolbox">
        <a class="addthis_button_print icon-printer"><!-- <span></span> --></a>
    </li>

</ul>