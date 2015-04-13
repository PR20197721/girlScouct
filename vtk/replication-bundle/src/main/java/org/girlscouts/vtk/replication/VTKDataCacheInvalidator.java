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

    // Go to sleep if nothing happens after seconds of this value.
    protected static final int COUNT_DOWN = 10; 
    
    // The count down value
    protected int countDown;

    protected Collection<String> paths;
    protected Object lock;

    @Reference
    protected Scheduler scheduler;
    
    @Activate
    public void init() {
        lock = new Object();
        paths = new HashSet<String>();
    }
    
    public void addPath(String path) {
        synchronized(lock) {
            paths.add(path);
            // If nothing is scheduled, schedule a new task.
            if (countDown <= 0) {
                countDown = COUNT_DOWN;
                schedule();
            }
        }
    }
    
    public void execute(JobContext ctx) {
        System.out.println("Execute timestamp: " + System.currentTimeMillis());
        Collection<String> _paths;
        synchronized(lock) {
            _paths = paths;
            // If nothing comes in, reduce countDown.
            if (_paths.isEmpty()) {
                countDown--;
            } else {
                countDown = COUNT_DOWN;
                paths = new HashSet<String>();
            }

            System.out.println("countDown = " + countDown);
            if (countDown > 0) {
                schedule();
            }
        }
        
        if (!_paths.isEmpty()) {
            invalidateCache(_paths);
        }
    }

    protected void schedule() {
        try {
            scheduler.fireJobAt(null, this, null, new Date(System.currentTimeMillis() + 1000));
        } catch (Exception e) {
            log.error("Job not scheduled.");
        }
    }

    public void invalidateCache(Collection<String> paths) {
        // TODO
        System.out.println("==================Invalidating cache: ");
        for (String path : paths) {
            System.out.println(path);
        }
        System.out.println("Invalidating cache done ==================");
    }
    
}
