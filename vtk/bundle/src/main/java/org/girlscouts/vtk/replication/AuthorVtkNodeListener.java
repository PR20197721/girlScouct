package org.girlscouts.vtk.replication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.Event;
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
        while (iter.hasNext()) {
            try {
                Event event = iter.nextEvent();
                String path = event.getPath();
                
                int type = event.getType();
                if (type == Event.PROPERTY_ADDED || type == Event.PROPERTY_CHANGED || type == Event.PROPERTY_REMOVED) {
                    String property = path.substring(path.lastIndexOf('/') + 1);
                    String namespace = property.split(":")[0];
                    if (namespace.equals("jcr") || namespace.equals("cq")) {
                        log.debug("This is CQ property, ignore");
                        continue;
                    }
                    path = path.substring(0, path.lastIndexOf('/'));
                }
                Node node = session.getNode(path);
                String fromPublisher = node.getProperty(Constants.FROM_PUBLISHER_PROPERTY).getString();
                node.setProperty(Constants.FROM_PUBLISHER_PROPERTY, (Value)null);

                boolean isRemove = false;
                if (node.hasProperty(Constants.NODE_REMOVED_PROPERTY)) {
                    isRemove = node.getProperty(Constants.NODE_REMOVED_PROPERTY).getBoolean();
                    node.setProperty(Constants.NODE_REMOVED_PROPERTY, (Value)null);
                }
                
                // This should not take long, but might be a bottleneck.
                ReplicationOptions opts = new ReplicationOptions();
                opts.setFilter(new AgentIdExcludeFilter(fromPublisher));
                    
                if (isRemove) {
                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, path, opts);
                } else {
                    replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
                }
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }

}
