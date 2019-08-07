package org.girlscouts.vtk.osgi.component.dao.impl;

import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.osgi.component.dao.ActivityDAO;
import org.girlscouts.vtk.osgi.component.util.UserUtil;
import org.girlscouts.vtk.osgi.service.GirlScoutsActivityOCMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(metatype = true, immediate = true)
@Service(value = ActivityDAO.class)
@Properties({@Property(name = "label", value = "Girl Scouts VTK Activity DAO"), @Property(name = "description", value = "Girl Scouts VTK Activity DAO")})
public class ActivityDAOImpl implements ActivityDAO {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private UserUtil userUtil;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();
    @Reference
    private GirlScoutsActivityOCMService girlScoutsActivityOCMService;

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
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
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            Node node = session.getNodeByIdentifier(uuid);
            if (node != null) {
                path = node.getPath().replace("/jcr:content", "");
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return path;
    }

    public boolean isActivityByPath(User user, String path) {
        boolean isActivity = true;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            String sql = "SELECT * FROM [nt:base] as s WHERE ISDESCENDANTNODE([" + path + "])";
            log.debug("SQL " + sql);
            session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.JCR_SQL2);
            QueryResult result = q.execute();
            boolean isFound = false;
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value excerpt = r.getValue("jcr:path");
                log.debug("Patha: " + excerpt.getString());
                if (excerpt.getString().equals(path)) {
                    isActivity = true;
                    isFound = true;
                } else {
                    isFound = false;
                }
            }
            if (!isFound) {
                isActivity = false;
            }
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        } finally {
            try {
                if (rr != null) {
                    rr.close();
                }
            } catch (Exception e) {
                log.error("Exception is thrown closing resource resolver: ", e);
            }
        }
        return isActivity;
    }

    public Activity updateActivity(User user, Troop troop, Activity activity) throws IllegalAccessException, IllegalStateException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        return girlScoutsActivityOCMService.update(activity);
    }
}
