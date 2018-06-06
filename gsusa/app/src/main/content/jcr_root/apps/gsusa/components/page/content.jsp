<%@include file="/libs/foundation/global.jsp" %>
<!-- One Column Page -->
<div id="main" class="one-cols">
    <cq:include path="content/top/par" resourceType="foundation/components/parsys" />
    <div class="middle-col">
        <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
        <cq:include script="main.jsp"/>
    </div>
    <div class="wrapper clearfix"></div>
    <cq:include path="content/bottom/par" resourceType="girlscouts-common/components/styled-parsys" />
</div>
<!-- END of content -->
