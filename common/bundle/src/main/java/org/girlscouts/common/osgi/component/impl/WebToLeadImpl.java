package org.girlscouts.common.osgi.component.impl;

import org.girlscouts.common.osgi.component.WebToLead;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component(service = {
		WebToLead.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToLeadImpl")
@Designate(ocd = WebToLeadImpl.Config.class)
public class WebToLeadImpl implements WebToLead {

	private static final Logger log = LoggerFactory.getLogger(WebToLeadImpl.class);

    private String oid;
    private String apiURL;
    private final Set<String> expectedParams = new HashSet<>();


	@Activate
	private void activate(Config config) {
        this.oid = config.oid();
        this.apiURL = config.apiURL();
        String [] expectedParamsArr = config.expectedParams();
        if(expectedParamsArr != null){
            for(String paramName:expectedParamsArr){
                expectedParams.add(paramName);
            }
        }
        log.info("Activated.");
	}


    @Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Deactivated.");
	}

    @Override
    public String getOID() {
        return this.oid;
    }

    @Override
    public String getApiURL() {
        return this.apiURL;
    }

    @Override
    public Set<String> getExpectedParams(){
        return this.expectedParams;
    }

    @ObjectClassDefinition(name = "Girl Scouts Web To Lead Configuration Service")
    public @interface Config {
        @AttributeDefinition(name = "Organization ID") String oid() default "00DG0000000leqU";

        @AttributeDefinition(name = "Form submit path") String apiURL() default "https://pub.s6.exacttarget.com/3la2yxuyhd1";
        @AttributeDefinition(name = "Expected Fields") String[] expectedParams() default {
                "LeadType",
                "Email",
                "CouncilCode",
                "ZipCode",
                "FirstName",
                "LastName",
                "CampaignID",
                "Phone",
                "City",
                "StreetAddress",
                "State",
                "SchoolName",
                "Title",
                "GirlFirstName",
                "GirlLastName",
                "Grade",
                "GirlAge",
                "University",
                "Alum",
                "UTM_Campaign",
                "UTM_Medium",
                "UTM_Source",
                "VolunteerInterest",
                "IsMember",
                "FormURL",
                "DownloadURL",
                "GraduationYear",
                "debug",
                "debugEmail"
        };
    }

}
