package org.girlscouts.vtk.replication;

import java.util.ArrayList;
import java.util.List;

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
import org.girlscouts.vtk.ejb.SessionFactory;
import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.girlscouts.vtk.utils.VtkUtil;
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
    private static final String[] MONITOR_PATHS = { Constants.ROOT_PATH, Constants.DAM_PATH };
    private static final Logger log = LoggerFactory
            .getLogger(ReplicationManager.class);

    @Reference
    private SlingRepository repository;
    
    @Reference
    private TroopHashGenerator troopHashGenerator;
    
    @Reference
    private VTKDataCacheInvalidator cacheInvator;
     
    @Reference
    private Replicator replicator;

    @Reference
   private SessionFactory sessionFactory;
    
    // TODO: should I leave this?
    // All methods called here are static, but keeping this reference might help
    // make VTKUtil initialize before this component. 
    @Reference
    private VtkUtil vtkUtil;

    private Session session;
    private ObservationManager manager;
    private List<EventListener> listeners;

    public Session getSession() {
        return this.session;
    }

    @Activate
    protected void activate(ComponentContext context) throws Exception {
	
        listeners = new ArrayList<EventListener>();
        
        // Login repository
       session = repository.loginAdministrative(null);
       // session = sessionFactory.getSession();
        
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
        if (repository.getDescriptor(Repository.OPTION_OBSERVATION_SUPPORTED)
                .equals("true")) {
            manager = session.getWorkspace().getObservationManager();
            
            for (String path : monitorPaths) {
                EventListener listener = new NodeListener(session, replicator, troopHashGenerator, cacheInvator, yearPlanBase);
                listeners.add(listener);
                manager.addEventListener(listener, Constants.PROPERTY_UPDATE | Event.NODE_REMOVED | Event.NODE_MOVED,
                        path, true, null, Constants.PRIMARY_TYPES, true);
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
            }
        }

        if (session != null) {
            session.logout();
            session = null;
        }
    }
}
