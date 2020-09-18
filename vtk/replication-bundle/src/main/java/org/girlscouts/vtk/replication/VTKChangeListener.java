package org.girlscouts.vtk.replication;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.observation.ExternalResourceChangeListener;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(immediate = true)
@Designate(ocd = VTKChangeListener.Config.class)
public class VTKChangeListener implements EventHandler, ResourceChangeListener, ExternalResourceChangeListener {
    private static final Logger log = LoggerFactory.getLogger(VTKChangeListener.class);

    @Reference
    private VtkUtil vtkUtil;

    private ServiceRegistration registration;

    @Activate
    @SuppressWarnings({"squid:S1149","deprecation"})
    protected void activate(BundleContext context, Map<String, Object> config) {
        try {
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
            if (!monitorPaths.isEmpty()) {
                log.warn("LDAP-style path filter detected, so a legacy event-based listener will be registered. " + "Consider using a list of paths instead to improve performance.");
                Dictionary<String, Object> properties = new Hashtable<>();
                properties.put(EventConstants.EVENT_TOPIC, new String[]{SlingConstants.TOPIC_RESOURCE_CHANGED, SlingConstants.TOPIC_RESOURCE_ADDED, SlingConstants.TOPIC_RESOURCE_REMOVED, SlingConstants.PROPERTY_CHANGED_ATTRIBUTES, SlingConstants.PROPERTY_ADDED_ATTRIBUTES, SlingConstants.PROPERTY_REMOVED_ATTRIBUTES});
                properties.put(EventConstants.EVENT_FILTER, (String[]) monitorPaths.toArray());
                registration = (ServiceRegistration) context.registerService(String.valueOf(EventHandler.class), this, properties);
            } else {
                Dictionary<String, Object> properties = new Hashtable<>();
                properties.put(ResourceChangeListener.PATHS, config.get(ResourceChangeListener.PATHS));
                properties.put(ResourceChangeListener.CHANGES, config.get(ResourceChangeListener.CHANGES));
                registration = (ServiceRegistration) context.registerService(String.valueOf(ResourceChangeListener.class), this, properties);
            }
            log.debug("Activated");
        }catch(Exception e){
            log.error("Error Occured while activating VTKChangeListener: ",e);
        }
    }

    @Deactivate
    protected void deactivate() {
        registration.unregister();
    }

    @Override
    public void handleEvent(final Event event) {
        log.debug("handleEvent is called");
        // Get the required information from the event.
        final String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);
        final String changedattrs = (String) event.getProperty(SlingConstants.PROPERTY_CHANGED_ATTRIBUTES);
        final String removedattrs = (String) event.getProperty(SlingConstants.PROPERTY_REMOVED_ATTRIBUTES);
        final String addedattrs = (String) event.getProperty(SlingConstants.PROPERTY_ADDED_ATTRIBUTES);
        log.debug("path: {}, change attr: {}, removed attr: {}, added attr: {}", path, changedattrs, removedattrs,addedattrs);
    }

    @Override
    public void onChange(List<ResourceChange> changes) {
        log.debug("onChange is called");
        for (ResourceChange change : changes) {
            log.debug("Change Path:{}", change.getPath());
            log.debug("Change Type:{}", change.getType());
        }
    }
    @ObjectClassDefinition(name="VTK Data Change Listener")
    public static @interface Config {


    }
}