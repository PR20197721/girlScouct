<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node, org.slf4j.Logger, org.slf4j.LoggerFactory"  %>
<%
    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    Node homeNode = currentPage.getAbsoluteParent(2).adaptTo(Node.class);
    Node logoNode = homeNode;
    String headerImagePath = "";
    boolean addHeaderImage = false;
    try{
        homeNode = homeNode.getNode("jcr:content");
        headerImagePath = homeNode.getProperty("headerImagePath").getString();
        addHeaderImage = (!headerImagePath.equals("") && headerImagePath != null);
    }catch(Exception e){
        log.error("Error finding header background: ",e);
    }

    String logoPath = "";
    try{
        if(logoNode.hasNode("jcr:content/header/logo/regular")){
            logoNode = logoNode.getNode("jcr:content/header/logo/regular");
            logoPath = logoNode.getProperty("fileReference").getString();
        }
    }catch(Exception e){
        log.error("Error finding Logo: ",e);
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
<!--PAGE STRUCTURE: MAIN-->
  <div id="main" class="row collapse">
<!--<div class="large-24 medium-24 small-24 columns"> -->
    <cq:include path="content/breaking-news" resourceType="girlscouts/components/breaking-news" />
    <cq:include path="content/styled-subpar" resourceType="girlscouts/components/styled-subparsys"/>
<!--</div> -->
  </div>
