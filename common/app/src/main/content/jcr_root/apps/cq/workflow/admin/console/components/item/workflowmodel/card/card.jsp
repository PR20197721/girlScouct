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
%><%@ page session="false"
           import="com.adobe.granite.ui.components.AttrBuilder,
                 com.adobe.granite.ui.components.Tag,
                 java.util.LinkedHashMap,
                 java.util.Map,
                 java.util.Map.Entry,
                 com.adobe.granite.security.user.util.AuthorizableUtil,
                 com.adobe.granite.workflow.WorkflowSession"%>
<%@ page import="java.util.*" %>
<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@include file="/apps/cq/workflow/admin/console/components/models/modelsutils.jsp" %><%

    final WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
    final String ctx = request.getContextPath();

    String workflowModelId = convertPathToModelId(resource.getPath());
    String modelDesignTimePath = getModelDesigntimePath(resource);
    String xssPublish = xssAPI.encodeForHTML(i18n.getVar("Published"));

    Resource designResource = resourceResolver.getResource(modelDesignTimePath + "/jcr:content");
    String publishState = null;
    Long modelLastMod = 0L;
    ValueMap designVM = null;
    Date designLastModified = null;
    String replicatedDate=null;
    String lastPublishedBy="";

    if(designResource != null) {
        designVM = designResource.getValueMap();
        if(designVM != null) {
            publishState = designVM.get("cq:lastReplicationAction", String.class);
            designLastModified = designVM.get("cq:lastModified", Date.class);
            if(designLastModified != null) {
                modelLastMod = designLastModified.getTime() / 1000;
            }

            Calendar cal1 = designVM.get("cq:lastReplicated", Calendar.class);
            if(cal1 != null) {
                replicatedDate = cal1.toInstant().toString();
                String publishedByPrincipal = designVM.get("cq:lastReplicatedBy", String.class);
                if(StringUtils.isNotBlank(publishedByPrincipal)) {
                    String storedFormattedName = (String) request.getAttribute(publishedByPrincipal);
                    if(StringUtils.isBlank(storedFormattedName)) {
                        lastPublishedBy = AuthorizableUtil.getFormattedName(resource.getResourceResolver(), publishedByPrincipal);
                    } else {
                        lastPublishedBy = storedFormattedName;
                    }
                }
            }
        }
    }

    String synced = i18n.getVar("Synced");

    // load
    WorkflowModel model = workflowSession.getModel(workflowModelId);
    if (model == null) {
        log.warn("Unable to resolve model: {} ", workflowModelId);
        return;
    }

    String openUrl = null;
    if (StringUtils.isNotBlank(modelDesignTimePath)) {
        openUrl = xssAPI.getValidHref(ctx + modelDesignTimePath);
    } else {
        log.warn("Design time path not found for model with ID: {}", workflowModelId);
    }

    Boolean isSynced = false;
    Long syncLastMod = model.getMetaDataMap().get("cq:lastModified", Long.class);
    if(syncLastMod != null && syncLastMod > 1) {
        syncLastMod = syncLastMod / 1000;

        //synched
        if (syncLastMod >= modelLastMod) {
            isSynced = true;
        }
    }

    String xssDescription = xssAPI.encodeForHTML(i18n.getVar(model.getDescription()));
    String xssTitle = xssAPI.encodeForHTML(i18n.getVar(model.getTitle()));
    String xssVersion = xssAPI.encodeForHTML(model.getVersion());
    Map<String, Object> badgesVm = new LinkedHashMap<String, Object>();
    Boolean isTransient = model.getMetaDataMap().get("transient", Boolean.class);

    // last mod
    Calendar lastModified = getWorkflowLastModified(resourceResolver, modelDesignTimePath, workflowModelId, log);
    boolean isNew = false;
    if (lastModified != null) {
        Calendar oneDayAgo = Calendar.getInstance();
        oneDayAgo.add(Calendar.DATE, -1);
        isNew = lastModified.getTimeInMillis() >= oneDayAgo.getTimeInMillis();
    }

    String actionssRel = "cq-workflow-admin-models-action-run cq-workflow-admin-models-action-delete cq-workflow-admin-models-action-copy cq-workflow-admin-actions-publish-activator cq-workflow-admin-actions-quickpublish-activator cq-workflow-admin-models-action-revert";

    if (modelDesignTimePath!=null) {
        actionssRel += " " + "cq-workflow-admin-models-action-open";

        if (modelDesignTimePath.startsWith("/libs")) {
            badgesVm.put(i18n.get("Default"), "grey");
            isSynced = true;
        } else if (modelDesignTimePath.startsWith("/etc")) {
            badgesVm.put(i18n.get("Legacy"), "grey");
            isSynced = true;
        }
    } else {
        badgesVm.put(i18n.get("Not Editable"), "red");
    }

    Tag tag = cmp.consumeTag();
    AttrBuilder attrs = tag.getAttrs();
    attrs.addOther("cq-workflow-model-open-url", openUrl);

    String thumbnailUrl = getWorkflowModelImageUrl(log, resourceResolver, modelDesignTimePath);
    thumbnailUrl = xssAPI.getValidHref(ctx + thumbnailUrl);

    String isModelOverride = String.valueOf(isModelOverride(resourceResolver, modelDesignTimePath));

    Resource publishResources = resourceResolver.getResource(modelDesignTimePath);

    ReplicationStatus replicationStatus = publishResources.adaptTo(ReplicationStatus.class);
    String xssPubStatus = xssAPI.encodeForHTMLAttr(getPublicationPendingStatus(replicationStatus,i18n));
    if(xssPubStatus.length() > 0)
        xssPublish = xssPubStatus;

