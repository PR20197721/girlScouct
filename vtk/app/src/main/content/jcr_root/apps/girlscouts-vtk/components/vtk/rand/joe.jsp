<%@page import="java.net.URLEncoder"%>
<%@page import="java.text.SimpleDateFormat,
                org.apache.commons.lang3.time.FastDateFormat,
                org.girlscouts.vtk.models.Troop,
                org.girlscouts.vtk.auth.permission.*,
                org.girlscouts.vtk.utils.VtkUtil,
                org.apache.commons.lang3.time.FastDateFormat,
                org.apache.sling.runmode.RunMode"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%
final org.girlscouts.vtk.ejb.ConnectionFactory connectionFactory = sling.getService(org.girlscouts.vtk.ejb.ConnectionFactory.class);
String code= request.getParameter("code");
String myResource= request.getParameter("myResource");
String redirectUri="http://localhost:8080/aem-connector/signin";

new org.girlscouts.vtk.auth.dao.SalesforceDAO(null,connectionFactory).doJoe(code, redirectUri , myResource);
%>



<%!
/*
static void requestAccessToken() throws IOException {
    try {
      GoogleTokenResponse response =
          new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(), new JacksonFactory(),
              "812741506391.apps.googleusercontent.com", "{client_secret}",
              "4/P7q7W91a-oMsCeLvIaQm6bTrgtp7", "https://oauth2-login-demo.appspot.com/code")
              .execute();
      System.out.println("Access token: " + response.getAccessToken());
    } catch (TokenResponseException e) {
      if (e.getDetails() != null) {
        System.err.println("Error: " + e.getDetails().getError());
        if (e.getDetails().getErrorDescription() != null) {
          System.err.println(e.getDetails().getErrorDescription());
        }
        if (e.getDetails().getErrorUri() != null) {
          System.err.println(e.getDetails().getErrorUri());
        }
      } else {
        System.err.println(e.getMessage());
      }
    }
  }
 */
%>