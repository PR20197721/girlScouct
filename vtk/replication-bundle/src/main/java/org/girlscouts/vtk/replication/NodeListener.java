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
    private ReplicationOptions opts;
    private ReplicationOptions vtkDataOpts;
    private TroopHashGenerator troopHashGenerator;
    private VTKDataCacheInvalidator cacheInvalidator;
    private String yearPlanBase;
    private Pattern troopPattern;
    
    public NodeListener(Session session, Replicator replicator, 
            TroopHashGenerator troopHashGenerator, VTKDataCacheInvalidator cacheInvalidator, String yearPlanBase) {
        this.session = session;
        this.replicator = replicator;
        this.troopHashGenerator = troopHashGenerator;
        this.cacheInvalidator = cacheInvalidator;

        opts = new ReplicationOptions();
        opts.setFilter(new AgentIdRegexFilter("^" + Constants.VTK_AGENT_PREFIX + ".*"));
        opts.setSuppressStatusUpdate(true);
        opts.setSuppressVersions(true);

        vtkDataOpts = new ReplicationOptions();
        vtkDataOpts.setFilter(new AgentIdFilter("flush"));
        vtkDataOpts.setSuppressStatusUpdate(true);
        vtkDataOpts.setSuppressVersions(true);
        
        this.yearPlanBase = yearPlanBase;
        // /vtk/603/troops/701G0000000uQzTIAU => 701G0000000uQzTIAU
        // yearPlanBase = /vtk2015/
        troopPattern = Pattern.compile(yearPlanBase + "[0-9]+/troops/([^/]+)");
    }

    
    public void onEvent(EventIterator iter) {
        Collection<NodeEvent> events = NodeEventCollector.getEvents(iter);
        String affectedTroop = null;
        for (NodeEvent event : events) {
            try {
            
                String path = event.getPath();
                if (path.endsWith("jcr:content")) {
                    continue;
                }
              
                int type = event.getType();
                if (type == Constants.EVENT_UPDATE) {
                	
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
                } else if (type == Constants.EVENT_REMOVE){
                    replicator.replicate(session, ReplicationActionType.DELETE, path, opts);
                } else {
                    log.error("Unknown replication type. Do nothing.");
                }
                
                // Get the affected troop
                if (affectedTroop == null) {
                    Matcher troopMatcher = troopPattern.matcher(path);
                    while (troopMatcher.find()) {
                        affectedTroop = troopMatcher.group(1);
                        log.debug("Affected Troop found: " + affectedTroop);
                    }
                }
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
        
        // Found affected troop. Invalidate VTK data cache on dispatcher.
        if (affectedTroop != null) {
            String troopPath = troopHashGenerator.getPath(affectedTroop);
            cacheInvalidator.addPath(troopPath);
        }
    }
}
