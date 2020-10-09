package org.girlscouts.common.osgi.component.impl;

import org.girlscouts.common.osgi.component.GirlscoutsVtkConfigProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Component(service = {
		GirlscoutsVtkConfigProvider.class }, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsVtkConfigProviderImpl")
@Designate(ocd = GirlscoutsVtkConfigProviderImpl.Config.class)
public class GirlscoutsVtkConfigProviderImpl implements GirlscoutsVtkConfigProvider {

	private static final Logger log = LoggerFactory.getLogger(GirlscoutsVtkConfigProviderImpl.class);

	private String helloUrl=null;
	private String loginUrl=null;
	private String logoutUrl=null;
    private String renewUrl=null;
	private String[] councilMapping=null;


	@Activate
	private void activate(Config config) {
		this.helloUrl=config.helloUrl();
		this.loginUrl=config.loginUrl();
		this.logoutUrl=config.logoutUrl();
		this.councilMapping=config.councilMapping();
		this.renewUrl = config.renewUrl();
        log.info("Girl Scouts VTK Config Provider Activated.");
	}

    @Override
    public String getHelloUrl() {
        return helloUrl;
    }
    @Override
    public String getLoginUrl() {
        return loginUrl;
    }
    @Override
    public String getLogoutUrl() {
        return logoutUrl;
    }
    @Override
    public String[] getCouncilMapping() {
        return councilMapping;
    }
    @Override
    public String getRenewUrl() {
        return renewUrl;
    }

    @Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Girl Scouts VTK Config Provider Deactivated.");
	}

    @ObjectClassDefinition(name = "Girl Scouts VTK settings provider Configuration", description = "Girl Scouts VTK settings provider Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Hello Url", description = "Hello Url", type = AttributeType.STRING)
        String helloUrl();

        @AttributeDefinition(name = "VTK Log In URL", type = AttributeType.STRING)
        String loginUrl();

        @AttributeDefinition(name = "VTK Log Out URL", type = AttributeType.STRING)
        String logoutUrl();

        @AttributeDefinition(name = "Member Community URL", type = AttributeType.STRING)
        String renewUrl();

        @AttributeDefinition(name = "Council Mapping", description = "Defines mappings between a council ID and a content branch. Format: council id::content branch. e.g. 12::gateway")
        String[] councilMapping();
    }

    @Override
    public String toString() {
        return "GirlscoutsVtkConfigProviderImpl{" + "helloUrl='" + helloUrl + '\'' + ", loginUrl='" + loginUrl + '\'' + ", logoutUrl='" + logoutUrl + '\'' + ", renewUrl='" + renewUrl + '\'' +  ", councilMapping=" + Arrays.toString(councilMapping) + '}';
    }
}
