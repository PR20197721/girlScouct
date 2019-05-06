package org.girlscouts.vtk.osgi.component.impl;

import com.day.cq.replication.*;
import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.sling.api.resource.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

// Policy set to REQUIRE to make this component disable by default.
// We turn on this component in author mode by adding pid in config nodes.
// Ref: http://www.computepatterns.com/43/osgi-component-in-aem-that-is-active-only-in-specific-run-mode-say-publish/
// TODO: this is not working fix it.
//@Component(metatype=true, policy=ConfigurationPolicy.REQUIRE)
@Component(label = "Resources 2 Replication Listener", metatype = true, immediate = true)
@Service(EventHandler.class)
@Properties({@Property(name = Constants.SERVICE_DESCRIPTION, value = "Listen to Resources 2 page replication events and help to clear cache."), @Property(name = Constants.SERVICE_VENDOR, value = "Girl Scouts"), @Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)})
public class Resources2ReplicationListener implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(Resources2ReplicationListener.class);
    private static final String RESOURCES_PAGE_PATTERN_STRING = "/content/vtk-resources2/[^/]+/[^/]+/resources2";
    private static final String VTKCONTENT_PAGE_PATTERN_STRING = "/content/vtkcontent/";
    private static final Pattern RESOURCES_PAGE_PATTERN = Pattern.compile(RESOURCES_PAGE_PATTERN_STRING);
    private static final String REPLICATION_ROOT = "/vtkreplication";
    private ResourceResolver rr;
    private boolean shouldHandle;
    private ReplicationOptions options;
    @Reference
    private SlingSettingsService slingSettingsService;
    @Reference
    private Replicator replicator;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    @SuppressWarnings("deprecation")
    public void init() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
        shouldHandle = slingSettingsService.getRunModes().contains("author");
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            options = new ReplicationOptions();
            options.setFilter(new AgentFilter() {
                public boolean isIncluded(Agent agent) {
                    return agent.getId() != null && agent.getId().startsWith("vtkreplication");
                }
            });
        } catch (LoginException e) {
            log.error("Cannot log into repository.");
        }
    }

    public void handleEvent(Event event) {
        // TODO: This component should disable itself if not in author mode.
        // Simple runmode based on/off is not working, using this shouldHandle instead.
        if (!shouldHandle) {
            return;
        }
        ReplicationAction action = ReplicationAction.fromEvent(event);
        String path = action.getPath();
        if (RESOURCES_PAGE_PATTERN.matcher(path).matches() || path.startsWith(VTKCONTENT_PAGE_PATTERN_STRING)) {
            log.debug("MATCHES. Path: %s Regex: %s", path, RESOURCES_PAGE_PATTERN_STRING);
            String replicationPath = REPLICATION_ROOT + path;
            log.debug("Replication path %s", replicationPath);
            try {
                Session session = rr.adaptTo(Session.class);
                JcrUtils.getOrCreateByPath(replicationPath, "nt:unstructured", session);
                replicator.replicate(session, ReplicationActionType.ACTIVATE, replicationPath, options);
            } catch (RepositoryException e) {
                log.error("RepositoryException when replication node: %s", path);
            } catch (ReplicationException e) {
                log.error("ReplicationException when replication node: %s", path);
            }

        } else {
            log.debug("Does not match. Path: %s Regex: %s", path, RESOURCES_PAGE_PATTERN_STRING);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        try {
            if (rr != null) {
                rr.close();
            }
        } catch (Exception e) {
            log.error("Exception is thrown closing resource resolver: ", e);
        }
    }
}
