package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.httpclient.HttpClient;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(
    value = VTKDataCacheInvalidator.class
)
public class VTKDataCacheInvalidator implements Job {
    private static final Logger log = LoggerFactory.getLogger(VTKReplicationReceiver.class);
    
    // Interval for the next invalidation, in milliseconds.
    private static final int INTERVAL = 100;
    // Interval for the cleanup, in seconds.
    private static final int PERIODIIC_INTERVAL = 10; 
    private static final String JOB_NAME = "VTKDataCacheInvalidatorJob";
    private static final String PERIODIC_JOB_NAME = "VTKDataCacheInvalidatorPeriodicJob";
    private static final String FLUSH_NODE = "/etc/replication/agents.publish/flush/jcr:content";
    private static final String FLUSH_PROPERTY = "transportUri";

    protected Set<String> paths;
    protected Object lock;
    
    protected HttpClient httpClient;
    protected String flushUri;

    @Reference
    protected Scheduler scheduler;
    
    @Reference
    protected SlingRepository repository;
    
    @Activate
    public void init() {
        lock = new Object();
        paths = new HashSet<String>();
        httpClient = new HttpClient();
        try {
            Session session = repository.loginAdministrative(null);
            flushUri = session.getNode(FLUSH_NODE).getProperty(FLUSH_PROPERTY).getString();
            session.logout();
            try {
                // Schedule a periodic cleanup process so there is a remedy a missed invalidation.
                scheduler.addPeriodicJob(PERIODIC_JOB_NAME, this, null, PERIODIIC_INTERVAL, true);
            } catch (Exception e) {
                log.error("VTKDataCacheInvalidator: Cannot schedule periodic.");
            }
            log.info("Started.");
        } catch (RepositoryException e) {
            log.error("VTKDataCacheInvalidator: RepositoryException while initializing.");
            e.printStackTrace();
        } 
    }
    
    @Deactivate
    // Remove the scheduled job and try to invalidate the job right away.
    public void deactivate() {
        scheduler.removeJob(JOB_NAME);
        execute(null);
    }
    
    public void addPath(String path) {
        synchronized(lock) {
            // If paths set is empty, assume nothing is scheduled. Schedule a new task.
            if (paths.isEmpty()) {
                schedule();
            }
            paths.add(path);
        }
    }
    
    // The method is executed when scheduled time arrives.
    public void execute(JobContext ctx) {
        log.info("VTKDataCacheInvalidator: Execute timestamp: " + System.currentTimeMillis());
        Set<String> _paths;
        synchronized(lock) {
            _paths = paths;
            if (!paths.isEmpty()) {
                paths = new HashSet<String>();
            }
        }
        
        if (!_paths.isEmpty()) {
            invalidateCache(_paths);
        }
    }

    protected void schedule() {
        try {
            scheduler.fireJobAt(JOB_NAME, this, null, new Date(System.currentTimeMillis() + INTERVAL));
           
        } catch (Exception e) {
            log.error("VTKDataCacheInvalidator: Job not scheduled. Execute right now.");
            execute(null);
        }
    }

    public void invalidateCache(Collection<String> paths) {
        // Heavy lifting that does not require synchronization.
        log.info("VTKDataCacheInvalidator: ==================Invalidating cache: ");
        GetMethod get = new GetMethod(flushUri);
        for (String path : paths) {
            log.debug("VTKDataCacheInvalidator: Path: " + path);
            get.setRequestHeader("CQ-Action", "Delete");
            get.setRequestHeader("CQ-Handle", path);
            get.setRequestHeader("CQ-Path", path);
            try {
                int resStatus = httpClient.executeMethod(get);
                if (resStatus != 200) {
                    log.error("VTKDataCacheInvalidator: Cannot invalidate this path: " + path + ". Putting back to the queue.");
                    //addPath(path);
                } else {
                    log.info("VTKDataCacheInvalidator: Successfully invalidate the cache: " + path);
                }
                
            } catch (Exception e) {
            	
                log.info("VTKDataCacheInvalidator: Cannot invalidate this path: " + path + ". Putting back to the queue.");
                //addPath(path);
            } finally{
            	get.releaseConnection();
            }
        }
        log.info("VTKDataCacheInvalidator: Invalidating cache done ==================");
    }
}
