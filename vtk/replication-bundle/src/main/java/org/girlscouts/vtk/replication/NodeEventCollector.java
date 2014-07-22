package org.girlscouts.vtk.replication;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeEventCollector {
    private static Logger log = LoggerFactory.getLogger(NodeEventCollector.class);
    private static int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    
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
            return (obj instanceof NodeEvent) && (((NodeEvent)obj).path == this.path);
        }

        @Override
        public int hashCode() {
            // Only look at the path, ignore the type
            return path.hashCode();
        }
    }
  
    public static Set<NodeEvent> getEvents(EventIterator iter) {
        Set<NodeEvent> nodes = new LinkedHashSet<NodeEvent>();
        
        while (iter.hasNext()) {
            Event event = iter.nextEvent();
            try {
                String path = event.getPath();
                int type = event.getType();

                // If this event is about node property change
                if ((event.getType() & PROPERTY_UPDATE) != 0) {
                    path = path.substring(0, path.lastIndexOf('/'));
                }
                nodes.add(new NodeEvent(path, type));
            } catch (RepositoryException e) {
                log.warn("Cannot get path of a VTK node event.");
            }
        }
        return nodes;
    }
}