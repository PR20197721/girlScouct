package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.girlscouts.vtk.helpers.TroopHashGenerator;
import org.girlscouts.vtk.replication.NodeEventCollector.NodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.AgentIdFilter;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class NodeListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(NodeListener.class);
    private Session session;
    private Replicator replicator;
    private ReplicationOptions syncOpts;
    private ReplicationOptions asyncOpts;
    private ReplicationOptions vtkDataOpts;
    private TroopHashGenerator troopHashGenerator;
    private VTKDataCacheInvalidator cacheInvalidator;
    private String yearPlanBase;
    private Pattern troopPattern;
    private Pattern councilInfoPattern;
    
    public NodeListener(Session session, Replicator replicator, 
            TroopHashGenerator troopHashGenerator, VTKDataCacheInvalidator cacheInvalidator, String yearPlanBase) {
        this.session = session;
        this.replicator = replicator;
        this.troopHashGenerator = troopHashGenerator;
        this.cacheInvalidator = cacheInvalidator;

        syncOpts = new ReplicationOptions();
        syncOpts.setFilter(new AgentIdRegexFilter("^" + Constants.VTK_AGENT_PREFIX + ".*"));
        syncOpts.setSuppressStatusUpdate(true);
        syncOpts.setSuppressVersions(true);
        syncOpts.setSynchronous(true);

        syncOpts = new ReplicationOptions();
        syncOpts.setFilter(new AgentIdRegexFilter("^" + Constants.VTK_AGENT_PREFIX + ".*"));
        syncOpts.setSuppressStatusUpdate(true);
        syncOpts.setSuppressVersions(true);
        syncOpts.setSynchronous(false);

        vtkDataOpts = new ReplicationOptions();
        vtkDataOpts.setFilter(new AgentIdFilter("flush"));
        vtkDataOpts.setSuppressStatusUpdate(true);
        vtkDataOpts.setSuppressVersions(true);
        
        this.yearPlanBase = yearPlanBase;
        // /vtk/603/troops/701G0000000uQzTIAU => 701G0000000uQzTIAU
        // yearPlanBase = /vtk2015/
        troopPattern = Pattern.compile(yearPlanBase + "[0-9]+/troops/([^/]+)");
        
        councilInfoPattern = Pattern.compile(yearPlanBase + "[0-9]+/councilInfo/.*");
    }

    
    public void onEvent(EventIterator iter) {

        Collection<NodeEvent> events = NodeEventCollector.getEvents(iter);
        String affectedTroop = null;
        String affectedCouncilInfo = null;

        for (NodeEvent event : events) {
            String path = event.getPath();
            if (path.endsWith("jcr:content")) {
                continue;
            }
            int type = event.getType();

            try {
                if (type == Constants.EVENT_UPDATE) {
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, syncOpts);
                } else if (type == Constants.EVENT_REMOVE){
                    replicator.replicate(session, ReplicationActionType.DELETE, path, syncOpts);
                } else {
                    log.warn("Unknown replication type. Do nothing. type = " + type + " path = " + path);
                }
            } catch (ReplicationException sre) {
            	// If synchronous replication does not work, for example, the target server is down,
            	// Put the event into the replication queue instead so the node will be replicated asynchronously.
            	log.warn("Exception while replicating node synchronously. Trying asynchronous replication. path = " + path);
            	try {
	                if (type == Constants.EVENT_UPDATE) {
	                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, asyncOpts);
	                } else if (type == Constants.EVENT_REMOVE){
	                    replicator.replicate(session, ReplicationActionType.DELETE, path, asyncOpts);
	                }
            	} catch (ReplicationException are) {
            	    log.error("Replication Exception still . Event not handled. path = " + path);
            	}
            }
                
            // Get the affected troop
            if (affectedTroop == null) {
                Matcher troopMatcher = troopPattern.matcher(path);
                while (troopMatcher.find()) {
                    affectedTroop = troopMatcher.group(1);
                    log.debug("Affected Troop found: " + affectedTroop);
                }
            }
            
            // Get the affected council info
            if (affectedCouncilInfo == null) {
                Matcher councilInfoMatcher = councilInfoPattern.matcher(path);
                if (councilInfoMatcher.matches()) {
                    affectedCouncilInfo = path;
                }
            }
        }
        
        // Found affected troop. Invalidate VTK data cache on dispatcher.
        if (affectedTroop != null) {
            String troopPath = troopHashGenerator.getPath(affectedTroop);
            cacheInvalidator.addPath(troopPath, true);
        }
        
        // Now troops are not separated by councils when caching:
        // e.g. /vtk-data/fd8d83hdhf
        // So, when a council info (for example milestone) is changed, 
        // the entire /vtk-data cache is invalidated.
        // TODO: separate troops into councils
        // e.g. /vtk-data/603/fd8d83hdhf
        if (affectedCouncilInfo != null) {
            // BASE = /vtk-data
            cacheInvalidator.addPath(troopHashGenerator.getBase());
        }
    }
}
