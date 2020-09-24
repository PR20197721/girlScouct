package org.girlscouts.common.osgi.component.impl;

import org.girlscouts.common.osgi.component.WebToCase;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component(service = {WebToCase.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToCaseImpl")
@Designate(ocd = WebToCaseImpl.Config.class)
public class WebToCaseImpl implements WebToCase {

	private static final Logger log = LoggerFactory.getLogger(WebToCaseImpl.class);

    private String oid;
    private String apiURL;
    private Map<String, String> recaptchaMap = new HashMap<>();
    private final Set<String> expectedParams = new HashSet<>();


	@Activate
	private void activate(Config config) {
        this.oid = config.oid();
        this.apiURL = config.apiURL();
        String[] recaptchaArr = config.recaptchaMap();
        if(recaptchaArr != null ){
            for(String mapping:recaptchaArr){
                if(mapping != null) {
                    String[] keyValPair = mapping.split("::");
                    if (keyValPair.length == 2) {
                        recaptchaMap.put(keyValPair[0], keyValPair[1]);
                    }
                }
            }
        }
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
    public Map<String, String> getRecaptchaMap() {
        return this.recaptchaMap;
    }

    @Override
    public Set<String> getExpectedParams(){
	    return this.expectedParams;
    }
    @ObjectClassDefinition(name = "Girl Scouts Web To Lead Configuration Service")
    public @interface Config {
        @AttributeDefinition(name = "Organization ID") String oid() default "00D220000004chr";

        @AttributeDefinition(name = "Form submit path") String apiURL() default "https://gsdev1--dev1.my.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";

        @AttributeDefinition(name = "Recaptcha Key Map") String[] recaptchaMap() default {
                "6LcuqJgUAAAAAInWzpQHvo-uWPfnVcR7bHPQw9S8::GoogleReCaptchaKeyPair1",
                "6Lddq5gUAAAAAAl_cYrCis6i0ONWhC_5ClYkPFRA::GoogleReCaptchaKeyPair2",
                "6LcDrJgUAAAAAFk6lqMCRX0jckqvll_oO2Z5Cq0c::GoogleReCaptchaKeyPair3",
                "6LdbspgUAAAAAF304uYUmiskh-ju5IYpXI5jWBYL::GoogleReCaptchaKeyPair4",
                "6Ldls5gUAAAAABX4ucAK1NLXKaw9lf7ktxSUdKUI::GoogleReCaptchaKeyPair5",
                "6Le5s5gUAAAAALqs02AZW3iieMcL5XepoIKxlSpN::GoogleReCaptchaKeyPair6",
                "6LcPtJgUAAAAAGwg6r5eVtDnziu3VbTQIv5Tz4mo::GoogleReCaptchaKeyPair7",
                "6LfEtJgUAAAAAB4btse4kjSKN6fBFqy4U1M15dna::GoogleReCaptchaKeyPair8"
        };

        @AttributeDefinition(name = "Expected Fields") String[] expectedParams() default {
            "orgid",
            "00N22000000ltnH",
            "00N22000000ltnp",
                "00N22000000ltns",
                "00N22000000ltnr",
                "00N22000000ltnt",
            "CouncilCode",
            "origin",
            "status",
            "name",
            "email",
            "phone",
            "type",
            "subject",
            "description",
            "g-recaptcha-response",
                "captcha_settings",
                "debug",
                "debugEmail"
            };
    }

}
