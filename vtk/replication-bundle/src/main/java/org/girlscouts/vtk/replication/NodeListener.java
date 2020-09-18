package org.girlscouts.vtk.replication;

import com.day.cq.replication.*;
import com.day.cq.replication.ReplicationLog.Level;
import org.girlscouts.vtk.osgi.component.TroopHashGenerator;
import org.girlscouts.vtk.replication.NodeEventCollector.NodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(NodeListener.class);
    private Session session;
    private Replicator replicator;
    private ReplicationOptions syncOpts;
    private ReplicationOptions asyncOpts;
    private ReplicationOptions vtkDataOpts;
    private TroopHashGenerator troopHashGenerator;
    private VTKDataCacheInvalidator cacheInvalidator;
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
        syncOpts.setSynchronous(true); // SYNC
        syncOpts.setListener(new VTKReplicationListener(session, replicator));

        asyncOpts = new ReplicationOptions();
        asyncOpts.setFilter(new AgentIdRegexFilter("^" + Constants.VTK_AGENT_PREFIX + ".*"));
        asyncOpts.setSuppressStatusUpdate(true);
        asyncOpts.setSuppressVersions(true);
        asyncOpts.setSynchronous(false); // ASYNC
        asyncOpts.setListener(new VTKReplicationListener(session, replicator, asyncOpts));


        vtkDataOpts = new ReplicationOptions();
        vtkDataOpts.setFilter(new AgentIdFilter("flush"));
        vtkDataOpts.setSuppressStatusUpdate(true);
        vtkDataOpts.setSuppressVersions(true);
        
        // /vtk/603/troops/701G0000000uQzTIAU => 701G0000000uQzTIAU
        // yearPlanBase = /vtk2015/
        troopPattern = Pattern.compile(yearPlanBase + "[0-9]+/troops/([^/]+)");
        
        councilInfoPattern = Pattern.compile(yearPlanBase + "[0-9]+/councilInfo/.*");
    }

    
    public void onEvent(EventIterator iter) {
        Collection<NodeEvent> events = NodeEventCollector.getEvents(iter);
        String affectedTroop = null;
        String affectedCouncilInfo = null;

        try {
            session.refresh(true);
        } catch (RepositoryException e) {
            log.error("Refresh session failed. Error: {}", e);
        }

        for (NodeEvent event : events) {
            String path = event.getPath();

            if (path.endsWith("jcr:content")) {
                continue;
            }

            int type = event.getType();

            try {
                if (type == Constants.EVENT_UPDATE) {
                    log.debug("Performing ReplicationActionType.ACTIVATE for NodeEvent type EVENT_UPDATE");
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, syncOpts);
                } else if (type == Constants.EVENT_REMOVE){
                    log.debug("Performing ReplicationActionType.DELETE for NodeEvent type EVENT_REMOVE");
                    replicator.replicate(session, ReplicationActionType.DELETE, path, syncOpts);
                } else {
                    log.warn("Unknown replication type when trying to sync replicate. Do nothing. type = " + type + " path = " + path);
                }
            } catch (ReplicationException re) {
                log.error("Replication Exception. Event not handled. type = {} path = {} error = {}", type, path, re);
            }
                
            // Get the affected troop
            if (affectedTroop == null) {
                Matcher troopMatcher = troopPattern.matcher(path);
                while (troopMatcher.find()) {
                    affectedTroop = path;
                    log.debug("Affected Troop found: " + affectedTroop);
                }
            }
            
            // Get the affected council info
            if (affectedCouncilInfo == null) {
                Matcher councilInfoMatcher = councilInfoPattern.matcher(path);
                if (councilInfoMatcher.matches()) {
                    affectedCouncilInfo = path;
                    log.debug("Affected CouncilInfo found: " + affectedCouncilInfo);
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
