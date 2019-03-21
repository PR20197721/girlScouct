<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
    String site_key = currentSite.get("recaptcha_site_key", "");
%>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<div id=":g-recaptcha" name=":g-recaptcha" class="g-recaptcha" data-sitekey="<%=site_key%>%>"></div>