<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="javax.jcr.Node"  %>
<%
    Node logoNode = currentPage.adaptTo(Node.class);
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
<!--PAGE STRUCTURE: MAIN-->
  <div id="main" class="row collapse">
<!--<div class="large-24 medium-24 small-24 columns"> -->
    <cq:include path="content/breaking-news" resourceType="girlscouts/components/breaking-news" />
    <cq:include path="content/styled-subpar" resourceType="girlscouts/components/styled-subparsys"/>
<!--</div> -->
  </div>
