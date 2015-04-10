package org.girlscouts.web.councilrollout;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;



//import com.day.cq.security.Group;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public interface CouncilCreator {

    List<Page> generateSite(Session session, ResourceResolver rr, String councilPath, String councilName, String councilTitle);

    List<Node> generateScaffolding(Session session, ResourceResolver rr, String councilName);
    
    List<Node> generateDAMFolders(Session session, String path, String councilName, String councilTitle);

    List<Tag> generateTags(Session session, ResourceResolver rr, String councilName, String councilTitle);
    
    List<Node> generateDesign(Session session, ResourceResolver rr, String councilName, String councilTitle);
    
    List<String> generateGroups(Session session, String councilName, String councilTitle);
    

}