 <%@page import="java.net.URLEncoder,
                 java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 java.net.URLEncoder,
				 org.apache.sling.commons.json.*"%>
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

//GSDO-1024
JSONObject boothInfo = new JSONObject();
boothInfo.put("uniqueID", uniqueID);
boothInfo.put("facebookId", facebookId);
boothInfo.put("address", address);
boothInfo.put("address1", address1);
boothInfo.put("address2", address2);
boothInfo.put("zip", zip);
boothInfo.put("latitudeData", latitudeData);
boothInfo.put("longitudeData", longitudeData);
boothInfo.put("tweet", tweet);
boothInfo.put("facebookTitle", facebookTitle);
boothInfo.put("imgPath", imgPath);
boothInfo.put("addressZip", addressZip);
boothInfo.put("facebookDesc", facebookDesc);
//GSDO-1024
%>
<html>
<head>
	<script async defer src="https://maps.googleapis.com/maps/api/js?key=<%= googleMapsAPI %>&callback=initMap"></script>
</head>
    <body>
        <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
        <h4>Check Out This Girl Scout Cookie Booth!</h4>
        <section>
            <div>
                <h5>Location:</h5>

				<%  String encodingFixedLocationString = new String (request.getParameter("Location").getBytes ("iso-8859-1"), "UTF-8"); %>
                
<!--                <p><%= request.getParameter("Location") %></p>		-->
                <p><%= encodingFixedLocationString %></p>
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
	$.getJSON("/cookiesapi/booth_list_detail.asp?d=<%= request.getParameter("DateStart") %>&l=<%= URLEncoder.encode(encodingFixedLocationString, "UTF-8") %>&a1=<%= URLEncoder.encode(request.getParameter("Address1")) %>&a2=<%= URLEncoder.encode(request.getParameter("Address2")) %>&z=<%= URLEncoder.encode(request.getParameter("ZipCode")) %>", function(data, textStatus, jqXHR){
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
        <div id='boothDetailInfo' data-booth='<%=boothInfo%>'></div>
    </body>
</html>