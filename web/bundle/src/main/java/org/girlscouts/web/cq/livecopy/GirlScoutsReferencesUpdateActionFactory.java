package org.girlscouts.web.cq.livecopy;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.*;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.girlscouts.web.cq.workflow.service.RolloutTemplatePageService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Property;
import javax.jcr.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
@Component(metatype=false)
@Service
public class GirlScoutsReferencesUpdateActionFactory implements LiveActionFactory<LiveAction> {

	private static Logger log = LoggerFactory.getLogger(GirlScoutsReferencesUpdateActionFactory.class);
    
	@Reference
	static RolloutTemplatePageService rolloutTemplatePageService;
	
	@Activate
	private void activate(ComponentContext context) {
		log.info("GirlScoutsReferencesUpdateActionFactory Activated.");
	}

	@Deactivate
	private void deactivate(ComponentContext componentContext) {
		log.info("GirlScoutsReferencesUpdateActionFactory Deactivated.");
	}

    @org.apache.felix.scr.annotations.Property(name="liveActionName")
    private static final String[] LIVE_ACTION_NAME = { GirlScoutsReferencesUpdateActionFactory.GirlScoutsReferencesUpdateAction.class.getSimpleName(), "gsReferencesUpdate" };
    
	public LiveAction createAction(Resource resource) throws WCMException {
        return new GirlScoutsReferencesUpdateAction();
    }

    public String createsAction() {
        return LIVE_ACTION_NAME[0];
    }
    
    public static class GirlScoutsReferencesUpdateAction implements LiveAction {
        private static Logger log = LoggerFactory.getLogger(GirlScoutsReferencesUpdateAction.class);
        // Level 1 branch. e.g. /content/girlscouts-template
        private static final int BRANCH_LEVEL = 1;
        private static final String LIVE_SYNC_ERROR_PROPERTY = "gslivesyncerror";
		private static final Pattern BRANCH_PATTERN = Pattern.compile("^(/content/[^/]+)/?");
        
