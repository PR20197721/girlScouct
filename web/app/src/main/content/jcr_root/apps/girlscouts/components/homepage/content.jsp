<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node"  %>
<%
    Node logoNode = currentPage.getAbsoluteParent(2).adaptTo(Node.class);
    try{
        if(logoNode.hasNode("jcr:content/header/logo/regular"))
            logoNode = logoNode.getNode("jcr:content/header/logo/regular")
    }catch(Exception e){

    }
    String logoPath = logoNode.getProperty("fileReference").getString();

%>
    <img id="printPageImg"style = "display: none;" src="<%= logoPath %>"/>
<!--PAGE STRUCTURE: MAIN-->
  <div id="main" class="row collapse">
<!--<div class="large-24 medium-24 small-24 columns"> -->
    <cq:include path="content/breaking-news" resourceType="girlscouts/components/breaking-news" />
    <cq:include path="content/styled-subpar" resourceType="girlscouts/components/styled-subparsys"/>
<!--</div> -->
  </div>
