<%@page import="java.util.Map,
                org.apache.sling.api.resource.ValueMap" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
    Map<String, String> propsFromAttr = (Map<String, String>)request.getAttribute("gsusa-contact-banner-conf");
	String btn = getDefault("btn", "Contact Your Local Council", propsFromAttr, properties);
	String title = getDefault("title", "", propsFromAttr, properties);
	String desc = getDefault("desc", "", propsFromAttr, properties);
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
        <div class="clearfix"><label>Email Address</label><input type="email" name="email" required/></div>
        <div class="clearfix"><label>First Name</label><input type="text" name="firstName"/></div>
        <div class="clearfix"><label>Phone</label><input type="text" maxlength="10" name="phone" pattern="\d{10}" title="Please input 10-digit phone number without dashes."/></div>
        <div class="clearfix"><label>Opt-In for GSUSA Communications</label><input type="checkbox" name="optIn" /></div>
        <button type="submit" class="button right" value="Send">Send</button>
    </form>
</div>

<script>
	$(document).ready(function(){
		$('.booth-finder form#contactlocalcouncil').submit(function(){
			$.post($(this).attr('action'), $(this).serialize(), function(response) {
				// Remove blank lines
				response = response.replace(/^\s*\n/gm, '').trim();
				if (response.toUpperCase() == 'OK') {
					$('#contactlocalcouncil').html('Thank you. A representative will contact you shortly.');
				} else {
					$('#contactlocalcouncil div.error').html(response);
				}
			});

			// Prevent default
			return false;
		});
	});
</script>

<%!
public String getDefault(String key, String defaultValue, Map<String, String> props, ValueMap properties) {
	if(props != null){
		String value = props.get(key);
		if (value != null) {
			return value;
		}	
	}
	return properties.get(key, defaultValue);
}
%>