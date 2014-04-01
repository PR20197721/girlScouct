<%@include file="/libs/foundation/global.jsp" %>
<%
    String footerPath = currentPage.getAbsoluteParent(2).getContentResource().getPath() + "/footer";
%>
<!--PAGE STRUCTURE: FOOTER-->
<div id="footer" class="row">
    <div class="large-24 medium-24 small-24 columns">
        <cq:include path="<%= footerPath + "/nav"%>" resourceType="girlscouts/components/navigation-bar"/>
        <cq:include path="<%= footerPath + "/par"%>" resourceType="foundation/components/parsys" />
    </div>
</div>