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
    private static final int INTERVAL = 1000;
    private static final String JOB_NAME = "VTKDataCacheInvalidatorJob";
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
    public void init() throws RepositoryException {
        lock = new Object();
        paths = new HashSet<String>();
        httpClient = new HttpClient();
        Session session = repository.loginAdministrative(null);
        flushUri = session.getNode(FLUSH_NODE).getProperty(FLUSH_PROPERTY).getString();
        session.logout();
    }
    
    @Deactivate
    // Remove the scheduled job and try to invalidate the job right away.
    public void deactivate() {
        scheduler.removeJob(JOB_NAME);
        execute(null);
    }
    
    public void addPath(String path) {
    	System.out.println("ADDING PATH");
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
        log.debug("Execute timestamp: " + System.currentTimeMillis());
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
            log.error("Job not scheduled. Execute right now.");
            execute(null);
        }
    }

    public void invalidateCache(Collection<String> paths) {
        // Heavy lifting that does not require synchronization.
        log.debug("==================Invalidating cache: ");
        GetMethod get = new GetMethod(flushUri);
        for (String path : paths) {
            log.debug("Path: " + path);
            get.setRequestHeader("CQ-Action", "Delete");
            get.setRequestHeader("CQ-Handle", path);
            get.setRequestHeader("CQ-Path", path);

            try {
                int resStatus = httpClient.executeMethod(get);
                if (resStatus != 200) {
                    log.error("Cannot invalidate this path: " + path + ". Putting back to the queue.");
                    addPath(path);
                } else {
                    log.debug("Successfully invalidate the cache: " + path);
                }
                
            } catch (Exception e) {
            	
                log.debug("Cannot invalidate this path: " + path + ". Putting back to the queue.");
                addPath(path);
            } finally{
            	get.releaseConnection();
            }
        }
        log.debug("Invalidating cache done ==================");
    }
}
