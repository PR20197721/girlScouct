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

@Component(service = {WebToCase.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToCaseImpl")
@Designate(ocd = WebToCaseImpl.Config.class)
public class WebToCaseImpl implements WebToCase {

	private static final Logger log = LoggerFactory.getLogger(WebToCaseImpl.class);

    private String oid;
    private String apiURL;


	@Activate
	private void activate(Config config) {
        this.oid = config.oid();
        this.apiURL = config.apiURL();
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

    @ObjectClassDefinition(name = "Girl Scouts Web To Lead Configuration Service")
    public @interface Config {
        @AttributeDefinition(name = "Organization ID") String oid() default "00D220000004chr";

        @AttributeDefinition(name = "Form submit path") String apiURL() default "https://gsdev1--dev1.my.salesforce.com/servlet/servlet.WebToCase?encoding=UTF-8";
    }

}
