package org.girlscouts.vtk.osgi.component.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component
@Service(value = SessionFactory.class)
public class SessionFactory {
    private static final Logger log = LoggerFactory.getLogger(SessionFactory.class);

    @Reference
    public ResourceResolverFactory resourceFactory;

    public Session getSession() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        ResourceResolver adminResolver = null;
        try {
            adminResolver = resourceFactory.getServiceResourceResolver(paramMap);
            log.debug("Created new admin resource resolver " + adminResolver);
        } catch (org.apache.sling.api.resource.LoginException e) {
            log.error("Error occurred: ", e);
        }
        Session adminSession = adminResolver.adaptTo(Session.class);
        log.debug("Created new admin session " + adminSession);
        return adminSession;
    }
}
