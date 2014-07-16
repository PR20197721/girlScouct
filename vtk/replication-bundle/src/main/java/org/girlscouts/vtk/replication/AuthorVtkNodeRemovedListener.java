package org.girlscouts.vtk.replication;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import com.day.cq.replication.Replicator;

public class AuthorVtkNodeRemovedListener implements EventListener {
    private static final Logger log = LoggerFactory.getLogger(AuthorVtkNodeRemovedListener.class);
    private Session session;
    private Replicator replicator;
    
    public AuthorVtkNodeRemovedListener(Session session, Replicator replicator) {
        this.session = session;
        this.replicator = replicator;
    }
    public void onEvent(EventIterator iter) {
        Set<String> paths = NodeEventCollector.getEvents(iter);
        
        for (String path : paths) {
            try {
                String realPath = path.substring(Constants.NODE_GRAVEYARD_ROOT.length());
                Node node;
                node = session.getNode(path);
                String fromPublisher = node.getProperty(Constants.FROM_PUBLISHER_PROPERTY).getString();
    
                ReplicationOptions opts = new ReplicationOptions();
                opts.setFilter(new AgentIdExcludeFilter(fromPublisher));
                opts.setSuppressStatusUpdate(true);
                opts.setSuppressVersions(true);
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, realPath, opts);
                
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
