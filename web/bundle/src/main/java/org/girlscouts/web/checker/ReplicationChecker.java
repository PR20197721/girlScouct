package org.girlscouts.web.replication;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;

import org.girlscouts.web.exception.GirlScoutsException;




//import com.day.cq.security.Group;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public interface ReplicationChecker {
	List<Asset> checkAssets(Session authSession, Session pubSession, ResourceResolver rr, String contentPath) 
			throws GirlScoutsException;    

}