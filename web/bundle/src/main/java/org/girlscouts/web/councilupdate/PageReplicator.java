package org.girlscouts.web.councilupdate;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface PageReplicator{

	public void run();

	public void processReplicationNode(Node dateRolloutNode) throws RepositoryException;

}