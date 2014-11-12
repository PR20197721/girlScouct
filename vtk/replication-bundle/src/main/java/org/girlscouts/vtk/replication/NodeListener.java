package org.girlscouts.vtk.replication;

import java.util.Collection;

import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.girlscouts.vtk.replication.NodeEventCollector.NodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class NodeListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(NodeListener.class);
    private Session session;
    private Replicator replicator;
    private ReplicationOptions opts;
    
    public NodeListener(Session session, Replicator replicator) {
        this.session = session;
        this.replicator = replicator;

        opts = new ReplicationOptions();
        opts.setFilter(new AgentIdRegexFilter("^" + Constants.VTK_AGENT_PREFIX + ".*"));
        opts.setSuppressStatusUpdate(true);
        opts.setSuppressVersions(true);
    }

   
    
    public void onEvent(EventIterator iter) {
    	/*
    	try{
    	while( iter.hasNext() ){
    		Event e = iter.nextEvent();
    		//System.err.println( "** "+ e.NODE_ADDED+" : "+ e.NODE_MOVED +" : "+e.NODE_REMOVED +" : "+e.PROPERTY_CHANGED+" : "+e.getPath() +" : "+ e.getInfo() + " : "+ e.getIdentifier() +" : "+e.getType());
    		
    		if( e.PROPERTY_CHANGED==16 && 
    				e.getPath().endsWith("/jcr:lastModified") ){
    					System.err.println(">> "+ (e.PROPERTY_CHANGED==16) + " : "+e.getPath());
    					saveModified( e.getPath().substring(0, e.getPath().lastIndexOf("/")) );
    		}
    	}
    	}catch(Exception e){e.printStackTrace();}
    	*/
    	
    	
        Collection<NodeEvent> events = NodeEventCollector.getEvents(iter);
        for (NodeEvent event : events) {
            try {
                String path = event.getPath();
                int type = event.getType();
        
                if (type == Constants.EVENT_UPDATE) {
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
                } else if (type == Constants.EVENT_REMOVE){
                    replicator.replicate(session, ReplicationActionType.DELETE, path, opts);
                } else {
                    log.error("Unknown replication type. Do nothing.");
                }
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }
}
