package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
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
    private static final String JOB_NAME = "VTKDataCacheInvalidatorJob";

    protected Set<String> paths;
    protected Object lock;

    @Reference
    protected Scheduler scheduler;
    
    @Activate
    public void init() {
        lock = new Object();
        paths = new HashSet<String>();
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
                paths.add(path);
                schedule();
            }
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
        // TODO heavy lifting that does not require synchronization.
        System.out.println("==================Invalidating cache: ");
        for (String path : paths) {
            System.out.println(path);
        }
        System.out.println("Invalidating cache done ==================");
    }
    
}
