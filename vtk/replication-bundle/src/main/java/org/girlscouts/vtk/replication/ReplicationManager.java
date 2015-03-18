package org.girlscouts.vtk.replication;

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
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;

@Component(metatype=true, immediate=true)
@Service(value=ReplicationManager.class)
@Properties ({
    @Property(name=Constants.REPLICATION_INTERVAL_PROPERTY, longValue = Constants.REPLICATION_INTERVAL, description="Replication Interval, in seconds."),
    @Property(name=Constants.SIBLING_SERVERS_PROPERTY, description="Sibling servers, separated by space. Node changes will be replicated to these servers")
})
public class ReplicationManager {
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH };
    private static final Logger log = LoggerFactory
            .getLogger(ReplicationManager.class);

    @Reference
    private SlingRepository repository;
    
     
    @Reference
    private Replicator replicator;

    private Session session;
    private ObservationManager manager;
    private EventListener listener;

    public Session getSession() {
        return this.session;
    }

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        // Login repository
        session = repository.loginAdministrative(null);

        // Setup the listener
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED)
                .equals("true")) {
            manager = session.getWorkspace().getObservationManager();
            final String[] types = { Constants.PRIMARY_TYPE };
            listener = new NodeListener(session, replicator);

            for (int i = 0; i < MONITOR_PATHS.length; i++) {
                manager.addEventListener(listener, Constants.PROPERTY_UPDATE | Event.NODE_REMOVED | Event.NODE_MOVED,
                        MONITOR_PATHS[i], true, null, types, true);
            }
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

        if (session != null) {
            session.logout();
            session = null;
        }
    }
}
