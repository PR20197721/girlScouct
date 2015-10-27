<form method="POST" action="https://gsuat-gsmembers.cs17.force.com/members/services/oauth2/token">

ASSERTION_TYPE<input type="text" value="urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser" name="assertion_type">
<br/>Assertion<input type="text" name="assertion" value="">
<br/>Grant_TYPE<input type="text" value="assertion" name="grant_type">

<input type="submit"/>
</form>


<%
// new org.girlscouts.vtk.sso.OAuthJWTHandler_v1().doIt();
%>