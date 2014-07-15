package org.girlscouts.vtk.replication;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.girlscouts.vtk.replication.NodeEventCollector.NodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class AuthorVtkNodeListener implements EventListener, Constants {
    private static final Logger log = LoggerFactory.getLogger(AuthorVtkNodeListener.class);
    private Session session;
    private Replicator replicator;
    
    public AuthorVtkNodeListener(Session session, Replicator replicator) {
        this.session = session;
        this.replicator = replicator;
    }

    public void onEvent(EventIterator iter) {
        Set<NodeEvent> events = NodeEventCollector.getEvents(iter);

        for (NodeEvent event : events) {
            try {
                String path = event.path;
                
                //TODO: Remove later
                log.error("Event logged: " + path);
                
                Node node = session.getNode(path);
                String fromPublisher = node.getProperty(Constants.FROM_PUBLISHER_PROPERTY).getString();

                boolean isRemove = false;
                if (node.hasProperty(Constants.NODE_REMOVED_PROPERTY)) {
                    isRemove = node.getProperty(Constants.NODE_REMOVED_PROPERTY).getBoolean();
                    node.setProperty(Constants.NODE_REMOVED_PROPERTY, (Value)null);
                    session.save();
                }
                
                ReplicationOptions opts = new ReplicationOptions();
                opts.setFilter(new AgentIdExcludeFilter(fromPublisher));
                opts.setSuppressStatusUpdate(true);
                opts.setSuppressVersions(true);
                    
                // This should not take long, but might be a bottleneck.
                if (isRemove) {
                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, opts);
                } else {
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
                    //////////////////////////////////////
                    log.error("##### Replicated this node: " + path);
                }
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }

}
