package org.girlscouts.cq.livecopy;

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
public class GirlScoutsReferenceUpdateActionFactory implements LiveActionFactory {

    @Property(name="liveActionName")
    private static final String[] LIVE_ACTION_NAME = { GirlScoutsReferenceUpdateActionFactory.GirlScoutsReferenceUpdateAction.class.getSimpleName(), "GSReferencesUpdate" };

    public LiveAction createAction(Resource arg0) throws WCMException {
        // TODO Auto-generated method stub
        return null;
    }

    public String createsAction() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static class GirlScoutsReferenceUpdateAction implements LiveAction {

        public void execute(ResourceResolver arg0, LiveRelationship arg1,
                ActionConfig arg2, boolean arg3) throws WCMException {
            // TODO Auto-generated method stub
            
        }

        public void execute(Resource arg0, Resource arg1,
                LiveRelationship arg2, boolean arg3, boolean arg4)
                throws WCMException {
            // TODO Auto-generated method stub
            
        }

        public void execute(ResourceResolver arg0, LiveRelationship arg1,
                ActionConfig arg2, boolean arg3, boolean arg4)
                throws WCMException {
            // TODO Auto-generated method stub
            
        }

        public String getName() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getParameterName() {
            // TODO Auto-generated method stub
            return null;
        }

        public String[] getPropertiesNames() {
            // TODO Auto-generated method stub
            return null;
        }

        public int getRank() {
            // TODO Auto-generated method stub
            return 0;
        }

        public String getTitle() {
            // TODO Auto-generated method stub
            return null;
        }

        public void write(JSONWriter arg0) throws JSONException {
            // TODO Auto-generated method stub
            
        }
        
    }
}
