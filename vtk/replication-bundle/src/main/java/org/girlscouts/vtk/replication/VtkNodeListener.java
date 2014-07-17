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

import com.day.cq.replication.Replicator;

@Component(metatype=true, immediate=true)
@Properties ({
    @Property(name=Constants.MODE_PROPERTY, description="author/publish"),
    @Property(name=Constants.FROM_PUBLISHER_PROPERTY, description="This publisher ID")
})
public class VtkNodeListener {
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH };
    private static final Logger log = LoggerFactory
            .getLogger(VtkNodeListener.class);

    @Reference
    private SlingRepository repository;
    
    @Reference
    private Replicator replicator;
    
    @Reference
    private Scheduler scheduler;
    private boolean isScheduled = false;

    private Session session;
    private ObservationManager manager;
    private EventListener listener;
    
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
                this.listener = new AuthorVtkNodeListener(session, replicator);
                // On author, listen to update events only, on replication root
                manager.addEventListener(this.listener, Constants.PROPERTY_UPDATE,
                        Constants.NODE_REPLICATION_ROOT, true, null, types, false);
            } else {
                String publishId = (String)dict.get(Constants.FROM_PUBLISHER_PROPERTY);
                if (publishId == null) {
                    publishId = "publish";
                }
                this.listener = new PublishVtkNodeListener(session, replicator, publishId);
                // On publish, listen to every interested path, both update and remove
                for (int i = 0; i < MONITOR_PATHS.length; i++) {
                    manager.addEventListener(this.listener, Constants.PROPERTY_UPDATE | Event.NODE_REMOVED,
                            MONITOR_PATHS[i], true, null, types, false);
                }
                
                // TODO: make interval configurable
                scheduler.addPeriodicJob(Constants.JOB_NAME, this.listener, null, 60L, true);
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
                manager.removeEventListener(this.listener);
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
