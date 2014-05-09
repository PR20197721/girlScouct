package org.girlscouts.vtk.replication;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
@Service
public class VtkNodeListener implements EventListener, Constants {
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH };
    private static final int PROPERTY_UPDATE = Event.PROPERTY_ADDED
            | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    private static final Logger log = LoggerFactory
            .getLogger(VtkNodeListener.class);

    @Reference
    private SlingRepository repository;

    private Session session;
    private ObservationManager manager;
    private EventListener listener;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        String mode = context.getBundleContext().getProperty("mode");
        if (mode == null) {
            log.error("Cannot find run mode. Quit");
            return;
        }
        if (mode.equals("author") && !mode.equals("publish")) {
            log.error("Wrong run mode: " + mode + ". Quit");
            return;
        }

        session = repository.loginAdministrative(null);
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED)
                .equals("true")) {
            manager = session.getWorkspace().getObservationManager();
            final String[] types = { Constants.PRIMARY_TYPE };

            if (mode.equals("author")) {
                for (int i = 0; i < MONITOR_PATHS.length; i++) {
                    manager.addEventListener(
                            new AuthorVtkNodeListener(session),
                            PROPERTY_UPDATE, MONITOR_PATHS[i], true, null,
                            types, false);
                }
            } else {
                for (int i = 0; i < MONITOR_PATHS.length; i++) {
                    manager.addEventListener(
                            new PublishVtkNodeListener(session),
                            PROPERTY_UPDATE, MONITOR_PATHS[i], true, null,
                            types, false);
                }
            }
        } else {
            log.error("Listeners not added.");
        }
    }

    public void onEvent(EventIterator iter) {
        listener.onEvent(iter);
    }

}
