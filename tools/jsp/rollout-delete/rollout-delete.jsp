<%@page import="java.util.Iterator,
                javax.jcr.NodeIterator,
                com.day.cq.commons.Filter,
                com.day.cq.replication.Replicator,
                com.day.cq.replication.ReplicationActionType" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
final String CHECK_POINT_REL_PATH = "en/resources/safety-and-travel/staying-safe-on-adventures--safety-activity-checkpoints-";
final String CHECK_POINT_ROOT = "/content/girlscouts-template" + "/" + CHECK_POINT_REL_PATH;
final Session session = resourceResolver.adaptTo(Session.class);
final Replicator replicator = sling.getService(Replicator.class);

NodeIterator iter = session.getNode("/content").getNodes();

while (iter.hasNext()) {
    Node councilNode = iter.nextNode();

    if ("girlscouts-template".equals(councilNode.getName()) ||
        !councilNode.isNodeType("cq:Page")) {
        continue;
    }

    String councilPath = councilNode.getPath();
    String path = councilPath + "/" + CHECK_POINT_REL_PATH;

    if (session.nodeExists(path)) {
		NodeIterator cpIter = session.getNode(path).getNodes();
        while (cpIter.hasNext()) {
            Node cpNode = cpIter.nextNode();
            if (
                 !cpNode.isNodeType("cq:Page")) {
                continue;
            }
            String cpPath = cpNode.getPath();
            out.println(cpPath + "<br/>");
            //replicator.replicate(session, ReplicationActionType.DELETE, cpPath);
        }
    }
}
%>
