<%--
asset-usage-history component.
--%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%>
<%@page import="
            org.apache.sling.api.resource.Resource,
            org.apache.sling.api.resource.ResourceResolver,
            org.apache.sling.api.resource.ResourceUtil,
            com.day.cq.search.QueryBuilder,
            com.day.cq.search.Query,
            com.day.cq.search.PredicateGroup,
            com.day.cq.search.result.SearchResult,
            com.day.cq.search.result.Hit,
            java.util.ArrayList,
            java.util.HashMap,
            java.util.Iterator,
            java.util.List,
            java.util.Map,
            javax.jcr.Session,
            org.apache.sling.api.resource.ValueMap,
            javax.jcr.Node
"%><%
%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%><%
%><%@taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0"%><%
%><%@taglib prefix="ui" uri="http://www.adobe.com/taglibs/granite/ui/1.0"%><%
%>
<cq:defineObjects />
<div id="Key-1380270455919" class="insight-report-container">
    <label data-metatype="section" class="coral-Form-fieldlabel">
        <h3>Usage Statistics</h3>
        <span> </span>
    </label>
    <table
        is="coral-table"
        id="Key-1383270455819"
        class="foundation-layout-util-maximized-alt foundation-collection foundation-layout-table coral-Table-wrapper coral-Table-wrapper--default coral-Table-wrapper--sticky"
        data-foundation-selections-mode="multiple"
        data-foundation-layout='{"name":"foundation-layout-table","limit":40,"sortMode":null,"rowReorderAction":null,"layoutId":"recentusages","trackingFeature":null,"trackingElement":null}'
        multiple=""
        role="presentation"
        style="position: relative;">
        <div handle="container" class="coral-Table-wrapper-container" role="presentation" coral-table-scroll="" style="margin-top: 37px; height: calc(100% - 39px);">
            <table handle="table" class="coral-Table" role="grid" aria-multiselectable="true">
                <thead is="coral-table-head" sticky="" class="coral-Table-head coral-Table-divider--row" divider="row" role="rowgroup">
                    <tr is="coral-table-row" class="coral-Table-row" role="row" aria-selected="false">
                        <th is="coral-table-headercell" alignment="null" class="coral-Table-headerCell" sortabledirection="default" aria-sort="none" role="columnheader" scope="col" style="min-width: 120px;">
                            <coral-table-headercell-content style="padding-top: 10.1667px; top: 0px; width: 202.667px;">Date</coral-table-headercell-content>
                        </th>
                        <th is="coral-table-headercell" alignment="null" class="coral-Table-headerCell" sortabledirection="default" aria-sort="none" role="columnheader" scope="col" style="min-width: 300px;">
                            <coral-table-headercell-content style="width: 50px; padding-top: 10.1667px; top: 0px;">Component</coral-table-headercell-content>
                        </th>
                        <th is="coral-table-headercell" alignment="null" class="coral-Table-headerCell" sortabledirection="default" aria-sort="none" role="columnheader" scope="col" style="min-width: 90px;">
                            <coral-table-headercell-content style="width: 203.333px; padding-top: 10.1667px; top: 0px;">Action</coral-table-headercell-content>
                        </th>

                    </tr>
                </thead>
                <tbody is="coral-table-body" class="coral-Table-body coral-Table-divider--row" divider="row" role="rowgroup">
                   <%
                        String assetPath = request.getParameter("item");
                        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
                        Session session = resourceResolver.adaptTo(Session.class);
                        String metadataPath = resource.getPath() + "/jcr:content/metadata/";
                        Resource metadataRes = resourceResolver.getResource(metadataPath);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("path", assetPath);
                        map.put("property", "councilPageUrl");
                        map.put("property.operation", "exists");
                        map.put("1_property","assetUsedDate");
                        map.put("orderby", "1_property");
                        map.put("orderby.sort","desc");
                        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
                        SearchResult result = query.getResult();
                        Iterator<Resource> it = result.getResources();
                        if (it != null) {
                           while (it.hasNext()) {
                                Resource child = (Resource)it.next();
                               //out.print("child path----"+child.getPath());
                                Node prppertyNode =  child.adaptTo(Node.class);
                                String dateTime = prppertyNode.getProperty("assetUsedDate").getValue().getString();
                                dateTime = dateTime.substring(0,19);
				                dateTime = dateTime.replace("T"," ");
                                String componentPath= prppertyNode.getProperty("componentPath").getValue().getString();
                                String action= prppertyNode.getProperty("eventType").getValue().getString();
                          %>
                                  <tr is="coral-table-row" class="coral-Table-row" role="row" aria-selected="false">
                                    <td is="coral-table-cell" alignment="null" class="coral-Table-cell" value="" role="gridcell" aria-selected="false">
                                        <span class="solution-type"><%=dateTime%></span>
                                    </td>
                                    <td class="usage-count coral-Table-cell" is="coral-table-cell" alignment="null" value="" role="gridcell" aria-selected="false"><%=componentPath%></td>
                                    <td class="last-used-date coral-Table-cell" is="coral-table-cell" align="right" alignment="null" value="" role="gridcell" aria-selected="false"><%=action%></td>
                                 </tr>
                    <%
                           }
                        }
                    %>
                </tbody>
                <colgroup>
                    <col is="coral-table-column" data-foundation-layout-table-column-name="col1" class="coral-Table-column" sortabletype="alphanumeric" sortabledirection="default" />
                    <col is="coral-table-column" data-foundation-layout-table-column-name="col2" class="coral-Table-column" sortabletype="alphanumeric" sortabledirection="default" />
                    <col is="coral-table-column" data-foundation-layout-table-column-name="col3" class="coral-Table-column" sortabletype="alphanumeric" sortabledirection="default" />
                </colgroup>
            </table>
        </div>
        <object aria-hidden="true" tabindex="-1" style="display: block; position: absolute; top: 0; left: 0; height: 100%; width: 100%; opacity: 0; overflow: hidden; z-index: -100;" type="text/html" data="about:blank">​</object>
    </table>
</div>