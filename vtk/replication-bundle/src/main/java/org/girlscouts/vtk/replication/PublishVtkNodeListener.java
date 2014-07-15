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

public class PublishVtkNodeListener implements EventListener, Constants {
    private static final Logger log = LoggerFactory.getLogger(PublishVtkNodeListener.class);
    private Session session;
    private Replicator replicator;
    private String publishId;
    private ReplicationOptions opts;
    
    public PublishVtkNodeListener(Session session, Replicator replicator, String publishId) {
        this.session = session;
        this.replicator = replicator;
        this.publishId = publishId;
        opts = new ReplicationOptions();
        opts.setFilter(new OutboxAgentFilter());
        opts.setSuppressStatusUpdate(true);
        opts.setSuppressVersions(true);
    }

    public void onEvent(EventIterator iter) {
        Set<NodeEvent> events = NodeEventCollector.getEvents(iter);
        
        for (NodeEvent event : events) {
            try {
                String path = event.path;
                
                Node node = session.getNode(path);
                if (node.hasProperty(FROM_PUBLISHER_PROPERTY)) {
                    log.error("Found \"fromPublisher\" property. Ignore.");
                    node.setProperty(FROM_PUBLISHER_PROPERTY, (Value)null);
                    session.save();
                    continue;
                }

                node.setProperty(Constants.FROM_PUBLISHER_PROPERTY, this.publishId);
                if (event.type == NodeEvent.REMOVE) {
                    node.setProperty(Constants.NODE_REMOVED_PROPERTY, true);
                }
                session.save();
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
                
                // Remove the fromPublisher property after replication
                node.setProperty(Constants.FROM_PUBLISHER_PROPERTY, (Value)null);
                session.save();
                //////////////////////////
                log.error("##### Replicated this node: " + path);
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }
}
