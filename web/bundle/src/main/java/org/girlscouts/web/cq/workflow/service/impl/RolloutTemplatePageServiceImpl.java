package org.girlscouts.web.cq.workflow.service.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.*;
import org.apache.sling.settings.SlingSettingsService;
import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.constants.PageReplicationConstants;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.common.util.PageReplicationUtil;
import org.girlscouts.web.cq.workflow.service.RolloutTemplatePageService;
import org.girlscouts.web.cq.workflow.service.pojo.RolloutContentDifference;
import org.girlscouts.web.service.replication.PageReplicator;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.version.VersionManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The type Rollout template page service.
 */
/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 */
@Component
@Service
public class RolloutTemplatePageServiceImpl implements RolloutTemplatePageService, PageReplicationConstants, PageReplicationConstants.Email {
    /**
     * The constant DESCRIPTION.
     */
    @Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
    static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
    /**
     * The constant VENDOR.
     */
    @Property(value = "Girl Scouts")
    static final String VENDOR = Constants.SERVICE_VENDOR;
    /**
     * The constant LABEL.
     */
    @Property(value = "Girl Scouts Roll out Service")
    static final String LABEL = "process.label";
    /**
     * The constant log.
     */
    private static Logger log = LoggerFactory.getLogger(RolloutTemplatePageServiceImpl.class);
    /**
     * The Resolver factory.
     */
    @Reference
    private ResourceResolverFactory resolverFactory;
    /**
     * The Rollout manager.
     */
    @Reference
    private RolloutManager rolloutManager;
    /**
     * The Settings service.
     */
    @Reference
    private SlingSettingsService settingsService;
    /**
     * The Gs email service.
     */
    @Reference
    public GSEmailService gsEmailService;
    /**
     * The Relation manager.
     */
    @Reference
    private LiveRelationshipManager relationManager;
    /**
     * The Page replicator.
     */
    @Reference
    private PageReplicator pageReplicator;
    /**
     * The Service params.
     */
    private Map<String, Object> serviceParams;
    /**
     * The Is publisher.
     */
    private boolean isPublisher = false;
    /**
     * The constant COOKIE_HEADER_RESOURCE_TYPE.
     */
    private static final String  COOKIE_HEADER_RESOURCE_TYPE = "girlscouts/components/standalone-cookie-header";
    /**
     * The constant ACTION_TYPE_ADDED.
     */
    private static final String  ACTION_TYPE_ADDED = "Added";
    /**
     * The constant ACTION_TYPE_REMOVED.
     */
    private static final String  ACTION_TYPE_REMOVED = "<>";
    /**
     * The constant TABLE_START.
     */
    private static final String  TABLE_START = "<table>";
    /**
     * The constant TR_START.
     */
    private static final String  TR_START = "<tr>";
    /**
     * The constant TR_END.
     */
    private static final String  TR_END = "</tr>";
    /**
     * The constant TH_START.
     */
    private static final String  TH_START = "<th>";
    /**
     * The constant TH_END.
     */
    private static final String  TH_END = "</th>";
    /**
     * The constant TD_START.
     */
    private static final String  TD_START = "<td>";
    /**
     * The constant TD_END.
     */
    private static final String  TD_END = "</td>";
    /**
     * The constant TABLE_END.
     */
    private static final String  TABLE_END = "</table>";


    /**
     * Activate.
     *
     * @param context the context
     */
    @Activate
    private void activate(ComponentContext context) {
        serviceParams = new HashMap<String, Object>();
        serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        log.info("Girlscouts Rollout Service Activated.");
        isPublisher = isPublisher();
    }

