package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.Date;

import javax.jcr.RepositoryException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.vtk.ejb.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(
    value = VTKDataCacheInvalidator.class
)
public class VTKDataCacheInvalidator {
    private static final Logger log = LoggerFactory.getLogger(VTKReplicationReceiver.class);
    
    // Interval for the next invalidation, in milliseconds.
    private static final int INTERVAL = 4000;
    private static final String FLUSH_NODE = "/etc/replication/agents.publish/flush/jcr:content";
    private static final String FLUSH_NODE2 = "/etc/replication/agents.publish/flush2/jcr:content";
    private static final String FLUSH_PROPERTY = "transportUri";
    private static final String SCHEDULER_PATH_PREFIX = "VTK_PATH_";
    private static final String SCHEDULER_PATH_PREFIX2 = "VTK_PATH2_";

    protected HttpClient httpClient;
    protected MultiThreadedHttpConnectionManager connectionManager;
    protected String flushUri;
    protected String flushUri2 = "";

    @Reference
    protected Scheduler scheduler;
    
    @Reference
    protected SlingRepository repository;
    
    @Reference
    private SessionFactory sessionFactory;
    
    private class CacheInvalidationJob implements Job {
        private String path;

        public CacheInvalidationJob(String path) {
            this.path = path;
        }
        
        public void execute(JobContext context) {
        	log.info("VTKDataCacheInvalidator: ================== Replication Invalidating cache: ");
            GetMethod get = new GetMethod(flushUri);
            log.debug("VTKDataCacheInvalidator: Path: " + path);
            get.setRequestHeader("CQ-Action", "Delete");
            get.setRequestHeader("CQ-Handle", path);
            get.setRequestHeader("CQ-Path", path);
            try {
                int resStatus = httpClient.executeMethod(get);
                if (resStatus != 200) {
                    log.error("VTKDataCacheInvalidator: Cannot invalidate this path: " + path);
                } else {
                    log.info("VTKDataCacheInvalidator: Successfully invalidate the cache: " + path);
                }
            } catch (Exception e) {
                log.info("VTKDataCacheInvalidator: Cannot invalidate this path: " + path + ". Do nothing.");
            } finally{
            	get.releaseConnection();
            }
            log.info("VTKDataCacheInvalidator: Replication Invalidating cache done ==================");
            
            //second seasonal dispatcher if it exists
            
            if (!"".equals(flushUri2) && flushUri2 != null) {
	            log.info("VTKDataCacheInvalidator2: ================== Replication Invalidating cache: ");
	            GetMethod get2 = new GetMethod(flushUri2);
	            log.debug("VTKDataCacheInvalidator2: Path: " + path);
	            get2.setRequestHeader("CQ-Action", "Delete");
	            get2.setRequestHeader("CQ-Handle", path);
	            get2.setRequestHeader("CQ-Path", path);
	            try {
	                int resStatus = httpClient.executeMethod(get2);
	                if (resStatus != 200) {
	                    log.error("VTKDataCacheInvalidator2: Cannot invalidate this path: " + path);
	                } else {
	                    log.info("VTKDataCacheInvalidator2: Successfully invalidate the cache: " + path);
	                }
	            } catch (Exception e) {
	                log.info("VTKDataCacheInvalidator2: Cannot invalidate this path: " + path + ". Do nothing.");
	            } finally{
	            	get2.releaseConnection();
	            }
	            log.info("VTKDataCacheInvalidator2: Replication Invalidating cache done==================");
            }
        }
    }
    
    @Activate
    public void init() {
        connectionManager = new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(connectionManager);
        try {
            Session session = repository.loginAdministrative(null);
        	//Session session = sessionFactory.getSession();
        	flushUri = session.getNode(FLUSH_NODE).getProperty(FLUSH_PROPERTY).getString();
            session.logout();
            log.info("Started.");
        } catch (RepositoryException e) {
            log.error("VTKDataCacheInvalidator: RepositoryException while initializing.");
            e.printStackTrace();
        } 
        
        //second seasonal dispatcher
        try {
        	Session session = repository.loginAdministrative(null);
        	//Session session = sessionFactory.getSession();
            flushUri2 = session.getNode(FLUSH_NODE2).getProperty(FLUSH_PROPERTY).getString();
            session.logout();
            log.info("The second flush agent is up and Started. The flushUri is " + flushUri2 + " with the node " + FLUSH_NODE2);
        } catch(PathNotFoundException pnfe) {
        	log.info("VTKDataCacheInvalidator2. There is no second flush agent. Please ignore.");
        	pnfe.printStackTrace();
        } catch (RepositoryException e) {
            log.error("VTKDataCacheInvalidator2: RepositoryException while initializing.");
            e.printStackTrace();
        } 
    }
    
    @Deactivate
    public void deactivate() {
    	connectionManager.shutdown();
    }
    
    public void addPath(String path) {
        try {
        	scheduler.fireJobAt(SCHEDULER_PATH_PREFIX + path, new CacheInvalidationJob(path), null, new Date(System.currentTimeMillis() + INTERVAL));
        	log.info("scheduler 1 invalidator scheduled");
        	
        	if (!"".equals(flushUri2) && flushUri2 != null) {
        		scheduler.fireJobAt(SCHEDULER_PATH_PREFIX2 + path, new CacheInvalidationJob(path), null, new Date(System.currentTimeMillis() + INTERVAL));
        		log.info("scheduler 2 invalidator scheduled");
        	}
        } catch (Exception e) {
            log.error("VTKDataCacheInvalidator: Cannot add path: " + path);
        }
    }
    
    public void addPath(String path, boolean immediate) {
        if (immediate) {
            new CacheInvalidationJob(path).execute(null);
        } else {
            addPath(path);
        }
    }
    
    public void invalidateCache(Collection<String> paths) {
    }
}
