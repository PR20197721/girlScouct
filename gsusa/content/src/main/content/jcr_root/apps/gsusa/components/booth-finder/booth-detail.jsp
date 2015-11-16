 <%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");
String address = request.getParameter("address1");
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
                <a href="" title="">Get Directions ></a>
            </div>
            <div>
                <h5>Date and Time</h5>
                <p><%= request.getParameter("dateStart")%></p>
                <p><%= request.getParameter("timeOpen") %> - <%= request.getParameter("timeClose") %></p>
            </div>
            <div>
                <h5>Council</h5>
                <p>Cuuncil NameXXXX</p>
            </div>
        </section>
        <section>
            <div id="map"></div>
            <ul class="inline-list">
                <li><a class="icon-social-facebook" href=""></a></li>
                <li><a class="icon-social-twitter-tweet-bird" href=""></a></li>
                <li><a class="icon-mail" href=""></a></li>
                <li><a class="icon-printer" href=""></a></li>
            </ul>
        </section>
    </body>
</html>