package org.girlscouts.vtk.impl.helpers;

import java.util.regex.Pattern;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.framework.Constants;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.Agent;
import com.day.cq.replication.AgentFilter;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

// Policy set to REQUIRE to make this component disable by default.
// We turn on this component in author mode by adding pid in config nodes.
// Ref: http://www.computepatterns.com/43/osgi-component-in-aem-that-is-active-only-in-specific-run-mode-say-publish/
// TODO: this is not working fix it.
//@Component(metatype=true, policy=ConfigurationPolicy.REQUIRE)
@Component(label = "Resources 2 Replication Listener", metatype = true, immediate=true)
@Service(EventHandler.class)
@Properties({
    @Property(name = Constants.SERVICE_DESCRIPTION, value = "Listen to Resources 2 page replication events and help to clear cache."),
    @Property(name = Constants.SERVICE_VENDOR, value = "Girl Scouts"),
    @Property(name = EventConstants.EVENT_TOPIC, value = ReplicationAction.EVENT_TOPIC)
})
public class Resources2ReplicationListener implements EventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(Resources2ReplicationListener.class);

	private Session session;
	private boolean shouldHandle;
	private ReplicationOptions options;

	@Reference
    private SlingSettingsService slingSettingsService;
	
	@Reference
	private SlingRepository repository;
	
	@Reference
	private Replicator replicator;
	
	private static final String RESOURCES_PAGE_PATTERN_STRING = "/content/vtk-resources2/[^/]+/[^/]+/resources2";
	private static final String VTKCONTENT_PAGE_PATTERN_STRING = "/content/vtkcontent/";
	private static final Pattern RESOURCES_PAGE_PATTERN = Pattern.compile(RESOURCES_PAGE_PATTERN_STRING);
	private static final String REPLICATION_ROOT = "/vtkreplication";

	@Activate
	@SuppressWarnings("deprecation")
	public void init() {
		shouldHandle = slingSettingsService.getRunModes().contains("author");
		try {
			session = repository.loginAdministrative(null);
			
			options = new ReplicationOptions();
			options.setFilter(new AgentFilter() {
				public boolean isIncluded(Agent agent) {
					return agent.getId() != null && agent.getId().startsWith("vtkreplication");
				}
			});
		} catch (LoginException e) {
			log.error("Cannot log into repository.");
		} catch (RepositoryException e) {
			log.error("RepositoryException when trying to login repository.");
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

}
