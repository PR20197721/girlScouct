<%@page session="false"%>
<%@page import="com.day.cq.replication.Replicator,
				com.day.cq.replication.ReplicationActionType,
				com.day.cq.replication.ReplicationException,
				javax.jcr.RepositoryException,
				javax.jcr.Node,
				javax.jcr.NodeIterator,
				javax.jcr.Session" %>

<%@include file="/libs/foundation/global.jsp"%>
<%
	String action = request.getParameter("action");
	if (action != null && action.equals("activate")) {
	    Replicator replicator = (Replicator)sling.getService(Replicator.class);
	    Session session = (Session)resourceResolver.adaptTo(Session.class);
	    replicate(currentNode, session, replicator);
	} else {
	    // TODO: forward to the next servlet.
	}
%>
<%!
	public void replicate(Node currentNode, Session session, Replicator replicator) throws RepositoryException, ReplicationException {
    	replicator.replicate(session, ReplicationActionType.ACTIVATE, currentNode.getPath());
    	NodeIterator iter = currentNode.getNodes();
    	while (iter.hasNext()) {
    	    Node node = iter.nextNode();
    	    replicate(node, session, replicator);
    	}
	}
%>