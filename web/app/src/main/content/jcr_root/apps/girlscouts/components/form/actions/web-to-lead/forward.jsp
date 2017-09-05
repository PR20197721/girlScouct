<%@page session="false" %><%
%><%@page import="com.day.cq.wcm.foundation.forms.FormsHelper"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
FormsHelper.setForwardPath(slingRequest, resource.getPath() + ".webtolead.html");
/* FormsHelper.setRedirectToReferrer(request, true);
 */
%>
%>