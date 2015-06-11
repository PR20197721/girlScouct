<%@page import="javax.jcr.query.RowIterator, javax.jcr.query.*, javax.jcr.Session, org.girlscouts.vtk.models.Troop, org.girlscouts.vtk.auth.permission.*, org.girlscouts.vtk.utils.VtkUtil"%>
<%@include file="/libs/foundation/global.jsp"%>



<%
final org.girlscouts.vtk.ejb.SessionFactory sessionFactory = sling.getService( org.girlscouts.vtk.ejb.SessionFactory.class);


                   Node node = session.getNode(request.getParmater("path") );
                   node.setProperty("isAnalytics", true);
                   session.save();
                   System.err.println("saved: "+path);
                   %>
                   
                  