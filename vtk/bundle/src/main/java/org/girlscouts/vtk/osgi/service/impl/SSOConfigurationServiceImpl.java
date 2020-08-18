package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.osgi.service.SSOConfigurationService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {SSOConfigurationService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.SSOConfigurationServiceImpl")
@Designate(ocd = SSOConfigurationServiceImpl.Config.class)
public class SSOConfigurationServiceImpl extends BasicGirlScoutsService implements SSOConfigurationService {

    @ObjectClassDefinition(name = "VTK SSO Screen Set Configuration Service")
    public @interface Config {
        @AttributeDefinition(name = "API Key", type = AttributeType.STRING) String apiKey();
        @AttributeDefinition(name = "SP Name", description = "Service Provider Name", type = AttributeType.STRING) String spName();
        @AttributeDefinition(name = "Log In Path", description = "Fully qualified path to log in page", type = AttributeType.STRING) String logInPath();
        @AttributeDefinition(name = "Log Out Path", description = "Fully qualified path to log out page", type = AttributeType.STRING) String logOutPath();
    }
    private static Logger log = LoggerFactory.getLogger(SSOConfigurationServiceImpl.class);

    private String apiKey;
    private String spName;
    private String logInPath;
    private String logOutPath;

    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.apiKey = getConfig("apiKey");
        this.spName = getConfig("spName");
        this.logInPath = getConfig("logInPath");
        this.logOutPath = getConfig("logOutPath");
        log.info(this.getClass().getName() + " activated.");
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

    @Override
    public String getSPName() {
        return this.spName;
    }

    @Override
    public String getLogInPath() {
        return this.logInPath;
    }

    @Override
    public String getLogOutPath() {
        return this.logOutPath;
    }

}
