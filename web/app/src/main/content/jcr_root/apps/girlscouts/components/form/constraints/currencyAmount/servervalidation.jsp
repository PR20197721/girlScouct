<%@page session="false" %><%
%><%@page import="java.util.regex.Matcher,
                java.util.regex.Pattern,
                com.day.cq.wcm.foundation.forms.FieldDescription,
                com.day.cq.wcm.foundation.forms.FieldHelper,
                com.day.cq.wcm.foundation.forms.FormsHelper,
                com.day.cq.wcm.foundation.forms.ValidationInfo"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0" %><%
%><sling:defineObjects/><%
//validation format xx.00
	final Pattern p = Pattern.compile("^([1-9][0-9]{0,4}|0)[.][0-9]{2}$");
	final FieldDescription desc = FieldHelper.getConstraintFieldDescription(slingRequest);
	final String[] values = request.getParameterValues(desc.getName());
	if ( values != null ) {
		for(int i=0; i<values.length; i++) {
			final Matcher m = p.matcher(values[i]);
			if ( !m.matches() ) {
                if(desc.getConstraintMessage()==null){//if no custom error msg from the council
                    //default error msg
                    desc.setConstraintMessage("The format for currency should be XXXXX.00");
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
