 <%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
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

--%><%@page session="false" import="javax.jcr.Node,
                                    com.adobe.granite.references.Reference,
                                    com.adobe.granite.ui.components.AttrBuilder,
                                    org.apache.sling.api.resource.Resource,
                                    org.apache.jackrabbit.util.Text,
                                    com.day.cq.i18n.I18n
                                    " %><%
%><%@include file="/libs/foundation/global.jsp"%><%
    I18n i18n = new I18n(slingRequest);

    AttrBuilder attr = new AttrBuilder(slingRequest, xssAPI);
    Reference reference = (Reference) request.getAttribute("granite.ui.references.reference");

    if (reference == null) {
        return;
    }

    Resource source = reference.getSource();
    Resource target = reference.getTarget();

    attr.addOther("type", "tagReference");
    attr.addOther("path", target.getPath());
    attr.addClass("granite-references-item");

    Node targetNode = target.adaptTo(Node.class);
    String itemPath = "";
    String title = "";
    try {
        if (targetNode.getName().equals("jcr:content")) { // hack
            targetNode = targetNode.getParent();
        }
        title = Text.getName(targetNode.getPath());
        itemPath = targetNode.getPath();
    } catch (RepositoryException re) {
        // do nothing
    }

%><section <%= attr.build() %>>
    <div class="info tag-reference-div">
        <span class="granite-references-title">
            <span id="<%= xssAPI.encodeForHTML(itemPath) %>" class="tag-reference-title shortenpath" data-keep-host="true"><%= xssAPI.encodeForHTML(title) %></span>
        </span>
        <span class="coral-Tooltip coral-Tooltip--info coral-Tooltip--positionRight references-tooltip" data-init="tooltip" data-target="#<%= xssAPI.encodeForHTML(itemPath.replaceAll("/", "\\\\/")) %>"
            data-interactive="true"> <%= xssAPI.encodeForHTML(itemPath) %>
        </span>
    </div>
</section>
