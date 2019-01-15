package org.girlscouts.gsusa.access;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.tagging.TagManager;

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
    

	@Activate
	private void activate(ComponentContext context) {
		serviceParams = new HashMap<String, Object>();
		serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
		log.info("Access Resolver Service Activated.");
	}
	
	@Override
	public String getColorFromTagNode(String tagId) {
		ResourceResolver rr = null;
		String color = null;
		try {
			rr = resolverFactory.getServiceResourceResolver(serviceParams);
			TagManager tagManager = rr.adaptTo(TagManager.class);
			Node tag = tagManager.resolve(tagId).adaptTo(Node.class);
			if (tag.hasProperty("color")) {
				String hexColor = "FFFFFF";
				hexColor = tag.getProperty("color").getString();
				int rPart = Integer.parseInt(hexColor.substring(0, 2), 16);
				int gPart = Integer.parseInt(hexColor.substring(2, 4), 16);
				int bPart = Integer.parseInt(hexColor.substring(4, 6), 16);
				color = "rgba(" + rPart + ", " + gPart + ", " + bPart + ", 0.8)";

			}
		} catch (Exception e) {
			log.error("Getting Tag Node Failed with message: " + e.getMessage());
		} finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (Exception e) {
					log.error("Error while closing resource resolver: " + e);
				}
			}
		}
		return color;
	}

	@Deactivate
	private void deactivate(ComponentContext context) {
		log.info("Access Resolver Service Deactivated.");
	}
}
