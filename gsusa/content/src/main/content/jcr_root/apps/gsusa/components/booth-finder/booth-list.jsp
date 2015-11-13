<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.util.List" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
Council council = (Council)request.getAttribute("gsusa_council_info");
List<BoothBasic> booths = (List<BoothBasic>)request.getAttribute("gsusa_cookie_booths");
String zip = (String)request.getAttribute("gsusa_booth_list_zip");
String radius = (String)request.getAttribute("gsusa_booth_list_radius");
String date = (String)request.getAttribute("gsusa_booth_list_date");
String sortBy = (String)request.getAttribute("gsusa_booth_list_sortby");
int numPerPage = (Integer)request.getAttribute("gsusa_booth_list_numperpage");
int showContactBannerPer = properties.get("showContactBannerPer", 25);

String nearestDistance = "";
if ("distance".equals(sortBy) && !booths.isEmpty()) {
    nearestDistance = booths.get(0).distance;
} else {
    double nearestDistanceValue = Double.MAX_VALUE;
    for (BoothBasic booth : booths) {
        double distance = Double.MAX_VALUE;
        try {
            nearestDistanceValue = Double.parseDouble(booth.distance);
            if (distance < nearestDistanceValue) {
                nearestDistanceValue = distance;
            }
        } catch (Exception e) {}
    }
    nearestDistance = Double.toString(nearestDistanceValue);
}
%>

<h4>Booth Locations near you:</h4>
<p>The nearest location is <%= nearestDistance %> miles away from <%= zip %>.</p>
<form class="sort-form clearfix">
    <section>
        <label>Radius:</label>
        <input type="hidden" name="zip" value="<%=zip%>"></input>
        <select name="radius" onchange="this.form.submit()">
            <option value="1" <%= "1".equals(radius) ? "selected" : "" %>>1 miles</option>
            <option value="5" <%= "5".equals(radius) ? "selected" : "" %>>5 miles</option>
            <option value="10" <%= "10".equals(radius) ? "selected" : "" %>>10 miles</option>
            <option value="15" <%= "15".equals(radius) ? "selected" : "" %>>15 miles</option>
            <option value="25" <%= "25".equals(radius) ? "selected" : "" %>>25 miles</option>
            <option value="50" <%= "50".equals(radius) ? "selected" : "" %>>50 miles</option>
            <option value="100" <%= "100".equals(radius) ? "selected" : "" %>>100 miles</option>
            <option value="250" <%= "250".equals(radius) ? "selected" : "" %>>250 miles</option>
            <option value="500" <%= "500".equals(radius) ? "selected" : "" %>>500 miles</option>
        </select>
    </section>
    <section>
        <label>Date:</label>
        <select name="date" onchange="this.form.submit()">
            <option value="7" <%= "7".equals(date) ? "selected" : "" %>>1 week</option>
            <option value="14" <%= "14".equals(date) ? "selected" : "" %>>2 weeks</option>
            <option value="30" <%= "30".equals(date) ? "selected" : "" %>>1 month</option>
            <option value="60" <%= "60".equals(date) ? "selected" : "" %>>2 months</option>
            <option value="90" <%= "90".equals(date) ? "selected" : "" %>>3 months</option>
            <option value="all" <%= "all".equals(date) ? "selected" : "" %>>all</option>
        </select>
    </section>
    <section>
        <label>Sort by:</label>
        <select name="sortBy" onchange="this.form.submit()">
            <option value="distance" <%= "distance".equals(date) ? "selected" : "" %>>distance</option>
            <option value="date" <%= "date".equals(date) ? "selected" : "" %>>date</option>
        </select>
    </section>
</form>
<div id="modal_booth_item_map"  class="reveal-modal"  data-reveal data-options="close_on_esc:true"></div>

<div class="row headers">
  <section>
        <h4>Location</h4>
  </section>
  <section>
        <h4>Date</h4>
  </section>
  <section>
        <h4>Distance</h4>
  </section>
</div>

<cq:include script="booths.jsp" />

<div class="row show-more">
    <%
    String baseLink = "?zip=" + zip + "&radius=" + radius +
                      "&date" + date + "&sortBy=" + sortBy + "&numPerPage=" + numPerPage;

    // If there are more booths, display the "more" link.
    if (booths.size() > numPerPage) {
    %>
        <a id="more" title="show more results">MORE</a>
        <script>
            var pageNum = 1;
            $('.booth-finder a#more').on('click', function() {
                var url = '<%= resource.getPath() %>.headless.html';
                url = url + '?zip=<%=zip%>&radius=<%=radius%>&date=<%=date%>&sortBy=<%=sortBy%>&numPerPage=<%=numPerPage%>&page=' + pageNum;
                pageNum++;
            	$.ajax({
            		url: url,
            		success: function(html) {
            			$('.booth-finder .show-more').before(html);
            		}
            	})
            });
        </script>
    <%
    }
    %>
</div>
<div class="not-finding">
    <h4>Not finding what you're looking for?</h4>
    <cq:include script="contact-banner.jsp"/>
</div>

<div id="contactCouncil" class="reveal-modal local-lookup" data-reveal aria-hidden="true" role="dialog">
    <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
    <h4>Support Girl Scouts in Your Area</h4>
    <p>Enter you info below and girls from the local council name will contact you to help you place an order.</p>
    <form id="contactlocalcouncil" action="<%= resource.getPath() %>.contact.html">
        <div class="error"></div>
        <input type="hidden" name="zipCode" value="<%= zip %>"></input>
        <div class="clearfix"><label>Enter address</label><input type="text" name="email"/></div>
        <div class="clearfix"><label>First Name</label><input type="text" name="firstName"/></div>
        <div class="clearfix"><label>Phone</label><input type="text" maxlength="10" name="phone" pattern="\d{10}" title="Please input 10-digit phone number without dashes."/></div>
        <div class="clearfix"><label>Opt-in</label><input type="checkbox" name="optIn" /></div>
        <button type="submit" class="button right" value="Submit">Submit</button>
    </form>
</div>
<script>
	$(document).ready(function(){
		$('.booth-finder form#contactlocalcouncil').submit(function(){
			$.post($(this).attr('action'), $(this).serialize(), function(response) {
				// Remove blank lines
				response = response.replace(/^\s*\n/gm, '').trim();
				if (response.toUpperCase() == 'OK') {
					$('#contactlocalcouncil').html('Thank you for contacting us');
				} else {
					$('#contactlocalcouncil div.error').html(response + '. Please correct the form.');
				}
			});

			// Prevent default
			return false;
		});
	});
</script>