<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="com.day.cq.wcm.foundation.forms.FormsHelper,
   com.day.cq.wcm.foundation.forms.LayoutHelper,
   org.girlscouts.common.osgi.component.WebToCase, java.util.Map"%>
<cq:includeClientLib categories="apps.girlscouts.components.form.recaptcha-v2" />
<%
    String site_key = currentSite.get("recaptcha_site_key", "");
    WebToCase webToCase = sling.getService(WebToCase.class);
	Map<String, String> recaptchaMap = webToCase.getRecaptchaMap();
%>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    <div id=":g-recaptcha-response" name=":g-recaptcha-response" class="g-recaptcha" data-sitekey="<%site_key%>>"></div>
    <%
        if (recaptchaMap.containsKey(site_key)) {
            String sfmcMapping = recaptchaMap.get(site_key);
            if (sfmcMapping != null) {
                %>
                <input name="captcha_settings" value='{"keyname":"<%=sfmcMapping%>","fallback":"true","orgId":"<%=webToCase.getOID()%>","ts":""}' type="hidden"/>
                <%
            }
        }

	%>
	<div id="recaptcha-error" style="color:red;font-weight:bold;display:none">Please validate the recaptcha</div>
<%
    }
    LayoutHelper.printDescription(FormsHelper.getDescription(resource, ""), out);
    LayoutHelper.printErrors(slingRequest, ":g-recaptcha-response", out);
%>