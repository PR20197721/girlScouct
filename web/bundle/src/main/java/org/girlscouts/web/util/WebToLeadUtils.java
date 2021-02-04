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

public class WebToLeadUtils {

    public static List<String> validateForm(SlingHttpServletRequest request){
        List<String> errors = new ArrayList<>();
        String leadType = request.getParameter("LeadType");
        String email = request.getParameter("Email");
        String councilCode = request.getParameter("CouncilCode");
        String g_recaptcha_response = request.getParameter("g-recaptcha-response");
        if (StringUtils.isBlank(leadType)){
            errors.add("Missing value for required field: LeadType");
        }else{
            if(leadType.equals("DirectContact") || leadType.equals("General")){
                String zipCode = request.getParameter("ZipCode");
                if (StringUtils.isBlank(zipCode)){
                    errors.add("Missing value for required field: ZipCode");
                }
                String firstName = request.getParameter("FirstName");
                if (StringUtils.isBlank(firstName)){
                    errors.add("Missing value for required field: FirstName");
                }
                String lastName = request.getParameter("LastName");
                if (StringUtils.isBlank(lastName)){
                    errors.add("Missing value for required field: LastName");
                }
                String campaignId = request.getParameter("CampaignID");
                if (StringUtils.isBlank(campaignId)){
                    errors.add("Missing value for required field: CampaignID");
                }
            }
        }
        if (StringUtils.isBlank(email)){
            errors.add("Missing value for required field: Email");
        }
        if (StringUtils.isBlank(councilCode)){
            errors.add("Missing value for required field: CouncilCode");
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
                	if(childProperties.containsKey("required") && childProperties.get("required").equals("true")){
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
