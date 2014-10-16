<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="../include/session.jsp"%>
<%
final MeetingDAO meetingDAO= sling.getService( MeetingDAO.class);

	if( request.getParameter("xyd") ==null )
	{out.println("Cant run script.abort"); return;}

	if(request.getParameter("xyd").equals("u2t") )
		meetingDAO.doX();
	else
		meetingDAO.undoX();
%>