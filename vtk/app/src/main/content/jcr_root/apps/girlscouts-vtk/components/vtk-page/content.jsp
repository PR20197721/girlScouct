
<%
try{	
	HttpSession session = request.getSession();
	org.girlscouts.vtk.auth.models.ApiConfig apiConfig=
			(org.girlscouts.vtk.auth.models.ApiConfig)
				session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName());
	if( apiConfig==null ){
		/*
		%><jsp:forward page="/content/girlscouts-vtk/controllers/auth.sfauth.html?action=signin"/><% 
		return;
		*/
	}
}catch(Exception e){e.printStackTrace();}
%>






<%--

  Volunteer Toolkit component.

  

--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	// TODO add you code here
%>

<!-- TODO -->
<!-- <cq:includeClientLib categories="apps.girlscouts-vtk" /> -->
<script src="/etc/designs/girlscouts-vtk/clientlibs.js"></script>

<!--PAGE STRUCTURE: MAIN-->
<div id="main" class="row">
    <div class="large-24 medium-24 small-24 columns">
        <cq:include path="vtk" resourceType="girlscouts-vtk/components/vtk" />
    </div>
</div>

