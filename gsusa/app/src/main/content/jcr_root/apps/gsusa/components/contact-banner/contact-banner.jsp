<%@page import="java.util.Map,
                org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp" %>
<a href="#" data-reveal-id="contactCouncil" class="button">{{contactBanner.btn}}</a>
<div id="contactCouncil" class="reveal-modal local-lookup" data-reveal aria-hidden="true" role="dialog">
    <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
    <h4>{{contactBanner.title}}</h4>
    <p>{{contactBanner.desc}}</p>
    <form id="contactlocalcouncil" class="contactlocalcouncil" action="<%= resource.getPath() %>.contact.html">
        <div class="error"></div>
        <input type="hidden" name="zipCode" value="{{env.zip}}"></input>
        <div class="clearfix"><label>Email Address</label><input type="email" name="email" required/></div>
        <div class="clearfix"><label>First Name</label><input type="text" name="firstName"/></div>
        <div class="clearfix"><label>Phone</label><input type="text" maxlength="10" name="phone" pattern="\d{10}" title="Please input 10-digit phone number without dashes."/></div>
        <div class="clearfix"><label>Opt-In for GSUSA Communications</label><input type="checkbox" name="optIn" /></div>
        <button type="submit" class="button right" value="Send">Send</button>
    </form>
</div>