package org.girlscouts.vtk.replication;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.RepositoryException;
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
    private Pattern councilInfoPattern;
    
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
        
        councilInfoPattern = Pattern.compile(yearPlanBase + "[0-9]+/councilInfo/.*");
    }

    
    public void onEvent(EventIterator iter) {
    	
    	/*
    	while( iter.hasNext()){
    		System.err.println("tataxx: :"+iter.next());
    	}
    	
    	
    	while( iter.hasNext()){
    		try {
				System.err.println("tatayy: :"+iter.nextEvent().getPath() );
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	*/
    	
        Collection<NodeEvent> events = NodeEventCollector.getEvents(iter);
        String affectedTroop = null;
        String affectedCouncilInfo = null;

        for (NodeEvent event : events) {
            try {

   /*         	
//-----------------------
 System.err.println("tatat: "+ event.getPath()+" : "+ event.getType() 	);  
 if (event.getType() == Constants.EVENT_UPDATE) 
	 System.err.println("tatat EVENT_UPDATE"); 
 if (event.getType() == Constants.EVENT_REMOVE)
	 System.err.println("tatat EVENT_REMOVE");
//-----------------------
 */
 
 
 
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
                
                // Get the affected council info
                if (affectedCouncilInfo == null) {
                    Matcher councilInfoMatcher = councilInfoPattern.matcher(path);
                    if (councilInfoMatcher.matches()) {
                        affectedCouncilInfo = path;
                    }
                }
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
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
