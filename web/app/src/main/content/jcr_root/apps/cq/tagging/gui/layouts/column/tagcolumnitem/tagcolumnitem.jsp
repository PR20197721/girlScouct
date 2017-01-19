<%--
  ADOBE CONFIDENTIAL

  Copyright 2015 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@page session="false"%><%
%><%@page import="java.util.UUID,				  
				  javax.jcr.RepositoryException,
				  javax.jcr.Session,
				  org.apache.jackrabbit.JcrConstants,
				  javax.jcr.security.AccessControlManager,
				  javax.jcr.security.Privilege,
				  org.apache.jackrabbit.util.Text,                  
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.day.cq.tagging.Tag,
                  com.day.cq.tagging.TagManager" %><%

AccessControlManager acm = null;
try {
    acm = resourceResolver.adaptTo(Session.class).getAccessControlManager();
} catch (RepositoryException e) {
    log.error("Unable to get access manager", e);
}

TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
if (tagManager == null) {
    log.error("Unable to get access manager");
    return;
}

Tag tag = resource.adaptTo(Tag.class);
if (tag == null) {
    return;
}
String title = tag.getLocalizedTitle(request.getLocale());
if (title == null) {
    title = tag.getTitle();
}
String actionRels = "";

com.adobe.granite.ui.components.Tag tagComp = cmp.consumeTag();
AttrBuilder attrs = tagComp.getAttrs();
attrs.add("itemscope", "itemscope");
attrs.add("data-path", resource.getPath()); // for compatibility
attrs.add("data-item-title", title);

attrs.addClass("coral-ColumnView-item");
attrs.add("data-href", "#" + UUID.randomUUID());

boolean tagHasChildren = tag.listChildren().hasNext();
if (tagHasChildren) {
    attrs.addOther("type", "parenttag");
    attrs.addOther("src", request.getContextPath() + "/libs/cq/tagging/gui/content/tags/miller/columnview{.offset,limit}.html" + Text.escapePath(resource.getPath()));
    attrs.addClass("coral-ColumnView-item--hasChildren");    
} else {   
    attrs.addOther("type", "leaftag");
    attrs.addOther("src", request.getContextPath() + "/libs/cq/tagging/gui/content/tags/miller/previewcolumn.html" + Text.escapePath(resource.getPath()));

}
actionRels = getActionRels(resource, acm);

%><a <%= attrs.build() %>>
    <div class="coral-ColumnView-icon">
        <%if (!tag.isNamespace()) { 
            if (tagHasChildren) { %>
                <i class="coral-Icon coral-Icon--tags coral-Icon--sizeM"></i>
         <% } else { %>
                <i class="coral-Icon coral-Icon--tag coral-Icon--sizeM"></i>
            <%}%>
        <%} else { %> <i class="coral-Icon coral-Icon--folder coral-Icon--sizeM"></i>
        <%} %>       
    </div>
    <div class="coral-ColumnView-label foundation-collection-item-title" itemprop="title" title="<%= xssAPI.encodeForHTMLAttr(title) %>"><%= xssAPI.encodeForHTML(title) %></div>
    <div class="foundation-collection-quickactions" hidden data-foundation-collection-quickactions-rel="<%= xssAPI.encodeForHTMLAttr(actionRels) %>"></div>
</a><%!

    private String getActionRels(Resource resource, AccessControlManager acm) throws RepositoryException {
        String actionRels = "";       
        String resPath = resource.getPath();
        Resource parentResource = resource.getParent();
        if (parentResource != null) { 
            Privilege[] createNamespacePrivs = new Privilege[]{(acm.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES))};
            if (acm.hasPrivileges(parentResource.getPath(), createNamespacePrivs)) {
                actionRels += " cq-tagging-touch-actions-createnamespace-activator"; 
            }
        }
        Privilege[] createTagPrivs = new Privilege[]{(acm.privilegeFromName(Privilege.JCR_ADD_CHILD_NODES))};
        if (acm.hasPrivileges(resPath, createTagPrivs)) {
            actionRels += " cq-tagging-touch-actions-createtag-activator"; 
        }
        Privilege[] editTagPrivs = new Privilege[]{(acm.privilegeFromName(Privilege.JCR_WRITE))};
        if (acm.hasPrivileges(resPath, editTagPrivs)) {
            actionRels += " cq-tagging-touch-actions-edittag-activator"; 
        }
        Privilege[] replicateTagPrivs = new Privilege[]{(acm.privilegeFromName("crx:replicate"))};
        if (acm.hasPrivileges(resPath, replicateTagPrivs)) {
            actionRels += " cq-tagging-touch-actions-publishtag-activator";
            actionRels += " cq-tagging-touch-actions-unpublishtag-activator";
        }
        Privilege[] removeTagPrivs = new Privilege[]{(acm.privilegeFromName(Privilege.JCR_REMOVE_NODE))};
        if (acm.hasPrivileges(resPath, removeTagPrivs)) {
            actionRels += " cq-tagging-touch-actions-movetag-activator";
            actionRels += " cq-tagging-touch-actions-mergetag-activator";
            actionRels += " cq-tagging-touch-actions-deletetag-activator";
        }        
		actionRels += " cq-tagging-touch-actions-viewproperties-activator";

        return actionRels;
    }
%>
