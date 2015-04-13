package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.Scheduler;
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

    // If invalidation is scheduled
    protected boolean scheduled;

    protected Collection<String> paths;
    protected Object lock;

    @Reference
    protected Scheduler scheduler;
    
    @Activate
    public void init() {
        lock = new Object();
        paths = new HashSet<String>();
        scheduled = false;
    }
    
    public void addPath(String path) {
        synchronized(lock) {
            paths.add(path);
            // If nothing is scheduled, schedule a new task.
            if (!scheduled) {
                scheduled = true;
                schedule();
            }
        }
    }
    
    public void execute(JobContext ctx) {
        log.debug("Execute timestamp: " + System.currentTimeMillis());
        Collection<String> _paths;
        synchronized(lock) {
            _paths = paths;
            if (!paths.isEmpty()) {
                paths = new HashSet<String>();
            }
            scheduled = false;
        }
        
        if (!_paths.isEmpty()) {
            invalidateCache(_paths);
        }
    }

    protected void schedule() {
        try {
            scheduler.fireJobAt(null, this, null, new Date(System.currentTimeMillis() + INTERVAL));
        } catch (Exception e) {
            log.error("Job not scheduled.");
        }
    }

    public void invalidateCache(Collection<String> paths) {
        // TODO heavy lifting that does not require synchronization.
        System.out.println("==================Invalidating cache: ");
        for (String path : paths) {
            System.out.println(path);
        }
        System.out.println("Invalidating cache done ==================");
    }
    
}
