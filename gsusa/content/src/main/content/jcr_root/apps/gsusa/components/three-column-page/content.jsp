<%@include file="/libs/foundation/global.jsp" %>
<!-- content -->
<div id="main" class="three-cols">
    <div class="left-col">
        <cq:include script="left.jsp"/>
    </div>
    <div class="hero-section">
        <cq:include path="content/hero/par" resourceType="foundation/components/parsys" />
    </div>
    <div class="middle-col">
        <cq:include script="main.jsp"/>
    </div>
    <div class="right-col">
        <cq:include script="right.jsp"/>
    </div>
</div>
<!-- END of content -->
