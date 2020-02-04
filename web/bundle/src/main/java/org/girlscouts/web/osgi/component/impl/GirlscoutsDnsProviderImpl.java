package org.girlscouts.web.osgi.component.impl;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.component.GirlscoutsDnsProvider;
import org.girlscouts.web.osgi.component.configuration.GirlscoutsDnsProviderConfig;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = {GirlscoutsDnsProvider.class}, immediate = true, name = "org.girlscouts.web.osgi.component.impl.GirlscoutsDnsProviderImpl")
@Designate(ocd = GirlscoutsDnsProviderConfig.class)
public class GirlscoutsDnsProviderImpl extends BasicGirlscoutsService implements GirlscoutsDnsProvider{

    private String[] dnsMap;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    public void activate(ComponentContext context) {
        this.context = context;
        this.dnsMap = getConfig("dnsMapping");
    }

    @Override
    public String getDns(String councilPath) {

        return this.dnsMap[0];
    }


}
