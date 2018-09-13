package org.girlscouts.gsusa.access;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype = true, label = "Resolver Access Service",
description = "Assists with bulkeditor performance")
@Service
@Properties({
@Property(name = "service.description", value = "Provides a resource resolver with elevated access")
}) 
public class ResolverAccessServiceImpl implements ResolverAccessService{
	
	@Reference
	private ResourceResolverFactory resolverFactory;
	
	
	private Map<String, Object> serviceParams;
    
    private static Logger log = LoggerFactory.getLogger(ResolverAccessServiceImpl.class);
    
    private ResourceResolver rr = null;
	
	@Activate
	private void activate(ComponentContext context) {
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		log.info("Access Resolver Service Activated.");
	}
	
	public ResourceResolver getAccessResolver() {
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
		} catch(Exception e) {
			log.error("Getting Access Resolver Failed with message: " + e.getMessage());
		}
		
		return rr;
		
	}
	

}
