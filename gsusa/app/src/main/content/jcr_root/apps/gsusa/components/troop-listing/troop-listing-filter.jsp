<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<!--
<h3>Ship Cookies from a Troop</h3>
-->
<h4>Booth Locations near you:</h4>
<b>Select distance, date, and sorting method to filter your results:</b>
<form class="sort-form clearfix" style="margin-top: 0.7rem;">
    <section>
        <label>Distance:</label>
        <select name="radius" onchange="getUpdatedTroopListingFilterResult(event)">
            <option value="1">1 miles</option>
            <option value="5">5 miles</option>
            <option value="10">10 miles</option>
            <option value="15">15 miles</option>
            <option value="25">25 miles</option>
            <option value="50">50 miles</option>
            <option value="100">100 miles</option>
            <option value="250">250 miles</option>
            <option value="500" selected>500 miles</option>
        </select>
    </section>
    <section>
        <label>Date:</label>
        <select name="date" onchange="getUpdatedTroopListingFilterResult(event)">
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
        <select name="sortBy" onchange="getUpdatedTroopListingFilterResult(event)">
            <!-- default -->
            <option value="distance" selected>distance</option>
            <option value="date">date</option>
        </select>
    </section>
    <input type="hidden" name="zip" value="{{env.zip}}"></input>
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

<cq:include script="booth-list-more.jsp" />
<div class="row show-more">
    <a id="more" title="show more results">MORE</a>
</div>