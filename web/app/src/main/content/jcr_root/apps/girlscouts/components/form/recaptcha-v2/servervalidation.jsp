<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo,
                com.day.text.Text,
java.io.BufferedReader, java.io.InputStream, java.io.InputStreamReader, java.net.URL, java.nio.charset.Charset, com.google.gson.Gson, com.google.gson.JsonParser, com.google.gson.JsonObject"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %>
<sling:defineObjects/>
<%!
public static boolean isReCaptchaValid(String secretKey, String response) {
    try {
        String url = "https://www.google.com/recaptcha/api/siteverify?" + "secret=" + secretKey + "&response=" + response;
        InputStream res = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(res, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        String jsonText = sb.toString();
        res.close();
        JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
        return json.getAsJsonObject("success").getAsBoolean();
    } catch (Exception e) {
        return false;
    }
}
%>
<%
    // Get field description and force its name
    FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
    desc.setName(":g-recaptcha");
    String site_key = currentSite.get("recaptcha_key", "");
    String recaptcha_response = request.getParameter("g-recaptcha-response");
    // Check if a value has been provided
    if (FieldHelper.checkRequired(slingRequest, desc)) {
        if (!isReCaptchaValid(site_key, recaptcha_response)) {
            ValidationInfo.addConstraintError(slingRequest, desc);
        }
    }

%>
