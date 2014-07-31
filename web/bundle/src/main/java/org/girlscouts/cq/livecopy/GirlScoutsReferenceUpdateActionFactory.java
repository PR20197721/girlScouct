package org.girlscouts.cq.livecopy;

import javax.jcr.Node;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
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

@Component(metatype=false)
@Service
public class GirlScoutsReferenceUpdateActionFactory implements LiveActionFactory<LiveAction> {

    @Property(name="liveActionName")
    private static final String[] LIVE_ACTION_NAME = { GirlScoutsReferenceUpdateActionFactory.GirlScoutsReferenceUpdateAction.class.getSimpleName(), "gsReferencesUpdate" };

    public LiveAction createAction(Resource resource) throws WCMException {
        System.err.println("Resource path = " + resource.getPath());
        return new GirlScoutsReferenceUpdateAction();
    }

    public String createsAction() {
        return LIVE_ACTION_NAME[0];
    }
    
    public static class GirlScoutsReferenceUpdateAction implements LiveAction {
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
            
            System.err.println("src = " + sourcePath + " dest = " + targetPath);
        }
        
        public String getName() {
            return GirlScoutsReferenceUpdateActionFactory.LIVE_ACTION_NAME[0];
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

        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave, boolean isResetRollout)
                throws WCMException {
            // deprecated 
        }

        public void execute(ResourceResolver rr, LiveRelationship relation,
                ActionConfig config, boolean autosave) throws WCMException {
            // deprecated 
        }

        public String getParameterName() {
            // deprecated 
            return null;
        }

        public String[] getPropertiesNames() {
            // deprecated 
            return null;
        }

        public int getRank() {
            // deprecated 
            return 0;
        }

        public String getTitle() {
            // deprecated 
            return null;
        }

        public void write(JSONWriter arg0) throws JSONException {
            // deprecated 
        }
    }
}
