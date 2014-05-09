package org.girlscouts.vtk.replication;

import javax.jcr.Session;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

public class PublishVtkNodeListener implements EventListener {

    private Session session;
    
    public PublishVtkNodeListener(Session session) {
        this.session = session;
    }

    public void onEvent(EventIterator iter) {
        
    }

}