%>


<coral-card data-cq-workflow-model-open-url="<%=openUrl%>" data-cq-workflow-model-override="<%=isModelOverride%>">
    <meta class="foundation-collection-quickactions" data-foundation-collection-quickactions-rel="<%= actionssRel %>">
    <coral-card-asset>
        <img src="<%= thumbnailUrl %>"/>
    </coral-card-asset><%
    if (isNew) {
    %><coral-card-info><%
    %>  <coral-tag color="blue" class="u-coral-pullRight"><%= xssAPI.encodeForHTML(i18n.get("New")) %></coral-tag><%
    %></coral-card-info><%
    }
%>
    <coral-card-content class="coral-Card-content">
        <coral-card-title class="foundation-collection-item-title"><%= xssTitle %></coral-card-title>
        <coral-card-propertylist>
            <coral-card-property title="<%= i18n.get("Description") %>"><%= xssDescription %></coral-card-property>
            <coral-card-property title="<%= i18n.get("Version") %>"><%= i18n.get("Version: {0}", "example: Version: 1.1", xssVersion) %></coral-card-property>
            <% if (isTransient!=null && isTransient.booleanValue()) {%>
            <coral-card-property title="<%= i18n.get("Transient") %>"><%= i18n.get("Transient") %></coral-card-property>
            <% }
            if (isSynced == true) { %>
                <coral-icon class="foundation-collection-item-thumbnail sync-indicator" icon="checkCircle" title="<%=synced%>"></coral-icon>
            <% } %>
        </coral-card-propertylist>
        <% if (publishState != null  && publishState.equalsIgnoreCase("activate")) { %>
            <coral-card-propertylist>
                <coral-card-property>
                    <coral-icon class="foundation-collection-item-thumbnail <%=xssPubStatus.length() > 0 ? "wf-publish-pending-status":"wf-publish-status"%>" icon="pending" size="s" title="<%=xssPublish%>"></coral-icon>

            <% if(replicatedDate != null) {
                %><foundation-time type="datetime" value="<%= xssAPI.encodeForHTMLAttr(replicatedDate) %>"></foundation-time>
            <% } %>
                </coral-card-property>
            </coral-card-propertylist>
        <% } %>
        <% if (badgesVm.size()>0) {%>
        <coral-card-propertylist>
            <coral-card-property>
                <% for(Entry<String, Object> e : badgesVm.entrySet()) {
                    String badgeLabel = e.getKey();
                    String badgeColor = (String) e.getValue();
                %>
                <coral-tag color="<%=badgeColor%>" size="S" quiet><%=badgeLabel%></coral-tag>
                <% } %>
            </coral-card-property>
        </coral-card-propertylist>
        <% } %>
    </coral-card-content>
</coral-card>
<coral-quickactions target="_prev" alignMy="left top" alignAt="left top">
    <coral-quickactions-item icon="check" class="foundation-collection-item-activator"><%= xssAPI.encodeForHTML(i18n.get("Select")) %></coral-quickactions-item>
</coral-quickactions>