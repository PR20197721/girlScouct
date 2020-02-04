<%@include file="/libs/foundation/global.jsp" %>
<%@page import="javax.jcr.Node, org.slf4j.Logger, org.slf4j.LoggerFactory"  %>

<cq:includeClientLib categories="apps.gsusa.components.twoColPage" />
<div id="main" class="two-cols">
    <cq:include path="content/top/par" resourceType="girlscouts-common/components/styled-parsys" />
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="middle-col">
        <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
        <cq:include script="main-two-cols.jsp"/>
    </div>
    <div class="middle-col bottom-parsys">
        <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
    </div>
</div>
<!-- END of content -->
