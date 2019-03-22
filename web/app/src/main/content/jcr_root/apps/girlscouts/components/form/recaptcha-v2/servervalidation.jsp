<%@page session="false" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo,
                com.day.text.Text,
                java.io.BufferedReader, java.io.InputStream, java.io.InputStreamReader,
                java.net.URL, java.nio.charset.Charset, com.google.gson.Gson,
                com.google.gson.JsonParser, com.google.gson.JsonObject"%>
<%
    FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
    desc.setName(":g-recaptcha-response");
    String recaptcha_secret = currentSite.get("recaptcha_secret", "");
    String recaptcha_response = slingRequest.getParameter("g-recaptcha-response");
    try {
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptcha_secret + "&response=" + recaptcha_response;
        InputStream res = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(res, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        res.close();
        String jsonText = sb.toString();
        JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
        if(!json.get("success").getAsBoolean()){
            ValidationInfo.addConstraintError(slingRequest, desc);
        }
    } catch (Exception e) {
        ValidationInfo.addConstraintError(slingRequest, desc);
    }

%>