        public void execute(Resource source, Resource target,
                LiveRelationship relation, boolean autosave, boolean isResetRollout)
				throws WCMException {
            log.debug("Processing source:"+source.getPath()+", target:"+target.getPath());
        	String checkBlockReferenceUpdate= rolloutTemplatePageService.blockReferenceUpdateAction.get();//GSWP-2235 checks if update reference action was initiated from rollout workflow
        	log.info("checking if update reference action is initiated from rollout workflow, checkBlockReferenceUpdate="+checkBlockReferenceUpdate);
        	if(checkBlockReferenceUpdate != null) {
        		if (checkBlockReferenceUpdate.equalsIgnoreCase("blockInitiatedFromWorkflow")) {
        		 log.info("Reference update action is initiated from rollout workflow, hence Quit");
        		 return;
        		}
        	}
        	
            if (source == null) {
                log.info("Source is null. Quit");
                return;
            }
            if (target == null) {
                log.info("Target is null. Quit");
                return;
            }
            Node sourceNode = (Node)source.adaptTo(Node.class);
            Node targetNode = (Node)target.adaptTo(Node.class);
            if (sourceNode == null) {
                log.error("Cannot access source node: " + source + ". Quit.");
                return;
            }
            if (targetNode == null) {
                log.error("Cannot access target node: " + target + ". Quit.");
                return;
            }
            
            String sourcePath = source.getPath();
            String targetPath = target.getPath();
            String sourceBranch = getBranch(sourcePath);
            String targetBranch = getBranch(targetPath);
            
            try {
                PropertyIterator iter = targetNode.getProperties();
                Property property = null;
                while (iter.hasNext()) {
                    try { // Try and catch property exception. We want to do our best.
                        property = iter.nextProperty();
						String propertyName = property.getName();
                        if (propertyName.startsWith("jcr:") || 
                                propertyName.startsWith("cq:") ||
                                propertyName.startsWith("sling:")) {
                            // Skip CQ properties
                            continue;
                        }
                        if (!property.isMultiple()) {
                            // Single value
                            if (checkPropertyType(property.getType())) {
                                String stringValue = property.getString();
                                stringValue = replaceBranch(stringValue, sourceBranch, targetBranch, source.getResourceResolver());
                                if (stringValue != null) {
                                    targetNode.setProperty(property.getName(), stringValue);
                                } 
                            }
                        } else {
                            // Multiple values
                            Value[] values = property.getValues();
                            if (values.length > 0 && 
                                    checkPropertyType(values[0].getType())) { // Values are of the same type.
                                String[] stringValues = new String[values.length];
                                boolean replacedFlag = false;
                                for (int i = 0; i < values.length; i++) {
                                    Value value = values[i];
                                    String stringValue = value.getString();
                                    String newStringValue = replaceBranch(stringValue, sourceBranch, targetBranch, source.getResourceResolver());
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
                    } catch (RepositoryException e) {
                        // Save the live sync error in the node
                        String error = "";
                        if (targetNode.hasProperty(LIVE_SYNC_ERROR_PROPERTY)) {
                            error = targetNode.getProperty(LIVE_SYNC_ERROR_PROPERTY).getString() + ",";
                        }
                        error += property.getName();
                        targetNode.setProperty(LIVE_SYNC_ERROR_PROPERTY, error);
                        
                        log.error("Live Sync error: Girl Scouts References Update Action: node = " + targetNode.getPath() + " property = " + property.getName());
                    }
                }
                
                Session session = targetNode.getSession();
				if (autosave) {
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
                }
            } catch (RepositoryException e) {
                log.error("Repository Exception: source node = " + source + " target node = " + target);
                throw new WCMException(e.getMessage(), e);
            }
        }
        
        private boolean checkPropertyType(int type) {
            return type == PropertyType.STRING || type == PropertyType.NAME;
        }

        private String replaceBranch(String value, String sourceBranch, String targetBranch, ResourceResolver resourceResolver) {
            Pattern p = Pattern.compile("href=\"(.*?)\"", Pattern.DOTALL);
            Matcher m = p.matcher(value);
            boolean isReplaced = false;
            while (m.find()){
                String hrefValue = m.group(1);
                log.debug("Found href: "+hrefValue);
                //Is href pointing to template site page?
                if(hrefValue != null && hrefValue.startsWith(sourceBranch)){
                    log.debug("Replacing : "+sourceBranch + " with " + targetBranch);
                    String newHrefValue = hrefValue.replace(sourceBranch, targetBranch);
                    log.debug("Generated new href value: "+newHrefValue);
                    String pathToTargetPage = newHrefValue.replace(".html","");
                    Resource resource = resourceResolver.resolve(pathToTargetPage);
                    if(ResourceUtil.isNonExistingResource(resource)){
                        log.debug("No page at: "+pathToTargetPage);
                        String lookedUpHrefValue = locatePageInTargetSite(hrefValue, targetBranch, resourceResolver);
                        if(lookedUpHrefValue != null && lookedUpHrefValue.trim().length() > 0){
                            newHrefValue = lookedUpHrefValue;
                            log.debug("Before replace:"+value);
                            value = value.replace(hrefValue, newHrefValue);
                            log.debug("After replace:"+value);
                            isReplaced = true;
                        }
                    }else{
                        log.debug("Before replace:"+value);
                        value = value.replace(hrefValue, newHrefValue);
                        log.debug("After replace:"+value);
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
            log.debug("Locating live sync pages : "+hrefValue+ " in "+targetBranch);
            String sourcePath = hrefValue.replace(".html","");
            Resource sourcePageResource = resourceResolver.resolve(sourcePath);
            if(sourcePageResource != null) {
                try {
                    String lookUpPath = targetBranch;
                    if(lookUpPath.endsWith("/")){
                        lookUpPath = lookUpPath.substring(0,lookUpPath.length()-1);
                    }
                    LiveRelationshipManager relationManager = resourceResolver.adaptTo(LiveRelationshipManager.class);
                    log.debug("Looking up live relationships for : "+sourcePageResource.getPath()+ " in "+lookUpPath);
                    RangeIterator relationsIterator = relationManager.getLiveRelationships(sourcePageResource, lookUpPath, null);
                    while (relationsIterator.hasNext()) {
                        try {
                            LiveRelationship relation = (LiveRelationship) relationsIterator.next();
                            String relationPagePath = relation.getTargetPath();
                            log.debug("Found relation at "+relationPagePath);
                            Resource candidateResource = resourceResolver.resolve(relationPagePath);
                            if(!ResourceUtil.isNonExistingResource(candidateResource)){
                                log.debug("Found valid resource at "+relationPagePath);
                                return candidateResource.getPath()+".html";
                            }else{
                                log.debug("No valid resource at "+relationPagePath);
                            }
                        } catch (Exception e) {
                            log.error("Unable to iterate through relations: "+e);
                        }
                    }
                }catch(Exception e){
                    log.error("Error occurred while looking up live sync pages for "+hrefValue+": ",e);
                }
            }else{
                log.debug("Unable to locate source page at "+sourcePath);
            }
            log.debug("Did not find any valid relationships for : "+sourcePageResource.getPath()+ " in "+targetBranch);
            return null;
        }

        private String getBranch(String path) throws WCMException {
            Matcher matcher = BRANCH_PATTERN.matcher(path);
            if (matcher.find()) {
                return matcher.group();
            } else {
                throw new WCMException("Cannot get level " + BRANCH_LEVEL + " branch: " + path);
            }
        }

        public String getName() {
            return GirlScoutsReferencesUpdateActionFactory.LIVE_ACTION_NAME[0];
        } 

		@Deprecated
		public void execute(ResourceResolver rr, LiveRelationship relation, ActionConfig config, boolean autosave,
				boolean isResetRollout) throws WCMException {
		}

		@Deprecated
		public void execute(ResourceResolver rr, LiveRelationship relation, ActionConfig config, boolean autosave)
				throws WCMException {
		}

		@Deprecated
		public String getParameterName() {
			return null;
		}

		@Deprecated
		public String[] getPropertiesNames() {
			return null;
		}

		@Deprecated
		public int getRank() {
			return 0;
		}

		@Deprecated
		public String getTitle() {
			return null;
		}

		@Deprecated
		public void write(JSONWriter writer) throws JSONException {
		}
    }
}
