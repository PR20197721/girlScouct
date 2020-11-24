package org.girlscouts.common.osgi.component.impl;

import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.common.osgi.component.GirlscoutsDnsProvider;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

@Component(service = {GirlscoutsDnsProvider.class}, immediate = true, name = "org.girlscouts.common.osgi.component.impl.GirlscoutsDnsProviderImpl")
@Designate(ocd = GirlscoutsDnsProviderImpl.Config.class)
public class GirlscoutsDnsProviderImpl implements GirlscoutsDnsProvider{
    private ComponentContext context;
    private Map<String, String> councilMapping;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Activate
    public void activate(Config config) {
        this.councilMapping = new HashMap<>();
        for (String s : config.dnsMapping()) {
            String[] temp = s.split("::");
            this.councilMapping.put(temp[0],temp[1]);
        }

    }

    @Override
    public String getDns(String councilPath) {
        int councilStart = councilPath.indexOf("/content/") + 9;
        String councilName = councilPath.substring(councilStart, councilPath.indexOf("/", councilStart));
        StringBuilder dnsName = new StringBuilder();

        dnsName.append("http://preview.");
        dnsName.append(councilMapping.get(councilName));
        dnsName.append(".org");

        return dnsName.toString();
    }

    private String[] getConfig(String property) {
        if (this.context != null) {
            Dictionary properties = this.context.getProperties();
            return (String [])(properties.get(property));
        } else {
            return null;
        }
    }

    @ObjectClassDefinition(name = "Girl Scouts DNS Mapping Configuration", description = "Girl Scouts DNS Mapping Configuration")
    public @interface Config {
        @AttributeDefinition(name = "Get Council DNS mapping", description = "Get Council DNS mapping to council names", type = AttributeType.STRING) String[] dnsMapping();
    }
}
