package org.girlscouts.vtk.replication;

import com.day.cq.replication.*;
import com.day.cq.replication.ReplicationLog.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class VTKReplicationListener implements ReplicationListener {
    private static final Logger log = LoggerFactory.getLogger(VTKReplicationListener.class);
    private Session session;
    private Replicator replicator;
    private ReplicationOptions asyncOpts;

    public VTKReplicationListener(Session session, Replicator replicator) {
        this.session = session;
        this.replicator = replicator;
    }

    public VTKReplicationListener(Session session, Replicator replicator, ReplicationOptions asyncOpts) {
        this.session = session;
        this.replicator = replicator;
        this.asyncOpts = asyncOpts;
    }

    public void onStart(Agent agent, ReplicationAction action) {
        // Do nothing
        log.debug("Starting replication...");
    }

    public void onMessage(Level level, String msg) {
        // Do nothing
    }

    public void onEnd(Agent agent, ReplicationAction action, ReplicationResult result) {
        if (result.isSuccess()) {
            if (log.isDebugEnabled()) {
                log.debug("Replication succeeded type: " + action.getType().getName() + " path: " + action.getPath());
            }
        } else {
            ReplicationActionType type = action.getType();
            String path = action.getPath();
            replicateAsync(type, path);
        }
    }

    public void onError(Agent agent, ReplicationAction action, Exception exception) {
        replicateAsync(action.getType(), action.getPath());
    }

    private void replicateAsync(ReplicationActionType type, String path) {
        try {
            log.warn("Sync replication error. Trying to replicate asynchronously. type = " + type.getName() + " path = " + path);
            session.refresh(true);
            replicator.replicate(session, type, path, asyncOpts);
        } catch (ReplicationException re) {
            log.error("Replication Exception even in async mode. Event not handled. type = {} path = {} error: {}", type, path, re);
        } catch (RepositoryException e) {
            log.error("RepositoryException in async mode. type = {} path = {} error: {}", type, path, e);
        }
    }
}