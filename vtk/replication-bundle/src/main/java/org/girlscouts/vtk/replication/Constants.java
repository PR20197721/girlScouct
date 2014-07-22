package org.girlscouts.vtk.replication;

import javax.jcr.observation.Event;

public interface Constants {
    static String PRIMARY_TYPE = "nt:unstructured";
    static String NODE_REMOVED_PROPERTY = "nodeRemoved";
    // TODO: make it an OSGI conf
    static String ROOT_PATH = "/vtk";
    static int EVENT_UPDATE = 0;
    static int EVENT_REMOVE = 1;
    static String JOB_NAME = "Publish VTK Node Job";
    final int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
}
