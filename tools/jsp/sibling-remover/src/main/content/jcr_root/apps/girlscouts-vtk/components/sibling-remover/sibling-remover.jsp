<%@page import="org.apache.sling.jcr.api.SlingRepository,
                java.util.List,
                java.util.ArrayList,
                java.util.Collections,
                javax.jcr.NodeIterator" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
final String ROOT = "/vtk2015"; 

String dryRun = request.getParameter("dryRun");
// REAL run only is dryRun==false. Otherwise dryrun is true.
boolean isDryRun = !(dryRun != null && dryRun.equals("false"));

%> DRY RUN: <%= Boolean.toString(isDryRun) %><br/> <%

SlingRepository repository = sling.getService(SlingRepository.class);
Session session = repository.loginAdministrative(null);

List<String> toRemove = new ArrayList<String>();

Node rootNode = session.getNode(ROOT);
NodeIterator councilIter = rootNode.getNodes();
int total = 0;
while (councilIter.hasNext()) {
    Node councilNode = councilIter.nextNode();
    if (!councilNode.hasNode("troops")) {
        continue;
    }
    Node troopsNode = councilNode.getNode("troops");
    NodeIterator troopIter = troopsNode.getNodes();
    while (troopIter.hasNext()) {
        Node troopNode = troopIter.nextNode();
        String path = troopNode.getPath();
        if (path.contains("[")) {
            toRemove.add(path);
            total++;
        }
    }
}

// Remove higher-order siblings first.
// because:
// samplenode[3] will become samplenode[2]
// if you remove samplenode[2].
Collections.reverse(toRemove);

for (String path : toRemove) {
    if (!isDryRun) {
	    try {
	        session.removeItem(path);
	        session.save();
	        %>DONE <%
	    } catch (Exception e) {
	        %>ERROR <%
	    }
    } else {
        %>DRYRUN <% 
    }
    %><%= path %><br/><%

}
%>
total: <%= total %>