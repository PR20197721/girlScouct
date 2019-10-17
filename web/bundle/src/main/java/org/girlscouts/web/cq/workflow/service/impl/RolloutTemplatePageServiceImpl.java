package org.girlscouts.web.cq.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.*;
import javax.jcr.query.Query;

import org.girlscouts.common.components.GSEmailAttachment;
import org.girlscouts.common.osgi.service.GSEmailService;
import org.girlscouts.common.util.PageReplicationUtil;
import org.girlscouts.common.constants.PageReplicationConstants;
import org.girlscouts.web.cq.workflow.service.RolloutTemplatePageService;
import org.girlscouts.web.service.replication.PageReplicator;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutConfig;
import com.day.cq.wcm.msm.api.RolloutConfigManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.api.resource.ResourceResolver;

/*
 * Girl Scouts Page Activator - DL
 * This process activates a queue of pages, in batches, with a timed delay between batches
 */
@Component
@Service
public class RolloutTemplatePageServiceImpl implements RolloutTemplatePageService, PageReplicationConstants, PageReplicationConstants.Email {
    @Property(value = "Roll out a page if it is the source page of a live copy, and then activate target pages.")
    static final String DESCRIPTION = Constants.SERVICE_DESCRIPTION;
    @Property(value = "Girl Scouts")
    static final String VENDOR = Constants.SERVICE_VENDOR;
    @Property(value = "Girl Scouts Roll out Service")
    static final String LABEL = "process.label";
    private static Logger log = LoggerFactory.getLogger(RolloutTemplatePageServiceImpl.class);
    @Reference
    private ResourceResolverFactory resolverFactory;
    @Reference
    private RolloutManager rolloutManager;
    @Reference
    private SlingSettingsService settingsService;
    @Reference
    public GSEmailService gsEmailService;
    @Reference
    private LiveRelationshipManager relationManager;
    @Reference
    private PageReplicator pageReplicator;
    private Map<String, Object> serviceParams;
    private boolean isPublisher = false;

    @Activate
    private void activate(ComponentContext context) {
        serviceParams = new HashMap<String, Object>();
        serviceParams.put(ResourceResolverFactory.SUBSERVICE, "workflow-process-service");
        log.info("Girlscouts Rollout Service Activated.");
        isPublisher = isPublisher();
    }

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
                                    processExistingLiveRelationships(councils, srcRes, pages, rolloutLog, notifyCouncils, rr, updateReferences);
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

    private boolean isPublisher() {
        log.info("checking if running on publisher instance.");
        if (settingsService.getRunModes().contains("publish")) {
            return true;
        }
        return false;
    }

    @Deactivate
    private void deactivate(ComponentContext componentContext) {
        log.info("GS Page Rollout Service Deactivated.");
    }

