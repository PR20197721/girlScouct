package org.girlscouts.web.osgi.component.impl;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.web.osgi.component.GirlscoutsDnsProvider;
import org.girlscouts.web.osgi.component.configuration.GirlscoutsDnsProviderConfig;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> councilMapping = new HashMap<>();
        for(String s : this.dnsMap) {
            String[] temp = s.split("::");
            councilMapping.put(temp[0],temp[1]);
        }

        int councilStart = councilPath.indexOf("/content/") + 9;
        String councilName = councilPath.substring(councilStart, councilPath.indexOf("/", councilStart));
        StringBuilder dnsName = new StringBuilder();

        dnsName.append("http://preview.");
        dnsName.append(councilMapping.get(councilName));
        dnsName.append(".org");

        return dnsName.toString();
    }


}
