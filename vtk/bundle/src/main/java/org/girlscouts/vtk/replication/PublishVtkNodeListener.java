package org.girlscouts.vtk.replication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

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
    }

    public void onEvent(EventIterator iter) {
        log.error("##################### IN EVENT");
        int i = 0;
        while (iter.hasNext()) {
            log.error("############ i = " + Integer.toString(i++));
            try {
                Event event = iter.nextEvent();
                String path = event.getPath();
                int type = event.getType();
                
                log.error("##################### path = " + path);
                if (type == Event.PROPERTY_ADDED || type == Event.PROPERTY_CHANGED || type == Event.PROPERTY_REMOVED) {
                    String property = path.substring(path.lastIndexOf('/') + 1);
                    if (property.indexOf("jcr") == 0 || property.indexOf("cq") == 0 || property.equals(FROM_PUBLISHER_PROPERTY)) {
                        log.error("@@@@@@@@@@This is CQ property, ignore");
                        log.debug("This is CQ property, ignore");
                        continue;
                    }
                    path = path.substring(0, path.lastIndexOf('/'));
                }

                Node node = session.getNode(path);
                if (node.hasProperty(FROM_PUBLISHER_PROPERTY)) {
                    log.debug("Found \"fromPublisher\" property. This node comes from another publisher. Ignore.");
                    continue;
                }
                node.setProperty(Constants.FROM_PUBLISHER_PROPERTY, this.publishId);
                if (event.getType() == Event.NODE_REMOVED) {
                    node.setProperty(Constants.NODE_REMOVED_PROPERTY, true);
                }
                replicator.replicate(session, ReplicationActionType.ACTIVATE, path, opts);
            } catch (RepositoryException e) {
                log.error("Repository Exception. Event not handled.");
            } catch (ReplicationException e) {
                log.error("Replication Exception. Event not handled.");
            }
        }
    }

}
