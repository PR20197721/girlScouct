package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.*;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.ActivityDAO;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import org.girlscouts.vtk.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(metatype = true, immediate = true)
@Service(value = ActivityDAO.class)
@Properties({@Property(name = "label", value = "Girl Scouts VTK Activity DAO"), @Property(name = "description", value = "Girl Scouts VTK Activity DAO")})
public class ActivityDAOImpl implements ActivityDAO {
    private final Logger log = LoggerFactory.getLogger("vtk");

    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Reference
    private UserUtil userUtil;

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public void createActivity(User user, Troop troop, Activity activity) throws IllegalStateException, IllegalAccessException {
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_ACTIVITY_ID)) {
                throw new IllegalAccessException();
            }
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Troop.class);
            classes.add(Activity.class);
            classes.add(JcrCollectionHoldString.class);
            classes.add(YearPlan.class);
            classes.add(MeetingE.class);
            classes.add(Cal.class);
            classes.add(Location.class);
            classes.add(Asset.class);
            classes.add(Milestone.class);
            classes.add(SentEmail.class);
            classes.add(MeetingCanceled.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
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
                e1.printStackTrace();
            }
            activities.add(activity);
            plan.setActivities(activities);
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
    }

    public boolean isActivity(User user, Troop troop, String uuid) throws IllegalAccessException {
        if (user != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        Session session = null;
        javax.jcr.Node node = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            node = session.getNodeByIdentifier(uuid);

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
        return node != null;

    }

    private String getPath(User user, String uuid) throws IllegalStateException {
        if (uuid == null) {
            return null;
        }
        String path = null;
        javax.jcr.Node node = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            node = session.getNodeByIdentifier(uuid);
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
        Session session = null;
        boolean isActivity = true;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select jcr:path from nt:base where jcr:path = '" + path + "'";
            log.debug("SQL " + sql);
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.SQL);
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

    public Activity findActivity(User user, String path) {
        Activity activity = null;
        Session session = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            String sql = "select child.register, child.address, parent.[jcr:uuid], child.start, parent.[jcr:title], child.details, child.end,child.locationLabel,child.srchdisp  from [nt:base] as parent INNER JOIN [nt:base] as child ON ISCHILDNODE(child, parent) where  (isdescendantnode (parent, [" + path + "])) and child.start is not null and parent.[jcr:title] is not null ";
            session = rr.adaptTo(Session.class);
            javax.jcr.query.QueryManager qm = session.getWorkspace().getQueryManager();
            javax.jcr.query.Query q = qm.createQuery(sql, javax.jcr.query.Query.JCR_SQL2);
            int i = 0;
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                Value[] v = r.getValues();
                activity = new Activity();
                activity.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
                activity.setContent(r.getValue("child.details").getString());
                activity.setDate(r.getValue("child.start").getDate().getTime());
                try {
                    activity.setEndDate(r.getValue("child.end").getDate().getTime());
                } catch (Exception e) {
                }
                activity.setLocationName(r.getValue("child.locationLabel").getString());
                try {
                    activity.setLocationAddress(r.getValue("child.address").getString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                activity.setName(r.getValue("child.srchdisp").getString());
                activity.setName(r.getValue("parent.jcr:title").getString());
                activity.setType(YearPlanComponentType.ACTIVITY);
                activity.setId("ACT" + i);
                // patch
                if (activity.getDate() != null && activity.getEndDate() == null) {
                    activity.setEndDate(activity.getDate());
                }
                activity.setIsEditable(false);
                try {
                    activity.setRefUid(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    activity.setRegisterUrl(r.getValue("child.register").getString());
                } catch (Exception e) {
                }
                i++;
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
        return activity;
    }

    public boolean updateActivity(User user, Troop troop, Activity activity) throws IllegalAccessException, IllegalStateException {
        Session session = null;
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            session = rr.adaptTo(Session.class);
            List<Class> classes = new ArrayList<Class>();
            classes.add(Activity.class);
            classes.add(Asset.class);
            classes.add(SentEmail.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ObjectContentManager ocm = new ObjectContentManagerImpl(session, mapper);
            ocm.update(activity);
            ocm.save();
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
        return false;
    }
}
