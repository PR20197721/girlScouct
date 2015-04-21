import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.sling.commons.scheduler.Job;
import org.apache.sling.commons.scheduler.JobContext;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.testing.mock.jcr.MockJcr;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.girlscouts.vtk.replication.VTKDataCacheInvalidator;
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
    
    static class MockSlingRepository implements SlingRepository {

        public String[] getDescriptorKeys() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isStandardDescriptor(String key) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean isSingleValueDescriptor(String key) {
            // TODO Auto-generated method stub
            return false;
        }

        public Value getDescriptorValue(String key) {
            // TODO Auto-generated method stub
            return null;
        }

        public Value[] getDescriptorValues(String key) {
            // TODO Auto-generated method stub
            return null;
        }

        public String getDescriptor(String key) {
            // TODO Auto-generated method stub
            return null;
        }

        public Session login(Credentials credentials, String workspaceName)
                throws LoginException, NoSuchWorkspaceException,
                RepositoryException {
            // TODO Auto-generated method stub
            return null;
        }

        public Session login(Credentials credentials) throws LoginException,
                RepositoryException {
            // TODO Auto-generated method stub
            return null;
        }

        public Session login(String workspaceName) throws LoginException,
                NoSuchWorkspaceException, RepositoryException {
            // TODO Auto-generated method stub
            return null;
        }

        public Session login() throws LoginException, RepositoryException {
            // TODO Auto-generated method stub
            return null;
        }

        public String getDefaultWorkspace() {
            // TODO Auto-generated method stub
            return null;
        }

        public Session loginAdministrative(String workspace)
                throws RepositoryException {
            return MockJcr.newSession();
        }

    }
    
    // Test the Mock Scheduler
    private static final int MOCK_INTERVAL = 1000;
    private long beginTime, endTime;
    //@Test
    public void testMockScheduler() throws Exception {
        Scheduler scheduler = new MockScheduler();
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
    
    @Test
    public void testCacheInvalidator() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException, RepositoryException {
        Repository repository = new MockSlingRepository();
        Scheduler scheduler = new MockScheduler();
        VTKDataCacheInvalidator invalidator = new VTKDataCacheInvalidator();
        Field[] fields = invalidator.getClass().getDeclaredFields();
        setField(invalidator, "scheduler", scheduler);
        setField(invalidator, "repository", repository);
        
        invalidator.init();
    }
    
    private void setField(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(false);
    }

}
