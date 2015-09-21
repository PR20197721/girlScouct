<%@page import="org.girlscouts.vtk.helpers.ConfigManager,
				org.girlscouts.vtk.helpers.CouncilMapper,
				org.girlscouts.vtk.utils.VtkUtil" %>
<%@include file="/libs/foundation/global.jsp" %>
<%!
	// This method recursively removes all mix:lockable properties given a resource
	int removeLockable(Session mySession, Node x) {
		int lockCount =0;
		try {
			for (javax.jcr.nodetype.NodeType nt: x.getMixinNodeTypes()) {
				if(nt.isNodeType("mix:lockable")) {
					System.out.println("Remove Lockable path " + x);
					x.removeMixin("mix:lockable");
					lockCount++;
					mySession.save();
				}
			}
			javax.jcr.NodeIterator thisNodeIterator = x.getNodes();
			while (thisNodeIterator.hasNext()){
				Node child = thisNodeIterator.nextNode();
				lockCount += removeLockable(mySession, child);
			}
		} catch (javax.jcr.nodetype.NoSuchNodeTypeException nnte) {
			// skip
		} catch (javax.jcr.RepositoryException re) {
			re.printStackTrace();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return lockCount;
	}
%>
<div class="row content">
	<div class="small-24 columns">
MIGRATING LOCKABLE...
<%
	if ("true".equals(request.getParameter("migrate"))) {
		Session mySession = resourceResolver.adaptTo(Session.class);
		Node vtkRootNode = mySession.getNode("/vtk2015");
		int modifiedItems = removeLockable(mySession, vtkRootNode);
		mySession.save();
		out.println(modifiedItems + "modified");
	}
%>
	</div>
</div>
