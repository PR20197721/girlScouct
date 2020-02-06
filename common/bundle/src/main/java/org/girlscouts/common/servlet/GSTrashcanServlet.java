package org.girlscouts.common.servlet;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
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

    @Activate
    private void activate() {
        if (settingsService.getRunModes().contains("author")) {
            this.isAuthor = true;
        }
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
                try {
                    String payloadPath = request.getParameter("payload");
                    if (payloadPath != null && payloadPath.trim().length() > 0) {
                        ResourceResolver rr = request.getResourceResolver();
                        Resource payloadResource = rr.resolve(payloadPath);
                        if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                            if (payloadPath.startsWith(ASSET_TRASHCAN_PATH) || payloadPath.startsWith(PAGE_TRASHCAN_PATH)) {
                                restoreFromTrashcan(rr, payloadResource);
                            } else {
                                moveToTrashcan(rr, payloadResource);
                            }
                            try {
                                JsonObject json = new JsonObject();
                                json.addProperty("success", true);
                                response.setStatus(SlingHttpServletResponse.SC_OK);
                                response.setContentType("application/json");
                                response.getWriter().write(new Gson().toJson(json));
                            } catch (IOException e) {
                                log.error("Error occurred:", e);
                            }
                        } else {
                            throw new GirlScoutsException(new Exception(), "Item at path " + payloadPath + " doesn't exist");
                        }
                    }
                } catch (GirlScoutsException e) {
                    log.error("Error occurred:", e);
                    try {
                        JsonObject json = new JsonObject();
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

    private void restoreFromTrashcan(ResourceResolver rr, Resource payloadResource) throws WorkflowException {
        invokeTrashcanRestoreWorkflow(rr.adaptTo(Session.class), payloadResource.getPath());
    }

    private void moveToTrashcan(ResourceResolver rr, Resource payloadResource) throws RepositoryException, WorkflowException, GirlScoutsException {
        if (!TrashcanUtil.isPublished(payloadResource)) {
            if (!TrashcanUtil.hasReferences(payloadResource)) {
                if (!TrashcanUtil.isLiveCopy(payloadResource)) {
                    boolean isAsset = payloadResource.isResourceType("dam:Asset");
                    if (isAsset || (!isAsset && !TrashcanUtil.hasChildren(payloadResource))) {
                        invokeTrashcanWorkflow(rr.adaptTo(Session.class), payloadResource.getPath());
                    } else {
                        throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " has children");
                    }
                } else {
                    throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " has Live Relationship");
                }
            } else {
                throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " has references");
            }
        } else {
            throw new GirlScoutsException(new Exception(), "Item at path " + payloadResource.getPath() + " is published");
        }
    }

    private void invokeTrashcanWorkflow(Session session, String payloadPath) throws WorkflowException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(session);
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-move-to-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadPath);
        // Invoke the Workflow
        wfSession.startWorkflow(wfModel, wfData);
    }

    private void invokeTrashcanRestoreWorkflow(Session session, String payloadPath) throws WorkflowException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(session);
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-restore-from-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadPath);
        // Invoke the Workflow
        wfSession.startWorkflow(wfModel, wfData);
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