    /**
     * Rollout.
     *
     * @param path the path
     */
    @Override
    public void rollout(String path) {
        log.info("Girlscouts Rollout Service Start.");
        if (!isPublisher) {
            try {
                // N second remorse wait time in case workflow needs to be stopped.
                Thread.sleep(DEFAULT_REMORSE_WAIT_TIME);
            } catch (InterruptedException e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            ResourceResolver rr = null;
            try {
                rr = resolverFactory.getServiceResourceResolver(serviceParams);
                Session session = rr.adaptTo(Session.class);
                log.info("dateRolloutRes={}", path);
                Resource dateRolloutRes = rr.resolve(path);
                if (!dateRolloutRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    Node dateRolloutNode = dateRolloutRes.adaptTo(Node.class);
                    String srcPath = "", templatePath = "";
                    Boolean notify = false, activate = false, delay = false, useTemplate = false, newPage = false, updateReferences = false;
                    Set<String> councils = null;
                    Set<String> notifyCouncils = new TreeSet<String>();
                    Set<String> noLiveCopyCouncils = new TreeSet<String>();
                    List<String> rolloutLog = new ArrayList<String>();
                    try {
                        dateRolloutNode.setProperty(PARAM_STATUS, STATUS_PROCESSING);
                        session.save();
                        try {
                            srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("srcPath={}", srcPath);
                        try {
                            newPage = dateRolloutNode.getProperty(PARAM_NEW_PAGE).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("newPage={}", newPage);
                        try {
                            notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("notify={}", notify);
                        try {
                            updateReferences = dateRolloutNode.getProperty(PARAM_UPDATE_REFERENCES).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("updateReferences={}", updateReferences);
                        try {
                            activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("activate={}", activate);
                        try {
                            delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("delay={}", delay);
                        try {
                            useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("useTemplate={}", useTemplate);
                        try {
                            templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
                        } catch (Exception e) {
                            log.error("Girlscouts Rollout Service encountered error: ", e);
                        }
                        log.info("templatePath={}", templatePath);
                        if (useTemplate && (templatePath == null || templatePath.trim().length() == 0)) {
                            log.error("Rollout Error - Use Template checked, but no template provided. Cancelling.");
                            PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                            return;
                        }
                        councils = PageReplicationUtil.getCouncils(dateRolloutNode);
                        log.info("councils={}", councils);
                        Resource srcRes = rr.resolve(srcPath);
                        if (relationManager.isSource(srcRes)) {
                            Page srcPage = (Page) srcRes.adaptTo(Page.class);
                            if (srcPage != null) {
                                Set<String> pages = new HashSet<String>();
                                if (newPage) {
                                    processNewLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils, rr, updateReferences);
                                } else {
                                    processExistingLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils, noLiveCopyCouncils, rr, updateReferences);
                                }
                                if (!councils.isEmpty()) {
                                    int councilNameIndex = srcPath.indexOf("/", "/content/".length());
                                    String srcRelativePath = srcPath.substring(councilNameIndex);
                                    for (String council : councils) {
                                        notifyCouncils.add(council + srcRelativePath);
                                        log.info("Failed to rollout for {} council", council);
                                        rolloutLog.add("Failed to rollout for " + council + " council");
                                    }
                                }
                                dateRolloutNode.setProperty(PARAM_PAGES, pages.toArray(new String[pages.size()]));
                                dateRolloutNode.setProperty(PARAM_NOTIFY_COUNCILS, notifyCouncils.toArray(new String[notifyCouncils.size()]));
                                if(!noLiveCopyCouncils.isEmpty()) {
                                	dateRolloutNode.setProperty(PARAM_NO_LIVE_COPY_COUNCILS, noLiveCopyCouncils.toArray(new String[noLiveCopyCouncils.size()]));
                                }
                                session.save();
                            } else {
                                log.error("Resource is not a page. Quit. " + srcPath);
                                PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                                return;
                            }
                        } else {
                            log.error("Not a live copy source page. Quit. " + srcPath);
                            PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                            return;
                        }
                    } catch (Exception e) {
                        log.error("Girlscouts Rollout Service encountered error: ", e);
                        PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                        return;
                    }
                    List<String> councilNotificationLog = new ArrayList<String>();
                    Boolean isTestMode = PageReplicationUtil.isTestMode(rr);
                    try {
                        if (notify && notifyCouncils.size() > 0) {
                            sendCouncilNotifications(dateRolloutNode, councilNotificationLog, isTestMode, rr);
                        } else {
                            dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
                        }
                    } catch (Exception e) {
                        log.error("Girlscouts Rollout Service encountered error: ", e);
                    }
                    try {
                        sendGSUSANotifications(dateRolloutNode, rolloutLog, councilNotificationLog, isTestMode, rr);
                    } catch (Exception e) {
                        log.error("Girlscouts Rollout Service encountered error: ", e);
                    }
                    try {
                        if (activate) {
                            log.info("Processing Activations");
                            if (delay) {
                                log.info("Activations are delayed");
                                dateRolloutNode.setProperty(PARAM_STATUS, STATUS_DELAYED);
                                session.save();
                            } else {
                                log.info("Activations are instant, running pageReplicator");
                                pageReplicator.processReplicationNode(dateRolloutNode, rr);
                            }
                        } else {
                            log.info("Activation is not requested, job {} completed.", dateRolloutNode.getName());
                            dateRolloutNode.setProperty(PARAM_STATUS, STATUS_COMPLETE);
                            PageReplicationUtil.archive(dateRolloutNode);
                            log.info("Rollout is archived at {}.", dateRolloutNode.getPath());
                        }
                    } catch (Exception e) {
                        PageReplicationUtil.markReplicationFailed(dateRolloutNode);
                        log.error("Girlscouts Rollout Service encountered error: ", e);
                    }
                }
            } catch (LoginException e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            } finally {
                try {
                    rr.close();
                } catch (Exception e) {
                }
            }
        }
        log.info("Girlscouts Rollout Service Finished.");
    }

    /**
     * Is publisher boolean.
     *
     * @return the boolean
     */
    private boolean isPublisher() {
        log.info("checking if running on publisher instance.");
        if (settingsService.getRunModes().contains("publish")) {
            return true;
        }
        return false;
    }

    /**
     * Deactivate.
     *
     * @param componentContext the component context
     */
    @Deactivate
    private void deactivate(ComponentContext componentContext) {
        log.info("GS Page Rollout Service Deactivated.");
    }

    /**
     * Process new live relationships.
     *
     * @param submittedCouncils  the submitted councils
     * @param sourcePageResource the source page resource
     * @param pagesToActivate    the pages to activate
     * @param rolloutLog         the rollout log
     * @param notifyCouncils     the notify councils
     * @param rr                 the rr
     * @param updateReferences   the update references
     * @throws RepositoryException the repository exception
     * @throws WCMException        the wcm exception
     */
    private void processNewLiveRelationships(Set<String> submittedCouncils, Resource sourcePageResource, Set<String> pagesToActivate, List<String> rolloutLog, Set<String> notifyCouncils, ResourceResolver rr, Boolean updateReferences) throws RepositoryException, WCMException {
        log.info("Processing new live relationships.");
        Session session = rr.adaptTo(Session.class);
        Set<String> processedRelationCouncils = new HashSet<String>();
        for (String councilPath : submittedCouncils) {
            try {
                log.info("Looking up live relationships in {}", councilPath);
                Resource sourceParent = sourcePageResource.getParent();
                Resource parentTarget = getGoodParent(sourceParent, councilPath, rr, relationManager, rolloutLog);
                if (parentTarget != null && !parentTarget.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    String relationPagePath = parentTarget.getPath();
                    PageManager pageManager = rr.adaptTo(PageManager.class);
                    Page srcPage = (Page) sourcePageResource.adaptTo(Page.class);
                    Page copyPage = pageManager.copy(srcPage, relationPagePath + "/" + srcPage.getName(), srcPage.getName(), false, true);
                    RolloutConfigManager configMgr = (RolloutConfigManager) rr.adaptTo(RolloutConfigManager.class);
                    RolloutConfig gsConfig = configMgr.getRolloutConfig(GS_ROLLOUT_CONFIG);
                    log.info("Establishing relationship between: {} and {}", srcPage.getPath(), copyPage.getPath());
                    LiveRelationship newPageRelationship = relationManager.establishRelationship(srcPage, copyPage, true, false, gsConfig);
                    String targetPath = newPageRelationship.getTargetPath();
                    cancelInheritance(rr, copyPage.getPath());
                    blockReferenceUpdateAction.set("blockInitiatedFromWorkflow");
                    log.debug("Setting blockInitiatedFromWorkflow flag");
                    rolloutManager.rollout(rr, newPageRelationship, false);
                    if (updateReferences) {
                        Set<String> srcComponents = PageReplicationUtil.getComponents(sourcePageResource);
                        updateAllReferences(sourcePageResource, copyPage.adaptTo(Resource.class), srcComponents, new HashMap<String, String>());
                    }
                    session.save();
                    log.info("Page {} created", copyPage.getPath());
                    rolloutLog.add("Page " + copyPage.getPath() + " created");
                    rolloutLog.add("Live copy established");
                    if (targetPath.endsWith("/jcr:content")) {
                        targetPath = targetPath.substring(0, targetPath.lastIndexOf('/'));
                    }
                    pagesToActivate.add(targetPath);
                    rolloutLog.add("Page added to activation/cache build queue");
                    processedRelationCouncils.add(councilPath);

                }
            }catch(Exception e){
                rolloutLog.add("Exception thrown for " + councilPath+" "+e.getMessage()+" "+e.getLocalizedMessage()+", caused by "+ e.getCause().getMessage()+ " "+ e.getCause().getLocalizedMessage());
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }

        }
        submittedCouncils.removeAll(processedRelationCouncils);
    }

    /**
     * Process existing live relationships.
     *
     * @param submittedCouncils  the submitted councils
     * @param sourcePageResource the source page resource
     * @param pagesToActivate    the pages to activate
     * @param rolloutLog         the rollout log
     * @param notifyCouncils     the notify councils
     * @param noLiveCopyCouncils the no live copy councils
     * @param rr                 the rr
     * @param updateReferences   the update references
     * @throws RepositoryException the repository exception
     * @throws WCMException        the wcm exception
     */
    private void processExistingLiveRelationships(Set<String> submittedCouncils, Resource sourcePageResource, Set<String> pagesToActivate, List<String> rolloutLog, Set<String> notifyCouncils, Set<String> noLiveCopyCouncils, ResourceResolver rr, Boolean updateReferences) throws RepositoryException, WCMException {
        log.info("Processing existing live relationships.");
        Set<String> srcComponents = PageReplicationUtil.getComponents(sourcePageResource);
        Set<String> processedRelationCouncils = new HashSet<String>();
        Session session = rr.adaptTo(Session.class);
        final Workspace workspace = session.getWorkspace();
        final VersionManager versionManager = workspace.getVersionManager();
        for (String councilPath : submittedCouncils) {
            log.info("Looking up live relationships in {}", councilPath);
            RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, councilPath, null);
            boolean hasValidRelationship = false;
            String relationPagePath =  councilPath;
            while (relationsIterator.hasNext()) {
                try {
                    LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                    relationPagePath = relation.getTargetPath();
                    log.info("Attempting to roll out to: {}", relationPagePath);
                    rolloutLog.add("Attempting to roll out to: " + relationPagePath);
                    Resource relationPageResource = rr.resolve(relationPagePath);
                    if (relationPageResource != null && !relationPageResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    	hasValidRelationship= true;// this marker is required as there are non-existing node relationships 
                    	processedRelationCouncils.add(councilPath);
                        if (PageReplicationUtil.isPageInheritanceBroken(relationPageResource, rolloutLog)) {
                            notifyCouncils.add(relationPagePath);
                            rolloutLog.add("The page " + relationPagePath + " has Break Inheritance checked off. Will not roll out");
                            log.info("The page {} has Break Inheritance checked. Will not roll out", relationPagePath);
                            noLiveCopyCouncils.add(relationPagePath);//change for 2204 for the case when inheritance is broken.
                        } else {
                            String versionableNodePath = relationPageResource.getPath() + "/jcr:content";
                            try {
                                Map<String, String> sourceToTargetComponentRelations = PageReplicationUtil.getComponentRelationsByPage(srcComponents, relationPagePath, rolloutLog, rr);
                                Map<String, String> hrefReferencesMap = new HashMap<>();
                                validateRolloutConfig(sourcePageResource, relationPageResource);
                                Map<String, Set<String>> relationComponents = PageReplicationUtil.categorizeRelationComponents(relationPageResource, rolloutLog, rr);
                                Set<String> componentsToDelete = PageReplicationUtil.getComponentsToDelete(sourceToTargetComponentRelations, relationComponents, councilPath);
                                Set<String> componentsToRollout = PageReplicationUtil.getComponentsToRollout(sourceToTargetComponentRelations, relationComponents, councilPath, sourcePageResource, rolloutLog, hrefReferencesMap, updateReferences);
                                if (relationComponents.get(RELATION_CANC_INHERITANCE_COMPONENTS).size() > 0) {
                                    notifyCouncils.add(relationPagePath);
                                }
                                try {
                                    versionManager.checkout(versionableNodePath);
                                    versionManager.checkpoint(versionableNodePath);
                                } catch (Exception e) {
                                    log.error("Girlscouts Rollout Service encountered error: ", e);
                                }
                                //Check if cookie Header Component got added or removed
                                String cookieHeaderComponentAddedOrRemoved =  cookieHeaderComponentAddedOrRemoved(rr, sourceToTargetComponentRelations, relationComponents, componentsToDelete);

                                deleteComponents(rr, rolloutLog, componentsToDelete);
                                //Get the difference of content before rolling out
                                List<RolloutContentDifference> contentDifferences = compareRolloutComponents(rr, sourceToTargetComponentRelations, relationComponents);

                                rolloutComponents(sourcePageResource, rolloutLog, relationPagePath, componentsToRollout);
                                updatePageTitle(sourcePageResource, relationPageResource);
                                if (updateReferences) {
                                    updateAllReferences(sourcePageResource, relationPageResource, componentsToRollout, hrefReferencesMap);
                                }
                                pagesToActivate.add(relationPagePath);
                                rolloutLog.add("Page added to activation queue");
                                log.info("Page added to activation queue");
                                //lets send email to individual councils letting them know about what changed.
                                sendContentDifferenceEmail(rr,councilPath,contentDifferences,cookieHeaderComponentAddedOrRemoved);
                            } catch (Exception e) {
                                log.error("Girlscouts Rollout Service encountered error: ", e);
                                try {
                                    versionManager.checkin(versionableNodePath);
                                } catch (Exception e2) {
                                    log.error("Girlscouts Rollout Service encountered error: ", e);
                                }
                            }
                        }
                    } else {
                        log.info("Resource {} not found.", relationPagePath);
                        log.info("Will NOT rollout to this page");
                        rolloutLog.add("Resource " + relationPagePath + " not found.");
                        rolloutLog.add("Will NOT rollout to this page");
                    }
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
            } 
            if(!hasValidRelationship) {
            	log.info("No matching livecopy exists at target, page might be deleted or page is non existent at : "+relationPagePath);
            	noLiveCopyCouncils.add(relationPagePath);
            }
        }
        submittedCouncils.removeAll(processedRelationCouncils);
    }

    /**
     * Update all references.
     *
     * @param sourceResource    the source resource
     * @param targetResource    the target resource
     * @param sourceComponents  the source components
     * @param hrefReferencesMap the href references map
     */
    private void updateAllReferences(Resource sourceResource, Resource targetResource, Set<String> sourceComponents, Map<String, String> hrefReferencesMap) {
        if (sourceResource != null && sourceComponents != null && sourceComponents.size() > 0) {
            try {
                ResourceResolver rr = sourceResource.getResourceResolver();
                LiveRelationshipManager relationManager = rr.adaptTo(LiveRelationshipManager.class);
                String sourcePagePath = sourceResource.getPath();
                String targetPagePath = targetResource.getPath();
                String sourceBranch = PageReplicationUtil.getBranch(sourcePagePath);
                String targetBranch = PageReplicationUtil.getBranch(targetPagePath);
                for (String srcComponentPath : sourceComponents) {
                    try {
                        Resource srcComponentRes = rr.resolve(srcComponentPath);
                        RangeIterator relationsIterator = relationManager.getLiveRelationships(srcComponentRes, targetPagePath, null);
                        while (relationsIterator.hasNext()) {
                            try {
                                boolean isSaveSession = false;
                                LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                                String targetComponentPath = relation.getTargetPath();
                                Resource targetComponentResource = rr.resolve(targetComponentPath);
                                if (!ResourceUtil.isNonExistingResource(targetComponentResource)) {
                                    Node targetNode = targetComponentResource.adaptTo(Node.class);
                                    Session session = targetNode.getSession();
                                    PropertyIterator iter = targetNode.getProperties();
                                    while (iter.hasNext()) {
                                        String propertyName = "";
                                        try { // Try and catch property exception. We want to do our best.
                                            javax.jcr.Property property = iter.nextProperty();
                                            propertyName = property.getName();
                                            if (!PageReplicationUtil.isSystemProperty(propertyName)) {
                                                log.debug("Checking property " + property.getPath() + " for references");
                                                if (!property.isMultiple()) {
                                                    // Single value
                                                    if (PageReplicationUtil.checkPropertyType(property.getType())) {
                                                        String stringValue = property.getString();
                                                        stringValue = replaceBranch(stringValue, sourceBranch, targetBranch, rr, hrefReferencesMap);
                                                        if (stringValue != null) {
                                                            targetNode.setProperty(property.getName(), stringValue);
                                                            isSaveSession = true;
                                                        }
                                                    }
                                                } else {
                                                    // Multiple values
                                                    Value[] values = property.getValues();
                                                    if (values.length > 0 && PageReplicationUtil.checkPropertyType(values[0].getType())) { // Values are of the same type.
                                                        String[] stringValues = new String[values.length];
                                                        boolean replacedFlag = false;
                                                        for (int i = 0; i < values.length; i++) {
                                                            Value value = values[i];
                                                            String stringValue = value.getString();
                                                            String newStringValue = replaceBranch(stringValue, sourceBranch, targetBranch, rr, hrefReferencesMap);
                                                            if (newStringValue != null) {
                                                                stringValues[i] = newStringValue;
                                                                replacedFlag = true;
                                                            } else {
                                                                stringValues[i] = stringValue;
                                                            }
                                                        }
                                                        if (replacedFlag) {
                                                            targetNode.setProperty(property.getName(), stringValues);
                                                            isSaveSession = true;
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (RepositoryException e) {
                                            log.error("Error updating link references: node = " + targetNode.getPath() + " property = " + propertyName);
                                        }
                                    }
                                    if(isSaveSession) {
                                        try {
                                            log.info("Saving updates to {}",targetComponentPath);
                                            session.save();
                                        } catch (Exception e) {
                                            try {
                                                session.refresh(true);
                                            } catch (RepositoryException e1) {
                                                log.error("Cannot refresh the session.");
                                            }
                                            log.error("Cannot save the session.");
                                        }
                                    }
                                } else {
                                    log.debug("No valid resource at " + targetComponentPath);
                                }
                            } catch (Exception e) {
                                log.error("Error occurred:", e);
                            }
                        }
                    } catch (Exception ex) {
                        log.debug("Error occurred: ", ex);
                    }
                }
            } catch (Exception e) {
                log.debug("Error occurred: ", e);
            }
        }
    }

    /**
     * Replace branch string.
     *
     * @param value             the value
     * @param sourceBranch      the source branch
     * @param targetBranch      the target branch
     * @param resourceResolver  the resource resolver
     * @param hrefReferencesMap the href references map
     * @return the string
     */
    private String replaceBranch(String value, String sourceBranch, String targetBranch, ResourceResolver resourceResolver, Map<String, String> hrefReferencesMap) {
        Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(value);
        boolean isReplaced = false;
        List<String> hrefs = new ArrayList<>();
        boolean isHtmlRef = false;
        while (m.find()) { // If field value is HTML
            isHtmlRef = true;
            String hrefValue = m.group(1);
            log.debug("Found href: " + hrefValue);
            hrefs.add(hrefValue);
        }
        if (!isHtmlRef) { // If field value is the link
            hrefs.add(value);
        }
        for (String hrefValue : hrefs) {
            //Is href pointing to template site page?
            if (hrefValue != null && hrefValue.startsWith(sourceBranch)) {
                String referenceKey = targetBranch+":"+hrefValue;
                String newHrefValue = hrefReferencesMap.get(referenceKey);
                log.debug("Looking up in reference map: key={}, value={}",referenceKey,newHrefValue);
                if(newHrefValue == null || newHrefValue.trim().length() == 0) {
                    newHrefValue = hrefValue.replace(sourceBranch, targetBranch);
                    log.debug("Replacing : " + sourceBranch + " with " + targetBranch);
                }
                log.debug("New href value: " + newHrefValue);
                String pathToTargetPage = newHrefValue.replace(".html", "");
                Resource resource = resourceResolver.resolve(pathToTargetPage);
                if (ResourceUtil.isNonExistingResource(resource)) {
                    log.debug("No page at: " + pathToTargetPage);
                    String lookedUpHrefValue = PageReplicationUtil.locatePageInTargetSite(hrefValue, targetBranch, resourceResolver);
                    if (lookedUpHrefValue != null && lookedUpHrefValue.trim().length() > 0) {
                        newHrefValue = lookedUpHrefValue;
                        log.debug("Before replace:" + value);
                        value = value.replace(hrefValue, newHrefValue);
                        log.debug("After replace:" + value);
                        isReplaced = true;
                    }
                } else {
                    log.debug("Before replace:" + value);
                    value = value.replace(hrefValue, newHrefValue);
                    log.debug("After replace:" + value);
                    isReplaced = true;
                }
            }
        }
        if (isReplaced) {
            return value;
        } else {
            return null; // Returns null if nothing replaced
        }
    }

    /**
     * Delete components.
     *
     * @param rr                 the rr
     * @param rolloutLog         the rollout log
     * @param componentsToDelete the components to delete
     */
    private void deleteComponents(ResourceResolver rr, List<String> rolloutLog, Set<String> componentsToDelete) {
        for (String component : componentsToDelete) {
            log.info("Deleting node {}", component);
            try {
                rr.delete(rr.resolve(component));
                rr.commit();
                rolloutLog.add("Node " + component + " deleted successfully");
                log.info("Node {} deleted successfully", component);
            } catch (PersistenceException e) {
                log.error("Girlscouts Rollout Service encountered error: Unable to delete {} ", component, e);
            }
        }
    }

    /**
     * Rollout components.
     *
     * @param srcRes              the src res
     * @param rolloutLog          the rollout log
     * @param relationPath        the relation path
     * @param componentsToRollout the components to rollout
     * @throws WCMException the wcm exception
     */
    private void rolloutComponents(Resource srcRes, List<String> rolloutLog, String relationPath, Set<String> componentsToRollout) throws WCMException {
        log.info("Rolling out content for {}, components {}.", relationPath, componentsToRollout);
        RolloutManager.RolloutParams params = new RolloutManager.RolloutParams();
        params.isDeep = false;
        params.master = srcRes.adaptTo(Page.class);
        params.targets = new String[]{relationPath};
        params.paragraphs = componentsToRollout.toArray(new String[componentsToRollout.size()]);
        params.trigger = RolloutManager.Trigger.ROLLOUT;
        params.reset = false;
        log.info("Params before reference:: {}", params);
        //GSWP-2235 inform  GirlScoutsReferencesUpdateAction to stop updating reference as rollout is from workflow
        blockReferenceUpdateAction.set("blockInitiatedFromWorkflow");
        log.info("Params before rollout:: {}", params);
        rolloutManager.rollout(params);
        log.info("Params:: {}", params);
        rolloutLog.add("Rolled out content to " + relationPath);
        log.info("Rollout log:: {}", rolloutLog);
        log.info("Successfully rolled out content for {}.", relationPath);
    }

    /**
     * Update page title.
     *
     * @param srcRes         the src res
     * @param targetResource the target resource
     */
    private void updatePageTitle(Resource srcRes, Resource targetResource) {
        try {
            Resource srcContent = srcRes.getChild("jcr:content");
            Node srcContentNode = srcContent.adaptTo(Node.class);
            Resource targetContent = targetResource.getChild("jcr:content");
            Node targetContentNode = targetContent.adaptTo(Node.class);
            String targetTitle = targetContentNode.getProperty("jcr:title").getString();
            String sourceTitle = srcContentNode.getProperty("jcr:title").getString();
            if (!sourceTitle.equals(targetTitle)) {
                log.info("Updating page title of {} to {}.", targetResource.getPath(), sourceTitle);
                targetContentNode.setProperty("jcr:title", sourceTitle);
                targetContentNode.getSession().save();
            }
        } catch (Exception e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

    /**
     * Validate rollout config.
     *
     * @param srcRes         the src res
     * @param targetResource the target resource
     */
    private void validateRolloutConfig(Resource srcRes, Resource targetResource) {
        log.info("Girlscouts Rollout Service : Validating rollout config for {}", targetResource.getPath());
        try {
            Node page = targetResource.adaptTo(Node.class);
            Node srcPage = srcRes.adaptTo(Node.class);
            if (!isGSRolloutConfig(page)) {
                log.info("Girlscouts Rollout Service : Setting girlscouts rollout config for {}", targetResource.getPath());
                Node liveSyncNode = null;
                if (getLiveSyncNode(page) != null) {
                    liveSyncNode = getLiveSyncNode(page);
                } else {
                    liveSyncNode = createLiveSyncNode(page, srcPage);
                }
                liveSyncNode.setProperty(PARAM_CQ_MASTER, srcPage.getPath());
                liveSyncNode.setProperty(PARAM_CQ_IS_DEEP, Boolean.TRUE);
                liveSyncNode.setProperty(PARAM_CQ_ROLLOUT_CONFIG, new String[]{GS_ROLLOUT_CONFIG});
                liveSyncNode.getSession().save();

            }
        } catch (RepositoryException e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

    /**
     * Create live sync node node.
     *
     * @param page    the page
     * @param srcPage the src page
     * @return the node
     */
    private Node createLiveSyncNode(Node page, Node srcPage) {
        try {
            Node contentNode = page.getNode("jcr:content");
            if (contentNode != null) {
                Node liveSyncNode = contentNode.addNode("cq:LiveSyncConfig");
                liveSyncNode.setPrimaryType(PARAM_CQ_LIVE_SYNC_CONFIG);
                return liveSyncNode;
            }
        } catch (RepositoryException e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
        return null;
    }

    /**
     * cancel the Inheritance for certain components (nodes with mixin type
     * "cq:LiveSyncCancelled" under national template page)
     *
     * @param rr          the rr
     * @param councilPath the council path
     */
    private void cancelInheritance(ResourceResolver rr, String councilPath) {
        try {
            String sql = "SELECT * FROM [cq:LiveSyncCancelled] AS s WHERE ISDESCENDANTNODE(s, '" + councilPath + "')";
            log.error("Girlscouts Rollout Service SQL2: " + sql);
            for (Iterator<Resource> it = rr.findResources(sql, Query.JCR_SQL2); it.hasNext(); ) {
                Resource target = it.next();
                log.error("Girlscouts Rollout Service : End relationship " + target.getPath());
                relationManager.endRelationship(target, true);
            }
        } catch (Exception e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

    /**
     * Send gsusa notifications.
     *
     * @param dateRolloutNode        the date rollout node
     * @param rolloutLog             the rollout log
     * @param councilNotificationLog the council notification log
     * @param isTestMode             the is test mode
     * @param rr                     the rr
     */
    private void sendGSUSANotifications(Node dateRolloutNode, List<String> rolloutLog, List<String> councilNotificationLog, Boolean isTestMode, ResourceResolver rr) {
        try {
            log.info("Girlscouts Rollout Service : Sending GSUSA notifications for {} rollout. isTestMode={}", dateRolloutNode.getPath(), isTestMode);
            Set<String> councils = null;
            String councilNotificationSubject = DEFAULT_ROLLOUT_NOTIFICATION_SUBJECT;
            StringBuffer html = new StringBuffer();
            html.append(DEFAULT_REPORT_HEAD);
            html.append("<body>");
            html.append("<p>" + DEFAULT_ROLLOUT_REPORT_SUBJECT + "</p>");
            html.append("<p>" + DEFAULT_REPORT_GREETING + "</p>");
            html.append("<p>" + DEFAULT_REPORT_INTRO + "</p>");
            String message = "", templatePath = "", srcPath = "",wfInitiatorName = null;
            Boolean notify = false, useTemplate = false, delay = false, activate = true;
            //GSWP-2077 : Start 
            try {
            	wfInitiatorName = dateRolloutNode.getProperty(WORKFLOW_INITIATOR_NAME).getString();
            	if(StringUtils.isNotEmpty(wfInitiatorName)) {
                	html.append("<p>This workflow was initiated by " + wfInitiatorName + "</p>");
                }
            } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered workflow error: ", e);
                }                        
            //GSWP-2077 : end
            Date runtime = new Date();
            html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
            
            try {
                notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                activate = dateRolloutNode.getProperty(PARAM_ACTIVATE).getBoolean();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                delay = dateRolloutNode.getProperty(PARAM_DELAY).getBoolean();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                if (useTemplate) {
                    councilNotificationSubject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
                } else {
                    councilNotificationSubject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
                }
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                if (useTemplate) {
                    message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
                } else {
                    message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
                }
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
            }
            try {
                councils = PageReplicationUtil.getCouncils(dateRolloutNode);
            } catch (Exception e) {
                log.error("Girlscouts Rollout Service encountered error: ", e);
                return;
            }
            html.append("<p>This workflow will " + (notify ? "" : "not ") + "send emails to councils.</p>");
            html.append("<p>This workflow will " + (activate ? "" : "not ") + "activate pages upon completion.</p>");
            if (activate) {
                html.append("<p>This workflow will " + (delay ? "" : "not ") + "delay the page activations until tonight.</p>");
            }
            if (useTemplate) {
                html.append("<p>An email template is in use. The path to the template is " + templatePath + "</p>");
            }
            html.append("<p>The email subject is " + councilNotificationSubject + "</p>");
            html.append("<p>The email message is: " + message + "</p>");
            html.append("<p>The following councils have been selected:</p>");
            for (String council : councils) {
                html.append("<p>" + council + "</p>");
            }
            html.append("<p><b>Processing:</b><br>Source Page:" + srcPath + "</p>");
            for (String log : rolloutLog) {
                html.append("<p>" + log + "</p>");
            }
            StringBuffer logData = new StringBuffer();
            logData.append("The following is the process log for council notifications:\n\n");
            if (councilNotificationLog != null && !councilNotificationLog.isEmpty()) {
                for (String log : councilNotificationLog) {
                    logData.append(log + "\n");
                }
            }
            List<String> gsusaEmails = PageReplicationUtil.getGsusaEmails(rr);
            Set<GSEmailAttachment> attachments = new HashSet<GSEmailAttachment>();
            String fileName = DEFAULT_ROLLOUT_REPORT_ATTACHMENT + "_" + dateRolloutNode.getName();
            GSEmailAttachment attachment = new GSEmailAttachment(fileName, logData.toString(), null, GSEmailAttachment.MimeType.TEXT_PLAIN);
            attachments.add(attachment);
            String environment = PageReplicationUtil.getEnvironment(rr);
            String reportSubject = environment != null ? "(" + PageReplicationUtil.getEnvironment(rr) + ") " + DEFAULT_ROLLOUT_REPORT_SUBJECT : DEFAULT_ROLLOUT_REPORT_SUBJECT;
            if (isTestMode) {
                gsusaEmails = PageReplicationUtil.getReportEmails(rr);
                gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
            } else {
                gsEmailService.sendEmail(reportSubject, gsusaEmails, html.toString(), attachments);
            }
        } catch (Exception e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

    /**
     * Send council notifications.
     *
     * @param dateRolloutNode        the date rollout node
     * @param councilNotificationLog the council notification log
     * @param isTestMode             the is test mode
     * @param rr                     the rr
     */
    private void sendCouncilNotifications(Node dateRolloutNode, List<String> councilNotificationLog, Boolean isTestMode, ResourceResolver rr) {
        Set<String> notifyCouncils = new TreeSet<String>();
        Set<String> noLiveCopyCouncils = new TreeSet<String>();
        String subject = DEFAULT_ROLLOUT_NOTIFICATION_SUBJECT;
        String message = DEFAULT_ROLLOUT_NOTIFICATION_MESSAGE, templatePath = "", srcPath = "";
        Boolean notify = false, useTemplate = false;
        Boolean newPage = false;
        
        try {
            log.info("Girlscouts Rollout Service : Sending Council notifications for {} rollout. isTestMode={}", dateRolloutNode.getPath(), isTestMode);
            notify = dateRolloutNode.getProperty(PARAM_NOTIFY).getBoolean();
            if (notify) {
                try {
                    useTemplate = dateRolloutNode.getProperty(PARAM_USE_TEMPLATE).getBoolean();
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
                try {
                    if (useTemplate) {
                        subject = PageReplicationUtil.getTemplateSubject(templatePath, rr);
                    } else {
                        subject = dateRolloutNode.getProperty(PARAM_EMAIL_SUBJECT).getString();
                    }
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
                try {
                    if (useTemplate) {
                        message = PageReplicationUtil.getTemplateMessage(templatePath, rr);
                    } else {
                        message = dateRolloutNode.getProperty(PARAM_EMAIL_MESSAGE).getString();
                    }
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
                try {
                    templatePath = dateRolloutNode.getProperty(PARAM_TEMPLATE_PATH).getString();
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
                try {
                    srcPath = dateRolloutNode.getProperty(PARAM_SOURCE_PATH).getString();
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
                try {
                    notifyCouncils.addAll(PageReplicationUtil.getNotifyCouncils(dateRolloutNode));
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                    return;
                }
                try {
	                if(dateRolloutNode.hasProperty(PARAM_NO_LIVE_COPY_COUNCILS)) {
	                	Value[] values = dateRolloutNode.getProperty(PARAM_NO_LIVE_COPY_COUNCILS).getValues();
	                    for (Value value : values) {
	                    	noLiveCopyCouncils.add(value.getString());
	                    }
	                }
                }catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                    return;
                }
                try {
                	if(dateRolloutNode.hasProperty(PARAM_NEW_PAGE)) {
                		newPage = dateRolloutNode.getProperty(PARAM_NEW_PAGE).getBoolean();
                	}
                }catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                    return;
                }
                Resource source = rr.resolve(srcPath);
                if (!source.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                    if (notifyCouncils != null && !notifyCouncils.isEmpty()) {
                        for (String targetPath : notifyCouncils) {
                            if (message != null && message.trim().length() > 0) {
                                String pathToCouncilSite = null;
                                List<String> toAddresses = null;
                                try {
                                    pathToCouncilSite = PageReplicationUtil.getCouncilName(targetPath);
                                    // get the email addresses configured in
                                    // page properties of the council's homepage
                                    Page homepage = rr.resolve(pathToCouncilSite + "/en").adaptTo(Page.class);
                                    toAddresses = PageReplicationUtil.getCouncilEmails(homepage.adaptTo(Node.class));
                                    log.info("sending email to " + pathToCouncilSite.substring(9) + " emails:" + toAddresses.toString());
                                    String body = PageReplicationUtil.generateCouncilNotification(srcPath, targetPath, message, rr, settingsService);
                                    if (isTestMode) {
                                        councilNotificationLog.add("Notification is running in test mode!");
                                        councilNotificationLog.add("Replacing " + toAddresses.toString());
                                        toAddresses = PageReplicationUtil.getReportEmails(rr);
                                        councilNotificationLog.add("with " + toAddresses.toString());
                                        gsEmailService.sendEmail(subject, toAddresses, body);
                                    } else {
                                        gsEmailService.sendEmail(subject, toAddresses, body);
                                    }
                                    councilNotificationLog.add("Notification for " + pathToCouncilSite.substring(9) + " council sent to emails:" + toAddresses.toString());
                                    councilNotificationLog.add("Notification Email Body: \n" + body);
                                    try {
                                        dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.TRUE);
                                        dateRolloutNode.getSession().save();
                                    } catch (RepositoryException e1) {
                                        log.error("Girlscouts Rollout Service encountered error: ", e1);
                                    }
                                } catch (Exception e) {
                                    log.error("Girlscouts Rollout Service encountered error: ", e);
                                    try {
                                        dateRolloutNode.setProperty(PARAM_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
                                        dateRolloutNode.getSession().save();
                                    } catch (RepositoryException e1) {
                                        log.error("Girlscouts Rollout Service encountered error: ", e1);
                                    }
                                    councilNotificationLog.add("Failed to send notification for " + String.valueOf(pathToCouncilSite) + " council to emails:" + String.valueOf(toAddresses));
                                }
                            }
                        }
                    }
                    if(null != noLiveCopyCouncils && !noLiveCopyCouncils.isEmpty() && !newPage) {
                    	subject = dateRolloutNode.getProperty(NO_LIVE_COPY_NOTIFICATION_SUB).getString();
                    	message = dateRolloutNode.getProperty(NO_LIVE_COPY_NOTIFICATIOPN_MSG).getString();
                    	for(String targetPath : noLiveCopyCouncils) {
                    		if(null !=message && message.trim().length() > 0) {
                    			String pathToCouncilSite = null;
                                List<String> toAddresses = null;
                                try {
                                	pathToCouncilSite = PageReplicationUtil.getCouncilName(targetPath);
                                    Page homepage = rr.resolve(pathToCouncilSite + "/en").adaptTo(Page.class);
                                    toAddresses = PageReplicationUtil.getCouncilEmails(homepage.adaptTo(Node.class));
                                    log.info("sending email to " + pathToCouncilSite.substring(9) + " emails:" + toAddresses.toString());
                                    String body = PageReplicationUtil.generateCouncilNotification(srcPath, targetPath, message, rr, settingsService);
                                    if(isTestMode) {
                                    	councilNotificationLog.add("Notification is running in test mode!");
                                    	gsEmailService.sendEmail(subject, toAddresses, body);
                                    }else {
                                    	 gsEmailService.sendEmail(subject, toAddresses, body);
                                    }
                                    try {
                                        dateRolloutNode.setProperty(PARAM_NO_LIVE_COPY_COUNCIL_NOTIFICATIONS_SENT, Boolean.TRUE);
                                        dateRolloutNode.getSession().save();
                                    } catch (RepositoryException e1) {
                                        log.error("Girlscouts Rollout Service encountered error: ", e1);
                                    }
                                    
                                } catch (Exception e) {
                                	log.error("Girlscouts Rollout Service encountered error: ", e);
                                	 try {
                                         dateRolloutNode.setProperty(PARAM_NO_LIVE_COPY_COUNCIL_NOTIFICATIONS_SENT, Boolean.FALSE);
                                         dateRolloutNode.getSession().save();
                                     } catch (RepositoryException e1) {
                                         log.error("Girlscouts Rollout Service encountered error: ", e1);
                                     }
								}
                    		}
                    	}
                    }
                    
                }
            }
        } catch (Exception e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

    /**
     * Is gs rollout config boolean.
     *
     * @param pageNode the page node
     * @return the boolean
     */
    private Boolean isGSRolloutConfig(Node pageNode) {
        boolean isValid = false;
        if (pageNode != null) {
            Node liveSync = getLiveSyncNode(pageNode);
            if (liveSync != null) {
                try {
                    if (liveSync.hasProperty("cq:rolloutConfigs")) {
                        Value[] configs = liveSync.getProperty("cq:rolloutConfigs").getValues();
                        if (configs != null && configs.length > 0) {
                            for (Value config : configs) {
                                if (GS_ROLLOUT_CONFIG.equals(config.getString())) {
                                    isValid = true;
                                }
                            }
                        }
                    }
                } catch (RepositoryException e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
            } else {
                isValid = isGSRolloutConfig(getAncestorWithLiveSync(pageNode));
            }
        }
        return isValid;
    }

    /**
     * Gets live sync node.
     *
     * @param node the node
     * @return the live sync node
     */
    private Node getLiveSyncNode(Node node) {
        try {
            if (node.hasNode("jcr:content/cq:LiveSyncConfig")) {
                return node.getNode("jcr:content/cq:LiveSyncConfig");
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Gets ancestor with live sync.
     *
     * @param node the node
     * @return the ancestor with live sync
     */
    private Node getAncestorWithLiveSync(Node node) {
        try {
            Node parentNode = node.getParent();
            if (parentNode != null) {
                if (getLiveSyncNode(parentNode) != null) {
                    return parentNode;
                } else {
                    return getAncestorWithLiveSync(parentNode);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Get good parent resource.
     *
     * @param sourceParent    the source parent
     * @param councilPath     the council path
     * @param rr              the rr
     * @param relationManager the relation manager
     * @param rolloutLog      the rollout log
     * @return the resource
     */
    private Resource getGoodParent(Resource sourceParent, String councilPath, ResourceResolver rr, LiveRelationshipManager relationManager, List<String> rolloutLog){
        Page sourcePage = sourceParent.adaptTo(Page.class);
        if(sourcePage.getDepth() == 2){
            rolloutLog.add("Defaulting to first page under : " + councilPath);
            log.info("Defaulting to first page under : " + councilPath);
            //default to first page under en.html
            Resource councilResource = rr.resolve(councilPath);
            Page councilSite = councilResource.adaptTo(Page.class);
            Iterator<Page> sitePages = councilSite.listChildren();
            if(sitePages != null && sitePages.hasNext()){
                Page enPage = sitePages.next();
                rolloutLog.add("Will rollout page under : " + enPage.getPath());
                log.info("Will rollout page under : " + enPage.getPath());
                return enPage.adaptTo(Resource.class);
            }else{
                return null;
            }
        }
        try {
            RangeIterator relationsIterator = relationManager.getLiveRelationships(sourceParent, councilPath, null);
            while (relationsIterator.hasNext()){
                LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                String relationPagePath = relation.getTargetPath();
                rolloutLog.add("Attempting to roll out a child page of: " + relationPagePath);
                log.info("Attempting to roll out a child page of: {}", relationPagePath);
                Resource targetParent = rr.resolve(relationPagePath);
                if (targetParent != null && !targetParent.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)){
                    log.info("Rolling out to " + relationPagePath);
                    rolloutLog.add("Rolling out to " + relationPagePath);
                    return targetParent;
                }
            }
            Resource sourceGrandParent = sourceParent.getParent();
            if (sourceGrandParent != null) {
                log.info("Could not find parent resource " + sourceGrandParent.getPath() + " for council " + councilPath + ", trying grandparent");
                rolloutLog.add("Could not find parent resource " + sourceGrandParent.getPath() + " for council " + councilPath + ", trying grandparent");
                return getGoodParent(sourceGrandParent, councilPath, rr, relationManager,rolloutLog);
            } else {
                log.error("Could not find parent resource " + sourceGrandParent.getPath() + " for council " + councilPath + " and no grandparent exists, rollout failed");
                rolloutLog.add("Could not find parent resource " + sourceGrandParent.getPath() + " for council " + councilPath + " and no grandparent exists, rollout failed");
                return null;
            }

        } catch(Exception e){
            log.error("Error getting parent of rollout page parent: " + e.getMessage());
            rolloutLog.add("Error getting parent of rollout page parent: " + e.getMessage());
            return null;
        }

    }

    /**
     * Compare rollout components list.
     *
     * @param rr                               the rr
     * @param sourceToTargetComponentRelations the source to target component relations
     * @param relationComponents               the relation components
     * @return the list
     */
    /*
     * This function compares the content of council with template and check for any difference.
     */
    private List<RolloutContentDifference> compareRolloutComponents(ResourceResolver rr, Map<String, String> sourceToTargetComponentRelations, Map<String, Set<String>> relationComponents) {
        //Reversing the hashmap, so that we can have council URL's as key.
        sourceToTargetComponentRelations = sourceToTargetComponentRelations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Set<String> councilComponentsToRollout = relationComponents.get(RELATION_INHERITED_COMPONENTS);
        Set<String> cancInheritanceComponents =relationComponents.get(RELATION_CANC_INHERITANCE_COMPONENTS);
        //GSAWDO-60 - List of properties and resourceType we have to check against,which is provided by business.
        Map<String,List<String>> knownResourceType = new HashMap<>();
        List<String> imagePropertyArray= new ArrayList<>();
        imagePropertyArray.add("fileReference");
        knownResourceType.put("girlscouts/components/image",imagePropertyArray);
        List<String> textPropertyArray= new ArrayList<>();
        textPropertyArray.add("text");
        knownResourceType.put("girlscouts/components/text",textPropertyArray);
        List<String> titlePropertyArray= new ArrayList<>();
        titlePropertyArray.add("title");
        knownResourceType.put("girlscouts/components/title",titlePropertyArray);
        List<String> textImagePropertyArray= new ArrayList<>();
        textImagePropertyArray.add("text");
        knownResourceType.put("girlscouts/components/textimage",textImagePropertyArray);
        List<String> videoPropertyArray= new ArrayList<>();
        videoPropertyArray.add("html");
        knownResourceType.put("girlscouts/components/video",videoPropertyArray);
        List<String> tablePropertyArray= new ArrayList<>();
        tablePropertyArray.add("tableData");
        knownResourceType.put("girlscouts/components/table",tablePropertyArray);
        List<String> embeddedPropertyArray= new ArrayList<>();
        embeddedPropertyArray.add("html");
        knownResourceType.put("girlscouts/components/embedded",embeddedPropertyArray);

        //Form based difference checking
        List<String> formTextPropertyArray= new ArrayList<>();
        formTextPropertyArray.add(JcrConstants.JCR_TITLE);
        formTextPropertyArray.add("name");
        knownResourceType.put("girlscouts/components/form/text",formTextPropertyArray);
        List<String> formCheckboxPropertyArray= new ArrayList<>();
        formCheckboxPropertyArray.add("options");
        formCheckboxPropertyArray.add("name");
        knownResourceType.put("foundation/components/form/checkbox",formCheckboxPropertyArray);
        List<String> formDropdownPropertyArray= new ArrayList<>(formCheckboxPropertyArray);
        formDropdownPropertyArray.add(JcrConstants.JCR_TITLE);
        knownResourceType.put("foundation/components/form/dropdown",formDropdownPropertyArray);
        List<String> formRadioPropertyArray= new ArrayList<>(formDropdownPropertyArray);
        knownResourceType.put("foundation/components/form/radio",formDropdownPropertyArray);

        //Accordion Handling, its a special case, for this resourceType we have custom way of handling it.
        knownResourceType.put("girlscouts/components/accordion",null);

        List<RolloutContentDifference> contentDifferences = new ArrayList<>();

        //lets check for components which has inheritance enabled.
        for(String councilComponentToRollout :  councilComponentsToRollout){
            if(sourceToTargetComponentRelations.containsKey(councilComponentToRollout)){
                Resource councilComponentToRolloutResource = rr.resolve(councilComponentToRollout);
                String councilComponentToRolloutResourceType = councilComponentToRolloutResource.getResourceType();
                /* Checking the resourceType of the council component(which is getting rolled-out)from template against the known component resourceType.
                If found lets compare the properties of that component for any differences.*/
                if(knownResourceType.containsKey(councilComponentToRolloutResourceType)){
                    String templateComponentToRolloutPath = sourceToTargetComponentRelations.get(councilComponentToRolloutResource.getPath());
                    Resource templateComponentToRolloutResource = rr.getResource(templateComponentToRolloutPath);

                    //If Accordion resourceType is found,lets handle it separately.
                    if(councilComponentToRolloutResourceType.equals("girlscouts/components/accordion")){
                        contentDifferences.addAll(handleAccordionResourceType(rr,sourceToTargetComponentRelations,councilComponentToRolloutResource));
                        continue;
                    }

                    List<RolloutContentDifference> rolloutContentDifference = getContentDifference(knownResourceType, councilComponentToRolloutResource,templateComponentToRolloutResource,false);
                    contentDifferences.addAll(rolloutContentDifference);
                }
            }
        }

        //Lets check for components which has inheritance disabled.
        for(String councilPath : cancInheritanceComponents){
            if(sourceToTargetComponentRelations.containsKey(councilPath)){
                Resource councilComponentToRolloutResource = rr.getResource(councilPath);
                Resource templateComponentToRolloutResource = rr.getResource(sourceToTargetComponentRelations.get(councilPath));
                //Now since we have both councilPath and templatePath, lets see if the resourceType of cancel Inherited component lies in known resourceType
                List<RolloutContentDifference> rolloutContentDifference = getContentDifference(knownResourceType, councilComponentToRolloutResource,templateComponentToRolloutResource,true);
                contentDifferences.addAll(rolloutContentDifference);
            }
        }

        return contentDifferences;
    }


    /**
     * Cookie header component added or removed string.
     *
     * @param rr                               the rr
     * @param sourceToTargetComponentRelations the source to target component relations
     * @param relationComponents               the relation components
     * @param componentsToDelete               the components to delete
     * @return the string
     */
    /*
     * This function checks if Cookie Header Component Added Or Removed.
     */
    private String cookieHeaderComponentAddedOrRemoved(ResourceResolver rr, Map<String, String> sourceToTargetComponentRelations, Map<String, Set<String>> relationComponents, Set<String> componentsToDelete) {
        //Reversing the hashmap, so that we can have council URL's as key.
        sourceToTargetComponentRelations = sourceToTargetComponentRelations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Set<String> councilComponentsToRollout = relationComponents.get(RELATION_INHERITED_COMPONENTS);
        //Lets check for girlscouts/components/standalone-cookie-header,whether it was removed or added
        String cookieHeaderComponentAddedRemoved = null;
        //First removing all the component which has inheritance enabled in sourceToTargetComponentRelations Map, now we are left with component which are newly added.
        sourceToTargetComponentRelations.keySet().removeAll(councilComponentsToRollout);
        //Reversing the hashmap Again, so that we can have template URL's as key.
        sourceToTargetComponentRelations = sourceToTargetComponentRelations.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        Set<String> newAddedComponentTemplatePaths = sourceToTargetComponentRelations.keySet();
        //lets check in the new added component which are getting rolled out.
        for(String newAddedComponentTemplatePath : newAddedComponentTemplatePaths){
            Resource newAddedComponentResource = rr.getResource(newAddedComponentTemplatePath);
            if(null != newAddedComponentResource && newAddedComponentResource.getResourceType().equals(RolloutTemplatePageServiceImpl.COOKIE_HEADER_RESOURCE_TYPE)){
                cookieHeaderComponentAddedRemoved = RolloutTemplatePageServiceImpl.ACTION_TYPE_ADDED;
                break;
            }
        }
        //lets check in the removed components which are getting rolled out.
        for(String removedComponentTemplatePath:  componentsToDelete){
            Resource removedComponentTemplateResource = rr.getResource(removedComponentTemplatePath);
            if(null != removedComponentTemplateResource && removedComponentTemplateResource.getResourceType().equals(RolloutTemplatePageServiceImpl.COOKIE_HEADER_RESOURCE_TYPE)){
                cookieHeaderComponentAddedRemoved = RolloutTemplatePageServiceImpl.ACTION_TYPE_REMOVED;
                break;
            }
        }

        return cookieHeaderComponentAddedRemoved;
    }

    /**
     * Gets content difference.
     *
     * @param knownResourceType                  the known resource type
     * @param councilComponentToRolloutResource  the council component to rollout resource
     * @param templateComponentToRolloutResource the template component to rollout resource
     * @param isInheritanceBroken                the is inheritance broken
     * @return the content difference
     */
    /*
     * This function iterate over the components which are rolling out and checks for any content difference.
     */
    private List<RolloutContentDifference>  getContentDifference(Map<String, List<String>> knownResourceType, Resource councilComponentToRolloutResource,Resource templateComponentToRolloutResource,Boolean isInheritanceBroken) {
        List<RolloutContentDifference> contentDifferences = new ArrayList<>();
        String councilComponentToRolloutPath  = councilComponentToRolloutResource.getPath();
        String templateComponentToRolloutResourceType = templateComponentToRolloutResource.getResourceType();
        List<String> resourceTypePropertyArray = knownResourceType.get(templateComponentToRolloutResourceType);
        for (String resourceTypeProperty : resourceTypePropertyArray) {
            ValueMap templateResourceValueMap = templateComponentToRolloutResource.adaptTo(ValueMap.class);
            if (templateResourceValueMap.containsKey(resourceTypeProperty)) {
                ValueMap councilResourceValueMap = councilComponentToRolloutResource.adaptTo(ValueMap.class);
                if (councilResourceValueMap.containsKey(resourceTypeProperty)) {
                    String[] councilComponentToRolloutPathArray = councilComponentToRolloutPath.split("/");
                    String pathTillCouncilHeadPage = "/" + councilComponentToRolloutPathArray[1] + "/" + councilComponentToRolloutPathArray[2] + "/";
                    String newContent =null,oldContent =null;
                    /*if resourceType is girlscouts/components/form/checkbox and property is options,
                    we have to handle it differently. As options is a multifield */
                    if((templateComponentToRolloutResourceType.equals("foundation/components/form/checkbox") || templateComponentToRolloutResourceType.equals("foundation/components/form/dropdown")) && resourceTypeProperty.equals("options")){
                        String[] newContentArray = templateResourceValueMap.get(resourceTypeProperty, String[].class);
                        String[] oldContentArray = councilResourceValueMap.get(resourceTypeProperty, String[].class);
                        oldContent = convertStringArrayToString(oldContentArray, ",");
                        newContent = convertStringArrayToString(newContentArray, ",");
                    }else {
                        newContent = templateResourceValueMap.get(resourceTypeProperty, String.class);
                        oldContent = councilResourceValueMap.get(resourceTypeProperty, String.class);
                    }
                    //Exact property is found in council and template component. Lets check if value for both are same or not.
                    if (!oldContent.replaceAll(pathTillCouncilHeadPage, "/content/girlscouts-template/").equals(newContent)) {
                        //Hurray! this property was changed by someone in template before rolling out.
                        contentDifferences.add(new RolloutContentDifference(oldContent, newContent, templateComponentToRolloutResourceType, resourceTypeProperty, isInheritanceBroken));
                    }
                }
            }
        }
        return contentDifferences;
    }

    /**
     * Handle accordion resource type list.
     *
     * @param rr                                the rr
     * @param sourceToTargetComponentRelations  the source to target component relations
     * @param councilComponentToRolloutResource the council component to rollout resource
     * @return the list
     */
    /*
     * This function handles Accordion Resource Type- this is a special scenerio.
     */
    private List<RolloutContentDifference> handleAccordionResourceType(ResourceResolver rr,Map<String, String> sourceToTargetComponentRelations,Resource councilComponentToRolloutResource) {
        List<RolloutContentDifference> contentDifferences = new ArrayList<>();
        if(null != councilComponentToRolloutResource) {
            for(Resource childNode : councilComponentToRolloutResource.getChildren()){
                if(childNode.getName().equals("children")){
                    for(Resource childrenChildResource :  childNode.getChildren()){
                        ValueMap councilChildrenNodeChildValueMap = childrenChildResource.adaptTo(ValueMap.class);
                        String oldContent = councilChildrenNodeChildValueMap.get("nameField",String.class);
                        String templateComponentToRolloutPath = sourceToTargetComponentRelations.get(childrenChildResource.getPath());
                        Resource templateComponentToRolloutPathResource = rr.getResource(templateComponentToRolloutPath);
                        ValueMap templateChildrenNodeChildValueMap = templateComponentToRolloutPathResource.adaptTo(ValueMap.class);
                        String newContent = templateChildrenNodeChildValueMap.get("nameField",String.class);
                        //Exact property is found in council and template component. Lets check if value for both are same or not.
                        if (!newContent.equals(oldContent)) {
                            //Hurray! this property was changed by someone in template before rolling out.
                            contentDifferences.add(new RolloutContentDifference(oldContent, newContent, "Accordion-children", "nameField"));
                        }
                    }
                    break;
                }
            }
        }
        return contentDifferences;
    }

    /**
     * Convert string array to string string.
     *
     * @param strArr    the str arr
     * @param delimiter the delimiter
     * @return the string
     */
    /*
     * This function convert String array to String
     */
    private String convertStringArrayToString(String[] strArr, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str).append(delimiter);
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * Send content difference email.
     *
     * @param rr                                  the rr
     * @param councilPath                         the council path
     * @param contentDifferences                  the content differences
     * @param cookieHeaderComponentAddedOrRemoved the cookie header component added or removed
     */
    /*
     * This function sends the content difference email to individual concils.
     */
    private void sendContentDifferenceEmail(ResourceResolver rr, String councilPath, List<RolloutContentDifference> contentDifferences, String cookieHeaderComponentAddedOrRemoved){
        if (councilPath != null) {
            try {
                String pathToCouncilSite = PageReplicationUtil.getCouncilName(councilPath);
                // get the email addresses configured in
                // page properties of the council's homepage
                Page homepage = rr.resolve(pathToCouncilSite + "/en").adaptTo(Page.class);
                List<String> toAddresses = PageReplicationUtil.getCouncilEmails(homepage.adaptTo(Node.class));
                log.info("Sending content difference email to " + pathToCouncilSite.substring(9) + " emails:" + toAddresses.toString());
                String subject = DEFAULT_ROLLOUT_CONTENT_DIFFERENCE_NOTIFICATION_SUBJECT;
                String message = DEFAULT_ROLLOUT_CONTENT_DIFFERENCE_NOTIFICATION_MESSAGE;
                StringBuilder body = new StringBuilder();
                body.append(message);
                body.append("<style> table, th, td { border: 1px solid black;} </style>");
                body.append("<table style=\"width:100%;border-collapse: collapse;\">");
                body.append(RolloutTemplatePageServiceImpl.TR_START);
                body.append(RolloutTemplatePageServiceImpl.TH_START+"Component Type"+RolloutTemplatePageServiceImpl.TH_END);
                body.append(RolloutTemplatePageServiceImpl.TH_START+"Status"+RolloutTemplatePageServiceImpl.TH_END);
                body.append(RolloutTemplatePageServiceImpl.TH_START+"Old"+RolloutTemplatePageServiceImpl.TH_END);
                body.append(RolloutTemplatePageServiceImpl.TH_START+"New"+RolloutTemplatePageServiceImpl.TH_END);
                body.append(RolloutTemplatePageServiceImpl.TR_END);

                //lets create entry for all the components having content difference in the table.
                for (RolloutContentDifference contentDifference : contentDifferences) {
                    body.append(RolloutTemplatePageServiceImpl.TR_START);
                    body.append(RolloutTemplatePageServiceImpl.TD_START+ contentDifference.getComponentResourceType() +RolloutTemplatePageServiceImpl.TD_END);
                    if(contentDifference.isInheritanceBroken()){
                        body.append("<td style=\"color:red\">" + "<b>Not Updated (Inheritance is Broken)<b>" + RolloutTemplatePageServiceImpl.TD_END);
                    }else{
                        body.append("<td style=\"color:green\"><b>" + "Updated" + "</b>"+ RolloutTemplatePageServiceImpl.TD_END);
                    }
                    body.append(RolloutTemplatePageServiceImpl.TD_START+ contentDifference.getOldContent() + RolloutTemplatePageServiceImpl.TD_END);
                    body.append(RolloutTemplatePageServiceImpl.TD_START+ contentDifference.getNewContent() + RolloutTemplatePageServiceImpl.TD_END);
                    body.append(RolloutTemplatePageServiceImpl.TR_END);
                }
                //lets create one entry for cookieHeaderComponent in the table.
                if(null != cookieHeaderComponentAddedOrRemoved){
                    body.append(RolloutTemplatePageServiceImpl.TR_START);
                    body.append(RolloutTemplatePageServiceImpl.TD_START+RolloutTemplatePageServiceImpl.COOKIE_HEADER_RESOURCE_TYPE+RolloutTemplatePageServiceImpl.TD_END);
                    if(cookieHeaderComponentAddedOrRemoved.equals(RolloutTemplatePageServiceImpl.ACTION_TYPE_REMOVED)){
                        body.append("<td style=\"color:red\"><b>" + RolloutTemplatePageServiceImpl.ACTION_TYPE_REMOVED + "</b>"+RolloutTemplatePageServiceImpl.TD_END);
                    }else{
                        body.append("<td style=\"color:green\"><b>" + RolloutTemplatePageServiceImpl.ACTION_TYPE_ADDED + "</b>"+RolloutTemplatePageServiceImpl.TD_END);
                    }
                    body.append(RolloutTemplatePageServiceImpl.TD_START+RolloutTemplatePageServiceImpl.TD_END);
                    body.append(RolloutTemplatePageServiceImpl.TD_START+RolloutTemplatePageServiceImpl.TD_END);
                    body.append(RolloutTemplatePageServiceImpl.TR_END);
                }

                body.append(RolloutTemplatePageServiceImpl.TABLE_END);

                gsEmailService.sendEmail(subject, toAddresses, body.toString());
            } catch (Exception e) {
                log.error("Girlscouts Content Difference Email Trigger encountered error: ", e);
            }
        }
    }
}