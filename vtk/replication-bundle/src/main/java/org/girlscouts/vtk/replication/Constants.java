package org.girlscouts.vtk.replication;

import javax.jcr.observation.Event;

public interface Constants {
    String PRIMARY_TYPE = "nt:unstructured";
    String NODE_REMOVED_PROPERTY = "nodeRemoved";
    // TODO: make it an OSGI conf
    String ROOT_PATH = "/vtk";
    int EVENT_UPDATE = 0;
    int EVENT_REMOVE = 1;
    String JOB_NAME = "Publish VTK Node Job";
    int PROPERTY_UPDATE = Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED;
    String REPLICATION_INTERVAL_PROPERTY = "replicationInterval";
    long REPLICATION_INTERVAL = 60L;
    String SIBLING_SERVERS_PROPERTY = "siblingServers";
    String SERVLET_ENDPOINT = "/etc/vtk/replication";
}
