package org.girlscouts.vtk.replication;

import com.day.cq.replication.Replicator;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(metatype = true, immediate = true)
@Service(value = ReplicationManager.class)
@Properties({@Property(name = Constants.REPLICATION_INTERVAL_PROPERTY, longValue = Constants.REPLICATION_INTERVAL, description = "Replication Interval, in seconds."), @Property(name = Constants.SIBLING_SERVERS_PROPERTY, description = "Sibling servers, separated by space. Node changes will be replicated to these servers")})
public class ReplicationManager {
    private static final String[] MONITOR_PATHS = {Constants.ROOT_PATH, Constants.DAM_PATH};
    private static final Logger log = LoggerFactory.getLogger(ReplicationManager.class);
    @Reference
    private SlingRepository repository;
    @Reference
    private TroopHashGenerator troopHashGenerator;
    @Reference
    private VTKDataCacheInvalidator cacheInvator;
    @Reference
    private Replicator replicator;
    // TODO: should I leave this?
    // All methods called here are static, but keeping this reference might help
    // make VTKUtil initialize before this component. 
    @Reference
    private VtkUtil vtkUtil;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    private ObservationManager manager;
    private List<EventListener> listeners;
    private ResourceResolver rr;

    @Activate
    protected void activate(ComponentContext context) throws Exception {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        listeners = new ArrayList<EventListener>();
        // Generate paths to monitor
        String year = Integer.toString(vtkUtil._getCurrentGSYear());
        List<String> monitorPaths = new ArrayList<String>();
        // Add /vtk(year)
        String yearPlanBase = vtkUtil._getYearPlanBase(null, null);
        monitorPaths.add(yearPlanBase);
        // Add /vtk(previous year)
        String previousYear = Integer.toString(vtkUtil.getCurrentGSYear() - 1);
        monitorPaths.add("/vtk" + previousYear + "/");
        // Add /content/dam/girlscouts-vtk/troop-data(year)
        monitorPaths.add(Constants.DAM_PATH + year);
        // Add /vtkreplication for resources2
        monitorPaths.add("/vtkreplication");
        // Setup the listener
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED).equals("true")) {
            try {
                rr = resolverFactory.getServiceResourceResolver(resolverParams);
                Session session = rr.adaptTo(Session.class);
                manager = session.getWorkspace().getObservationManager();
                for (String path : monitorPaths) {
                    EventListener listener = new NodeListener(session, replicator, troopHashGenerator, cacheInvator, yearPlanBase);
                    listeners.add(listener);
                    manager.addEventListener(listener, Constants.PROPERTY_UPDATE | Event.NODE_REMOVED | Event.NODE_MOVED, path, true, null, Constants.PRIMARY_TYPES, true);
                }
            } catch (Exception e) {
                log.error("Error Occurred: ", e);
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
                for (EventListener listener : listeners) {
                    manager.removeEventListener(listener);
                }
            } catch (RepositoryException e) {
                log.error("Error deactivating listeners");
            }finally {
                try {
                    if (rr != null) {
                        rr.close();
                    }
                } catch (Exception e) {
                    log.error("Exception is thrown closing resource resolver: ", e);
                }
            }
        }
    }

    public Session getSession() {
        return rr.adaptTo(Session.class);
    }
}
