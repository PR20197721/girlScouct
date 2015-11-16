<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String zipCode = request.getParameter("zipCode");
String email= request.getParameter("email");
String firstName = request.getParameter("firstName");
String phone = request.getParameter("phone");
boolean optIn = "on".equalsIgnoreCase(request.getParameter("optIn"));

BoothFinder boothFinder = sling.getService(BoothFinder.class);
String result = "";
try {
    result = boothFinder.saveContactMeInformation(zipCode, email, firstName, optIn, phone);
} catch (Exception e) {
	result = "There is a problem communicating with the server. Please try again later.";
}
%>
<%= result %>

<a href="#" data-reveal-id="contactCouncil" class="button">Contact your local council</a>
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