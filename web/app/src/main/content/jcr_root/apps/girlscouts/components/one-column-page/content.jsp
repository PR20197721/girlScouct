<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node"  %>
<%
    Node logoNode = currentPage.getAbsoluteParent(2).adaptTo(Node.class);
    String logoPath = "";
    try{
        if(logoNode.hasNode("jcr:content/header/logo/regular")){
            logoNode = logoNode.getNode("jcr:content/header/logo/regular");
            logoPath = logoNode.getProperty("fileReference").getString();
        }
    }catch(Exception e){

    }

%>
<img id="printPageImg"style = "display: none;" src="<%= logoPath %>"/>
<div id="main" class="content row">
		<div class="large-24 medium-24 small-24 columns">
			<div class="breadcrumbWrapper">
				<cq:include path="content/middle/breadcrumb" resourceType="girlscouts/components/breadcrumb-trail" />
			</div>
					<div>
						<cq:include path="content/middle/par" resourceType="foundation/components/parsys" />
					</div>
			</div>
	        <!--PAGE STRUCTURE: MAIN CONTENT STOP-->
</div>