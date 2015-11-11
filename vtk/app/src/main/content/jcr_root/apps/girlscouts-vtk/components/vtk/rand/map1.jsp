 <%
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); 
response.setHeader("Pragma", "no-cache"); 
response.setHeader("Expires", "0");
String address = request.getParameter("address");
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
       
 <style>
      html, body {
        margin: 0;
        padding: 0;
      }
      #map {
        height: 300px;
        width:200px;
        float:right;
      }
 </style>
</head>
<body>

<div style="height:500px;">
    <h1>Support Girlscouts</h1>
    <div id="map"></div>
</div>   

</body>
</html>