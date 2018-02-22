<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<!--  
<h3>Cookies Are Here!</h3>
<p>The cookie season for the <strong><a href="{{council.CookiePageURL}}" target="_blank">{{council.CouncilName}}</a></strong> is currently underway!</p>
-->
<h4>Booth Locations near you:</h4>
<p>The nearest location is {{env.nearestDistance}} miles away from {{env.zip}}.</p>
<form action="<%=slingRequest.getRequestURI() %>" class="sort-form clearfix">
    <section>
        <label>Radius:</label>
        <select name="radius" onchange="this.form.submit()">
            <option value="1">1 miles</option>
            <option value="5">5 miles</option>
            <option value="10">10 miles</option>
            <option value="15">15 miles</option>
            <option value="25">25 miles</option>
            <!-- default -->
            <option value="50" selected>50 miles</option>
            <option value="100">100 miles</option>
            <option value="250">250 miles</option>
            <option value="500">500 miles</option>
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
            <option value="all">all</option>
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
{{#if council.shoudShowContactUsFormAfterListing}}
  <div class="not-finding">
    <h4>Not finding what you're looking for?</h4>
    <cq:include path="contact-banner" resourceType="gsusa/components/contact-banner"/>
  </div>
{{/if}}