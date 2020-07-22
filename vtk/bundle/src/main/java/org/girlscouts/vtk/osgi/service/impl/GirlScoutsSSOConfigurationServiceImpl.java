package org.girlscouts.vtk.osgi.service.impl;

import org.girlscouts.vtk.osgi.conf.GirlScoutsSSOConfigurationServiceConfig;
import org.girlscouts.vtk.osgi.service.GirlScoutsSSOConfigurationService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = {GirlScoutsSSOConfigurationService.class}, immediate = true, name = "org.girlscouts.vtk.osgi.service.impl.GirlScoutsSSOConfigurationServiceImpl")
@Designate(ocd = GirlScoutsSSOConfigurationServiceConfig.class)
public class GirlScoutsSSOConfigurationServiceImpl extends BasicGirlScoutsService implements GirlScoutsSSOConfigurationService {
    private static Logger log = LoggerFactory.getLogger(GirlScoutsSSOConfigurationServiceImpl.class);

    private String apiKey;
    private String spName;
    private String logInPath;
    private String logOutPath;
    private String screenSet;
    private Integer sessionExpiration;


    @Activate
    private void activate(ComponentContext context) {
        this.context = context;
        this.apiKey = getConfig("apiKey");
        this.spName = getConfig("spName");
        this.logInPath = getConfig("logInPath");
        this.logOutPath = getConfig("logOutPath");
        this.screenSet = getConfig("screenSet");
        this.sessionExpiration = getIntegerConfig("sessionExpiration");
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

    @Override
    public String getScreenSet() {
        return this.screenSet;
    }

    @Override
    public Integer getSessionExpiration() {
        return this.sessionExpiration;
    }
}
