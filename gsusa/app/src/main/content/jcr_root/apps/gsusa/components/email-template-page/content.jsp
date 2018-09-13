<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils"%><%
%>


<div id="main" class="content row">
		<div class="large-24 medium-24 small-24 columns">
            <h1><%=currentPage.getTitle()%></h1>
			<div class="image parbase">
                
			<%=StringEscapeUtils.escapeHtml4(properties.get("data/content",""))%>
            <p>Please use scaffolding to edit</p>
            </div>


			</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
