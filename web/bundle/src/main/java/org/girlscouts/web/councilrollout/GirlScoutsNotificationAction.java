package org.girlscouts.web.councilrollout;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;

public interface GirlScoutsNotificationAction {

	public void notifyCouncils(String path);

	public void notifyGSUSA(String path);

	public void execute(Resource source, Resource target, String subject, String message, LiveRelationship relation, ResourceResolver rr)
			throws WCMException;
	
	public void send(String nationalPage, String councilPage,String email1, String email2, ValueMap vm, String subject, String message, ResourceResolver rr) 
			throws WCMException;

}