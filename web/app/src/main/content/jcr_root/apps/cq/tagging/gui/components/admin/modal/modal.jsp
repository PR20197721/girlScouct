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
 --%>
<%@include file="/libs/granite/ui/global.jsp"%>
<%@page session="false"
        import="com.adobe.granite.ui.components.Config,
        com.adobe.granite.ui.components.AttrBuilder,
        com.day.cq.i18n.I18n"%>
<sling:defineObjects/>
<% 
//This modal component creates the default modal from the specified properties
Config cfg = new Config(resource);
AttrBuilder attrs = new AttrBuilder(request, xssAPI);

String heading = i18n.getVar(cfg.get("jcr:title", String.class));
String content = i18n.getVar(cfg.get("jcr:description", String.class));
String dismissButtonText = i18n.getVar(cfg.get("dismiss-button-text", String.class));
String dismissButtonClass = cfg.get("dismiss-button-class", String.class);
String actionButtonText = i18n.getVar(cfg.get("action-button-text", String.class));
String actionButtonClass = cfg.get("action-button-class", String.class);

attrs.addClass("coral-Modal");
attrs.addClass(validate(cfg.get("rel", String.class)));
attrs.addClass(validate(cfg.get("class", String.class)));
attrs.add("id", validate(cfg.get("id", String.class)));
attrs.add("data-type", validate(cfg.get("type", String.class)));

attrs.addOthers(cfg.getProperties(), "jcr:title", "jcr:description", "dismiss-button-text",
    "dismiss-button-class", "action-button-text", "action-button-class", "id", "type", "rel", "class");
%>

<div <%=attrs.build() %>>
	<div class="coral-Modal-header">
        <i class="coral-Modal-typeIcon coral-Icon coral-Icon--sizeS"></i>
		<h2 class="coral-Modal-title coral-Heading coral-Heading--2"><%= validate(heading)%></h2>
		<button type="button" class="coral-MinimalButton coral-Modal-closeButton" data-dismiss="modal" title="<%=i18n.get("close")%>">
            <i class="coral-Icon coral-Icon--sizeXS coral-Icon--close coral-MinimalButton-icon"></i>
        </button>
	</div>
	<div class="coral-Modal-body">
		<p>
			<%=validate(content) %>
		</p>
	</div>
	<div class="coral-Modal-footer">
		<%if (dismissButtonText != null) { %>
			<button class="coral-Button <%=validate(dismissButtonClass)%>" data-dismiss="modal"><%=dismissButtonText %></button>
		<%} %>
		<%if (actionButtonText != null) { %>
			<button class="coral-Button <%=validate(actionButtonClass)%>"><%=actionButtonText %></button>
		<%} %>
	</div>
</div>

<%!
String validate(String s) {
    if (s == null || s.trim().length() == 0) {
        return "";
    }
    return s;
}
%>