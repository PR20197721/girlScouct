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

public class AuthorVtkNodeListener implements EventListener, Constants {
    private static final Logger log = LoggerFactory.getLogger(AuthorVtkNodeListener.class);
    private Session session;
    private Replicator replicator;
    
    public AuthorVtkNodeListener(Session session, Replicator replicator) {
        this.session = session;
        this.replicator = replicator;
    }

    public void onEvent(EventIterator iter) {
        Set<String> paths = NodeEventCollector.getEvents(iter);

        for (String path : paths) {
            try {
                Node node = session.getNode(path);
                String fromPublisher = node.getProperty(Constants.FROM_PUBLISHER_PROPERTY).getString();

                ReplicationOptions opts = new ReplicationOptions();
                opts.setFilter(new AgentIdExcludeFilter(fromPublisher));
                opts.setSuppressStatusUpdate(true);
                opts.setSuppressVersions(true);
                    
                replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, opts);
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }
}