package org.girlscouts.vtk.replication;

import java.util.Dictionary;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(metatype=true, immediate=true)
@Properties ({
    @Property(name=Constants.REPLICATION_INTERVAL_PROPERTY, longValue = Constants.REPLICATION_INTERVAL, description="Replication Interval, in seconds."),
    @Property(name=Constants.SIBLING_SERVERS_PROPERTY, description="Sibling servers, separated by space. Node changes will be replicated to these servers")
})
public class VtkNodeReplicationManager {
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH };
    private static final Logger log = LoggerFactory
            .getLogger(VtkNodeReplicationManager.class);

    @Reference
    private SlingRepository repository;
    
    @Reference
    private Scheduler scheduler;
    private boolean isScheduled = false;

    private Session session;
    private ObservationManager manager;
    private EventListener listener;
    private Replicator replicator;

    private String[] siblings;
    
    @Activate
    protected void activate(ComponentContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        final Dictionary dict = context.getProperties();
        String siblingStr = (String)dict.get(Constants.SIBLING_SERVERS_PROPERTY);
        if (siblingStr == null) {
            log.error("No siblings. Nowhere to replicate. Quit");
            return;
        }
        siblings = siblingStr.split(" ");
        long replicationInterval = (Long)dict.get(Constants.REPLICATION_INTERVAL_PROPERTY);
        
        // Login repository
        session = repository.loginAdministrative(null);
        // Setup the Replicator
        replicator = new Replicator(session, siblings);

        // Setup the listener
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED)
                .equals("true")) {
            manager = session.getWorkspace().getObservationManager();
            final String[] types = { Constants.PRIMARY_TYPE };
            listener = new NodeListener(session, replicator);

            for (int i = 0; i < MONITOR_PATHS.length; i++) {
                manager.addEventListener(listener, Constants.PROPERTY_UPDATE | Event.NODE_REMOVED,
                        MONITOR_PATHS[i], true, null, types, true);
            }
            
            // Setup the scheduler
            scheduler.addPeriodicJob(Constants.JOB_NAME, replicator, null, replicationInterval, true);
            isScheduled = true;
        } else {
            log.error("Listeners not added.");
        }
        
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        // Unregister the listeners
        if (manager != null) {
            try {
                manager.removeEventListener(listener);
            } catch (RepositoryException e) {
                log.error("Error deactivating listeners");
            }
        }

        // Cancel the queue job on publish
        if (scheduler != null && isScheduled) {
            scheduler.removeJob(Constants.JOB_NAME);
            isScheduled = false;
        }
        
        if (session != null) {
            session.logout();
            session = null;
        }
    }
}
