<%@include file="/libs/foundation/global.jsp" %>

<%
	String btn = properties.get("btn", "Contact Your Local Council");
	String title = properties.get("title", "");
	String desc = properties.get("desc", "");
	String zip = (String)request.getAttribute("gsusa_booth_list_zip");
%>

<a href="#" data-reveal-id="contactCouncil" class="button"><%= btn %></a>
<div id="contactCouncil" class="reveal-modal local-lookup" data-reveal aria-hidden="true" role="dialog">
    <a class="close-reveal-modal icon-button-circle-cross" aria-label="Close"></a>
    <h4><%= title %></h4>
    <p><%= desc %></p>
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