<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node, org.slf4j.Logger, org.slf4j.LoggerFactory"  %>
<%
   Logger logger = LoggerFactory.getLogger(this.getClass().getName());
   Node homeNode = currentPage.getAbsoluteParent(1).adaptTo(Node.class);
   String logoPath = "/content/dam/girlscouts-shared/print-logos";
   try{
       String councilName = homeNode.getPath().substring(homeNode.getPath().lastIndexOf("/"));
       logoPath = logoPath + councilName + "-printlogo.jpg";
   }catch(Exception e){
       logger.error("Error finding Logo: ",e);
   }

    %>
<div id="imgContainer" style="display: none;">
    <img id="printPageImg" style = "display: none;" src="<%= logoPath %>"/>
</div>
<div id="main" class="row content">
		<cq:include path="content/top/par" resourceType="foundation/components/parsys" />
		<div class="large-24 medium-24 small-24 columns">
			<div class="breadcrumbWrapper">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
					<div>
						<cq:include path="content/middle/par" resourceType="foundation/components/parsys" />
					</div>
			</div>
	<div class="wrapper clearfix"></div>
        <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
