package org.girlscouts.vtk.osgi.component.dao.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.dao.YearPlanDAO;
import org.girlscouts.vtk.osgi.component.util.VtkUtil;
import org.girlscouts.vtk.osgi.service.GirlScoutsActivityOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsMeetingOCMService;
import org.girlscouts.vtk.osgi.service.GirlScoutsYearPlanOCMService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.query.*;
import java.util.*;

@Component
@Service(value = YearPlanDAO.class)
public class YearPlanDAOImpl implements YearPlanDAO {
    private final Logger log = LoggerFactory.getLogger(YearPlanDAOImpl.class);
    @Reference
    private GirlScoutsYearPlanOCMService girlScoutsYearPlanOCMService;
    @Reference
    private GirlScoutsActivityOCMService girlScoutsActivityOCMService;
    @Reference
    private GirlScoutsMeetingOCMService girlScoutsMeetingOCMService;
    @Reference
    private ResourceResolverFactory resolverFactory;
    private Map<String, Object> resolverParams = new HashMap<String, Object>();

    @Activate
    void activate() {
        this.resolverParams.put(ResourceResolverFactory.SUBSERVICE, "vtkService");
    }

    public List<YearPlan> getAllYearPlans(User user, String ageLevel) {
        java.util.List<YearPlan> yearPlans = null;
        try {
            java.util.Calendar today = java.util.Calendar.getInstance();
            yearPlans = girlScoutsYearPlanOCMService.findObjects("/content/girlscouts-vtk/yearPlanTemplates/yearplan" + VtkUtil.getCurrentGSYear() + "/" + ageLevel + "/", null);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return yearPlans;
    }

    public YearPlan getYearPlan(String path) {
        YearPlan yearPlan = null;
        try {
            yearPlan = girlScoutsYearPlanOCMService.findObject(path, null);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return yearPlan;
    }

    public Date getLastModif(Troop troop) {
        Date toRet = null;
        ResourceResolver rr = null;
        try {
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            String sql = "select jcr:lastModified from nt:base where jcr:path = '" + troop.getPath() + "' and jcr:lastModified is not null";
            Query q = qm.createQuery(sql, Query.SQL);
            log.debug("Executing JCR query: " + sql);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                toRet = new Date(r.getValue("jcr:lastModified").getLong());
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
        return toRet;
    }

    public Date getLastModifByOthers(Troop troop, String sessionId) {
        Date toRet = null;
        ResourceResolver rr = null;
        try {
            String sql = "select jcr:lastModified from nt:base where jcr:path = '" + troop.getPath() + "' and jcr:lastModified is not null";
            if (sessionId != null) {
                sql += " and currentTroop <>'" + sessionId + "'";
            }
            rr = resolverFactory.getServiceResourceResolver(resolverParams);
            Session session = rr.adaptTo(Session.class);
            QueryManager qm = session.getWorkspace().getQueryManager();
            Query q = qm.createQuery(sql, Query.SQL);
            log.debug("Executing JCR query: " + sql);
            QueryResult result = q.execute();
            for (RowIterator it = result.getRows(); it.hasNext(); ) {
                Row r = it.nextRow();
                toRet = new Date(r.getValue("jcr:lastModified").getLong());
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
        return toRet;
    }

    public YearPlan getYearPlanJson(String yearPlanPath) {
        YearPlan yearPlan = null;
        try {
            //year plan info
            YearPlan templateYearPlan = girlScoutsYearPlanOCMService.read(yearPlanPath);
            if (templateYearPlan == null) {
                return null;
            }
            yearPlan = new YearPlan();
            yearPlan.setName(templateYearPlan.getName());
            yearPlan.setDesc(templateYearPlan.getDesc());
            List<MeetingE> meetingInfos = new ArrayList();
            List<MeetingE> templateMeetings = templateYearPlan.getMeetingEvents();
            if (templateMeetings != null) {
                for (MeetingE templateMeetingReference : templateMeetings) {
                    String refId = templateMeetingReference.getRefId();
                    int position = Integer.parseInt(templateMeetingReference.getId());
                    Meeting templateMeeting = girlScoutsMeetingOCMService.read(refId);
                    if (templateMeeting == null) {
                        continue;
                    }
                    MeetingE masterMeeting = new MeetingE();
                    Meeting meetingInfo = new Meeting();
                    meetingInfo.setName(templateMeeting.getName());
                    meetingInfo.setBlurb(templateMeeting.getBlurb());
                    meetingInfo.setPosition(position);
                    meetingInfo.setSortOrder(position);
                    masterMeeting.setSortOrder(position);
                    meetingInfo.setId(templateMeeting.getId());
                    meetingInfo.setCat(templateMeeting.getCat());
                    meetingInfo.setReq(templateMeeting.getReq());
                    meetingInfo.setReqTitle(templateMeeting.getReqTitle());
                    //get activities
                    List<Activity> activitiesFromTemplate = girlScoutsActivityOCMService.findObjects(refId, null);
                    List<Activity> activities = new ArrayList();
                    if (activitiesFromTemplate != null) {
                        for (Activity activitiyFromTemplate : activitiesFromTemplate) {
                            boolean isOutdoorAvailable = activitiyFromTemplate.getOutdoor();
                            boolean isGlobalAvailable = activitiyFromTemplate.getGlobal();
                            if (isOutdoorAvailable || isGlobalAvailable) {
                                Activity activity = new Activity();
                                if (isOutdoorAvailable) {
                                    activity.setOutdoor(true);
                                    masterMeeting.setAnyOutdoorActivityInMeetingAvailable(true);
                                }
                                if (isGlobalAvailable) {
                                    activity.setGlobal(true);
                                    masterMeeting.setAnyGlobalActivityInMeetingAvailable(true);
                                }
                                activities.add(activity);
                                break;
                            }
                        }
                        meetingInfo.setActivities(activities);
                    }
                    masterMeeting.setMeetingInfo(meetingInfo);
                    meetingInfos.add(masterMeeting);
                }
            }
            Comparator<MeetingE> comp = new Comparator<MeetingE>() {
                public int compare(MeetingE m1, MeetingE m2) {
                    return m1.getMeetingInfo().getSortOrder().compareTo(m2.getMeetingInfo().getSortOrder());
                }
            };
            Collections.sort(meetingInfos, comp);
            yearPlan.setMeetingEvents(meetingInfos);
        } catch (Exception e) {
            log.error("Error Occurred: ", e);
        }
        return yearPlan;
    }
}
