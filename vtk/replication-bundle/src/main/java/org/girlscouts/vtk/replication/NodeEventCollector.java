package org.girlscouts.vtk.replication;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeEventCollector {
    private static Logger log = LoggerFactory.getLogger(NodeEventCollector.class);
    private static int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    private static String[] _IGNORED_NAMESPACES = new String[] {Constants.FROM_PUBLISHER_PROPERTY};
    private static Set<String> IGNORED_NAMESPACES = new HashSet<String>(Arrays.asList(_IGNORED_NAMESPACES));
  
    public static Set<String> getEvents(EventIterator iter) {
        Set<String> nodes = new HashSet<String>();
        
        int i = 0;
        while (iter.hasNext()) {
            Event event = iter.nextEvent();
            try {
                String path = event.getPath();
                //////////////////////////////////////
                log.error("i = " + Integer.toString(i++) + "#### event path = " + path);

                // If this event is about node property change
                String property = path.substring(path.lastIndexOf('/') + 1);
                String namespace = property.split(":")[0];
                if (IGNORED_NAMESPACES.contains(namespace)) {
                    log.debug("This property is ignored. " + property);
                    continue;
                }
                path = path.substring(0, path.lastIndexOf('/'));
                nodes.add(path);
            } catch (RepositoryException e) {
                log.warn("Cannot get path of a VTK node event.");
            }
        }
        return nodes;
    }
}
