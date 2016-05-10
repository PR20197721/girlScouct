<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="java.util.regex.*, java.text.*" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<ul>
<%
java.util.List<org.girlscouts.vtk.models.Search> searchResults = (java.util.List<org.girlscouts.vtk.models.Search>)session.getValue("search");
if (searchResults == null || searchResults.size() < 1) {
%>
        <li class="searchResultsItem">
                <i>No results.</i>
        </li>
<%
} else {
	for(int i=0;i<searchResults.size();i++){
		org.girlscouts.vtk.models.Search search = searchResults.get(i);
		String docTypeImage = null;
		try {
			String docType = null;
			if (search.getType() != null) {
				docType = search.getType().toLowerCase();
			}

			if (docType != null) {
				// match by type
				if (docType.indexOf("pdf") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-pdf.png";
				} else if (docType.indexOf("indesign") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-indesign.png";
				} else if (docType.indexOf("htm") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-html.png";
				} else if (docType.indexOf("excel") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-excel.png";
				} else if (docType.indexOf("illustrator") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-illustrator.png";
				} else if (docType.indexOf("powerpoint") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-powerpoint.png";
				} else if (docType.indexOf("word") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-word.png";
				} else if (docType.indexOf("photoshop") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-photoshop.png";
				} else if (docType.indexOf("text") != -1) {
					docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-text.png";
				}
			}
			if (docTypeImage == null) {
				// match by name
				docTypeImage= org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(search.getPath());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String description = "Untitled";
		if (search.getDesc() != null) {
			description = search.getDesc();
		}
		if (docTypeImage == null) {
			docTypeImage = "/etc/designs/girlscouts-vtk/clientlibs/css/images/doctype-unknown.png";
		}
		
%>
	<li class="searchResultsItem1">
		<table width="100%">
			<tr>
				<td width="34">
				    <span class="docType">
				        <img width="40" height="40" src="<%=docTypeImage%>"/>
				    </span>
				</td> <!--  end 1 -->
				<td>
				    <h2><a class="searchResultPath" href="<%=search.getPath() %>" target="_blank"><%= description %></a></h2>
					<p><%=search.getContent() %></p>
					<p><%=search.getSubTitle() %></p>
				</td><!--  end 2 -->
				<td width="40">
					<%if(!apiConfig.isDemoUser() && search.getAssetType().equals(AssetComponentType.AID)  && VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){ %>
						 <input type="button" value="Add to Meeting" onclick="applyAids('<%=search.getPath()%>', '<%= java.net.URLEncoder.encode(description) %>','<%=AssetComponentType.AID %>')" class="button linkButton"/>
					<%}else{ %>
	                      <!--
						<input type="button" value="Add to Meeting" onclick="applyResource('<%=search.getPath()%>', '<%= java.net.URLEncoder.encode(description) %>')" class="button linkButton"/>
	                       -->
					<%} %>
				<td/><!-- end 3-->
			</tr> <!-- end tr -->
		</table> <!-- end table -->
	</li> <!--  end of record -->
<%
	}
}
%>
</ul>
