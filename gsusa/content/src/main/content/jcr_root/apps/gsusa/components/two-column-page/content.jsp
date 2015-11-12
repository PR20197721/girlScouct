<%@include file="/libs/foundation/global.jsp" %>
<!-- content -->
<div id="main" class="two-cols">
    <cq:include path="content/top/par" resourceType="girlscouts/components/styled-parsys" />
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="middle-col">
        <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
        <cq:include script="main.jsp"/>
        <cq:include script="cookie-footer.jsp"/>
    </div>
    <cq:include path="content/bottom/par" resourceType="girlscouts/components/styled-parsys" />
</div>
<!-- END of content -->
