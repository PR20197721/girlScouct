<%@page import="com.day.cq.wcm.api.WCMMode,
                java.lang.StringBuilder,
                java.net.URLEncoder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
// Get the URL
String canonicalUrl = resourceResolver.map(currentPage.getPath());
String url = properties.get("url", canonicalUrl);

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
if (currenNode.hasNode("image")) {
    imageUrl = resourceResolver.map(currentPage.getPath() + "/jcr:content.img.png");
}

// Options to show
boolean showFacebook = properties.get("showFacebook",true);
boolean showTwitter = properties.get("showTweet",true);
boolean showPinterest = properties.get("showPinterest",true);

// Texts
String facebookText = properties.get("facebookText","");
String twitterText = properties.get("twitterText","");
String pinterestText = properties.get("pinterestText","");

// IDs
String facebookId = currentSide.getValue("facebookId", ""); 

long uniqueId = System.currentTimeMillis();


if(!showFacebook && !showTwitter && !showPinterest && WCMMode.fromRequest(request) == WCMMode.EDIT){
    %> **Please check at least one social network to share <%
} else {
    %> <div id="addthisToolbox_<%= uniqueId %>" class="addthis_toolbox addthis_default_style addthis_32x32_style" style="margin:2px"> <%
    if (showFacebook) {
    %>
    <a class="facebook-icon" onclick="postToFeed(); return false;" />
 
    <script src='http://connect.facebook.net/en_US/all.js'></script>
    <script> 
      FB.init({appId: "<%= facebookId %>", status: true, cookie: true});
 
      function postToFeed() {
 
        // calling the API ...
        var obj = {
          method: 'feed',
          redirect_uri: '<%= url %>',
          link: '<%= url %>',
          picture: '<%= imageUrl %>',
          name: '<%= title %>',
          description: '<%= facebookText %>'
        };
 
        function callback(response) {
        }
 
        FB.ui(obj, callback);
      }
 
    </script>
    <% 
    }

    StringBuilder sb = new StringBuilder();
    if(showTwitter){
        sb.append("<a id=\"addthis_button_twitter" + uniqueId + "\" class=\"addthis_button_twitter\" addthis:url=\"" + url + "\"");
        sb.append(" addthis:title=\"" + title + "\"");
        if(!title.equals("")){
            sb.append(" addthis:title=\"" + title + "\"");
        }
        if(!twitterText.equals("")){
            sb.append(" addthis:description=\"" + twitterText + "\"");
        }
        sb.append("></a>");
    }
    
    if(showPinterest){
        sb.append("<a id=\"addthis_button_pinterest_share" + uniqueId + "\" class=\"addthis_button_pinterest_share\" addthis:url=\"" + url + "\"");
        sb.append(" addthis:title=\"" + title + "\"");
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