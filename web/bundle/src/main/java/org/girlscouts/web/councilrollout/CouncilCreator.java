package org.girlscouts.web.councilrollout;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.exception.GirlScoutsException;

import com.day.cq.security.Group;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public interface CouncilCreator {

    ArrayList<Page> generateSite(Session session, ResourceResolver rr, String councilPath, String councilName, String councilTitle) throws GirlScoutsException;

    ArrayList<Page> generateScaffolding(Session session, ResourceResolver rr, String councilName) throws GirlScoutsException;
    
    ArrayList<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle) throws GirlScoutsException;

    ArrayList<Tag> generateTags(Session session, ResourceResolver rr, String path, String councilName, String councilTitle) throws GirlScoutsException;
    
    ArrayList<Group> generateGroups(Session session, ResourceResolver rr, String councilName, String councilTitle) throws GirlScoutsException;

}