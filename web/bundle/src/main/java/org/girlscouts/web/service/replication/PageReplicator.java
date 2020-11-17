package org.girlscouts.web.service.replication;

import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface PageReplicator{

	public void run();

	public void processReplicationNode(Node dateRolloutNode, ResourceResolver rr) throws RepositoryException;

    public void processReplicationNode(String dateRolloutNode);

}