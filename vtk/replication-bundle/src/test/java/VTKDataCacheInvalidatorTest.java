import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.Repository;

import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.testing.mock.jcr.MockJcr;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class VTKDataCacheInvalidatorTest {
    static class MockScheduler implements Scheduler {

        public void addJob(String arg0, Object arg1,
                Map<String, Serializable> arg2, String arg3, boolean arg4)
                throws Exception {
            // Do nothing
        }

        public void addPeriodicJob(String arg0, Object arg1,
                Map<String, Serializable> arg2, long arg3, boolean arg4)
                throws Exception {
            // Do nothing
        }

        public void addPeriodicJob(String arg0, Object arg1,
                Map<String, Serializable> arg2, long arg3, boolean arg4,
                boolean arg5) throws Exception {
            // Do nothing
        }

        public void fireJob(Object arg0, Map<String, Serializable> arg1)
                throws Exception {
            // Do nothing
        }

        public boolean fireJob(Object arg0, Map<String, Serializable> arg1,
                int arg2, long arg3) {
            // Do nothing
            return false;
        }

        public void fireJobAt(String name, Object job,
                Map<String, Serializable> config, Date date) throws Exception {
            long period = date.getTime() - System.currentTimeMillis();
            Thread.sleep(period);
            ((Job)job).execute(null);
        }

        public boolean fireJobAt(String arg0, Object arg1,
                Map<String, Serializable> arg2, Date arg3, int arg4, long arg5) {
            // Do nothing
            return false;
        }

        public void removeJob(String arg0) throws NoSuchElementException {
            // Do nothing
        }
        
    }
    
    private Scheduler scheduler;
    private long beginTime, endTime;

    @Rule
    public final OsgiContext context = new OsgiContext();

    @Before
    public void init() {
        //Repository repository = MockJcr.newRepository();
        //context.registerInjectActivateService(repository);
        scheduler = new MockScheduler();
    }
    
    @Test
    public void testAddPath() throws Exception {
        Job job = new Job() {
            public void execute(JobContext ctx) {
                endTime = System.currentTimeMillis();
            }
        };
        beginTime = System.currentTimeMillis();
        scheduler.fireJobAt("Name", job, null, new Date(beginTime + 1000));
        Thread.sleep(2000);

        long period = endTime - beginTime;
        assertTrue("Waiting period is not within 1 second: " + period, period > 800 && period < 1200);
    }

}