    private void processNewLiveRelationships(Set<String> submittedCouncils, Resource sourcePageResource, Set<String> pagesToActivate, List<String> rolloutLog, Set<String> notifyCouncils, ResourceResolver rr, Boolean updateReferences) throws RepositoryException, WCMException {
        log.info("Processing new live relationships.");
        Session session = rr.adaptTo(Session.class);
        for (String councilPath : submittedCouncils) {
            log.info("Looking up live relationships in {}", councilPath);
            RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource.getParent(), councilPath, null);
            while (relationsIterator.hasNext()) {
                try {
                    LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                    String relationPagePath = relation.getTargetPath();
                    rolloutLog.add("Attempting to roll out a child page of: " + relationPagePath);
                    log.info("Attempting to roll out a child page of: {}", relationPagePath);
                    Resource parentResource = rr.resolve(relationPagePath);
                    if (parentResource != null && !parentResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                        PageManager pageManager = rr.adaptTo(PageManager.class);
                        Page srcPage = (Page) sourcePageResource.adaptTo(Page.class);
                        Page copyPage = pageManager.copy(srcPage, relationPagePath + "/" + srcPage.getName(), srcPage.getName(), false, true);
                        RolloutConfigManager configMgr = (RolloutConfigManager) rr.adaptTo(RolloutConfigManager.class);
                        RolloutConfig gsConfig = configMgr.getRolloutConfig(GS_ROLLOUT_CONFIG);
                        log.info("Establishing relationship between: {} and {}", srcPage.getPath(), copyPage.getPath());
                        LiveRelationship newPageRelationship = relationManager.establishRelationship(srcPage, copyPage, true, false, gsConfig);
                        String targetPath = newPageRelationship.getTargetPath();
                        cancelInheritance(rr, copyPage.getPath());
                        rolloutManager.rollout(rr, newPageRelationship, false);
                        if (updateReferences) {
                            Set<String> srcComponents = PageReplicationUtil.getComponents(sourcePageResource);
                            updateAllReferences(sourcePageResource, copyPage.adaptTo(Resource.class), srcComponents);
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
                    } else {
                        rolloutLog.add("No resource can be found to serve as a suitable parent page. In order to roll out to this council, you must roll out the parent of this template page first.");
                        rolloutLog.add("Will NOT rollout to this council");
                        notifyCouncils.add(relationPagePath);
                    }
                } catch (Exception e) {
                    log.error("Girlscouts Rollout Service encountered error: ", e);
                }
            }
        }
    }

