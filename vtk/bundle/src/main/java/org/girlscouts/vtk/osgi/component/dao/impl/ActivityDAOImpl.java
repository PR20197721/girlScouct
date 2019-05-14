package org.girlscouts.vtk.dao.impl;

import com.day.cq.commons.jcr.JcrConstants;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ValueMap;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.osgi.component.util.UserUtil;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.service.GirlScoutsActivityOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsJCRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(metatype = true, immediate = true)
@Service(value = ActivityDAO.class)
@Properties({@Property(name = "label", value = "Girl Scouts VTK Activity DAO"), @Property(name = "description", value = "Girl Scouts VTK Activity DAO")})
public class ActivityDAOImpl implements ActivityDAO {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference
    private UserUtil userUtil;

    @Reference
    private GirlScoutsJCRService girlScoutsRepoService;

    @Reference
    private GirlScoutsActivityOCMService girlScoutsActivityOCMService;

    @Activate
    void activate() {

    }

    public void addToYearPlan(User user, Troop troop, Activity activity) throws IllegalStateException, IllegalAccessException {

        try {
            if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_ACTIVITY_ID)) {
                throw new IllegalAccessException();
            }
            YearPlan plan = troop.getYearPlan();
            List<Activity> activities = plan.getActivities();
            if (activities == null) {
                activities = new java.util.ArrayList<Activity>();
            }
            // add refId path
            String refId = null;
            try {
                if (activity.getRefUid() != null) { // cust activ
                    refId = getPath(user, activity.getRefUid());
                    if (refId != null) {
                        activity.setRefUid(refId);
                    }
                }
            } catch (Exception e1) {
                log.error("Error Occurred: ", e1);
            }
            activities.add(activity);
            plan.setActivities(activities);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
    }

    private String getPath(User user, String uuid) throws IllegalStateException {
        if (uuid == null) {
            return null;
        }
        String path = null;
        ValueMap valueMap = null;
        try {
            valueMap = girlScoutsRepoService.getNodeById(uuid);
            if (valueMap != null) {
                path = valueMap.get(JcrConstants.JCR_PATH).toString().replace("/jcr:content", "");
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return path;
    }

    public boolean isActivityByPath(User user, String path) {
        Activity activity = girlScoutsActivityOCMService.read(path);
        if(activity != null){
            return true;
        }
        return false;
    }

    public Activity updateActivity(User user, Troop troop, Activity activity) throws IllegalAccessException, IllegalStateException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        return girlScoutsActivityOCMService.update(activity);
    }
}
