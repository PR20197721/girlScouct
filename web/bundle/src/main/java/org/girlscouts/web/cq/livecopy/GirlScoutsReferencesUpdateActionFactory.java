package org.girlscouts.web.cq.livecopy;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;

@SuppressWarnings("deprecation")
@Component(metatype=false)
@Service
public class GirlScoutsReferencesUpdateActionFactory implements LiveActionFactory<LiveAction> {

	private static Logger log = LoggerFactory.getLogger(GirlScoutsReferencesUpdateActionFactory.class);

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
                                stringValue = replaceBranch(stringValue, sourceBranch, targetBranch);
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
                                    String newStringValue = replaceBranch(stringValue, sourceBranch, targetBranch);
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

        private String replaceBranch(String value, String sourceBranch, String targetBranch) {
			String regex = sourceBranch.replace("/", "\\/");
            if (value.indexOf(sourceBranch) != -1) {
                return value.replaceAll(regex, targetBranch);
            } else {
                return null; // Returns null if not match
            }
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
