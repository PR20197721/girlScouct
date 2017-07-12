package org.girlscouts.web.councilrollout;

import java.util.ArrayList;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;

public interface GirlScoutsRolloutReporter {

	public void execute(String subject, ArrayList<String> messageLog, ResourceResolver rr)
			throws WCMException;
	public void send(String subject, ArrayList<String> messageLog, String[] addresses) 
			throws WCMException;

}