    private void processExistingLiveRelationships(Set<String> submittedCouncils, Resource sourcePageResource, Set<String> pagesToActivate, List<String> rolloutLog, Set<String> notifyCouncils, ResourceResolver rr, Boolean updateReferences) throws RepositoryException, WCMException {
        log.info("Processing existing live relationships.");
        Set<String> srcComponents = PageReplicationUtil.getComponents(sourcePageResource);
        Set<String> processedRelationCouncils = new HashSet<String>();
        // Session session = rr.adaptTo(Session.class);
        // final Workspace workspace = session.getWorkspace();
        // final VersionManager versionManager = workspace.getVersionManager();
        for (String councilPath : submittedCouncils) {
            log.info("Looking up live relationships in {}", councilPath);
            RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, councilPath, null);
            while (relationsIterator.hasNext()) {
                try {
                    LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                    String relationPagePath = relation.getTargetPath();
                    log.info("Attempting to roll out to: {}", relationPagePath);
                    rolloutLog.add("Attempting to roll out to: " + relationPagePath);
                    Resource relationPageResource = rr.resolve(relationPagePath);
                    if (relationPageResource != null && !relationPageResource.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
                        processedRelationCouncils.add(councilPath);
                        if (PageReplicationUtil.isPageInheritanceBroken(relationPageResource, rolloutLog)) {
                            notifyCouncils.add(relationPagePath);
                            rolloutLog.add("The page " + relationPagePath + " has Break Inheritance checked off. Will not roll out");
                            log.info("The page {} has Break Inheritance checked. Will not roll out", relationPagePath);
                        } else {
                            // String versionableNodePath =
                            // relationPageResource.getPath() + "/jcr:content";
                            try {
                                Map<String, String> sourceToTargetComponentRelations = PageReplicationUtil.getComponentRelationsByPage(srcComponents, relationPagePath, rolloutLog, rr);
                                validateRolloutConfig(sourcePageResource, relationPageResource);
                                Map<String, Set<String>> relationComponents = PageReplicationUtil.categorizeRelationComponents(relationPageResource, rolloutLog, rr);
                                Set<String> componentsToDelete = PageReplicationUtil.getComponentsToDelete(sourceToTargetComponentRelations, relationComponents, councilPath);
                                Set<String> componentsToRollout = PageReplicationUtil.getComponentsToRollout(sourceToTargetComponentRelations, relationComponents, councilPath);
                                if (relationComponents.get(RELATION_CANC_INHERITANCE_COMPONENTS).size() > 0) {
                                    notifyCouncils.add(relationPagePath);
                                }
                                // try {
                                // versionManager.checkout(versionableNodePath);
                                // versionManager.checkpoint(versionableNodePath);
                                // } catch (Exception e) {
                                // log.error("Girlscouts Rollout Service
                                // encountered error: ", e);
                                // }
                                deleteComponents(rr, rolloutLog, componentsToDelete);
                                checkReferences(sourcePageResource, relationPageResource, componentsToRollout);
                                rolloutComponents(sourcePageResource, rolloutLog, relationPagePath, componentsToRollout);
                                updatePageTitle(sourcePageResource, relationPageResource);
                                if (updateReferences) {
                                    updateAllReferences(sourcePageResource, relationPageResource, componentsToRollout);
                                }
                                pagesToActivate.add(relationPagePath);
                                rolloutLog.add("Page added to activation queue");
                                log.info("Page added to activation queue");
                            } catch (Exception e) {
                                log.error("Girlscouts Rollout Service encountered error: ", e);
                                // try {
                                // versionManager.checkin(versionableNodePath);
                                // } catch (Exception e2) {
                                // log.error("Girlscouts Rollout Service
                                // encountered error: ", e);
                                // }
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
        }
        submittedCouncils.removeAll(processedRelationCouncils);
    }

    private void checkReferences((Resource sourceResource, Resource targetResource, Set<String> sourceComponents) {
        Set<String> componentsWithNonExistingReferences = new HashSet<String>();
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
                                LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                                String targetComponentPath = relation.getTargetPath();
                                log.debug("Found relation at " + targetComponentPath);
                                Resource targetComponentResource = rr.resolve(targetComponentPath);
                                if (!ResourceUtil.isNonExistingResource(targetComponentResource)) {
                                    log.debug("Found valid resource at " + targetComponentPath);
                                    Node targetNode = targetComponentResource.adaptTo(Node.class);
                                    Session session = targetNode.getSession();
                                    PropertyIterator iter = targetNode.getProperties();
                                    while (iter.hasNext()) {
                                        String propertyName = "";
                                        try { // Try and catch property exception. We want to do our best.
                                            javax.jcr.Property property = iter.nextProperty();
                                            propertyName = property.getName();
                                            if (!isSystemProperty(propertyName)) {
                                                log.debug("Checking property " + property.getPath() + " for references");
                                                if (!property.isMultiple()) {
                                                    // Single value
                                                    if (checkPropertyType(property.getType())) {
                                                        String stringValue = property.getString();
                                                        if(!referencesValid(stringValue, sourceBranch, targetBranch, rr)){
                                                            componentsWithNonExistingReferences.add(srcComponentPath);
                                                        }
                                                    }
                                                } else {
                                                    // Multiple values
                                                    Value[] values = property.getValues();
                                                    if (values.length > 0 && checkPropertyType(values[0].getType())) { // Values are of the same type.
                                                        String[] stringValues = new String[values.length];
                                                        boolean replacedFlag = false;
                                                        for (int i = 0; i < values.length; i++) {
                                                            Value value = values[i];
                                                            String stringValue = value.getString();
                                                            if(!referencesValid(stringValue, sourceBranch, targetBranch, rr)){
                                                                componentsWithNonExistingReferences.add(srcComponentPath);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (RepositoryException e) {
                                            log.error("Error updating link references: node = " + targetNode.getPath() + " property = " + propertyName);
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

    private boolean referencesValid(String value, String sourceBranch, String targetBranch, ResourceResolver resourceResolver) {
        Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(value);
        while (m.find()) {
            String hrefValue = m.group(1);
            log.debug("Found href: " + hrefValue);
            //Is href pointing to template site page?
            if (hrefValue != null && hrefValue.startsWith(sourceBranch)) {
                log.debug("Replacing : " + sourceBranch + " with " + targetBranch);
                String newHrefValue = hrefValue.replace(sourceBranch, targetBranch);
                log.debug("Generated new href value: " + newHrefValue);
                String pathToTargetPage = newHrefValue.replace(".html", "");
                Resource resource = resourceResolver.resolve(pathToTargetPage);
                if (ResourceUtil.isNonExistingResource(resource)) {
                    log.debug("No page at: " + pathToTargetPage);
                    String lookedUpHrefValue = locatePageInTargetSite(hrefValue, targetBranch, resourceResolver);
                    if (lookedUpHrefValue == null || lookedUpHrefValue.trim().length() == 0) {
                        return false;
                    }else{
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateAllReferences(Resource sourceResource, Resource targetResource, Set<String> sourceComponents) {
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
                                LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                                String targetComponentPath = relation.getTargetPath();
                                log.debug("Found relation at " + targetComponentPath);
                                Resource targetComponentResource = rr.resolve(targetComponentPath);
                                if (!ResourceUtil.isNonExistingResource(targetComponentResource)) {
                                    log.debug("Found valid resource at " + targetComponentPath);
                                    Node targetNode = targetComponentResource.adaptTo(Node.class);
                                    Session session = targetNode.getSession();
                                    PropertyIterator iter = targetNode.getProperties();
                                    while (iter.hasNext()) {
                                        String propertyName = "";
                                        try { // Try and catch property exception. We want to do our best.
                                            javax.jcr.Property property = iter.nextProperty();
                                            propertyName = property.getName();
                                            if (!isSystemProperty(propertyName)) {
                                                log.debug("Checking property " + property.getPath() + " for references");
                                                if (!property.isMultiple()) {
                                                    // Single value
                                                    if (checkPropertyType(property.getType())) {
                                                        String stringValue = property.getString();
                                                        stringValue = replaceBranch(stringValue, sourceBranch, targetBranch, rr);
                                                        if (stringValue != null) {
                                                            targetNode.setProperty(property.getName(), stringValue);
                                                        }
                                                    }
                                                } else {
                                                    // Multiple values
                                                    Value[] values = property.getValues();
                                                    if (values.length > 0 && checkPropertyType(values[0].getType())) { // Values are of the same type.
                                                        String[] stringValues = new String[values.length];
                                                        boolean replacedFlag = false;
                                                        for (int i = 0; i < values.length; i++) {
                                                            Value value = values[i];
                                                            String stringValue = value.getString();
                                                            String newStringValue = replaceBranch(stringValue, sourceBranch, targetBranch, rr);
                                                            if (newStringValue != null) {
                                                                stringValues[i] = newStringValue;
                                                                replacedFlag = true;
                                                            } else {
                                                                stringValues[i] = stringValue;
                                                            }
                                                        }
                                                        if (replacedFlag) {
                                                            targetNode.setProperty(property.getName(), stringValues);
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (RepositoryException e) {
                                            log.error("Error updating link references: node = " + targetNode.getPath() + " property = " + propertyName);
                                        }
                                    }
                                    try {
                                        log.info("saving session");
                                        session.save();
                                    } catch (Exception e) {
                                        try {
                                            session.refresh(true);
                                        } catch (RepositoryException e1) {
                                            log.error("Cannot refresh the session.");
                                        }
                                        log.error("Cannot save the session.");
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

    private String replaceBranch(String value, String sourceBranch, String targetBranch, ResourceResolver resourceResolver) {
        Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
        Matcher m = p.matcher(value);
        boolean isReplaced = false;
        while (m.find()) {
            String hrefValue = m.group(1);
            log.debug("Found href: " + hrefValue);
            //Is href pointing to template site page?
            if (hrefValue != null && hrefValue.startsWith(sourceBranch)) {
                log.debug("Replacing : " + sourceBranch + " with " + targetBranch);
                String newHrefValue = hrefValue.replace(sourceBranch, targetBranch);
                log.debug("Generated new href value: " + newHrefValue);
                String pathToTargetPage = newHrefValue.replace(".html", "");
                Resource resource = resourceResolver.resolve(pathToTargetPage);
                if (ResourceUtil.isNonExistingResource(resource)) {
                    log.debug("No page at: " + pathToTargetPage);
                    String lookedUpHrefValue = locatePageInTargetSite(hrefValue, targetBranch, resourceResolver);
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

    private String locatePageInTargetSite(String hrefValue, String targetBranch, ResourceResolver resourceResolver) {
        log.debug("Locating live sync pages : " + hrefValue + " in " + targetBranch);
        String sourcePath = hrefValue.replace(".html", "");
        Resource sourcePageResource = resourceResolver.resolve(sourcePath);
        if (sourcePageResource != null) {
            try {
                String lookUpPath = targetBranch;
                if (lookUpPath.endsWith("/")) {
                    lookUpPath = lookUpPath.substring(0, lookUpPath.length() - 1);
                }
                LiveRelationshipManager relationManager = resourceResolver.adaptTo(LiveRelationshipManager.class);
                log.debug("Looking up live relationships for : " + sourcePageResource.getPath() + " in " + lookUpPath);
                RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, lookUpPath, null);
                while (relationsIterator.hasNext()) {
                    try {
                        LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                        String relationPagePath = relation.getTargetPath();
                        log.debug("Found relation at " + relationPagePath);
                        Resource candidateResource = resourceResolver.resolve(relationPagePath);
                        if (!ResourceUtil.isNonExistingResource(candidateResource)) {
                            log.debug("Found valid resource at " + relationPagePath);
                            return candidateResource.getPath() + ".html";
                        } else {
                            log.debug("No valid resource at " + relationPagePath);
                        }
                    } catch (Exception e) {
                        log.error("Unable to iterate through relations: " + e);
                    }
                }
            } catch (Exception e) {
                log.error("Error occurred while looking up live sync pages for " + hrefValue + ": ", e);
            }
        } else {
            log.debug("Unable to locate source page at " + sourcePath);
        }
        log.debug("Did not find any valid relationships for : " + sourcePageResource.getPath() + " in " + targetBranch);
        return null;
    }

    private boolean isSystemProperty(String propertyName) {
        return propertyName.startsWith("jcr:") || propertyName.startsWith("cq:") || propertyName.startsWith("sling:");
    }

    private boolean checkPropertyType(int type) {
        return type == PropertyType.STRING || type == PropertyType.NAME;
    }

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

    private void rolloutComponents(Resource srcRes, List<String> rolloutLog, String relationPath, Set<String> componentsToRollout) throws WCMException {
        log.info("Rolling out content for {}, components {}.", relationPath, componentsToRollout);
        RolloutManager.RolloutParams params = new RolloutManager.RolloutParams();
        params.isDeep = false;
        params.master = srcRes.adaptTo(Page.class);
        params.targets = new String[]{relationPath};
        params.paragraphs = componentsToRollout.toArray(new String[componentsToRollout.size()]);
        params.trigger = RolloutManager.Trigger.ROLLOUT;
        params.reset = false;
        rolloutManager.rollout(params);
        rolloutLog.add("Rolled out content to " + relationPath);
        log.info("Successfully rolled out content for {}.", relationPath);
    }

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
            Date runtime = new Date();
            html.append("<p>The workflow was run on " + runtime.toString() + ".</p>");
            String message = "", templatePath = "", srcPath = "";
            Boolean notify = false, useTemplate = false, delay = false, activate = true;
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

    private void sendCouncilNotifications(Node dateRolloutNode, List<String> councilNotificationLog, Boolean isTestMode, ResourceResolver rr) {
        Set<String> notifyCouncils = new TreeSet<String>();
        String subject = DEFAULT_ROLLOUT_NOTIFICATION_SUBJECT;
        String message = DEFAULT_ROLLOUT_NOTIFICATION_MESSAGE, templatePath = "", srcPath = "";
        Boolean notify = false, useTemplate = false;
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
                }
            }
        } catch (Exception e) {
            log.error("Girlscouts Rollout Service encountered error: ", e);
        }
    }

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

    private Node getLiveSyncNode(Node node) {
        try {
            if (node.hasNode("jcr:content/cq:LiveSyncConfig")) {
                return node.getNode("jcr:content/cq:LiveSyncConfig");
            }
        } catch (Exception e) {
        }
        return null;
    }

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
}