<%@include file="/libs/foundation/global.jsp" %>

<%
	String label = properties.get("label", "Keep in touch");
%>
<form id="tag_email-signin" class="email-signin" action="#">
    <input name="CORPHOME" type="hidden" value="Yes" />
    <label><%= label %></label>
    <input type="email" name="email" placeholder="Email address" tabindex="165" />
</form>
<div class="email-output-message">
</div>
