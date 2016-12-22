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
--%><%
%><%@page session="false"
          import="org.apache.commons.lang.StringUtils,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.adobe.granite.ui.components.Tag" %><%
%><%@include file="/libs/granite/ui/global.jsp" %><%

    Config cfg = cmp.getConfig();

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();

    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));

%><div <%= attrs.build() %>>
    <div class="endor-Panel-title cq-common-admin-innerrail-title">
        <%= xssAPI.encodeForHTML(i18n.getVar(cfg.get("title"))) %>
        <span class="endor-Panel-close coral-Icon coral-Icon--close coral-Icon--sizeXS js-endor-innerrail-toggle"></span>
    </div>
</div>
