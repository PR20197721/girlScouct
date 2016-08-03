package org.girlscouts.web.stat;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
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

//import com.adobe.cq.social.commons.AsyncReverseReplicator;
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
    // Stat nodes will be collected every 10 minutes.
    // To KISS, we use same time intervals on auth and pub, or we need to have two classes.
    @Property(name = "scheduler.period", longValue=600L, propertyPrivate=true),
    @Property(name = "service.pid", value = "org.girlscouts.web.stat.PageImpressionTracker", propertyPrivate = false),
    @Property(name = "service.description", value = "Collect page impressions", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts USA", propertyPrivate = false) 
})
public class PageImpressionTrackerImpl implements PageImpressionTracker, Runnable {
    private static Logger log = LoggerFactory.getLogger(PageImpressionTrackerImpl.class);

    // Root path to store stat nodes 
    private static String STAT_PATH = "/var/stat";
    // Property to save page views. Format: path===count|||anotherPath===anotherCount...
    private static String STAT_PROPERTY = "gsstat";
    private static String EQUAL_SIGN = "===";
    private static String DELIMITER = "\\|\\|\\|";
    // Property to record the last collected stat node timestamp.
    private static String LAST_COLLECTED = "gsstatLastCollected";
    // Time interval to cleanup obsolete stat nodes, in seconds.
    private static int STAT_TIMEOUT = 3600*24;

    private Session session;
    private PageManager pageManager;
    private Map<String, Long> statMap;
    private boolean isPublish;
    private String statisticsPath;
    
    @Reference
    private SlingRepository repository;
    
    @Reference
    private SlingSettingsService settingsService;
    
   // @Reference
    //private AsyncReverseReplicator replicator;
    
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
            log.error("Cannot login into the repository using the session token.");
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
	try {
	    if (isPublish) {
		// Publish
		try {
		    session.getNode(STAT_PATH);
		} catch (PathNotFoundException e) {
		    JcrUtil.createPath(STAT_PATH, "nt:unstructured", session);
		}
		StringBuilder sb = new StringBuilder();
		synchronized(statMap) {
		    for (String path : statMap.keySet()) {
			sb.append(path).append(EQUAL_SIGN).append(statMap.get(path)).append(DELIMITER);
		    }
		}
		// how to synchronize here?
		// Can exceptions be thrown within synchronized block?
		newStatMap();
		String mapValueStr = sb.toString();
		if (mapValueStr.contains(DELIMITER)) {
		    mapValueStr = mapValueStr.substring(0, sb.length() - DELIMITER.length());
		}
		if (!mapValueStr.isEmpty()) {
		    String statNodePath = STAT_PATH + "/" + Long.toString((new Date()).getTime());
		    Node statNode = JcrUtil.createPath(statNodePath, "nt:unstructured", session);
		    statNode.setProperty(STAT_PROPERTY, mapValueStr); 
		    session.save();
		    //replicator.reverseReplicate(statNodePath);
		}
	    } else {
		// Authoring
		Node rootStatNode = null;
		try {
		    rootStatNode = session.getNode(STAT_PATH);
		} catch (PathNotFoundException e) {
		    rootStatNode = JcrUtil.createPath(STAT_PATH, "nt:unstructured", session);
		}

		Date lastCollected = null;
		if (!rootStatNode.hasProperty(LAST_COLLECTED)) {
		    lastCollected = new Date(0);
		} else {
		    lastCollected = rootStatNode.getProperty(LAST_COLLECTED).getDate().getTime();
		}

		NodeIterator iter = rootStatNode.getNodes();
		Date newLastCollected = lastCollected;
		while (iter.hasNext()) {
		    Node statNode = iter.nextNode();
		    Date nodeDate = new Date(Long.parseLong(statNode.getName()));
		    if (nodeDate.after(lastCollected)) {
			if (nodeDate.after(newLastCollected)) {
			    newLastCollected = nodeDate;
			}
			String[] stats = statNode.getProperty(STAT_PROPERTY).getString().split(DELIMITER);
			// Known from the decompiled src of ImpressionsEntry.
			//DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			for (int i = 0; i < stats.length; i++) {
			    String[] currentStat = stats[i].split(EQUAL_SIGN);
        			if (currentStat.length == 2) {
        			String path = currentStat[0];
        			long count = Long.parseLong(currentStat[1]);
        			    
        			// Page view is generated here when it is collected at authoring
        			// rather than when the page is viewed at publishing.
        			// So, it is expected there is a delay of the view date
        			// and some records may go to the next day.
        			// However, this is a tradeoff for performance.
        			PageView view = new PageView(statisticsPath, pageManager.getPage(path), WCMMode.DISABLED);
        			for (long j = 0; j < count; j++) {
        			    // Do we have better option here?
        			    // Is it something similar like addEntry(view, count)?
        			    // How to make this work?
        			    //ImpressionsEntry entry = new ImpressionsEntry(statisticsPath, path, format.format(nodeDate), count);
        			    //statisticsService.addEntry(entry);
        			    statisticsService.addEntry(view);
        			}
			    }
			}
		    }
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(newLastCollected);
		rootStatNode.setProperty(LAST_COLLECTED, cal);
		session.save();
	    }
	    cleanup();
	} catch (RepositoryException e) {
	    e.printStackTrace();
	}
    }
    
    private void cleanup() {
	try {
	    Date timeoutCut = new Date(new Date().getTime() - STAT_TIMEOUT * 1000);
	    Node statRootNode = session.getNode(STAT_PATH);
	    NodeIterator iter = statRootNode.getNodes();
	    while (iter.hasNext()) {
		Node statNode = iter.nextNode(); 
		Date nodeDate = new Date(Long.parseLong(statNode.getName()));
		if (timeoutCut.after(nodeDate)) {
		    statNode.remove();
		}
	    }
	    session.save();
	} catch (RepositoryException e) {
	    e.printStackTrace();
	}
    }

}
