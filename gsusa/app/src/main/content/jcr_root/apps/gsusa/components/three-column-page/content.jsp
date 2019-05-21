<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="javax.jcr.Node"  %>
<%

    String headerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/header";
    String logoPath = headerPath + "/logo";
    String headerNavPath = headerPath + "/header-nav";
    String eyebrowNavPath = headerPath + "/eyebrow-nav";
    String headerSearchPath = headerPath + "/search";
    String cookieHeaderPath = headerPath + "/cookie-header";
    Node logoNode = currentPage.getAbsoluteParent(2).adaptTo(Node.class);
    String logoPaths = "";
    try{
        if(logoNode.hasNode("jcr:content/header/logo/image")){
            logoNode = logoNode.getNode("jcr:content/header/logo/image");
            logoPaths = logoNode.getProperty("fileReference").getString();
        }
    }catch(Exception e){

    }

    %>
<!-- content -->
<img id="printPageImg"style = "display: none;" src="<%= logoPaths %>"/>
<div id="main" class="three-cols">
    <cq:include path="content/top/par" resourceType="girlscouts-common/components/styled-parsys" />

    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="right-wrapper">
        <% if (isCookiePage(currentPage)) { %>
                <cq:include path="<%= cookieHeaderPath %>" resourceType="gsusa/components/cookie-header" />
        <% } %>
        <div class="hero-section">
            <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
            <cq:include path="content/hero/par" resourceType="girlscouts-common/components/styled-parsys" />
        </div>
        <div class="middle-col">
            <cq:include script="main.jsp"/>
        </div>
        <div class="right-col">
            <cq:include script="right.jsp"/>
        </div>
        
<!-- brightedge two-col lem code begin -->
<div class="be-ix-link-block"><!--Link Equity Target Div--></div>
<!-- brightedge lem code end -->         
        
    </div>
    <div class="wrapper clearfix"></div>
    <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
</div>
<!-- END of content -->
