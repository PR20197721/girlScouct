package org.girlscouts.vtk.replication;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.girlscouts.vtk.replication.NodeEventCollector.NodeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class PublishVtkNodeListener implements EventListener, Runnable {
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
                String path = event.getPath();
                int type = event.getType();
                Node node = session.getNode(path);
                
                Node destNode = null;
                String destPath = Constants.NODE_REPLICATION_ROOT + path;

                if (type == Constants.EVENT_UPDATE) {
                    String destParentPath = destPath.substring(0, destPath.lastIndexOf('/'));
                    // To save time: do not auto save
                    Node destParent = JcrUtil.createPath(destParentPath, "nt:unstructured", "nt:unstructured", session, false);
                    destNode = JcrUtil.copy(node, destParent, null);
                } else if (type == Constants.EVENT_REMOVE) {
                    destNode = JcrUtil.createPath(destPath, "nt:unstructured", "nt:unstructured", session, false);
                    destNode.setProperty(Constants.NODE_REMOVED_PROPERTY, true);
                }
                destNode.setProperty(Constants.FROM_PUBLISHER_PROPERTY, publishId);
                session.save();
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            }
        }
    }

    public void run() {
        try {
            NodeIterator iter = session.getNode(Constants.NODE_REPLICATION_ROOT).getNodes();
            while (iter.hasNext()) {
                Node node = iter.nextNode();
                replicator.replicate(session, ReplicationActionType.ACTIVATE, node.getPath(), opts);
                node.remove();
                session.save();
            }
        } catch (RepositoryException e) {
            log.error("Repository Exception while replicating nodes.");
        } catch (ReplicationException e) {
            log.error("Replication Exception while replicating nodes.");
        }
    }
}
