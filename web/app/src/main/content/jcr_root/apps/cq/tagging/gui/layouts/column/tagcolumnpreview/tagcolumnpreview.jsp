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
%><%@page import="java.util.Calendar,
				  java.util.Locale,
                  java.util.Iterator,
                  com.day.cq.tagging.Tag,
				  org.apache.sling.api.resource.ValueMap,
                  com.adobe.granite.ui.components.AttrBuilder,
                  com.adobe.granite.ui.components.Config,
                  com.day.cq.commons.date.RelativeTimeFormat" %><%

Config cfg = cmp.getConfig();
String path = cmp.getExpressionHelper().getString(cfg.get("path", String.class));
if (path == null) return;

Resource contentResource = resourceResolver.getResource(path);

if (contentResource == null) return;

Tag tag = contentResource.adaptTo(Tag.class);
if (tag == null) return;

Locale locale = request.getLocale();
RelativeTimeFormat rtf = new RelativeTimeFormat("r", slingRequest.getResourceBundle(locale));

long tagLastModification = tag.getLastModified();
ValueMap vm = contentResource.adaptTo(ValueMap.class);
if (tagLastModification == 0) {
    Calendar created = vm.get("jcr:created", Calendar.class);
    tagLastModification = (null != created) ? created.getTimeInMillis() : 0;
}
String lastModified = i18n.getVar(rtf.format(tagLastModification, true));
String lastModifiedBy = tag.getLastModifiedBy();

if (lastModifiedBy == null) {
    lastModifiedBy = vm.get("jcr:createdBy", String.class);
}

Calendar replicated = vm.get("cq:lastReplicated", Calendar.class);
long tagLastReplication = (null != replicated) ? replicated.getTimeInMillis() : 0;
String lastReplicated = i18n.getVar(rtf.format(tagLastReplication, true));

String lastReplicatedBy = vm.get("cq:lastReplicatedBy", String.class);
long referenceCount = tag.getCount();
String tagDescription = tag.getDescription();
com.adobe.granite.ui.components.Tag tagComp = cmp.consumeTag();
AttrBuilder attrs = tagComp.getAttrs();
attrs.addClass("coral-ColumnView-column");

%><div <%= attrs.build() %>>
    <div class="coral-ColumnView-column-content coral-ColumnView-preview">

        <div class="tag-preview-heading">
            <div class="coral-ColumnView-preview-label tag-preview-name-label"><%= i18n.get("Name") %></div>
            <div class="coral-ColumnView-preview-value tag-preview-name-value"><%= xssAPI.encodeForHTML(tag.getName()) %></div>
        </div>

        <div class="tag-preview-edit-info">
            <div class="tag-preview-edit-info-icon-div">
                <i class="coral-Icon coral-Icon--edit tag-preview-edit-info-icon"></i>
            </div>
            <div class="tag-preview-edit-info-modification">
                <div class="coral-ColumnView-preview-value tag-preview-edit-info-modified"><%=lastModified%></div>
                <div class="coral-ColumnView-preview-label tag-preview-name-label"><%=lastModifiedBy%></div>
            </div>
        </div><%
        if (lastReplicated != null && lastReplicatedBy != null) {%>
            <div class="tag-preview-edit-info">
                <div class="tag-preview-edit-info-icon-div">
                    <i class="coral-Icon coral-Icon--globe tag-preview-edit-info-icon"></i>
                </div>
                <div class="coral-ColumnView-preview-value tag-preview-edit-info-modified"><%=lastReplicated%></div>
                <div class="coral-ColumnView-preview-label tag-preview-name-label"><%=lastReplicatedBy%></div>
            </div>
        <%} 
        if (tagDescription != null && !tagDescription.equals("")) {%>
            <div class="tag-preview-description-div">
                <div class="tag-preview-description"><%= xssAPI.encodeForHTML(i18n.get(tag.getDescription()))%></div>
            </div>
        <%}%>
        <script>    
            function triggerReferences(elem) {
                var $target = $(elem);
                var $badge = $($target.find(".tag-preview-references-badge")[0]);
                if ($badge.hasClass("is-empty")) {
                    return;
                }
                /*$(".js-endor-innerrail-toggle[data-target='#aem-tags-rail-viewproperties']")
                .trigger("click", ["forceDetail"]);*/
                $(".js-endor-innerrail-toggle[data-target='#cq-rail-references']")
                    .trigger("click", ["forceDetail"]);

            }
        </script>        
        <div class="tag-preview-references-div" onclick="triggerReferences(this)"><%
        if (referenceCount > 0) {%>
            <div class="endor-Badge tag-preview-references-badge"><%=referenceCount%></div>            
            <div class="tag-preview-references-text"><%= i18n.get("SHOW TAG REFERENCES") %></div>
        <%} else {%>
            <div class="endor-Badge tag-preview-references-badge is-empty">0</div>
            <div class="coral-ColumnView-preview-value tag-preview-references-text"><%= i18n.get("No References to list") %></div>
        <%}%>            
        </div>
    </div>
</div>
