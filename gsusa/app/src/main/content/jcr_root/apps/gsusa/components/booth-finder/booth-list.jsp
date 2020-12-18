<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<!--
<h3>Cookies Are Here!</h3>
<p>The cookie season for the <strong><a href="{{council.CookiePageURL}}" target="_blank">{{council.CouncilName}}</a></strong> is currently underway!</p>
-->
<cq:include script="booth-list-filter.jsp" />
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