package org.girlscouts.web.checker;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import com.day.cq.dam.api.Asset;

import org.apache.sling.api.resource.ResourceResolver;

import org.girlscouts.web.exception.GirlScoutsException;




//import com.day.cq.security.Group;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public interface ReplicationChecker {
	List<Asset> checkAssets(Session authSession, String pubUrl, ResourceResolver rr, String contentPath) 
			throws GirlScoutsException;    

}