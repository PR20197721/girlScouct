package org.girlscouts.web.councilupdate.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.web.councilrollout.impl.CouncilCreatorImpl;
import org.girlscouts.web.councilupdate.CouncilDesignUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrUtil;


@Component
@Service(value = CouncilDesignUpdater.class)
@Properties({

    @Property(name = "service.pid", value = "org.girlscouts.web.councilupdate.updater", propertyPrivate = false),
    @Property(name = "service.description", value = "Girl Scouts Council Design Update Tool", propertyPrivate = false),
    @Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class CouncilDesignUpdaterImpl implements CouncilDesignUpdater {

	private static Logger LOG = LoggerFactory.getLogger(CouncilDesignUpdaterImpl.class);
	
	@Override
	public String mergeCouncilDesign(Session session, String contentPath, String councilName) {
		
		String message = "Unable to complete task.";
		String councilDesignName = "girlscouts-"+councilName;
		final String designPath = "/etc/designs";
		final String designPrototypePath = "girlscouts-prototype";

		try {
			Node designsFolder = session.getNode(designPath);
			if (designsFolder.hasNode(designPrototypePath)) {
				Node prototypeDesign = designsFolder.getNode(designPrototypePath);
				
				if(designsFolder.hasNode(councilDesignName)){
					Node councilDesign = designsFolder.getNode(councilDesignName).getNode("jcr:content");
					NodeIterator prototypeNodes = prototypeDesign.getNode("jcr:content").getNodes();
					while(prototypeNodes.hasNext()){
						Node protoNode = (Node) prototypeNodes.next();
						String protoName = protoNode.getName();
						if(!councilDesign.hasNode(protoName)){
							JcrUtil.copy(protoNode, councilDesign, protoName);
						}
					}
					
					message = "Successfully updated design for council: " + councilName;
				} else{
					message = "Council design not found for council " + "" + ". This council may not exist.";
					throw new RuntimeException(message);
				}

			} else {
				LOG.error("design prototype folder not found");
				throw new PathNotFoundException();
			}
			
			
		} catch (PathNotFoundException e) {
			message = "Path not found during design generation: " + e.toString();
			LOG.error(message);
			throw new RuntimeException(message);
		} catch (RepositoryException e) {
			message = "Error with Repository: " + e.toString();
			LOG.error(message);
			throw new RuntimeException(message);
		} catch (Exception e) {
			message = "Cannot generate design: " + e.toString();
			LOG.error(message);
			throw new RuntimeException(message);
		}
		return message;
	}

}
