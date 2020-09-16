package org.girlscouts.vtk.replication;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Service(
    value = VTKDataCacheInvalidator.class
)
public class VTKDataCacheInvalidator {
    private static final Logger log = LoggerFactory.getLogger(VTKReplicationReceiver.class);
    
    // Interval for the next invalidation, in milliseconds.
    private static final int INTERVAL = 4000;
    private static final String FLUSH_NODE = "/etc/replication/agents.publish/flush/jcr:content";
    //private static final String FLUSH_NODE2 = "/etc/replication/agents.publish/flush2/jcr:content";
    private static final String FLUSH_PROPERTY = "transportUri";
    private static final String SCHEDULER_PATH_PREFIX = "VTK_PATH_";
    //private static final String SCHEDULER_PATH_PREFIX2 = "VTK_PATH2_";

    protected HttpClient httpClient;
    protected MultiThreadedHttpConnectionManager connectionManager;
    protected String flushUri;
    //protected String flushUri2 = "";

    @Reference
    protected Scheduler scheduler;
    
    @Reference
    protected SlingRepository repository;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        log.debug("Activated");
    }
    
    @Activate
    public void init() {
    	this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        connectionManager = new MultiThreadedHttpConnectionManager();
        httpClient = new HttpClient(connectionManager);
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
        	flushUri = session.getNode(FLUSH_NODE).getProperty(FLUSH_PROPERTY).getString();
            session.logout();
            log.info("Started.");
        } catch (RepositoryException e) {
            log.error("VTKDataCacheInvalidator: RepositoryException while initializing.", e);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
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

    private class CacheInvalidationJob implements Job {
        private String path;

        public CacheInvalidationJob(String path) {
            this.path = path;
        }

        public void execute(JobContext context) {
            log.info("VTKDataCacheInvalidator: ================== Replication Invalidating cache: ");
            try{
                HttpClient client = new HttpClient();
                PostMethod post = new PostMethod(flushUri);
                post.setRequestHeader("CQ-Action", "Delete");
                post.setRequestHeader("CQ-Handle", path);
                StringRequestEntity body = new StringRequestEntity(path,null,null);
                post.setRequestEntity(body);
                post.setRequestHeader("Content-length", String.valueOf(body.getContentLength()));
                try {
                    int resStatus = client.executeMethod(post);
                    if (resStatus != 200) {
                        log.error("VTKDataCacheInvalidator: Cannot invalidate this path: " + path);
                    } else {
                        log.info("VTKDataCacheInvalidator: Successfully invalidate the cache: " + path);
                    }
                } catch (Exception e) {
                    log.info("VTKDataCacheInvalidator: Cannot invalidate this path: " + path + ". Do nothing.");
                } finally{
                    post.releaseConnection();
                }
                //log the results
                log.info("result: " + post.getResponseBodyAsString());
            }catch(Exception e){
                log.error("Flushcache servlet exception: " + e.getMessage());
            }

        }
}
