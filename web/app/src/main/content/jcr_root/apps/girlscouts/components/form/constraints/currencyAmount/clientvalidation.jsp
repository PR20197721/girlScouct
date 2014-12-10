<%@page session="false" %><%
%><%@page import="com.day.cq.wcm.foundation.forms.FieldDescription,
                  com.day.cq.wcm.foundation.forms.FormsHelper,
                  org.apache.sling.scripting.jsp.util.JspSlingHttpServletResponseWrapper"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
//validation format xx.00
                      final String regexp = "/^([1-9][0-9]{0,5}|0)[.][0-9]{2}$/";
    final FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
    FieldHelper.writeClientRegexpText(slingRequest, new JspSlingHttpServletResponseWrapper(pageContext), desc, regexp);
%>
