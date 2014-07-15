package org.girlscouts.vtk.replication;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodePathCollector {
    private static Logger log = LoggerFactory.getLogger(NodePathCollector.class);
    private static int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
  
    public static Set<String> getPaths(EventIterator iter) {
        Set<String> nodes = new HashSet<String>();
        
        while (iter.hasNext()) {
            Event event = iter.nextEvent();
            try {
                int type = event.getType();

                if ((type & PROPERTY_UPDATE) != 0) {
                    String path = event.getPath();
                    path = path.substring(0, path.lastIndexOf('/'));
                    nodes.add(path);
                }
            } catch (RepositoryException e) {
                log.warn("Cannot get path of a VTK node event.");
            }
        }
        return nodes;
    }
}
