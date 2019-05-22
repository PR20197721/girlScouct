<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node"  %>
<%
    Node homeNode = currentPage.getAbsoluteParent(2).adaptTo(Node.class);
    Node logoNode = logoNode;
    String headerImagePath = "";
    boolean addHeaderImage;
    try{
        homeNode = homeNode.getNode("jcr:content");
        headerImagePath = homeNode.getProperty("headerImagePath").getString();
        addHeaderImage = (!headerImagePath.equals("") && headerImagePath != null);
    }catch(Exception e){
        addHeaderImage = false;
    }

    String logoPath = "";
    try{
        if(logoNode.hasNode("jcr:content/header/logo/regular")){
            logoNode = logoNode.getNode("jcr:content/header/logo/regular");
            logoPath = logoNode.getProperty("fileReference").getString();
        }
    }catch(Exception e){

    }

 if(addHeaderImage){
    %>
<!-- content -->
    <div id="printImgBackground" style="display: none; background-image: url('<%= headerImagePath%>') !important">
        <img id="printPageImg"style = "display: none;" src="<%= logoPath %>"/>
    </div>
<% }
else{ %>
    <img id="printPageImg"style = "background-color: #00ae58 !important; display: none;" src="<%= logoPath %>"/>
<% } %>
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