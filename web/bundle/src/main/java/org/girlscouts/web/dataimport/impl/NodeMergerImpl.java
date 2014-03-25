package org.girlscouts.web.dataimport.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.dataimport.NodeMerger;
import org.girlscouts.web.exception.GirlScoutsException;

@Component
@Service(value = NodeMerger.class)
@Properties({
	@Property(name = "service.pid", value = "org.girlscouts.web.dataimport.nodemerger", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts node merger service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class NodeMergerImpl implements NodeMerger {
    
    public String[] merge(String origPath, String destPath, ResourceResolver rr) throws GirlScoutsException {
        List<String> errors = new ArrayList<String>();
        Session session = rr.adaptTo(Session.class);
        
   	
	return errors.toArray(new String[errors.size()]);
    }
}
