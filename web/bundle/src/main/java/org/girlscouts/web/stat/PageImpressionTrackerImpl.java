package org.girlscouts.web.stat;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value=PageImpressionTracker.class)
@Properties({
    @Property(name = "service.pid", value = "org.girlscouts.web.stat.PageImpressionTracker", propertyPrivate = false),
    @Property(name = "service.description", value = "Collect page impressions", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts USA", propertyPrivate = false) })
public class PageImpressionTrackerImpl implements PageImpressionTracker, Runnable {
    private static Logger log = LoggerFactory.getLogger(PageImpressionTrackerImpl.class);
    private Session session = null;
    private Map<String, Long> statMap;
    
    @Reference
    SlingRepository repository;

    protected void activate(ComponentContext componentContext) {
	statMap = new HashMap<String, Long>();
        try {
            session = repository.loginAdministrative(repository.getDefaultWorkspace());
        } catch (RepositoryException e) {
            log.error("Cannot login to JCR session.");
        }	
    }

    protected void deactivate(ComponentContext componentContext) {
        session.logout();
    }

    public synchronized void track(String path) {
	Long count = statMap.containsKey(path) ? statMap.get(path) : 0;
	statMap.put(path, count + 1);
    }

    public synchronized void run() {
	
    }

}
