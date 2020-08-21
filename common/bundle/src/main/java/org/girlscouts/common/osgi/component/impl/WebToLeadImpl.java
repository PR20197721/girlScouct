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

@Component(service = {
		WebToLead.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.WebToLeadImpl")
@Designate(ocd = WebToLeadImpl.Config.class)
public class WebToLeadImpl implements WebToLead {

	private static final Logger log = LoggerFactory.getLogger(WebToLeadImpl.class);

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
        @AttributeDefinition(name = "Organization ID") String oid() default "00DG0000000leqU";

        @AttributeDefinition(name = "Form submit path") String apiURL() default "https://pub.s6.exacttarget.com/3la2yxuyhd1";
    }

}
