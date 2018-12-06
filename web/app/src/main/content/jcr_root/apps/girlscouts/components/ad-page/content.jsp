<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode"%>
<div id="main" class="content row">
		<div class="large-24 medium-24 small-24 columns">
			<%
            boolean changedMode = false;
            if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
                WCMMode.DISABLED.toRequest(request);
                changedMode=true;
                out.println("<p align=\"center\">EDIT THIS AD THROUGH SCAFFOLDING</p>");
            }
            %>
            <h1><%=currentPage.getTitle()%></h1>
			<div class="image parbase">
			<cq:include path="image" resourceType="girlscouts/components/image" />
            <cq:include path="text" resourceType="girlscouts/components/text"/>
            <%
            if (changedMode) {
                WCMMode.EDIT.toRequest(request); 
                changedMode = false;
            }
			%>
            </div>
	</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
