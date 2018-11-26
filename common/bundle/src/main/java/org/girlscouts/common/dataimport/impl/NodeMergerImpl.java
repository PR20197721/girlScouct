package org.girlscouts.common.dataimport.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.girlscouts.common.dataimport.NodeMerger;
import org.girlscouts.common.exception.GirlScoutsException;

import com.day.cq.commons.jcr.JcrUtil;

@Component
@Service(value = NodeMerger.class)
@Properties({
		@Property(name = "service.pid", value = "org.girlscouts.common.dataimport.nodemerger", propertyPrivate = false),
	@Property(name = "service.description", value = "Girl Scouts node merger service", propertyPrivate = false),
	@Property(name = "service.vendor", value = "Girl Scouts", propertyPrivate = false) })
public class NodeMergerImpl implements NodeMerger {
    
    public String[] merge(String origPath, String destPath, ResourceResolver rr) throws GirlScoutsException {
        List<String> errors = new ArrayList<String>();
        Session session = rr.adaptTo(Session.class);
        
        Resource origResource = rr.resolve(origPath);
        if (origResource == null || origResource.equals("sling:nonexisting")) {
            throw new GirlScoutsException(null, "Path not found: " + origPath);
        }
        
        Resource destResource = rr.resolve(destPath);
        if (destResource == null || destResource.getResourceType().equals("sling:nonexisting")) {
            try {
		JcrUtil.createPath(destPath, "cq:Page", session);
	    } catch (RepositoryException e) {
		throw new GirlScoutsException(e, "Cannot create node." + destPath);
	    }
        }

        Node origNode = origResource.adaptTo(Node.class);
        Queue<Node> nodes = new LinkedList<Node>();
	try {
	    NodeIterator iter = origNode.getNodes();
	    while (iter.hasNext()) {
		nodes.offer(iter.nextNode());
	    }
	    while (!nodes.isEmpty()) {
		Node node = nodes.poll();
		String destNodePath = destPath + node.getPath().substring(origPath.length());
		String destParentNodePath = destPath + node.getParent().getPath().substring(origPath.length());
		Node destParentNode = rr.resolve(destParentNodePath).adaptTo(Node.class);
		Resource destNodeResource = rr.resolve(destNodePath);
		if (destNodeResource == null || destNodeResource.getResourceType() == "sling:nonexisting") {
		    JcrUtil.copy(node, destParentNode, null);
		} else {
		    iter = node.getNodes();
		    while (iter.hasNext()) {
			nodes.offer(iter.nextNode());
		    }
		}
	    }
	    
	    session.save();
	} catch (RepositoryException e) {
	    throw new GirlScoutsException(e, "Repository Exeption while saving nodes.");
	}
	return errors.toArray(new String[errors.size()]);
    }
}
