<!DOCTYPE html>
<html>
  <head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>Map</title>
    <style>
      html, body, #map-canvas {
        height: 100%;
        margin: 0px;
        padding: 0px
      }
      #panel {
        position: absolute;
        top: 5px;
        left: 50%;
        margin-left: -180px;
        z-index: 5;
        background-color: #fff;
        padding: 5px;
        border: 1px solid #999;
      }
    </style>
   
   
    <!--
to activate key go to:
https://developers.google.com/maps/documentation/business/clientside/auth#registering_authorized_urls
//Enterprise Support Portal
Google Enterprise Support Portal
//key=
<script src="https://maps.googleapis.com/maps/api/js?v=3.exp&key=AIzaSyCAYeuoI0xGM4V5p89BLKZALJoaSbsA17c&sensor=false"></script>
-->
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&sensor=false"></script>
   
   
   
    <script>
var geocoder;
var map;
function initialize() {
  geocoder = new google.maps.Geocoder();
  var latlng = new google.maps.LatLng(-34.397, 150.644);
  var mapOptions = {
    zoom: 8,
    center: latlng
  }
  map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
}

function codeAddress() {
  var address = "<%=request.getParameter("address")%>";
  geocoder.geocode( { 'address': address}, function(results, status) {
    if (status == google.maps.GeocoderStatus.OK) {
      map.setCenter(results[0].geometry.location);
      var marker = new google.maps.Marker({
          map: map,
          position: results[0].geometry.location
      });
    } else {
      alert('Geocode was not successful for the following reason: ' + status);
    }
  });
}

google.maps.event.addDomListener(window, 'load', initialize);

    </script>
  </head>
  <body onload="codeAddress()">
    <div id="panel">
    <%=request.getParameter("address")%>
     </div>
    <div id="map-canvas"></div>
  </body>
</html>

