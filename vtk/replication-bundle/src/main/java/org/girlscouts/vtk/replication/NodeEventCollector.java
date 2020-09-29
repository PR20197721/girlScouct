package org.girlscouts.vtk.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * When a node gets updated, JCR reports a series of property add/update/remove events.
 * But, we only have to know which nodes are touched.
 * This class normalizes the events and returns the nodes that are updated into a set.
 */
public class NodeEventCollector {
    private static Logger log = LoggerFactory.getLogger(NodeEventCollector.class);
    private static int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
  
    public static Set<NodeEvent> getEvents(EventIterator iter) {
        log.debug("Building VTK Node Event Objects");
        Set<NodeEvent> nodes = new LinkedHashSet<NodeEvent>();
        while (iter.hasNext()) {
            Event event = iter.nextEvent();
            try {
                String path = event.getPath();
                int type = event.getType();
                log.debug("Processing event type {} for {} ",type, path);
                int nodeEventType = -1;
                if ((type & PROPERTY_UPDATE) != 0) {
                    nodeEventType = Constants.EVENT_UPDATE;
                    // If this event is about node property change
                    path = path.substring(0, path.lastIndexOf('/'));
                    log.debug("Found Event with path: {} and type: PROPERTY_UPDATE", path);
                } else if (type == Event.NODE_MOVED){
                    nodeEventType = Constants.EVENT_UPDATE;
                    log.debug("Found Event with path: {} and type: NODE_MOVED", path);
                } else if (type == Event.NODE_REMOVED) {
                    nodeEventType = Constants.EVENT_REMOVE;
                    log.debug("Found Event with path: {} and type: NODE_REMOVED", path);
                } else {
                    log.warn("Event type " + Integer.toString(type) + " not supported. Discard. " + path);
                }

                log.debug("nodeEventType: {}", nodeEventType);
                if (nodeEventType != -1) {
                    log.debug("Added new node event to list");
                    nodes.add(new NodeEvent(path, nodeEventType));
                }
            } catch (RepositoryException e) {
                log.warn("Cannot get path of a VTK node event.");
            }
        }
        log.debug("Returning {} VTK Node Event Objects", nodes.size());
        return nodes;
    }

    public static class NodeEvent {
        private String path;
        private int type;

        public NodeEvent(String path, int type) {
            this.path = path;
            this.type = type;
        }
        public String getPath() {
            return path;
        }

        public int getType() {
            return type;
        }

        @Override
        public boolean equals(Object obj) {
            return (obj instanceof NodeEvent) && (((NodeEvent)obj).path.equals(this.path));
        }

        @Override
        public int hashCode() {
            // Only look at the path, ignore the type
            return path.hashCode();
        }

        @Override
        public String toString() {
            return "NodeEvent{" + "path='" + path + '\'' + ", type=" + type + '}';
        }
    }
}