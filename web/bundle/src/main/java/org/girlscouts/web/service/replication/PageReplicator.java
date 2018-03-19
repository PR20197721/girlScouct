package org.girlscouts.web.service.replication;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.ResourceResolver;

public interface PageReplicator{

	public void run();

	public void processReplicationNode(Node dateRolloutNode, ResourceResolver rr) throws RepositoryException;

}