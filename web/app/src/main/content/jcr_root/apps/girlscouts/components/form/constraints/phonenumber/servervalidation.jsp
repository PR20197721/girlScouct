<%@page session="false" %><%
%><%@page import="java.util.regex.Matcher,
                java.util.regex.Pattern,
                com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
	final Pattern p = Pattern.compile("(^[\\d]{3}[\\.-]?[\\d){3}[\\.-]?[\\d]{4}");
	final FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
	final String[] values = request.getParameterValues(desc.getName());
	if ( values != null ) {
		for(int i=0; i<values.length; i++) {
			final Matcher m = p.matcher(values[i]);
			if ( !m.matches() ) {
				if ( desc.isMultiValue() ) {
					ValidationInfo.addConstraintError(slingRequest, desc, i);
				} else {
					ValidationInfo.addConstraintError(slingRequest, desc);                    
				}
			}            
		}
	}
%>
