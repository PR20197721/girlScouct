package org.girlscouts.vtk.replication;

public interface ReplicationReceiverFilter {
    boolean accept(String path);
}
