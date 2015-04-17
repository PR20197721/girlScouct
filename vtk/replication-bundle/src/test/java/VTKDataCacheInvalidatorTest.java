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
            long interval = date.getTime() - System.currentTimeMillis();
            Thread.sleep(interval);
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
    }
    
    // Test the Mock Scheduler
    private static final int MOCK_INTERVAL = 1000;
    @Test
    public void testAddPath() throws Exception {
        scheduler = new MockScheduler();
        Job job = new Job() {
            public void execute(JobContext ctx) {
                endTime = System.currentTimeMillis();
            }
        };
        beginTime = System.currentTimeMillis();
        scheduler.fireJobAt("Name", job, null, new Date(beginTime + MOCK_INTERVAL));
        Thread.sleep(MOCK_INTERVAL + 1000);

        long interval = endTime - beginTime;
        assertTrue("Waiting interval is not within 1 second: " + interval, 
                interval > MOCK_INTERVAL - 200 && interval < MOCK_INTERVAL + 200);
    }

}
