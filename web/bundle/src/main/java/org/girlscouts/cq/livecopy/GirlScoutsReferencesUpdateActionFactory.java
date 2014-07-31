package org.girlscouts.cq.livecopy;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.ActionConfig;
import com.day.cq.wcm.msm.api.LiveAction;
import com.day.cq.wcm.msm.api.LiveActionFactory;
import com.day.cq.wcm.msm.api.LiveRelationship;

@SuppressWarnings("deprecation")
@Component(metatype=false)
@Service
public class GirlScoutsReferencesUpdateActionFactory implements LiveActionFactory<LiveAction> {

    @org.apache.felix.scr.annotations.Property(name="liveActionName")
    private static final String[] LIVE_ACTION_NAME = { GirlScoutsReferencesUpdateActionFactory.GirlScoutsReferencesUpdateAction.class.getSimpleName(), "gsReferencesUpdate" };

    public LiveAction createAction(Resource resource) throws WCMException {
        return new GirlScoutsReferencesUpdateAction();
    }

    public String createsAction() {
        return LIVE_ACTION_NAME[0];
    }
    
    public static class GirlScoutsReferencesUpdateAction implements LiveAction {
        // Level 1 branch. e.g. /content/girlscouts-template
        private static final int BRANCH_LEVEL = 1;
        
        public void execute(Resource source, Resource target,
                LiveRelationship relation, boolean autosave, boolean isResetRollout)
                throws WCMException {
            Node sourceNode = (Node)source.adaptTo(Node.class);
            Node targetNode = (Node)target.adaptTo(Node.class);
            if (sourceNode == null) {
                throw new WCMException("Cannot access node: " + source);
            }
            if (targetNode == null) {
                throw new WCMException("Cannot access node: " + target);
            }
            
            String sourcePath = source.getPath();
            String targetPath = target.getPath();
            String sourceBranch = getBranch(sourcePath);
            String targetBranch = getBranch(targetPath);
            
            PropertyIterator iter;
            try {
                iter = targetNode.getProperties();
                while (iter.hasNext()) {
                    Property property = iter.nextProperty();
                    String propertyName = property.getName();
                    if (propertyName.startsWith("jcr:") || 
                            propertyName.startsWith("cq:") ||
                            propertyName.startsWith("sling:")) {
                        // Skip CQ properties
                        continue;
                    }
                    if (!property.isMultiple()) {
                        if (property.getType() == PropertyType.STRING) {
                            String stringValue = property.getString();
                            stringValue = replaceBranch(stringValue, sourceBranch, targetBranch);
                            if (stringValue != null) {
                                targetNode.setProperty(property.getName(), stringValue);
                            } 
                        }
                    } else {
                        Value[] values = property.getValues();
                        if (values.length > 0 && 
                                values[0].getType() == PropertyType.STRING) { // Values are of the same type.
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
                }
            } catch (RepositoryException e) {
                throw new WCMException(e.getMessage(), e);
            }
        }
        
        private String replaceBranch(String value, String sourceBranch, String targetBranch) {
            String regex = "\\Q" + sourceBranch + "\\E";
            if (value.indexOf(sourceBranch) != -1) {
                return value.replaceAll(regex, targetBranch);
            } else {
                return null; // Returns null if not match
            }
        }
        
        private String getBranch(String path) throws WCMException {
            if (!path.startsWith("/")) {
                throw new WCMException("Not absolute path: " + path);
            }
            
            String[] parts = path.substring(1).split("/"); // Skip the root slash
            if (parts.length < BRANCH_LEVEL + 1) {
                throw new WCMException("Cannot get level " + BRANCH_LEVEL + " branch: " + path);
            }
            
            StringBuilder branchBuilder = new StringBuilder();
            for (int i = 0; i < BRANCH_LEVEL + 1; i++) {
                branchBuilder.append("/").append(parts[i]);
            }
            return branchBuilder.toString();
        }

        public String getName() {
            return GirlScoutsReferencesUpdateActionFactory.LIVE_ACTION_NAME[0];
        } 
        
        @Deprecated
        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave, boolean isResetRollout)
                throws WCMException {
        }

        @Deprecated
        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave) throws WCMException {
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
