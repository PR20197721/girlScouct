<%@page import="javax.jcr.query.RowIterator, javax.jcr.query.*, javax.jcr.Session, org.girlscouts.vtk.models.Troop, org.girlscouts.vtk.auth.permission.*, org.girlscouts.vtk.utils.VtkUtil"%>
<%@include file="/libs/foundation/global.jsp"%>
<!--  WHAT IS THIS FILE? -->

<%
final org.girlscouts.vtk.ejb.SessionFactory sessionFactory = sling.getService( org.girlscouts.vtk.ejb.SessionFactory.class);
Session session = null;
try {
    session = sessionFactory.getSession();

                   Node node = session.getNode(request.getParameter("path") );
                   node.setProperty("analyticsLastUpdated", new java.util.Date().getTime());
                   session.save();
                   
                   
}catch(Exception e){e.printStackTrace();
}finally{
    sessionFactory.closeSession(session);
}   
                   %>
                   
                  