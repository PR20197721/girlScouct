<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<h4>Booth Locations near you:</h4>
<p>The nearest location is {{env.nearestDistance}} miles away from {{env.zip}}.</p>
<b>Select distance, date, and sorting method to filter your results:</b>
<form action="<%=slingRequest.getRequestURI() %>" class="sort-form clearfix" style="margin-top: 0.7rem;">
    <section>
        <label>Distance:</label>
        <select name="radius" onchange="this.form.submit()">
            <option value="1">1 miles</option>
            <option value="5">5 miles</option>
            <option value="10">10 miles</option>
            <option value="15">15 miles</option>
            <option value="25">25 miles</option>
            <option value="50">50 miles</option>
            <option value="100">100 miles</option>
            <option value="250">250 miles</option>
            <option value="500" selected>500 miles</option>
			<option value="1000" selected>1000 miles</option>
            <option value="2000" selected>2000 miles</option>
            <option value="3000" selected>3000 miles</option>
            <option value="4000" selected>4000 miles</option>
            <option value="5000" selected>5000 miles</option>
        </select>
    </section>
    <section>
        <label>Date:</label>
        <select name="date" onchange="this.form.submit()">
            <option value="7">1 week</option>
            <option value="14">2 weeks</option>
            <option value="30">1 month</option>
            <!-- default -->
            <option value="60" selected>2 months</option>
            <option value="90">3 months</option>
        </select>
    </section>
    <section>
        <label>Sort by:</label>
        <select name="sortBy" onchange="this.form.submit()">
            <!-- default -->
            <option value="distance" selected>distance</option>
            <option value="date">date</option>
        </select>
    </section>
    <input type="hidden" name="zip" value="{{env.zip}}"></input>
</form>
<div id="modal_booth_item_map"  class="reveal-modal"  data-reveal data-options="close_on_esc:true"></div>