<%--

  vtk-data component.

--%><%
%><%@page import="com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%
	if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
		<cq:includeClientLib categories="cq.foundation-main"/>
		<script src="/libs/cq/ui/resources/cq-ui.js" type="text/javascript"></script>
		<script>
			function scaffoldingLandingCallback() {
				location.href = '/etc/scaffolding/girlscouts-vtk/landing.html';
			}
			function scaffoldingEditCallback() {
	        	location.href = location.href.replace(/\.html/, ".scaffolding.html");
			}
			function scaffoldingActivateCallback() {
        		$CQ.ajax({
        			url: '/bin/vtk-scaffolding-post',
        			type: "POST",
        			data: "action=activate&originalUrl=" + window.location.pathname.replace(/\.html$/, ""),
        			success: function(data) {
        				alert('Activation suceeded.');
        			},
        			fail: function() {
        				alert('Activation Failed.');
        			}
        		});
			}
			function scaffoldingDeactivateCallback() {
        		$CQ.ajax({
        			url: '/bin/vtk-scaffolding-post',
        			type: "POST",
        			data: "action=deactivate&originalUrl=" + window.location.pathname.replace(/\.html$/, ""),
        			success: function(data) {
        				alert('Deactivation Succeeded.');
        			},
        			fail: function() {
        				alert('Deactivation Failed.');
        			}
        		});
			}
		</script>
		<!-- Empty <p> for more spaces -->
		<p>
			<form>
				<input type="button" value="Edit" onclick="scaffoldingEditCallback();"></input>
                <!--
				<input type="button" value="Activate" onclick="scaffoldingActivateCallback();"></input>
				<input type="button" value="Deactivate" onclick="scaffoldingDeactivateCallback();"></input>
                -->
				<input type="button" value="VTK Scaffolding Home" onclick="scaffoldingLandingCallback();"></input>
			</form>
		</p>
<%
		String type = properties.get("vtkDataType", "no-supported");
		String script = type + ".jsp";
		%><cq:include script="<%= script %>" /><%
	}
%>
		<!-- Empty <p> for more spaces -->
		<p>
			<form>
				<input type="button" value="Edit" onclick="scaffoldingEditCallback();"></input>
				<input type="button" value="Activate" onclick="scaffoldingActivateCallback();"></input>
				<input type="button" value="Deactivate" onclick="scaffoldingDeactivateCallback();"></input>
				<input type="button" value="VTK Scaffolding Home" onclick="scaffoldingLandingCallback();"></input>
			</form>
		</p>
