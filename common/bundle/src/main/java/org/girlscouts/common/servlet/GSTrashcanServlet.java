package org.girlscouts.common.servlet;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.constants.TrashcanConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.util.TrashcanUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Trashcan Servlet", "sling.servlet.methods=" + HttpConstants.METHOD_GET, "sling.servlet.extensions=workflow", "sling.servlet.resourceTypes=" + "girlscouts/servlets/trashcan"})
public class GSTrashcanServlet extends SlingAllMethodsServlet implements OptingServlet, TrashcanConstants {
    private static final Logger log = LoggerFactory.getLogger(GSTrashcanServlet.class);
    /**
     *
     */
    private static final long serialVersionUID = -1341523521748738122L;
    private boolean isAuthor = false;
    @Reference
    private SlingSettingsService settingsService;
    @Reference
    private WorkflowService workflowService;
    @Reference
    protected ResourceResolverFactory resolverFactory;
    protected Map<String, Object> serviceParams;

    @Activate
    private void activate() {
        if (settingsService.getRunModes().contains("author")) {
            this.isAuthor = true;
        }
        this.serviceParams = new HashMap<String, Object>();
        this.serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    @Override
    protected void doPut(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        if (isAuthor) {
            if (request.getParameter("payload") != null) {
                JsonObject json = new JsonObject();
                try {
                    String payloadPath = request.getParameter("payload");
                    if (payloadPath != null && payloadPath.trim().length() > 0) {
                        ResourceResolver rr = null;
                        try {
                            rr = resolverFactory.getServiceResourceResolver(this.serviceParams);
                            Resource payloadResource = rr.resolve(payloadPath);
                            if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                String path = "";
                                if (payloadPath.startsWith(ASSET_TRASHCAN_PATH) || payloadPath.startsWith(PAGE_TRASHCAN_PATH)) {
                                    String restorePath = "";
                                    if (request.getParameter("restorePath") != null) {
                                        restorePath = request.getParameter("restorePath");
                                    }
                                    json.addProperty("action", "restore");
                                    path = restoreFromTrashcan(payloadResource, restorePath);
                                } else {
                                    json.addProperty("action", "trash");
                                    path = moveToTrashcan(payloadResource);
                                }
                                try {
                                    json.addProperty("success", true);
                                    json.addProperty("origin_path", path);
                                    json.addProperty("destination_path", path);
                                    response.setStatus(SlingHttpServletResponse.SC_OK);
                                    response.setContentType("application/json");
                                    response.getWriter().write(new Gson().toJson(json));
                                } catch (IOException e) {
                                    log.error("Error occurred:", e);
                                }
                            } else {
                                throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " doesn't exist");
                            }
                        } catch (LoginException e) {
                            log.error("Error Occurred: ", e);
                        } finally {
                            if(rr != null){
                                try {
                                    rr.close();
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                } catch (GirlScoutsException e) {
                    log.error("Error occurred:", e);
                    try {
                        json.addProperty("success", false);
                        json.addProperty("errorCause", e.getReason());
                        response.setStatus(SlingHttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(new Gson().toJson(json));
                    } catch (Exception ex) {
                        log.error("Error occurred:", ex);
                    }
                } catch (Exception e) {
                    log.error("Error occurred:", e);
                    try {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                    } catch (Exception ex) {
                        log.error("Error occurred:", ex);
                    }
                }
            } else {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Missing payload parameter");
                } catch (Exception ex) {
                    log.error("Error occurred:", ex);
                }

            }
        }
    }

    private String restoreFromTrashcan(Resource payloadResource, String restorePath) throws WorkflowException, RepositoryException, GirlScoutsException {
        if (TrashcanUtil.restorePathExists(payloadResource, restorePath)) {
            return invokeTrashcanRestoreWorkflow(payloadResource, restorePath);
        }
        return payloadResource.getPath();
    }

    private String moveToTrashcan(Resource payloadResource) throws RepositoryException, WorkflowException, GirlScoutsException {
        if (!TrashcanUtil.isPublished(payloadResource)) {
            if (!TrashcanUtil.hasReferences(payloadResource)) {
                if (!TrashcanUtil.isLiveCopy(payloadResource)) {
                    boolean isAsset = payloadResource.isResourceType("dam:Asset");
                    if (isAsset || (!isAsset && !TrashcanUtil.hasChildren(payloadResource))) {
                        return invokeTrashcanWorkflow(isAsset, payloadResource);
                    }
                }
            }
        }
        return payloadResource.getPath();
    }

    private String invokeTrashcanWorkflow(boolean isAsset, Resource payloadResource) throws WorkflowException, RepositoryException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(payloadResource.getResourceResolver().adaptTo(Session.class));
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-move-to-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        String trashItemPath = TrashcanUtil.getTrashItemPath(isAsset, payloadResource);
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadResource.getPath());
        wfData.getMetaDataMap().put(TRASH_PATH_PROP_NAME, trashItemPath);
        // Invoke the Workflow
        wfSession.startWorkflow(wfModel, wfData);
        return trashItemPath;
    }

    private String invokeTrashcanRestoreWorkflow(Resource payloadResource, String restorePath) throws WorkflowException, RepositoryException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(payloadResource.getResourceResolver().adaptTo(Session.class));
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-restore-from-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        if(restorePath == null || restorePath.trim().length() == 0) {
            restorePath = TrashcanUtil.getRestoreItemPath(payloadResource);
        }
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadResource.getPath());
        wfData.getMetaDataMap().put(RESTORE_PATH_PROP_NAME, restorePath);
        // Invoke the Workflow
        wfSession.startWorkflow(wfModel, wfData);
        return restorePath;
    }

    /**
     * OptingServlet Acceptance Method
     **/
    @Override
    public final boolean accepts(SlingHttpServletRequest request) {
        /*
         * Add logic which inspects the request which determines if this servlet
         * should handle the request. This will only be executed if the
         * Service Configuration's paths/resourcesTypes/selectors accept the request.
         */
        return true;
    }

}