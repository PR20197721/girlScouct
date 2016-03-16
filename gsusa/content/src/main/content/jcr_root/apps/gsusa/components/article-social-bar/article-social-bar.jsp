<%@page import="java.net.URLEncoder,
        org.apache.commons.lang.StringEscapeUtils,
        com.day.cq.commons.Externalizer,
        org.apache.sling.api.SlingHttpServletRequest"%>
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

	String title = pageProps.get("jcr:title","");
	if(!"".equals(pageProps.get("ogTitle",""))){
		title = pageProps.get("ogTitle","");
	} else if(!"".equals(pageProps.get("seoTitle",""))){
		title = pageProps.get("seoTitle","");
	}

	String facebookText = pageProps.get("jcr:description", "");
	if(!"".equals(pageProps.get("ogDescription",""))){
		facebookText = pageProps.get("ogDescription","");
	}

	facebookText = StringEscapeUtils.escapeHtml(facebookText);

	String tweetText = pageProps.get("jcr:title","");
	if(!"".equals(pageProps.get("ogTitle",""))){
		tweetText = pageProps.get("ogTitle","");
	} else if(!"".equals(pageProps.get("seoTitle",""))){
		tweetText = pageProps.get("seoTitle","");
	}
	tweetText = URLEncoder.encode(tweetText,"UTF-8").replace("+", "%20");
	String tweetUrl = url;

	String uniqueID = "" + System.currentTimeMillis();
	String facebookId = currentSite.get("facebookId", "");
	if(!"".equals(pageProps.get("fbAppId",""))){
		facebookId = pageProps.get("fbAppId","");
	}
	String imageUrl = pageProps.get("ogImage","");
	if("".equals(imageUrl)){
		String pageImagePath = currentPage.getPath() + "/jcr:content/image";
	    Session session = (Session)resourceResolver.adaptTo(Session.class);
	    if (session.nodeExists(pageImagePath)) {
	    	imageUrl = resourceResolver.map(currentPage.getPath() + "/jcr:content.img.png");
	    }
	} else{
		Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		imageUrl = externalizer.absoluteLink((SlingHttpServletRequest)request, "http", imageUrl);
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
          name: '<%= title.replaceAll("\\'","\\\\'") %>',
          description: '<%= facebookText.replaceAll("\\'","\\\\'") %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>

<script type="text/javascript">

	function get_bitly_short_url(long_url, login, api_key, func)
	{
	    $.getJSON(
        	"http://api.bitly.com/v3/shorten?callback=?",
	        ({
      	    "apiKey": api_key,
          	"login": login,
            "longUrl": long_url,
            "format": "json"
    	    }),
        	function(response)
	        {
    	        func(response.data.url);
        	}
	    );
	}

	var bitly_login = 'gsusa';
	var bitly_api_key = 'R_f738e9d3ad5d828e8c2224e4f1bf531b';
	var bitly_long_url = '<%=tweetUrl%>';

	$(document).ready(function() {
		get_bitly_short_url(bitly_long_url, bitly_login, bitly_api_key, function(short_url) {
			var bitly_twitter_url = "https://twitter.com/share?text=<%=tweetText%>&url=" + short_url;
			$("a.icon-social-twitter-tweet-bird").attr("href",bitly_twitter_url);
		});
	});
</script>

<ul class="inline-list">
    <li id="toolbox_1" class="addthis_toolbox">
      <a class="icon-social-facebook" onclick="postToFeed<%= uniqueID %>(); return false;"></a>
    </li>
    <li id="toolbox_2" class="addthis_toolbox">
      <a class="icon-social-twitter-tweet-bird" target="_blank"></a>
    </li>
    <li id="toolbox_3" class="addthis_toolbox">
          <a class="addthis_button_pinterest icon-social-pinterest"><!-- <span></span> --></a>
    </li>
    <li id="toolbox_4" class="addthis_toolbox">
          <a class="addthis_button_email icon-mail"><!-- <span></span> --></a>
    </li>
    <li id="toolbox_5" class="addthis_toolbox">
        <a class="addthis_button_print icon-printer"><!-- <span></span> --></a>
    </li>

</ul>

<style>
	span.at_PinItButton {display:none;} // hide default pin-it button
</style>