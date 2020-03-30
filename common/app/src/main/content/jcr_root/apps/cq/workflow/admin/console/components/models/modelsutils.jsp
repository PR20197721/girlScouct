<%--
  ADOBE CONFIDENTIAL

  Copyright 2018 Adobe Systems Incorporated
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
--%><%@ page  session="false"
              import="com.adobe.granite.ui.components.ds.ValueMapResource,
                    com.adobe.granite.workflow.model.WorkflowModel,
                    org.apache.commons.lang.StringUtils,
                    org.apache.jackrabbit.util.Text,
                    org.apache.sling.api.resource.Resource,
                    org.apache.sling.api.resource.ResourceResolver,
                    org.apache.sling.api.resource.ValueMap,
                    org.apache.sling.api.wrappers.ValueMapDecorator,
                    java.util.HashMap, org.apache.sling.api.resource.ResourceUtil,
                    com.day.cq.wcm.api.Page,
                    java.util.Arrays,
                    org.slf4j.Logger,
                    java.util.concurrent.TimeUnit,
                    java.util.Calendar" %>
<%
%><%@include file="/libs/cq/workflow/admin/console/components/launchers/launchersutils.jsp" %><%!
    private static final String WORKFLOW_MODELSPATH_VAR = "/var/workflow/models";
    private static final String WORKFLOW_MODELSPATH_ETC = "/etc/workflow/models";

    private Resource createWorkflowModelResource(ResourceResolver resourceResolver, String itemRT, WorkflowModel model) {
        ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
        String title = model.getTitle();
        if (StringUtils.isBlank(title)) {
            title = Text.getName(Text.getRelativeParent(model.getId(), 2));
        }
        vm.put("text", title);
        vm.put("value", model.getId());
        vm.put("model", model);

        vm.put("lastModified", model.getMetaDataMap().get("cq:lastModified", 0L));

        String modelId = model.getId();

        String pagePath = model.getMetaDataMap().get("cq:generatingPage", String.class);
        if (StringUtils.isBlank(pagePath)) {
            // no cq:generatingPage specified, instead convert runtime model ID into the associated designtime page path
            pagePath = StringUtils.removeEnd(modelId, "/jcr:content/model");
            if (pagePath.startsWith(WORKFLOW_MODELSPATH_VAR)) {
                pagePath = pagePath.replace(WORKFLOW_MODELSPATH_VAR, "/libs/settings/workflow/models");
            }
        } else {
            pagePath = StringUtils.removeEnd(pagePath, "/jcr:content");
        }

        vm.put("modelDesignTime", pagePath);

        return new ValueMapResource(resourceResolver, modelId, itemRT, vm);
    }

    private String rebaseModelIdToVar(final String modelId) {
        String result = modelId;
        if (StringUtils.isNotBlank(modelId) && modelId.startsWith(WORKFLOW_MODELSPATH_ETC)) {
            result = StringUtils.replace(result, WORKFLOW_MODELSPATH_ETC, WORKFLOW_MODELSPATH_VAR);
            result = StringUtils.replace(result, "/jcr:content/model", "");
        }
        return result;
    }

    private String convertPathToModelId(final String modelPath) {
        String result = modelPath;
        // if the result doesn't start with the runtime model path then we don't actually have the model
        if (!result.startsWith("/var/workflow")) {
            // we're rendering the design time version of the model, possibly as a
            // return from a search. Thus the resource.path is actually the design time model path
            // Handle the legacy /etc paths which have different layout
            if (result.startsWith(WORKFLOW_MODELSPATH_ETC)) {
                if (!result.endsWith("/jcr:content/model")) {
                    result = result + "/jcr:content/model";
                }
            }  else {
                result = result.replace("/libs/settings/workflow/models", WORKFLOW_MODELSPATH_VAR);
                result = result.replace("/conf/global/settings/workflow/models", WORKFLOW_MODELSPATH_VAR);
            }
        }
        return result;
    }

    private String getModelDesigntimePath(Resource resource) {
        String modelDesignTimePath = null;

        if (resource!=null && !ResourceUtil.isNonExistingResource(resource)) {
            // if the modelPath doesn't start with the runtime model path then we don't actually have the model
            String resourcePath = resource.getPath();
            if (!resourcePath.startsWith("/var/workflow")) {
                // we're rendering the design time version of the model, possibly as a
                // return from a search. Thus the resource.path is actually the design time model path
                // Handle the legacy /etc paths which have different layout
                if (resourcePath.startsWith(WORKFLOW_MODELSPATH_ETC)) {
                    modelDesignTimePath = resourcePath.replace("/jcr:content/model", "");
                } else {
                    modelDesignTimePath = resourcePath;
                }
            }

            // We came in with a run time model path, need design time path
            if (modelDesignTimePath == null) {
                modelDesignTimePath = resource.getValueMap().get("modelDesignTime", String.class);
            }
        }

        if(StringUtils.isNotBlank(modelDesignTimePath)){
            Resource modelDesignResource = resource.getResourceResolver().getResource(modelDesignTimePath);
            if (modelDesignResource != null && !ResourceUtil.isNonExistingResource(modelDesignResource)) {
                return modelDesignTimePath;
            }
        }

        return "";
    }

    private String getWorkflowModelImageUrl(Logger log, ResourceResolver resourceResolver, String pagePath) {

        String thumbnail = "/libs/cq/gui/components/workflow/console/default/thumbnail.png";
        if (pagePath != null) {
            Resource modelPageResource = resourceResolver.getResource(pagePath);
            if (modelPageResource == null) {
                log.warn("Unable to get page resource at: {}, using default thumbnail.", pagePath);
                return thumbnail;
            }
            Page workflowPage = modelPageResource.adaptTo(Page.class);

            if (workflowPage != null) {
                if (workflowPage.getContentResource().getChild("image") != null) {
                    thumbnail = workflowPage.getPath() + ".thumb.800.400.png" + pageCK(workflowPage);
                }
            }
        }

        return thumbnail;
    }

    private String getLastPublished(long publishedTimeInMilli) {

        List<Long> times = Arrays.asList(
                TimeUnit.DAYS.toMillis(365),
                TimeUnit.DAYS.toMillis(30),
                TimeUnit.DAYS.toMillis(1),
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MINUTES.toMillis(1),
                TimeUnit.SECONDS.toMillis(1) );

        List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

        StringBuffer res = new StringBuffer();
        for(int i=0;i< times.size(); i++) {
            Long current = times.get(i);
            long temp = publishedTimeInMilli/current;
            if(temp>0) {
                res.append(temp).append(" ").append( timesString.get(i) ).append(temp != 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if("".equals(res.toString()))
            return "0 seconds ago";
        else
            return res.toString();

    }

    private Calendar getWorkflowLastModified(ResourceResolver resourceResolver, String modelDesignTimePath, String workflowModelID, Logger log) {
        if (StringUtils.isNotBlank(modelDesignTimePath)) {
            Resource modelPageResource = resourceResolver.getResource(modelDesignTimePath);

            if (modelPageResource != null) {
                Page workflowPage = modelPageResource.adaptTo(Page.class);

                if (workflowPage != null) {
                    return workflowPage.getLastModified();
                }
            } else {
                log.warn("Unable to find model page resource {} for runtime model {}", modelDesignTimePath, workflowModelID);
            }
        }

        return null;
    }

    String pageCK(Page page) {
        ValueMap metadata = page.getProperties("image/file/jcr:content");
        if (metadata == null) return "";

        Calendar cal = metadata.get("jcr:lastModified", Calendar.class);
        if (cal == null) return "";

        return "?ck=" + (cal.getTimeInMillis() / 1000);
    }

    private boolean isModelOverride(ResourceResolver resolver, String modelDesignTime) {
        if (StringUtils.isBlank(modelDesignTime) || !modelDesignTime.startsWith("/conf/global/settings/workflow/models")) {
            return false;
        }

        String libsModel = modelDesignTime.replace("/conf/global/settings/workflow/models", "/libs/settings/workflow/models");
        Resource modelResource = resolver.getResource(libsModel);
        if (modelResource != null) {
            return true;
        }

        String etcModel = modelDesignTime.replace("/conf/global/settings/workflow/models", WORKFLOW_MODELSPATH_ETC);
        modelResource = resolver.getResource(etcModel);
        if (modelResource != null) {
            return true;
        }

        return false;
    }

%>