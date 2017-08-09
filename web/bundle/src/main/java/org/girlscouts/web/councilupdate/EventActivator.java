package org.girlscouts.web.councilupdate;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface EventActivator{

	public void run();

	public void processActivationNode(Node dateRolloutNode) throws RepositoryException;

}