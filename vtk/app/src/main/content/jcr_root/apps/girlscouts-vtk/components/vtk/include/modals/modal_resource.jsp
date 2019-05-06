<%@page import="
                com.day.cq.dam.api.Asset,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.Query,
                com.day.cq.search.QueryBuilder,
                com.day.cq.search.result.SearchResult,
                com.day.cq.wcm.api.Page,
                com.day.cq.wcm.api.PageManager,
                org.apache.sling.api.adapter.Adaptable,
                org.apache.sling.api.resource.Resource,
                org.apache.sling.api.resource.ResourceResolver,
                javax.jcr.Session,
                java.io.PrintWriter,
                java.io.StringWriter" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@include file="../session.jsp" %>
<%
    String resourceName = "";
    if (request.getParameter("resource") != null) {
        resourceName = request.getParameter("resource");
    }
    Page resourceContent;
%>
<% if (!resourceName.equals("")) {
    final PageManager manager = (PageManager) resourceResolver.adaptTo(PageManager.class);
    try {
        String councilId = null;
        if (apiConfig != null) {
            if (userTroops.size() > 0) {
                councilId = userTroops.get(0).getCouncilCode();
            }
        }
        CouncilMapper mapper = sling.getService(CouncilMapper.class);
        String branch = mapper.getCouncilBranch(councilId);
        // TODO: language?
        resourceContent = manager.getPage(resourceName);
%>
<div class="modal_resource">
    <div class="header clearfix">
        <h3 class="columns large-22">
            <%
                out.println(xssAPI.encodeForHTML(resourceContent.getTitle()));
            %>
        </h3>
        <a class="close-reveal-modal columns large-2" href="#"><i
                class="icon-button-circle-cross"></i></a>
    </div>
    <div class="scroll content">
        <section class="content">
            <%
                String fullPath = resourceContent.getPath() + "/_jcr_content/content/middle/par.html";
            %>
            <script>
                $.get("<%=fullPath%>", function (data) {
                    $("div.scroll.content").html(data);
                });
            </script>
        </section>
    </div>
</div>
<% } catch (Exception e) {
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    String stackTrace = sw.toString();
    out.println(stackTrace);
}
}
%>