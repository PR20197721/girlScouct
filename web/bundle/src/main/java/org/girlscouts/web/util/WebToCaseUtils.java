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
        String phone = request.getParameter("phone");
        String type = request.getParameter("type");
        String subject = request.getParameter("subject");
        String description = request.getParameter("description");
        String g_recaptcha_response = request.getParameter("g-recaptcha-response");
        if (StringUtils.isBlank(email)){
            errors.add("Missing value for required field: email");
        }
        if (StringUtils.isBlank(name)){
            errors.add("Missing value for required field: name");
        }
        if (StringUtils.isBlank(phone)){
            errors.add("Missing value for required field: phone");
        }
        if (StringUtils.isBlank(type)){
            errors.add("Missing value for required field: type");
        }
        if (StringUtils.isBlank(subject)){
            errors.add("Missing value for required field: subject");
        }
        if (StringUtils.isBlank(description)){
            errors.add("Missing value for required field: description");
        }
        if (StringUtils.isBlank(g_recaptcha_response)){
            errors.add("Missing value for required field: g-recaptcha-response");
        }
        Iterator<Resource> elements = FormsHelper.getFormElements(request.getResource());
        while (elements.hasNext()) {
            final Resource element = elements.next();
            final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, element);
            for (final FieldDescription desc : descs) {
                ValueMap childProperties = ResourceUtil.getValueMap(element);
                	if(childProperties.get("required").equals("true")){
                		String paramVal = request.getParameter(desc.getName());
                		if (null == paramVal || paramVal.equals("")) {
                			errors.add(desc.getRequiredMessage());
                		}
            		}
            }
        }
        return errors;
    }
}
