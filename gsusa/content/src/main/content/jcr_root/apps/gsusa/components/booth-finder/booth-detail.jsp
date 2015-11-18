 <%@page import="java.net.URLEncoder"%>
 <%@include file="/libs/foundation/global.jsp" %>
<%
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
        <h4>Support Girlscouts in Your Area</h4>
        <section>
            <div>
                <h5><%= request.getParameter("location") %></h5>
                <p><%= request.getParameter("address1") %></p>
            </div>
            <div>
                <a href="http://maps.google.com/maps/dir/<%= zip%>/<%= URLEncoder.encode(address) %>" title="" target="_blank">Get Directions ></a>
            </div>
            <div>
                <h5>Date and Time</h5>
                <p><%= request.getParameter("dateStart")%></p>
                <p><%= request.getParameter("timeOpen") %> - <%= request.getParameter("timeClose") %></p>
            </div>
            <div>
                <h5>Council</h5>
                <p><%=councilName %></p>
            </div>
        </section>
        <section>
            <div id="map"></div>
            <ul class="inline-list">
            	<li><div id="toolbox" class="addthis_toolbox addthis_default_style addthis_32x32_style" addthis:title="Cookies are here." addthis:description="I found mine. Now find yours. Girl Scout Cookies are in your neighborhood!">
					<a class="addthis_button_facebook"><span class="icon-social-facebook"></span></a>
					<a class="addthis_button_twitter"><span class="icon-social-twitter-tweet-bird"></span></a>
					<a class="addthis_button_email"><span class="icon-mail"></span></a>
					<a class="addthis_button_print"><span class="icon-printer"></span></a>
				</div>
            </li></ul>
        </section>
        <script>addthis.toolbox("#toolbox");</script>
    </body>
</html>