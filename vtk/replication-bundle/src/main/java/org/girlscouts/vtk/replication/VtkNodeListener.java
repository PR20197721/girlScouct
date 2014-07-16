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
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Replicator;

@Component(metatype=true, immediate=true)
@Service
@Properties ({
    @Property(name=Constants.MODE_PROPERTY, description="author/publish"),
    @Property(name=Constants.FROM_PUBLISHER_PROPERTY, description="This publisher ID")
})
public class VtkNodeListener implements Constants {
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH };
    private static final int PROPERTY_UPDATE = Event.PROPERTY_ADDED
            | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    private static final Logger log = LoggerFactory
            .getLogger(VtkNodeListener.class);

    @Reference
    private SlingRepository repository;
    
    @Reference
    private Replicator replicator;

    private Session session;
    private ObservationManager manager;
    private EventListener nodeUpdatedListener;
    private EventListener nodeRemovedListener;
    
    @Activate
    protected void activate(ComponentContext context) throws Exception {
        @SuppressWarnings("rawtypes")
        final Dictionary dict = context.getProperties();
        String mode = (String)dict.get(Constants.MODE_PROPERTY);
        if (mode == null) {
            log.error("Cannot find run mode. Quit");
            return;
        }
        if (!mode.equals("author") && !mode.equals("publish")) {
            log.error("Wrong run mode: " + mode + ". Quit");
            return;
        }

        session = repository.loginAdministrative(null);
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED)
                .equals("true")) {
            manager = session.getWorkspace().getObservationManager();
            final String[] types = { Constants.PRIMARY_TYPE };

            if (mode.equals("author")) {
                this.nodeUpdatedListener = new AuthorVtkNodeListener(session, replicator);
                this.nodeRemovedListener = new AuthorVtkNodeRemovedListener(session, replicator);
                for (int i = 0; i < MONITOR_PATHS.length; i++) {
                    manager.addEventListener(this.nodeUpdatedListener, PROPERTY_UPDATE,
                            MONITOR_PATHS[i], true, null, types, false);
                }
                // Author should listen to the grave yard for node removal.
                manager.addEventListener(this.nodeRemovedListener, Event.NODE_ADDED,
                        Constants.NODE_GRAVEYARD_ROOT, true, null, types, false);
            } else {
                String publishId = (String)dict.get(Constants.FROM_PUBLISHER_PROPERTY);
                if (publishId == null) {
                    publishId = "publish";
                }
                this.nodeUpdatedListener = new PublishVtkNodeListener(session, replicator, publishId);
                this.nodeRemovedListener = new PublishVtkNodeRemovedListener(session, replicator, publishId);
                for (int i = 0; i < MONITOR_PATHS.length; i++) {
                    manager.addEventListener(this.nodeUpdatedListener, PROPERTY_UPDATE,
                            MONITOR_PATHS[i], true, null, types, false);
                    manager.addEventListener(this.nodeRemovedListener, Event.NODE_REMOVED,
                            MONITOR_PATHS[i], true, null, types, false);
                }
            }

        } else {
            log.error("Listeners not added.");
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        if (manager != null) {
            try {
                manager.removeEventListener(this.nodeRemovedListener);
                manager.removeEventListener(this.nodeUpdatedListener);
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
