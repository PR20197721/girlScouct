package org.girlscouts.vtk.replication;

import javax.jcr.Session;

public class Replicator implements Runnable {
    private Session session;
    private String[] siblings;

    Replicator(Session session, String... siblings) {
        this.session = session;
        this.siblings = siblings;
    }

    public void run() {
        // TODO Auto-generated method stub
        
    }

}
