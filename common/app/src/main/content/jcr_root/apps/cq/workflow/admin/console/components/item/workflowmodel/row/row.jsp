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
                 com.adobe.granite.workflow.WorkflowSession,
                  com.day.cq.replication.ReplicationStatus,
                 java.util.Arrays,
                 com.adobe.granite.security.user.util.AuthorizableUtil,
                 java.util.Map.Entry"%>
<%@ page import="java.util.*" %>
<%
%><%@include file="/libs/granite/ui/global.jsp"%><%
%><%@include file="/apps/cq/workflow/admin/console/components/models/modelsutils.jsp" %><%

    final WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
    final String ctx = request.getContextPath();

    String workflowModelId = convertPathToModelId(resource.getPath());
    String modelDesignTimePath = getModelDesigntimePath(resource);

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

    String thumbnailUrl = getWorkflowModelImageUrl(log, resourceResolver, modelDesignTimePath);
    thumbnailUrl = xssAPI.getValidHref(ctx + thumbnailUrl);

    String xssDescription = xssAPI.encodeForHTML(i18n.getVar(model.getDescription()));
    String xssPublish = xssAPI.encodeForHTML(i18n.getVar("Published"));
    Resource designResource = resourceResolver.getResource(modelDesignTimePath + "/jcr:content");

    String publishState = null;
    ValueMap designVM = null;

    Date designLastModified = null;
    Long modelLastMod = 0L;
    String mydate = null;
    String synced = i18n.getVar("Synced");

    List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");
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

    Boolean isSynced = false;
    String xssTitle = xssAPI.encodeForHTML(i18n.getVar(model.getTitle()));
    String xssVersion = xssAPI.encodeForHTML(model.getVersion());
    Map<String, Object> badgesVm = new LinkedHashMap<String, Object>();
    Boolean isTransient = model.getMetaDataMap().get("transient", Boolean.class);
    Long syncLastMod = model.getMetaDataMap().get("cq:lastModified", Long.class);
    if(syncLastMod != null && syncLastMod > 1) {
        syncLastMod = syncLastMod / 1000;

        //synched
        if (syncLastMod >= modelLastMod) {
            isSynced = true;
        }
    }

    String actionRels = "cq-workflow-admin-models-action-open cq-workflow-admin-models-action-run cq-workflow-admin-models-action-delete cq-workflow-admin-models-action-copy cq-workflow-admin-actions-publish-activator cq-workflow-admin-actions-quickpublish-activator cq-workflow-admin-models-action-revert";

    // last mod
    Calendar lastModified = getWorkflowLastModified(resourceResolver, modelDesignTimePath, workflowModelId, log);
    boolean isNew = false;
    if (lastModified != null) {
        Calendar oneDayAgo = Calendar.getInstance();
        oneDayAgo.add(Calendar.DATE, -1);
        isNew = lastModified.getTimeInMillis() >= oneDayAgo.getTimeInMillis();
    }

    if (modelDesignTimePath!=null) {

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

    attrs.add("is", "coral-table-row");
    attrs.add("data-cq-workflow-model-open-url", openUrl);
    attrs.add("data-cq-workflow-model-override", String.valueOf(isModelOverride(resourceResolver, modelDesignTimePath)));

    Resource publishResources = resourceResolver.getResource(modelDesignTimePath);

    ReplicationStatus replicationStatus = publishResources.adaptTo(ReplicationStatus.class);
    String xssPubStatus = xssAPI.encodeForHTMLAttr(getPublicationPendingStatus(replicationStatus,i18n));
    if(xssPubStatus.length() > 0)
        xssPublish = xssPubStatus;


%><tr <%= attrs %>>
    <td is="coral-table-cell" coral-table-rowselect><%
        if (thumbnailUrl != null) {
    %><img class="foundation-collection-item-thumbnail" src="<%= thumbnailUrl %>" alt=""><%
    } else {
    %><coral-icon class="foundation-collection-item-thumbnail" icon="folder"></coral-icon><%
        }
    %></td>
    <td class="foundation-collection-item-title" is="coral-table-cell"><%= xssTitle %></td>
    <td is="coral-table-cell"><%= xssDescription %></td>
    <td is="coral-table-cell"><%=i18n.get("v{0}", "example (Version): v1.1", xssVersion)%><%
        if (isTransient!=null && isTransient.booleanValue()) { %>, <%=xssAPI.encodeForHTML(i18n.get("Transient"))%>
        <% } %>
        <meta class="foundation-collection-quickactions" data-foundation-collection-quickactions-rel="<%= xssAPI.encodeForHTMLAttr(actionRels) %>">
    </td>
    <td is="coral-table-cell"><%
        if (isSynced == true) {
        %><coral-icon class="foundation-collection-item-thumbnail sync-indicator" icon="checkCircle" title="<%=synced%>"></coral-icon>
        <%
        }%>
    </td>
    <td is="coral-table-cell"><%
        if (publishState != null  && publishState.equalsIgnoreCase("activate")) {
        %><coral-icon class="foundation-collection-item-thumbnail  <%=xssPubStatus.length() > 0 ? "wf-publish-pending-status":"wf-publish-status"%>" icon="pending" size="xs" title="<%=xssPublish%>"></coral-icon>

        <%
        }%><%
        if(replicatedDate != null) {
            %><foundation-time type="datetime" value="<%= xssAPI.encodeForHTMLAttr(replicatedDate) %>"></foundation-time>
            <div class="foundation-layout-util-subtletext"><%= xssAPI.encodeForHTML(lastPublishedBy) %></div>
        <%
            }
        %>
    </td>
    <td is="coral-table-cell">
        <% for(Entry<String, Object> e : badgesVm.entrySet()) {
            String badgeLabel = e.getKey();
            String badgeColor = (String) e.getValue();
        %>
        <coral-tag color="<%=badgeColor%>" size="S" quiet><%=badgeLabel%></coral-tag>
        <% } %>
    </td>
</tr>