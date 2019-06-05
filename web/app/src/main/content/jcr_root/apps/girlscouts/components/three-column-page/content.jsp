<%@include file="/libs/foundation/global.jsp"%>
<%@page import="javax.jcr.Node, org.slf4j.Logger, org.slf4j.LoggerFactory"  %>
<!-- apps/girlscouts/components/three-column-page/content.jsp -->
<!--PAGE STRUCTURE: MAIN-->
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
<div id="main" class="row content three-column">

		<!--PAGE STRUCTURE: LEFT CONTENT START-->
		<div class="large-5 hide-for-medium hide-for-small columns mainLeft">
			<div id="leftContent">
				<cq:include script="left.jsp" />
			</div>
		</div>
	        <!--PAGE STRUCTURE: LEFT CONTENT STOP-->

	        <!--PAGE STRUCTURE: MAIN CONTENT START-->
		<div class="large-19 medium-24 small-24 columns mainRight">
			<div class="breadcrumbWrapper">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
			<div class="row mainRightBottom">
				<div class="large-18 medium-18 small-24 columns rightBodyLeft">
					<!--PAGE STRUCTURE: MIDDLE CONTENT START-->
					<cq:include script="middle.jsp" />
	          <!--PAGE STRUCTURE: MIDDLE CONTENT STOP-->
				</div>
				<!--PAGE STRUCTURE: RIGHT CONTENT START-->
				<div id="rightContent" class="large-6 medium-6 small-24 columns">
					<cq:include script="right.jsp" />
				</div>
	      <!--PAGE STRUCTURE: RIGHT CONTENT STOP-->
			</div>
		</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>
