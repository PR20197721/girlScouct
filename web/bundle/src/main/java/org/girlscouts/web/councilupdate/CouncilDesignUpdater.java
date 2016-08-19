package org.girlscouts.web.councilupdate;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;

public interface CouncilDesignUpdater {

	public String mergeCouncilDesign(Session session, String contentPath, String councilName);
	
}
