package org.girlscouts.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormsHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WebToCaseUtils {

    public static List<String> validateForm(SlingHttpServletRequest request){
        List<String> errors = new ArrayList<>();
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String type = request.getParameter("type");
        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String g_recaptcha_response = request.getParameter("g-recaptcha-response");
        if (null != email && StringUtils.isBlank(email)){
            errors.add("Missing value for required field: email");
        }
        if (null != name && StringUtils.isBlank(name)){
            errors.add("Missing value for required field: name");
        }
        if (null != type && StringUtils.isBlank(type)){
            errors.add("Missing value for required field: type");
        }
        if (null != subject && StringUtils.isBlank(subject)){
            errors.add("Missing value for required field: subject");
        }
        if (null != description && StringUtils.isBlank(description)){
            errors.add("Missing value for required field: description");
        }
        if (null != g_recaptcha_response && StringUtils.isBlank(g_recaptcha_response)){
            errors.add("Missing value for required field: g-recaptcha-response");
        }
        
        Iterator<Resource> elements = FormsHelper.getFormElements(request.getResource());
        while (elements.hasNext()) {
            final Resource element = elements.next();
            final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, element);
            for (final FieldDescription desc : descs) {
                ValueMap childProperties = ResourceUtil.getValueMap(element);
                	if(childProperties.containsKey("required") && childProperties.get("required").equals("true")){
                		String paramVal = request.getParameter(desc.getName());
                		if (null == paramVal || paramVal.equals("")) {
                			if (null != desc.getRequiredMessage()) {
                				errors.add(desc.getRequiredMessage());
                			}
                		}
            		}
            }
        }
        return errors;
    }
}