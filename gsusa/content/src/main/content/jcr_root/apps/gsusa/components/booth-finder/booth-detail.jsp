 <%@page import="java.net.URLEncoder,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat"%>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
String uniqueID = "" + System.currentTimeMillis();
String facebookId = currentSite.get("facebookId", "");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");
String address2 = request.getParameter("address2");
address2 = address2 ==null ? "" : address2;
String address = request.getParameter("address1") +" " + address2 ; 
String zip = (String)request.getParameter("zip");
zip = (zip ==null ? "" : zip);
String councilName= (String)request.getParameter("councilName");
councilName= councilName== null ? "" : councilName;

String dateStart = request.getParameter("dateStart");
try {
    DateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
    DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
    dateStart = outputFormat.format(inputFormat.parse(dateStart));
} catch (Exception e) {
	log.error("Error parsing start date.");
}

// Get the URL
String url = properties.get("url", currentPage.getPath());
url = resourceResolver.map(currentPage.getPath());
if (!url.contains(".html")) {
    url += ".html";
}
%>
<html>
<head>
  <script type="text/javascript" src="https://www.google.com/jsapi"></script>
  <script>
    var map;
    var geocoder;
    function initMap() {
      map = new google.maps.Map(document.getElementById('map'), {
        zoom: 8,
        center: {lat: -34.397, lng: 150.644}
      });

      geocoder = new google.maps.Geocoder;
      setTimeout(function(){ doIt(); }, 1000);
    }
    function codeAddress( resultsMap, geocoder) {
        var address = "<%=address%>";
        geocoder.geocode({'address': address}, function(results, status) {
          if (status === google.maps.GeocoderStatus.OK) {
            resultsMap.setCenter(results[0].geometry.location);
            var marker = new google.maps.Marker({
              map: resultsMap,
              zoom: 8,
              position: results[0].geometry.location
            });
          } else {
          console.log('Geocode was not successful for the following reason: ' + status);
          }
        });
    }
    function doIt(){
        codeAddress(map, geocoder);
        google.maps.event.trigger(map, 'resize');
        google.maps.event.trigger(map, 'center');
    }
    function LoadGoogle(){
        if(typeof google != 'undefined' && google && google.load){
            google.load("maps", "3", {callback: initMap});
        }else{
            setTimeout(LoadGoogle, 30);
        }
    }
    LoadGoogle();
 </script>
</head>
    <body>
        <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
        <h4>Support Girl Scouts in Your Area</h4>
        <section>
            <div>
                <h5>Location:</h5>
                <p><%= request.getParameter("location") %></p>
                <p><%= request.getParameter("address1") %></p>
                <p><%= request.getParameter("address2") %></p>
                <p><%= request.getParameter("city") %>, <%= request.getParameter("state") %> <%= request.getParameter("zipCode") %></p>
            </div>
            <div>
                <a href="http://maps.google.com/maps/dir/<%= zip%>/<%= URLEncoder.encode(address) %>" title="" target="_blank">Get Directions ></a>
            </div>
            <div>
                <h5>Date and Time:</h5>
                <p><%= dateStart %></p>
                <p><%= request.getParameter("timeOpen") %> - <%= request.getParameter("timeClose") %></p>
            </div>
            <div>
                <h5>Council:</h5>
                <p><%=councilName %></p>
            </div>
        </section>
        <section>
            <div id="map"></div>
            <ul class="inline-list">
            	<li><div>
            		<a class="icon-social-facebook" onclick="postToFeed<%= uniqueID %>(); return false;"></a>
					<a class="icon-social-twitter-tweet-bird" target="_blank" href="https://twitter.com/share?text=Oh%20yeah!%20It%E2%80%99s%20%23GirlScoutCookie%20time%20in%20my%20neighborhood.%20Find%20your%20cookies.%20%40girlscouts"></a>
				</div></li>
				<li><div id="toolbox" class="addthis_toolbox addthis_default_style addthis_32x32_style" addthis:title="Cookies are here." addthis:description="I found mine. Now find yours. Girl Scout Cookies are in your neighborhood!">
					<a class="addthis_button_email"><span class="icon-mail"></span></a>
					<a class="addthis_button_print"><span class="icon-printer"></span></a>
				</div>
            </li></ul>
        </section>
        <script>addthis.toolbox("#toolbox");</script>
    </body>
</html>

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
          name: 'Support Girl Scouts in your neighborhood by visiting this Cookie Booth on <%= request.getParameter("dateStart") %>',
          picture: location.host + '/content/dam/girlscouts-gsusa/images/Cookies/share-default.png',
          caption: 'WWW.GIRLSCOUTS.ORG',
          description: 'WHEN: <%= request.getParameter("dateStart")%>, from <%= request.getParameter("timeOpen") %> to <%= request.getParameter("timeClose") %>. WHERE: <%= request.getParameter("location") %>, <%= request.getParameter("address1") %>, <%= zip %>, by <%= councilName %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>