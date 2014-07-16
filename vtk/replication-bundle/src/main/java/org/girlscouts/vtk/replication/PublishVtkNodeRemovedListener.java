package org.girlscouts.vtk.replication;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class PublishVtkNodeRemovedListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(PublishVtkNodeRemovedListener.class);
    private Session session;
    private Replicator replicator;
    private String publishId;
    private ReplicationOptions opts;
    
    public PublishVtkNodeRemovedListener(Session session, Replicator replicator, String publishId) {
        this.session = session;
        this.replicator = replicator;
        this.publishId = publishId;
        opts = new ReplicationOptions();
        opts.setFilter(new OutboxAgentFilter());
        opts.setSuppressStatusUpdate(true);
        opts.setSuppressVersions(true);
    }

    public void onEvent(EventIterator iter) {
        Set<String> paths = NodeEventCollector.getEvents(iter);
        
        for (String path : paths) {
            try {
                String destPath = Constants.NODE_GRAVEYARD_ROOT + path;
                // Create node, but do not save the session to save time
                Node node = JcrUtil.createPath(destPath, "nt:unstructured", "nt:unstructured", session, false);
                node.setProperty(Constants.FROM_PUBLISHER_PROPERTY, this.publishId);
                session.save();
                replicator.replicate(session, ReplicationActionType.ACTIVATE, destPath, opts);
                
                node.remove();
                session.save();
            } catch (RepositoryException e) {
                log.error("Repository exception on event of removing the node: " + path);
            } catch (ReplicationException e) {
                log.error("Replication exception on event of removing the node: " + path);
            }
        }
    }
}
