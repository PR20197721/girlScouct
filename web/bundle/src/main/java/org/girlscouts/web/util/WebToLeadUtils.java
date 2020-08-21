package org.girlscouts.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

public class WebToLeadUtils {

    public static List<String> validateForm(SlingHttpServletRequest request){
        List<String> errors = new ArrayList<>();
        String leadType = request.getParameter("leadType");
        String email = request.getParameter("Email");
        String councilCode = request.getParameter("CouncilCode");
        if (StringUtils.isBlank(leadType)){
            errors.add("Missing value for required field: Lead Type");
        }else{
            if(leadType.equals("DirectContact") || leadType.equals("General")){
                String zipCode = request.getParameter("ZipCode");
                if (StringUtils.isBlank(zipCode)){
                    errors.add("Missing value for required field: Zip Code");
                }
                String firstName = request.getParameter("FirstName");
                if (StringUtils.isBlank(firstName)){
                    errors.add("Missing value for required field: First Name");
                }
                String lastName = request.getParameter("LastName");
                if (StringUtils.isBlank(lastName)){
                    errors.add("Missing value for required field: Last Name");
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
            errors.add("Missing value for required field: Council Code");
        }
        return errors;
    }
}
