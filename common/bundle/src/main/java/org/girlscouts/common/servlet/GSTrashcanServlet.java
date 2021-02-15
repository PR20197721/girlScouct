package org.girlscouts.common.servlet;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.constants.TrashcanConstants;
import org.girlscouts.common.exception.GirlScoutsException;
import org.girlscouts.common.rest.entity.trashcan.TrashcanItem;
import org.girlscouts.common.rest.entity.trashcan.TrashcanRequest;
import org.girlscouts.common.util.TrashcanUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * The type Gs trashcan servlet.
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Girl Scouts Trashcan Servlet", "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.extensions=workflow", "sling.servlet.resourceTypes=girlscouts/servlets/trashcan"})
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
    /**
     * The Resolver factory.
     */
    @Reference
    protected ResourceResolverFactory resolverFactory;
    /**
     * The Service params.
     */
    protected Map<String, Object> serviceParams;

    /**
     * The Replicator.
     */
    @Reference
    Replicator replicator;

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
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        processRequest(request, response);
    }

    private void processRequest(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        if (isAuthor) {
            TrashcanRequest trashcanRequest = getTrashcanRequest(request);
            if (trashcanRequest != null && trashcanRequest.getItems() != null) {
                JsonObject json = new JsonObject();
                String path = "";
                json.addProperty("action", trashcanRequest.getAction());
                ResourceResolver workflowResourceResolver = null;
                ResourceResolver userResourceResolver = null;
                List<TrashCanError> errors = new ArrayList<>();
                List<TrashCanError> forceDeleteRefSuccess = new ArrayList<>();
                boolean isAsset = false;
                try {
                    userResourceResolver = request.getResourceResolver();
                    workflowResourceResolver = resolverFactory.getServiceResourceResolver(this.serviceParams);
                    Resource payloadResource =null;
                    /*Before doing anything lets remove references if it is asked and move the assert to Trashcan
                      and at the same time lets remove it from TrashcanItem so that these items does not get processed multiple times. */

                    //GSAWDO-61 - checking if this request is for force delete references., if yes, lets perform it and then move it to trashcan.
                    if(trashcanRequest.getAction().equals("trash") && trashcanRequest.getForceDeleteRef()){
                        List<String> location = trashcanRequest.getRefErrorAssertLocation();
                        Session session = userResourceResolver.adaptTo(Session.class);
                        for(int i =0 ; i <location.size() ;i++) {
                            payloadResource = userResourceResolver.getResource(location.get(i));
                            if(null!=payloadResource) {
                                Set<String> referenceSearchResultSet = TrashcanUtil.forceDeleteReference(userResourceResolver, payloadResource);
                                //Since force deletion of references is done lets invoke Trashcan Servlet for these items.
                                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                    isAsset = payloadResource.isResourceType("dam:Asset");
                                    path = invokeTrashcanWorkflow(isAsset, payloadResource, workflowResourceResolver);
                                    /*Adding to the forceDeleteRefSuccess list.
                                    This is to inform author that reference deletion is success ,in a case when there are other asserts
                                    which are throwing errors while moving them to trashcan.*/
                                    String message=null;
                                    if(trashcanRequest.getForceRepublishUpdatedPages()) {
                                        //Lets replicate the pages first and then do anything else
                                        for(String referenceResource : referenceSearchResultSet) {
                                            //if the path has "/en/pagecounter", lets not replicate it
                                            if(!referenceResource.contains("/en/pagecounter")) {
                                                replicate(userResourceResolver.getResource(referenceResource), session);
                                            }
                                        }

                                        if(isAsset){
                                            message="All the reference to <b>" + location.get(i) +"</b> are successfully removed" +
                                                    " and the Assert is moved to Trashcan." +
                                                    "Also the pages which are having this assert as reference are published successfully.";
                                        }else{
                                            message="All the reference to <b>" + location.get(i) +"</b> are successfully removed" +
                                                    " and the page is moved to Trashcan." +
                                                    "Also the pages which are having this page as reference are published successfully.";
                                        }
                                    }else{
                                        if(isAsset){
                                            message="All the reference to <b>" + location.get(i) +"</b> are successfully removed" +
                                                    " and the Assert is moved to Trashcan.";
                                        }else{
                                            message="All the reference to <b>" + location.get(i) +"</b> are successfully removed" +
                                                    " and the page is moved to Trashcan.";
                                        }
                                    }
                                    forceDeleteRefSuccess.add(new TrashCanError(message));
                                }
                            }
                        }
                        //Lets remove the above items from  TrashcanItem so that these items dosen't get processed multiple times
                        for (Iterator<TrashcanItem> trashcanItemItr = trashcanRequest.getItems().listIterator(); trashcanItemItr.hasNext(); ) {
                            TrashcanItem trashcanItem = trashcanItemItr.next();
                            for(int i =0 ; i <location.size() ;i++) {
                                if (trashcanItem.getSource().equals(location.get(i))) {
                                    trashcanItemItr.remove();
                                }
                            }
                        }
                    }

                    for (TrashcanItem item : trashcanRequest.getItems()) {
                        String sourcePath = item.getSource();
                        String targetPath = item.getTarget();
                        if (sourcePath != null && sourcePath.trim().length() > 0) {
                            payloadResource = userResourceResolver.resolve(sourcePath);
                            if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                if ("restore".equals(trashcanRequest.getAction())) {
                                    try {
                                        isValidToRestore(payloadResource, targetPath);
                                    } catch (GirlScoutsException e) {
                                        errors.add(new TrashCanError(e.getReason(),e.getTypeOfException(),sourcePath));
                                    }
                                } else {
                                    try {
                                        isValidToTrashcan(payloadResource);
                                    } catch (GirlScoutsException e) {
                                        errors.add(new TrashCanError(e.getReason(),e.getTypeOfException(),sourcePath));
                                    }
                                }
                            } else {
                                errors.add(new TrashCanError("Item at path " + sourcePath + " doesn't exist"));
                            }
                        }
                    }
                    if(errors.size()>0){
                        errors.addAll(forceDeleteRefSuccess);
                        StringBuffer sb = new StringBuffer();
                        sb.append("<ol>");
                        for(TrashCanError error : errors){
                            sb.append("<li>"+error.getErrorDetail()+"</li>");
                        }
                        sb.append("</ol>");
                        throw new GirlScoutsException(new Exception(), sb.toString());
                    }
                    for (TrashcanItem item : trashcanRequest.getItems()) {
                        try {
                            String sourcePath = item.getSource();
                            String targetPath = item.getTarget();
                            if (sourcePath != null && sourcePath.trim().length() > 0) {
                                payloadResource = userResourceResolver.resolve(sourcePath);
                                if (payloadResource != null && !payloadResource.isResourceType(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                                    isAsset = payloadResource.isResourceType("dam:Asset");
                                    if ("restore".equals(trashcanRequest.getAction())) {
                                        path = invokeTrashcanRestoreWorkflow(payloadResource, targetPath);
                                    }else{
                                        path = invokeTrashcanWorkflow(isAsset, payloadResource, workflowResourceResolver);
                                    }
                                } else {
                                    throw new GirlScoutsException(new Exception(), "Item at path " + sourcePath + " doesn't exist");
                                }
                            }
                        } catch (Exception e) {
                            log.error("Error occurred:", e);
                            try {
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                            } catch (Exception ex) {
                                log.error("Error occurred:", ex);
                            }
                        }
                    }
                    json.addProperty("success", true);
                    //json.addProperty("origin_path", path);
                    json.addProperty("destination_path", path);
                    response.setStatus(SlingHttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write(new Gson().toJson(json));
                } catch (GirlScoutsException e) {
                    log.error("Error occurred:", e);
                    try {
                        //FOR GSAWDO-61 - showing dialog if there is reference to the assert selected for trashcan.
                        Boolean hasReference = false;
                        StringBuffer hasReferenceString  = new StringBuffer();
                        JsonArray jsonArray = new JsonArray();
                        for(TrashCanError error : errors){
                            String typeOfException = error.getTypeOfException();
                            if(null != typeOfException && typeOfException.equals("typeHasReference")){
                                hasReference = true;
                                jsonArray.add(error.getErrorLocation());
                            }
                        }

                        if(hasReference){
                            json.addProperty("hasReferenceErrorType",true);
                            json.add("hasReferenceAssertLocation",jsonArray);
                            hasReferenceString.append("<ol><form>");
                            hasReferenceString.append("<div><input type=\"checkbox\" id=\"forceDeleteRef\" name=\"forceDeleteRef\" value=\"forceDeleteRef\"></div>");
                            if(isAsset) {
                                hasReferenceString.append("<label for=\"forceDeleteRef\">Force delete references? (force deleting image references will be shown as broken images)</label><br>");
                            }else{
                                hasReferenceString.append("<label for=\"forceDeleteRef\">Force delete references? <br>");
                            }
                            hasReferenceString.append("<div><input type=\"checkbox\" id=\"forceRepublishUpdatedPages\" name=\"forceRepublishUpdatedPages\" value=\"forceRepublishUpdatedPages\"></div>");
                            hasReferenceString.append("<label for=\"forceRepublishUpdatedPages\">Force republish updated pages?</label><br>");
                            hasReferenceString.append("</form></ol>");
                        }else if(forceDeleteRefSuccess.size()>0){ // This is so that can,we can refresh the page in frontend if any processing of references has happen.
                            json.addProperty("referenceErrorTypeProcessed",true);
                        }

                        json.addProperty("success", false);
                        json.addProperty("errorCause", e.getReason()+hasReferenceString.toString());
                        response.setStatus(SlingHttpServletResponse.SC_OK);
                        response.setContentType("application/json");
                        response.getWriter().write(new Gson().toJson(json));
                    } catch (Exception ex) {
                        log.error("Error occurred:", ex);
                    }
                } catch (Exception e) {
                    log.error("Error Occurred: ", e);
                } finally {
                    if (workflowResourceResolver != null) {
                        try {
                            workflowResourceResolver.close();
                        } catch (Exception e) {
                        }
                    }
                    if (userResourceResolver != null) {
                        try {
                            userResourceResolver.close();
                        } catch (Exception e) {
                        }
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

    private TrashcanRequest getTrashcanRequest(SlingHttpServletRequest request) {
        TrashcanRequest trashcanRequest = null;

        try {
            List<RequestParameter> params = request.getRequestParameterList();
            Gson gson = new Gson();
            trashcanRequest = gson.fromJson(params.get(0).getName(), TrashcanRequest.class);
        } catch (Exception e) {
            log.error("Error Occurred:", e);
        }
        return trashcanRequest;
    }

    private String restoreFromTrashcan(Resource payloadResource, String restorePath, ResourceResolver userResourceResolver) throws WorkflowException, RepositoryException, GirlScoutsException {
        if (TrashcanUtil.restorePathExists(payloadResource, restorePath)) {
            if (TrashcanUtil.isAllowedToRestore(payloadResource, restorePath)) {
                return invokeTrashcanRestoreWorkflow(payloadResource, restorePath);
            }
        }
        return payloadResource.getPath();
    }

    private String moveToTrashcan(Resource payloadResource, ResourceResolver workflowResourceResolver) throws RepositoryException, WorkflowException, GirlScoutsException {
        if (TrashcanUtil.isAllowedToTrash(payloadResource)) {
            if (!TrashcanUtil.isPublished(payloadResource)) {
                if (!TrashcanUtil.hasReferences(payloadResource)) {
                    if (!TrashcanUtil.isLiveCopy(payloadResource)) {
                        boolean isAsset = payloadResource.isResourceType("dam:Asset");
                        if (isAsset || (!isAsset && !TrashcanUtil.hasChildren(payloadResource))) {
                            return invokeTrashcanWorkflow(isAsset, payloadResource, workflowResourceResolver);
                        }
                    }
                }
            }
        }
        return payloadResource.getPath();
    }

    private String invokeTrashcanWorkflow(boolean isAsset, Resource payloadResource, ResourceResolver workflowResourceResolver) throws WorkflowException, RepositoryException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(payloadResource.getResourceResolver().adaptTo(Session.class));
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-move-to-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        String trashItemPath = TrashcanUtil.getTrashItemPath(isAsset, payloadResource, workflowResourceResolver);
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadResource.getPath());
        wfData.getMetaDataMap().put(TRASH_PATH_PROP_NAME, trashItemPath);
        // Invoke the Workflow
        wfSession.startWorkflow(wfModel, wfData);
        trashItemPath = trashItemPath.substring(0,trashItemPath.lastIndexOf("/"));
        return trashItemPath;
    }

    private Boolean isValidToRestore(Resource payloadResource, String restorePath) throws RepositoryException, GirlScoutsException {
        log.debug("Checking if item "+payloadResource.getPath()+" can be restored");
        if (TrashcanUtil.isAllowedToRestore(payloadResource, restorePath)) {
            if (TrashcanUtil.restorePathExists(payloadResource, restorePath)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isValidToTrashcan(Resource payloadResource) throws RepositoryException, GirlScoutsException {
        log.debug("Checking if item "+payloadResource.getPath()+" can be moved to trash");
        if (TrashcanUtil.isAllowedToTrash(payloadResource)){
            if(!TrashcanUtil.isPublished(payloadResource)){
                if(!TrashcanUtil.hasReferences(payloadResource)){
                    if(!TrashcanUtil.isLiveCopy(payloadResource)){
                        boolean isAsset = payloadResource.isResourceType("dam:Asset");
                        if (isAsset || !TrashcanUtil.hasChildren(payloadResource)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String invokeTrashcanRestoreWorkflow(Resource payloadResource, String restorePath) throws WorkflowException, RepositoryException {
        //Create a workflow session
        WorkflowSession wfSession = workflowService.getWorkflowSession(payloadResource.getResourceResolver().adaptTo(Session.class));
        // Get the workflow model
        WorkflowModel wfModel = wfSession.getModel("/var/workflow/models/girl-scouts-restore-from-trashcan");
        // Get the workflow data
        // The first param in the newWorkflowData method is the payloadType.  Just a fancy name to let it know what type of workflow it is working with.
        if (restorePath == null || restorePath.trim().length() == 0) {
            restorePath = TrashcanUtil.getRestoreItemPath(payloadResource);
        }
        String restoreName = TrashcanUtil.getRestoreItemName(payloadResource);
        WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", payloadResource.getPath());
        wfData.getMetaDataMap().put(RESTORE_PATH_PROP_NAME, restorePath);
        wfData.getMetaDataMap().put(RESTORE_NAME_PROP_NAME, restoreName);
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

    private void replicate(Resource resource, Session session) throws RepositoryException, ReplicationException {
        if(null!=session) {
            replicator.replicate(session, ReplicationActionType.ACTIVATE, resource.getPath());
        }
    }

}


/**
 * The type Trash can error.
 */
class TrashCanError{
    private String errorDetail;
    private String typeOfError;
    private String errorLocation;

    /**
     * Instantiates a new Trash can error.
     *
     * @param errorDetail   the error detail
     * @param typeOfError   the type of error
     * @param errorLocation the error location
     */
    TrashCanError(String errorDetail, String typeOfError, String errorLocation){
        this.errorDetail = errorDetail;
        this.typeOfError = typeOfError;
        this.errorLocation = errorLocation;
    }

    /**
     * Instantiates a new Trash can error.
     *
     * @param errorDetail the error detail
     */
    public TrashCanError(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    /**
     * Gets type of exception.
     *
     * @return the type of exception
     */
    public String getTypeOfException() {
        return typeOfError;
    }

    /**
     * Gets error detail.
     *
     * @return the error detail
     */
    public String getErrorDetail() {
        return errorDetail;
    }

    /**
     * Gets error location.
     *
     * @return the error location
     */
    public String getErrorLocation() {
        return errorLocation;
    }
}