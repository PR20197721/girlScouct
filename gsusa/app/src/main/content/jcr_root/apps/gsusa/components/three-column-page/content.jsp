<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="javax.jcr.Node, org.slf4j.Logger, org.slf4j.LoggerFactory"  %>
<!-- content -->
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
