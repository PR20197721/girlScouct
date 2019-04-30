package org.girlscouts.vtk.ejb;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.*;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Component
@Service(value = YearPlanUtil.class)
public class YearPlanUtil {

    @Reference
    TroopDAO troopDAO;

    @Reference
    ActivityDAO activityDAO;

    @Reference
    YearPlanDAO yearPlanDAO;

    @Reference
    private MeetingDAO meetingDAO;

    @Reference
    private CouncilDAO councilDAO;

    @Reference
    private UserUtil userUtil;

    public void createActivity(User user, Troop troop, Activity activity) throws java.lang.IllegalAccessException, VtkException, IllegalStateException {
        activity.setDbUpdate(true);
        activityDAO.createActivity(user, troop, activity);
        troop.getYearPlan().setAltered("true");
        troopDAO.updateTroop(user, troop);
    }

    public void checkCanceledActivity(User user, Troop troop) throws java.lang.IllegalAccessException, VtkException {
        if (troop == null || troop.getYearPlan() == null || troop.getYearPlan().getActivities() == null || troop.getYearPlan().getActivities().size() == 0) {
            return;
        }
        List<Activity> activity2Cancel = new ArrayList<Activity>();
        List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            if (!activities.get(i).getIsEditable() && !(activities.get(i).getCancelled() != null && activities.get(i).getCancelled().equals("true")) && !activityDAO.isActivityByPath(user, activities.get(i).getRefUid())) {
                activities.get(i).setCancelled("true");
                activity2Cancel.add(activities.get(i));
            }
        }
        if (activity2Cancel != null && activity2Cancel.size() > 0) {
            troopDAO.updateTroop(user, troop);
        }
    }

    public List<YearPlan> getAllYearPlans(User user, String ageLevel) {
        return yearPlanDAO.getAllYearPlans(user, ageLevel);
    }

    public YearPlan getYearPlan(String path) {
        return yearPlanDAO.getYearPlan(path);
    }

    public List<Asset> getAids(User user, Troop troop, String tags, String meetingName, String uids, String path) throws IllegalAccessException {
        List<Asset> container = new ArrayList();
        container.addAll(meetingDAO.getAidTag_local(user, troop, tags, meetingName, path));
        container.addAll(meetingDAO.getAidTag(user, troop, tags, meetingName));
        return container;
    }

    @SuppressWarnings("unchecked")
    public net.fortuna.ical4j.model.Calendar yearPlanCal(User user, Troop troop) throws Exception {
        Map<Date, YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
        if (!userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            return null;
        }
        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        Iterator itr = sched.keySet().iterator();
        while (itr.hasNext()) {
            Date dt = (Date) itr.next();
            YearPlanComponent _comp = sched.get(dt);
            Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(dt);
            String desc = "", location = "";
            java.util.Date endDate = null;
            switch (_comp.getType()) {
                case ACTIVITY:
                    Activity a = ((Activity) _comp);
                    location = (a.getLocationName() == null ? "" : a.getLocationName());
                    location += " " + (a.getLocationAddress() == null ? "" : a.getLocationAddress().replace("\r", ""));
                    desc = ((Activity) _comp).getName();
                    endDate = a.getEndDate();
                    break;
                case MEETING:
                    Meeting meetingInfo = meetingDAO.getMeeting(user, troop, ((MeetingE) _comp).getRefId());
                    desc = meetingInfo.getName();
                    location = getLocation(troop, ((MeetingE) _comp).getLocationRef());
                    int totalMeetingMin = VtkUtil.getMeetingEndTime(((MeetingE) _comp).getMeetingInfo());
                    java.util.Calendar endTimeCal = java.util.Calendar.getInstance();
                    endTimeCal.setTime(cal.getTime());
                    endTimeCal.add(java.util.Calendar.MINUTE, totalMeetingMin);
                    endDate = endTimeCal.getTime();
                    break;
            }
            if (endDate == null) {
                endDate = cal.getTime();
            }
            final List events = new ArrayList();
            final VEvent event = new VEvent(new DateTime(cal.getTime()), new DateTime(endDate), desc);
            event.getProperties().add(new Description(desc));
            if (location != null) {
                event.getProperties().add(new net.fortuna.ical4j.model.property.Location(location));
            }
            Uid uid = new Uid(new java.util.Date().getTime() + "" + Math.random());
            event.getProperties().add(uid);
            events.add(event);
            calendar.getComponents().addAll(events);
        }// end while
        return calendar;
    }


    private String getLocation(Troop user, String locationId) {
        String fmtLocation = "";
        if (locationId == null || user == null || !userUtil.hasPermission(user, Permission.PERMISSION_VIEW_MEETING_ID))
            return fmtLocation;
        try {
            if (user != null && user.getYearPlan() != null
                    && user.getYearPlan().getLocations() != null)
                for (int i = 0; i < user.getYearPlan().getLocations().size(); i++) {
                    if (user.getYearPlan().getLocations().get(i).getPath()
                            .equals(locationId)) {
                        String lName = user.getYearPlan().getLocations().get(i)
                                .getName();
                        String lAddress = user.getYearPlan().getLocations()
                                .get(i).getAddress();
                        fmtLocation = (lName == null ? "" : lName) + " "
                                + (lAddress == null ? "" : lAddress);
                        break;
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fmtLocation;
    }

    public java.util.List<Asset> getResources(User user, Troop troop,
                                              String tags, String meetingName, String uids, String meetingPath)
            throws IllegalAccessException {
        java.util.List<Asset> container = new java.util.ArrayList();
        container.addAll(meetingDAO.getResource_local(user, troop,
                meetingName, meetingPath));
        container.addAll(meetingDAO.getResource_global(user, troop, tags,
                meetingName));
        return container;
    }

    public java.util.List<Milestone> getCouncilMilestones(User user, Troop troop) throws IllegalAccessException {
        return councilDAO.getCouncilMilestones(user, troop);
    }

    public Meeting getMeeting(User user, Troop troop, String path)
            throws IllegalAccessException, VtkException {
        Meeting meeting = meetingDAO.getMeeting(user, troop, path);
        return meeting;
    }

    public List<org.girlscouts.vtk.models.Search> getData(User user,
                                                          Troop troop, String query) throws IllegalAccessException {
        return meetingDAO.getData(user, troop, query);
    }

    public java.util.List<Meeting> getAllMeetings(User user, Troop troop,
                                                  String gradeLevel) throws IllegalAccessException {
        return meetingDAO.getAllMeetings(user, troop, gradeLevel);
    }


    public java.util.List<Meeting> getAllMeetings(User user, Troop troop) throws IllegalAccessException {
        return meetingDAO.getAllMeetings(user, troop);
    }

    public java.util.List<MeetingE> getAllEventMeetings_byPath(User user,
                                                               Troop troop, String yearPlanPath) throws IllegalAccessException {
        return meetingDAO.getAllEventMeetings_byPath(user, troop,
                yearPlanPath.endsWith("/") ? yearPlanPath : yearPlanPath + "/");
    }

    public SearchTag searchA(User user, Troop troop, String councilCode)
            throws IllegalAccessException {
        return meetingDAO.searchA(user, troop, councilCode);
    }

    public java.util.List<Asset> getGlobalResources(String resourceTags) {
        return meetingDAO.getGlobalResources(resourceTags);
    }

    public Meeting createCustomMeeting(User user, Troop troop,
                                       MeetingE meetingEvent) throws IllegalAccessException {
        return meetingDAO.createCustomMeeting(user, troop, meetingEvent);
    }

    public Meeting updateCustomMeeting(User user, Troop troop,
                                       MeetingE meetingEvent, Meeting meeting)
            throws IllegalAccessException {
        return meetingDAO.updateCustomMeeting(user, troop, meetingEvent,
                meeting);
    }

    public void saveCouncilMilestones(User user, List<Milestone> milestones, Troop troop) throws IllegalAccessException {
        councilDAO.updateCouncilMilestones(user, milestones, troop);
    }

    public java.util.List<Activity> searchA1(User user, Troop troop,
                                             String lvl, String cat, String keywrd, java.util.Date startDate,
                                             java.util.Date endDate, String region)
            throws IllegalAccessException {
        return meetingDAO.searchA1(user, troop, lvl, cat, keywrd, startDate,
                endDate, region);
    }

    public List<Asset> getAllResources(User user, Troop troop, String path)
            throws IllegalAccessException {
        return meetingDAO.getAllResources(user, troop, path);
    }

    public Meeting createCustomMeeting(User user, Troop troop,
                                       MeetingE meetingEvent, Meeting meeting)
            throws IllegalAccessException {
        return meetingDAO.createCustomMeeting(user, troop, meetingEvent,
                meeting);
    }

    public void createCustActivity(User user, Troop troop,
                                   java.util.List<org.girlscouts.vtk.models.Activity> activities,
                                   String activityId) throws IllegalAccessException, VtkException {
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getUid().equals(activityId)) {
                createActivity(user, troop, activities.get(i));
                break;
            }
        }

    }

    public boolean isYearPlanAltered(User user, Troop troop) {
        if (troop.getYearPlan() != null) {
            return troop.getYearPlan().getAltered() != null
                    && troop.getYearPlan().getAltered().equals("true");
        }
        return false;
    }

    public void search(User user, Troop troop,
                       javax.servlet.http.HttpServletRequest request) {
        try {
            java.util.Date startDate = null, endDate = null;
            if (request.getParameter("startDate") != null
                    && !request.getParameter("startDate").equals("")) {
                startDate = new java.util.Date(
                        request.getParameter("startDate"));
            }
            if (request.getParameter("endDate") != null
                    && !request.getParameter("endDate").equals("")) {
                endDate = new java.util.Date(request.getParameter("endDate"));
            }
            java.util.List activities = searchA1(user, troop,
                    request.getParameter("lvl"), request.getParameter("cat"),
                    request.getParameter("keywrd"), startDate, endDate,
                    request.getParameter("region"));

            HttpSession session = request.getSession();
            session.setAttribute("vtk_search_activity", activities);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getAllResourcesCount(User user, Troop troop, String path)
            throws IllegalAccessException {
        return meetingDAO.getAllResourcesCount(user, troop, path);
    }


    public int getAssetCount(User user, Troop troop, String _path)
            throws IllegalAccessException {
        return meetingDAO.getAssetCount(user, troop, _path);
    }


    public int getCountLocalMeetingAidsByLevel(User user, Troop troop, String _path)
            throws IllegalAccessException {
        return meetingDAO.getCountLocalMeetingAidsByLevel(user, troop, _path);
    }

    public java.util.Collection<bean_resource> getResourceData(User user, Troop troop, String _path)
            throws IllegalAccessException {
        return meetingDAO.getResourceData(user, troop, _path);
    }

    public int getMeetingCount(User user, Troop troop, String path) throws IllegalAccessException {
        return meetingDAO.getMeetingCount(user, troop, path);
    }

    public int getVtkAssetCount(User user, Troop troop, String path) throws IllegalAccessException {
        return meetingDAO.getVtkAssetCount(user, troop, path);
    }


    public void updateMilestones(User user, Troop troop, HttpServletRequest request) throws java.lang.IllegalAccessException {
        String councilId = request.getParameter("cid");

        java.util.List<Milestone> milestones = getCouncilMilestones(user, troop);
        for (int i = 0; i < milestones.size(); i++) {

            Milestone m = milestones.get(i);
            String blurb = request.getParameter("blurb" + i);
            String date = request.getParameter("date" + i);

            m.setBlurb(blurb);
            m.setDate(new java.util.Date(date));

        }
    }

    public void saveMilestones(User user, Troop troop, HttpServletRequest request) throws java.text.ParseException {
        String councilId = request.getParameter("cid");
        java.util.List<Milestone> milestones = new ArrayList<Milestone>();
        String[] blurbs = request.getParameterValues("ms_blurb[]");
        String[] dates = request.getParameterValues("ms_date[]");
        String[] shows = request.getParameterValues("ms_show[]");
        if (blurbs != null) {
            for (int i = 0; i < blurbs.length; i++) {
                String blurb = blurbs[i];
                if (blurb == null || blurb.trim().isEmpty()) {
                    break;
                }
                boolean show = shows[i].equals("true");
                java.util.Date date = null;
                if (!dates[i].isEmpty()) {
                    date = VtkUtil.parseDate(
                            VtkUtil.FORMAT_MMddYYYY, dates[i]);
                }

                Milestone m = new Milestone(blurb, show, date);
                milestones.add(m);
            }
        }

        try {
            saveCouncilMilestones(user, milestones, troop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createMilestones(User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws java.lang.IllegalAccessException {
        String councilId = request.getParameter("cid");
        java.util.List<Milestone> milestones = getCouncilMilestones(user, troop);

        Milestone m = new Milestone();
        m.setBlurb(request.getParameter("blurb"));
        m.setDate(new java.util.Date(request.getParameter("date")));
        milestones.add(m);
    }

    public void GSMonthlyDetailedRpt(String year) {
        councilDAO.GSMonthlyDetailedRpt(year);
    }

    public void GSRptCouncilPublishFinance() {
        councilDAO.GSRptCouncilPublishFinance();
    }

    public void test() {
    }

    public YearPlan getYearPlanJson(String yearPlanPath) {

        return yearPlanDAO.getYearPlanJson(yearPlanPath);
    }
}// edn class
