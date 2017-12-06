package org.girlscouts.web.service.replication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface PageReplicator{

	public void run();

	public void processReplicationNode(Node dateRolloutNode) throws RepositoryException;

}