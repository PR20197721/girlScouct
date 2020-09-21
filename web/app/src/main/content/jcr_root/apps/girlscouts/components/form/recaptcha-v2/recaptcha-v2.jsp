<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="java.io.IOException,
   com.day.cq.wcm.foundation.forms.FormsHelper,
   com.day.cq.wcm.foundation.forms.LayoutHelper"%>
<cq:includeClientLib categories="apps.girlscouts.components.form.recaptcha-v2" />
<%
    String site_key = currentSite.get("recaptcha_site_key", "");
%>
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<div id=":g-recaptcha-response" name=":g-recaptcha-response" class="g-recaptcha" data-callback ="recaptchaCallback" data-sitekey="<%=site_key%>"></div>
<input id="g-recaptcha-ts" name="g-recaptcha-ts" type="hidden"/>
<%
    LayoutHelper.printDescription(FormsHelper.getDescription(resource, ""), out);
    LayoutHelper.printErrors(slingRequest, ":g-recaptcha-response", out);
%>