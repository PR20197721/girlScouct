package org.girlscouts.vtk.scaffolding;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

@Component(metatype = false)
@Service({ Servlet.class })
@Property(name = "sling.servlet.paths", value = { "/bin/vtk-scaffolding-post" })
public class ScaffoldingPostServlet extends SlingAllMethodsServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 4468363069979147472L;

    @Reference
    Replicator replicator;
    
    @Override
    protected void doPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        String action = request.getParameter("action");
        if (action != null && action.equals("activate")) {
            doActivate(request, response);
        } else if (action != null && action.equals("deactivate")) {
            doDeactivate(request, response);
        } else {
            doRegularPost(request, response);
        }
    }
    
    protected void doActivate(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
	    Session session = (Session)request.getResourceResolver().adaptTo(Session.class);
	    try {
            String path = request.getParameter("originalUrl");
            Node currentNode = session.getNode(path);
	        replicate(currentNode, session);
	    } catch (RepositoryException e0) {
	        throw new ServletException(e0);
	    } catch (ReplicationException e1) {
	        throw new ServletException(e1);
	    }
	}
    
	protected void replicate(Node currentNode, Session session) throws RepositoryException, ReplicationException {
    	replicator.replicate(session, ReplicationActionType.ACTIVATE, currentNode.getPath());
    	NodeIterator iter = currentNode.getNodes();
    	while (iter.hasNext()) {
    	    Node node = iter.nextNode();
    	    replicate(node, session);
    	}
	}

    protected void doDeactivate(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
	    Session session = (Session)request.getResourceResolver().adaptTo(Session.class);
	    try {
            String path = request.getParameter("originalUrl");
	        replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);
	    } catch (ReplicationException e) {
	        throw new ServletException(e);
	    }
    }

    protected void doRegularPost(SlingHttpServletRequest request,
            SlingHttpServletResponse response) throws ServletException,
            IOException {
        
        try {
            if (request.getParameter("./vtkDataType").equals("meeting")) {
                removeObsoleteNodes(request, "activities");
            } else if (request.getParameter("./vtkDataType").equals("year-plan")) {
                removeObsoleteNodes(request, "meetings");
            }
        } catch (RepositoryException e) {
            throw new ServletException(e);
        }
        
        request.getRequestDispatcher(request.getParameter("originalUrl")).forward(request, response);
    }
    
    protected void removeObsoleteNodes(SlingHttpServletRequest request, String childTop) throws RepositoryException {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        String rootPath = request.getParameter("originalUrl");
        if (rootPath.endsWith("*")) {
            // /content/girlscouts-vtk/meetings/myyearplan/*
            // =>
            // /content/girlscouts-vtk/meetings/myyearplan/brownie/B14B05
            rootPath = rootPath.substring(0, rootPath.length() - 1); 
            rootPath = rootPath + request.getParameter("./level").toLowerCase() + "/" 
                    + request.getParameter("./id").toUpperCase();
        }

        Set<String> childrenNodePaths = getChildrenNodePaths(rootPath, request);

        createNodes(childrenNodePaths, session);
        
        // Special logic for year plans. Get existing meetings. 
        Set<String> existingNodePaths = new HashSet<String>();
        Node childTopNode = session.getNode(rootPath + "/" + childTop);
        NodeIterator iter = childTopNode.getNodes();
        
        while (iter.hasNext()) {
            existingNodePaths.add(iter.nextNode().getPath());
        }

        // Do set sub to see if any meeting node needs to be removed.
        existingNodePaths.removeAll(childrenNodePaths);
        
        // Cannot do this way because of special character problems.
        // e.g. /content/girlscouts-vtk/meetings/myyearplan/brownie/B14B04/activities/A1405659407340[7]
        //for (String path : existingNodePaths) {
        //    session.removeItem(path);
        //}
        iter = childTopNode.getNodes();
        while (iter.hasNext()) {
            Node node = iter.nextNode();

            if (existingNodePaths.contains(node.getPath())) {
                node.remove();
            }
        }
        
        session.save();          
    }

    protected Set<String> getChildrenNodePaths(String rootPath, SlingHttpServletRequest request) {
        // e.g. ./meetingInfo/overview/str
        @SuppressWarnings("unchecked")
        Map<String, String[]> paramMap = request.getParameterMap();
        Set<String> nodePaths = new HashSet<String>();
        for (String key : paramMap.keySet()) {
            if (key.startsWith("./")) {
                // ./meetingInfo/overview/str => meetingInfo/overview/str
                // ./id => id
                String relPath = key.substring(2);
                if (relPath.contains("/")) {
                    // meetingInfo/overview/str => meetingInfo/overview
                    relPath = relPath.substring(0, relPath.lastIndexOf('/'));
                    String path = rootPath + "/" + relPath;
                    nodePaths.add(path);
                }
            }
        }
        return nodePaths;
    }

    protected void createNodes(Set<String> nodePaths, Session session) throws RepositoryException {
        for (String path : nodePaths) {
            if (!session.nodeExists(path)) {
                JcrUtil.createPath(path, "nt:unstructured", session);
            }
        }
    }
}