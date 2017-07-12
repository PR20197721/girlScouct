<%@ page import="com.day.cq.wcm.api.WCMMode,
                org.girlscouts.vtk.helpers.ConfigManager,
                org.girlscouts.vtk.helpers.CouncilMapper,
                java.text.SimpleDateFormat,java.util.*,
                org.girlscouts.vtk.auth.models.ApiConfig,
                org.girlscouts.vtk.models.*,
                org.girlscouts.vtk.dao.*,
                org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
<%!

public String buildMobileCTAMenu(String[] mobileCtas) {
	StringBuilder menuBuilder = new StringBuilder();
	if(mobileCtas != null && mobileCtas.length > 0){
		menuBuilder.append("<div class=\"buttons\">");
		for(String cta:mobileCtas){
			String[] values = cta.split("\\|\\|\\|");
            String label = values[0];
            String path = values.length >= 2 ? values[1] : "";
            String css = values.length >= 3 ? " "+values[2] : "";
            String newWindow = values.length >= 4 ? values[3] : "";
            if("true".equals(newWindow)){
            	newWindow = "_new";
            }else{
            	newWindow = "_self";
            }
            menuBuilder.append("<a class=\"donate-btn circle-btn"+css+"\" target=\""+newWindow+"\" href=\""+path+"\">"+label+"</a>");
		}
		
		menuBuilder.append("</div>");
	}
	return menuBuilder.toString();
}
%>
<%
try{
	Page newCurrentPage = (Page)request.getAttribute("newCurrentPage");
	Design newCurrentDesign= (Design)request.getAttribute("newCurrentDesign");
	if (newCurrentPage != null) {
	    currentPage = newCurrentPage;
	}
	String globalNavPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header/global-nav";
	Resource globalNav = resourceResolver.getResource(globalNavPath);
	if(globalNav != null){
		ValueMap globalNavProps = globalNav.adaptTo(ValueMap.class); 
		String[] mobileCtas = globalNavProps.get("mobileCtas", String[].class);
		String ctaHtml = buildMobileCTAMenu(mobileCtas);
		if(ctaHtml != null && ctaHtml.trim().length() >0){
			out.print(ctaHtml);
		}
	}
}catch(Exception e){}
%>