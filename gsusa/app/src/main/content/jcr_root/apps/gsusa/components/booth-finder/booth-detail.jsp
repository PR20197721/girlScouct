 <%@page import="java.net.URLEncoder,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 java.net.URLEncoder"%>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>

<%
String facebookTitle = request.getParameter("fbTitle");
if(facebookTitle != null) {facebookTitle = facebookTitle.replace("\'","\\\'");}
String facebookDesc = request.getParameter("fbDesc");
if(facebookDesc != null) {facebookDesc = facebookDesc.replace("\'","\\\'");}
String tweet = request.getParameter("tweet");
if(tweet != null) {tweet = URLEncoder.encode(tweet,"UTF-8");}
String imgPath = request.getParameter("shareImgPath");

String uniqueID = "" + System.currentTimeMillis();
String facebookId = currentSite.get("facebookId", "");
response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
response.setHeader("Pragma", "no-cache");
response.setHeader("Expires", "0");
String address1 = request.getParameter("Address1");
address1 = address1 ==null ? "" : address1;
String address2 = request.getParameter("Address2");
address2 = address2 ==null ? "" : address2;
String address = request.getParameter("Address1") +" " + address2 + " " + 
	request.getParameter("City") + ", " + request.getParameter("State") + " " + request.getParameter("ZipCode");
String zip = (String)request.getParameter("queryZip");
zip = (zip ==null ? "" : zip);
String councilName= (String)request.getParameter("CouncilName");
councilName= councilName== null ? "" : councilName;

String addressZip = (String)request.getParameter("ZipCode");
addressZip = (addressZip ==null ? "" : addressZip);
String latitudeData= (String)request.getParameter("Latitude");
latitudeData= latitudeData== null ? "0" : latitudeData;
String longitudeData= (String)request.getParameter("Longitude");
longitudeData= longitudeData== null ? "0" : longitudeData;


String dateStart = request.getParameter("DateStart");
try {
    DateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
    DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
    dateStart = outputFormat.format(inputFormat.parse(dateStart));
} catch (Exception e) {
	log.error("Error parsing start date.");
}

//String googleMapsAPI = properties.get("mapAPI", "AIzaSyDWhROdret3d0AGaTTZrYeFH8hP5SIbmzw");	// 1M Google MAPS API Grant
String googleMapsAPI = properties.get("mapAPI", "AIzaSyCQ1pG4dKsTrA8mqAo-0qwAI0I8AaoWdiE");		// Free Version

%>
<html>
<head>
  <script async defer src="https://maps.googleapis.com/maps/api/js?key=<%= googleMapsAPI %>&callback=initMap"></script>
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
      var latitude = "<%=latitudeData%>";
      var longitude = "<%=longitudeData%>";

	  if(latitude.length > 0 && longitude.length > 0) {
		  	var myLatLng = {
		  		"lat"	: parseFloat(parseFloat(latitude).toFixed(6)), 
		  		"lng"	: parseFloat(parseFloat(longitude).toFixed(6)) 
		  	};
			//console.log(myLatLng);		  	
			var marker = new google.maps.Marker({
				map: resultsMap,
				zoom: 8,
				position: myLatLng
			});
			resultsMap.setCenter(myLatLng); 
			
	  } else {
	  
	    	geocoder.geocode({'address': address}, function(results, status) {
    		    if (status === google.maps.GeocoderStatus.OK) {
        		  var marker = new google.maps.Marker({
    		        map: resultsMap,
		            zoom: 8,
        		    position: results[0].geometry.location
    		      });
		          resultsMap.setCenter(results[0].geometry.location);

    		      // save the lat/long
				  var formData = {
				    "a1" : "<%=address1%>",
					"z"	 : "<%=addressZip%>",
					"lat": results[0].geometry.location.lat().toFixed(6),
					"long": results[0].geometry.location.lng().toFixed(6)
				  };
				  //console.log(formData);
				  $.ajax({
  					method: "POST",
					url: "/cookiesapi/booth_list_geo_insert.asp",
					data: formData
				  });
    		      
		        } else {
	    	    	console.log('Geocode was not successful for the following reason: ' + status);
	    	    }
	    	});
	  }
	  
      map.addListener('click', function() {
        window.open("http://maps.google.com/maps/dir/<%= zip%>/<%= URLEncoder.encode(address) %>");
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
        <h4>Check Out This Girl Scout Cookie Booth!</h4>
        <section>
            <div>
                <h5>Location:</h5>
                <p><%= request.getParameter("Location") %></p>
                <p><%= request.getParameter("Address1") %></p>
                <p><%= request.getParameter("Address2") %></p>
                <p><%= request.getParameter("City") %>, <%= request.getParameter("State") %> <%= request.getParameter("ZipCode") %></p>
            </div>
            <div>
                <a href="http://maps.google.com/maps/dir/<%= zip%>/<%= URLEncoder.encode(address) %>" title="" target="_blank">Get Directions ></a>
            </div>
            <div>
                <h5>Date and Time:</h5>
                <p><%= dateStart %></p>
<script>
	$.getJSON("/cookiesapi/booth_list_detail.asp?d=<%= request.getParameter("DateStart") %>&l=<%= URLEncoder.encode(request.getParameter("Location")) %>&a1=<%= URLEncoder.encode(request.getParameter("Address1")) %>&a2=<%= URLEncoder.encode(request.getParameter("Address2")) %>&z=<%= URLEncoder.encode(request.getParameter("ZipCode")) %>", function(data, textStatus, jqXHR){
		var timeslotOutput = '';
		for(var i = 0; i < data.TimeSlots.length; i++){
			//alert(data.TimeSlots[i].TroopName+' '+data.TimeSlots[i].TimeOpen);
			timeslotOutput+=data.TimeSlots[i].TimeOpen+' - '+data.TimeSlots[i].TimeClose+'<br>';
		}
		$('#timeslots').html(timeslotOutput);
	});    
</script>                
                <p id="timeslots"></p>
            </div>
            <div>
                <h5>Council:</h5>
                <p><%= councilName %></p>
            </div>
        </section>
        <section>
            <div id="map"></div>
            <ul class="inline-list">
            	<li><div style="margin-top:5px">
            		<a class="icon-social-facebook" onclick="postToFeed<%= uniqueID %>(); return false;" style="margin-right:-6px"></a>
					<a class="icon-social-twitter-tweet-bird" target="_blank" href="https://twitter.com/share?text=<%=tweet%>"></a>
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

      function postToFeed<%= uniqueID %>() {

        // calling the API ...
        var obj = {
          method: 'feed',
          link: window.location.href,
          name: '<%= facebookTitle %>',
          picture: location.host + '<%= imgPath %>',
          caption: 'WWW.GIRLSCOUTS.ORG',
          description: '<%= facebookDesc %>'
        };

        function callback(response) {
        }

        FB.ui(obj, callback);
      }

    </script>
