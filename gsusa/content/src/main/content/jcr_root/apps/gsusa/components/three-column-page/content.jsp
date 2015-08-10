<%@include file="/libs/foundation/global.jsp" %>
<!-- content -->
<div id="main" class="three-cols">
    <cq:include path="content/top/par" resourceType="girlscouts/components/styled-parsys" />
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="right-wrapper">
        <div class="hero-section">
            <cq:include path="content/middle/breadcrumb" resourceType="gsusa/components/breadcrumb-trail" />
            <cq:include path="content/hero/par" resourceType="girlscouts/components/styled-parsys" />
        </div>
        <div class="middle-col">
            <cq:include script="main.jsp"/>
        </div>
        <div class="right-col">
            <cq:include script="right.jsp"/>
        </div>
    </div>
</div>
<!-- END of content -->
