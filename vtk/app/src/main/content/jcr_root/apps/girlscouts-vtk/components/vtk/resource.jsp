<%@page import="java.util.*,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.helpers.CouncilMapper,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.dao.*,
                org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="include/session.jsp"%>
<%-- VTK tab --%>
<%
	String activeTab = "resource";
	boolean showVtkNav = true;
	String sectionClassDefinition = "";

	String branch = "/content/vtkcontent", councilName="vtkcontent";
	try{
		apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig)session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
		int councilIdInt = apiConfig.getTroops().get(0).getCouncilCode();
		String councilId = Integer.toString(councilIdInt);
		branch = sling.getService(CouncilMapper.class).getCouncilBranch(councilId);
		councilName = sling.getService(CouncilMapper.class).getCouncilName(councilId);
	}catch(Exception e){e.printStackTrace();}
	
	//String resourcesPagePath = branch + "/en/resources2/jcr:content";
	String resourcesPagePath = ("vtkcontent".equals(councilName) ? "/content/vtkcontent" : "/content/vtk-resources2/"+councilName) +"/en/resources2/jcr:content";
%>

<%@include file="include/bodyTop.jsp"%>

<sling:include path="<%= resourcesPagePath %>" replaceSelectors="content" />

<script>
	var __currentLevel__ = "<%=VtkUtil.formatLevel(user, troop)%>";
	var fixVerticalSizing = true;
	loadNav('resource');
</script>
<%@include file="include/bodyBottom.jsp" %>