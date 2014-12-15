<%@page session="false" %><%
%><%@page import="java.util.regex.Matcher,
                java.util.regex.Pattern,
                com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
// Validated formate (xxx)xxx-xxxx
    final Pattern p = Pattern.compile("^[(][2-9][0-9]{2}[)][0-9]{3}-[0-9]{4}$");
	final FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
	final String[] values = request.getParameterValues(desc.getName());
	if ( values != null ) {
		for(int i=0; i<values.length; i++) {
			final Matcher m = p.matcher(values[i]);
			if ( !m.matches() ) {
                if(desc.getConstraintMessage()==null){//if no custom error msg from the council
                    //default error msg
                    desc.setConstraintMessage("Please enter a valid phone number with format (xxx)xxx-xxxx");
                }
				if ( desc.isMultiValue() ) {
					ValidationInfo.addConstraintError(slingRequest, desc, i);
				} else {
					ValidationInfo.addConstraintError(slingRequest, desc);                    
				}
			}            
		}
	}
%>
