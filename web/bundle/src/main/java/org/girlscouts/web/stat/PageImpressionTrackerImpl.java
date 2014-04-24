package org.girlscouts.web.stat;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Services;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.social.commons.AsyncReverseReplicator;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.statistics.StatisticsService;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMMode;
import com.day.cq.wcm.core.stats.PageView;

@Component
@Services({
    @Service(value=PageImpressionTracker.class),
    @Service(value=Runnable.class)
})
@Properties({
    @Property(name = "scheduler.period", longValue=10L, propertyPrivate=true),
    @Property(name = "service.pid", value = "org.girlscouts.web.stat.PageImpressionTracker", propertyPrivate = false),
    @Property(name = "service.description", value = "Collect page impressions", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts USA", propertyPrivate = false) 
})
public class PageImpressionTrackerImpl implements PageImpressionTracker, Runnable {
    private static Logger log = LoggerFactory.getLogger(PageImpressionTrackerImpl.class);
    private static String STAT_PATH = "/var/tracker";
    private static String EQUAL_SIGN = "===";
    private static String DELIMITER = "\\|\\|\\|";
    private static String STAT_PROPERTY = "gsstat";
    private static String LAST_COLLECTED = "gsstatLastCollected";

    private Session session;
    private PageManager pageManager;
    private Map<String, Long> statMap;
    private boolean isPublish;
    private String statisticsPath;
    
    @Reference
    private SlingRepository repository;
    
    @Reference
    private SlingSettingsService settingsService;
    
    @Reference
    private AsyncReverseReplicator replicator;
    
    @Reference
    private StatisticsService statisticsService;
    
    @Reference
    private ResourceResolverFactory rrFactory;

    protected void activate(ComponentContext componentContext) {
	isPublish = settingsService.getRunModes().contains("publish") ? true : false;
	statisticsPath = statisticsService.getPath() + "/pages";
	newStatMap();
        try {
            session = repository.loginAdministrative(repository.getDefaultWorkspace());
            Map<String, Object> rrInfo = new HashMap<String, Object>();
            rrInfo.put("user.jcr.session", session);
            pageManager = (PageManager)(rrFactory.getResourceResolver(rrInfo).adaptTo(PageManager.class));
        } catch (RepositoryException e) {
            log.error("Cannot login to JCR session.");
        } catch (LoginException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}	
        log.error("Startup complete");
    }

    protected void deactivate(ComponentContext componentContext) {
	if (isPublish) {
	    run();
	}
        session.logout();
    }

    public void track(String path) {
	synchronized (statMap) {
	    Long count = statMap.containsKey(path) ? statMap.get(path) : 0;
	    statMap.put(path, count + 1);
	}
    }
    
    private void newStatMap() {
	statMap = Collections.synchronizedMap(new HashMap<String, Long>());
    }

    public void run() {
	log.error("RUNNING");
	try {
	    if (isPublish) {
		// Publish
		log.error("publish");
		String statNodePath = STAT_PATH + "/" + Long.toString((new Date()).getTime());
		// TODO: what does createPath give us?
		Node statNode = JcrUtil.createPath(statNodePath, "nt:unstructured", session);
		StringBuilder sb = new StringBuilder();
		synchronized(statMap) {
		    for (String path : statMap.keySet()) {
			sb.append(path).append(EQUAL_SIGN).append(statMap.get(path)).append(DELIMITER);
		    }
		}
		// TODO: how to synchronize here?
		// Can exceptions be thrown within synchronized block?
		newStatMap();
		String mapValueStr = sb.toString();
		if (mapValueStr.contains(DELIMITER)) {
		    mapValueStr = mapValueStr.substring(0, sb.length() - DELIMITER.length());
		}
		statNode.setProperty(STAT_PROPERTY, mapValueStr); 
		session.save();
		replicator.reverseReplicate(statNodePath);
	    } else {
		// Authoring
		log.error("authoring");
		// TODO: createPath?
		Node rootStatNode = session.getNode(STAT_PATH);
		Date lastCollected = null;
		if (!rootStatNode.hasProperty(LAST_COLLECTED)) {
		    lastCollected = new Date(0);
		} else {
		    lastCollected = rootStatNode.getProperty(LAST_COLLECTED).getDate().getTime();
		}

		NodeIterator iter = rootStatNode.getNodes();
		while (iter.hasNext()) {
		    Node statNode = iter.nextNode();
		    Date nodeDate = new Date(Long.parseLong(statNode.getName()));
		    if (nodeDate.after(lastCollected)) {
			String[] stats = statNode.getProperty(STAT_PROPERTY).getString().split(DELIMITER);
			for (int i = 0; i < stats.length; i++) {
			    String[] currentStat = stats[i].split(EQUAL_SIGN);
        			if (currentStat.length == 2) {
        			String path = currentStat[0];
        			long count = Long.parseLong(currentStat[1]);
        			    
        			PageView view = new PageView(statisticsPath, pageManager.getPage(path), WCMMode.DISABLED);
        			for (long j = 0; j < count; j++) {
        			    // TODO: tracker
        			    log.error("log: " + j);
        			    statisticsService.addEntry(view);
        			}
			    }
			}
		    }
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		rootStatNode.setProperty(LAST_COLLECTED, cal);
		session.save();
	    }
	} catch (RepositoryException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
