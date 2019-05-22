<%@include file="/libs/foundation/global.jsp" %>
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
        if(logoNode.hasNode("jcr:content/header/logo/image")){
            logoNode = logoNode.getNode("jcr:content/header/logo/image");
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
<div id="main" class="two-cols">
    <cq:include path="content/top/par" resourceType="girlscouts-common/components/styled-parsys" />
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="middle-col">
        <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
        <cq:include script="main-two-cols.jsp"/>
    </div>
    <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
</div>
<!-- END of content -->
