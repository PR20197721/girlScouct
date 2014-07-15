package org.girlscouts.vtk.replication;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.emory.mathcs.backport.java.util.Arrays;

public class NodePathCollector {
    private static Logger log = LoggerFactory.getLogger(NodePathCollector.class);
    private static int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    private static String[] _IGNORED_NAMESPACES = new String[] {"cq", "jcr", Constants.FROM_PUBLISHER_PROPERTY};
    private static Set<String> IGNORED_NAMESPACES = new HashSet<String>(Arrays.asList(_IGNORED_NAMESPACES));
  
    public static class NodeEvent {
        public static int UPDATE = 0;
        public static int REMOVE = 1;
        
        public String path;
        public int type;
        public NodeEvent(String path, int type) {
            this.path = path;
            this.type = type;
        }
    }

    public static Set<NodeEvent> getPaths(EventIterator iter) {
        Set<NodeEvent> nodes = new HashSet<NodeEvent>();
        
        while (iter.hasNext()) {
            Event event = iter.nextEvent();
            try {
                int type = event.getType();
                String path = event.getPath();

                // If this event is about node property change
                if ((type & PROPERTY_UPDATE) != 0) { 
                    String property = path.substring(path.lastIndexOf('/') + 1);
                    String namespace = property.split(":")[0];
                    if (IGNORED_NAMESPACES.contains(namespace)) {
                        log.debug("This property is ignored. " + property);
                        continue;
                    }
                    path = path.substring(0, path.lastIndexOf('/'));
                }
                nodes.add(new NodeEvent(path, type == Event.NODE_REMOVED ? NodeEvent.REMOVE : NodeEvent.UPDATE));
            } catch (RepositoryException e) {
                log.warn("Cannot get path of a VTK node event.");
            }
        }
        return nodes;
    }
}
