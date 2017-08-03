package org.girlscouts.web.councilupdate;

import javax.jcr.Node;

public interface PageActivator{

	public void run();

	public void processActivationNode(Node dateRolloutNode);

}