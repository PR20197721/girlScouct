<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*, org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<ul>
<%
java.util.List<org.girlscouts.vtk.models.Search> searchs = (java.util.List<org.girlscouts.vtk.models.Search>)session.getValue("search");
for(int i=0;i<searchs.size();i++){
	org.girlscouts.vtk.models.Search search = searchs.get(i);
	String docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-unknown.png";
	try {
		if (search.getType().toLowerCase().indexOf("pdf") != -1) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-pdf.png";
		} else if (search.getType().toLowerCase().indexOf("indesign") != -1) {
                        docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-indesign.png";
                } else if (search.getType().toLowerCase().indexOf("html") != -1) {
                        docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-html.png";
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
%>
	<li class="searchResultsItem">
		<span class="docType"><img width="30" height="30" src="<%=docTypeImage%>"/></span>
		<h2> <a class="searchResultPath" href="<%=search.getPath() %>"><%=search.getDesc() %></a> </h2>
		<p><%=search.getContent() %></p>
		<input type="button" value="Add to Meeting" onclick="applyAids('<%=search.getPath()%>')"/>
	</li>
<%
}
%>
</ul>
