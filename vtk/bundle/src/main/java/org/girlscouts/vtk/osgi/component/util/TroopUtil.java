package org.girlscouts.vtk.osgi.component.util;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.exception.VtkException;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.dao.CouncilDAO;
import org.girlscouts.vtk.osgi.component.dao.TroopDAO;
import org.girlscouts.vtk.osgi.component.dao.YearPlanDAO;
import org.girlscouts.vtk.utils.MeetingESortOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.*;

@Component
@Service(value = TroopUtil.class)
public class TroopUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private TroopDAO troopDAO;
    @Reference
    private CouncilDAO councilDAO;
    @Reference
    private YearPlanDAO yearPlanDAO;
    @Reference
    private UserUtil userUtil;
    @Reference
    private YearPlanUtil yearPlanUtil;

    public Troop getTroopByPath(User user, String troopPath) throws IllegalAccessException, VtkException {
        Troop troop = troopDAO.getTroopByPath(user, troopPath);
        if (troop != null) {
            if (troop.getYearPlan() != null) {
                if (troop.getYearPlan().getMeetingEvents() != null) {
                    Collections.sort(troop.getYearPlan().getMeetingEvents(), new MeetingESortOrderComparator());
                    for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
                        troop.getYearPlan().getMeetingEvents().get(i).setSortOrder(i);
                    }
                }
                yearPlanUtil.checkCanceledActivity(user, troop);
                if (troop.getYearPlan().getCalFreq() == null) {
                    troop.getYearPlan().setCalFreq("biweekly");
                }
                doDbReset(troop);
            }
        }
        return troop;
    }

    private void doDbReset(Troop troop) {
        if (troop != null) {
            troop.setDbUpdate(false);
            if (troop.getYearPlan() != null) {
                troop.getYearPlan().setDbUpdate(false);
                if (troop.getYearPlan().getSchedule() != null) {
                    troop.getYearPlan().getSchedule().setDbUpdate(false);
                }
                if (troop.getYearPlan().getLocations() != null) {
                    for (int i = 0; i < troop.getYearPlan().getLocations().size(); i++) {
                        troop.getYearPlan().getLocations().get(i).setDbUpdate(false);
                    }
                }
                if (troop.getYearPlan().getActivities() != null) {
                    for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++) {
                        troop.getYearPlan().getActivities().get(i).setDbUpdate(false);
                    }
                }
                if (troop.getYearPlan().getMeetingEvents() != null) {
                    for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
                        troop.getYearPlan().getMeetingEvents().get(i).setDbUpdate(false);
                        java.util.List<Asset> assets = troop.getYearPlan().getMeetingEvents().get(i).getAssets();
                        if (assets != null) {
                            for (int y = 0; y < assets.size(); y++) {
                                assets.get(y).setDbUpdate(false);
                            }
                        }
                    }
                }

            }
        }
    }

    // check if info was updated and need to pull from DB fresh copy
    public boolean isUpdated(Troop troop) {
        java.util.Date lastUpdate = yearPlanDAO.getLastModif(troop);
        if (lastUpdate != null && troop.getRetrieveTime().before(lastUpdate)) {
            troop.setRefresh(true);
            return true;
        }
        return false;
    }

    public void createCouncil(User user, Troop troop) throws IllegalAccessException, VtkException {
        Council council = councilDAO.getOrCreateCouncil(user, troop);
    }

    public void addAsset(User user, Troop troop, String meetingUid, Asset asset) throws java.lang.IllegalAccessException, java.lang.IllegalStateException, VtkException {
        // permission to update
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getUid().equals(meetingUid)) {
                meetings.get(i).getAssets().add(asset);
            }
        }
        troopDAO.updateTroop(user, troop);

    }

    public boolean updateTroop(User user, Troop troop) throws java.lang.IllegalAccessException, VtkException {
        return troopDAO.updateTroop(user, troop);

    }

    public void rmTroop(Troop troop) throws java.lang.IllegalAccessException {
        troopDAO.rmTroop(troop);
    }

    public YearPlan addYearPlan(User user, Troop troop, String yearPlanPath) throws java.lang.IllegalAccessException {
        YearPlan plan = null;
        try {
            if (yearPlanPath == null || yearPlanPath.equals("")) {
                return new YearPlan();
            }
            plan = troopDAO.addYearPlan1(user, troop, yearPlanPath);
            plan.setRefId(yearPlanPath);
            plan.setMeetingEvents(yearPlanUtil.getAllEventMeetings_byPath(user, troop, yearPlanPath.endsWith("/meetings/") ? yearPlanPath : (yearPlanPath + "/meetings/")));
            Collections.sort(plan.getMeetingEvents(), new MeetingESortOrderComparator());
            plan.setPath(troop.getPath()+"/yearPlan");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plan;

    }

    public void selectTroopForView(User user, String newSelectedTroopHash, HttpSession session) throws IllegalAccessException, VtkException {
        if (newSelectedTroopHash == null || newSelectedTroopHash.trim().equals("")) {
            log.error("loginAs invalid.abort");
            return;
        }
        List<Troop> troops = user.getTroops();
        session.setAttribute("USER_TROOP_LIST", troops);
        Troop newSelectedTroop = null;
        for (Troop troop : user.getTroops()) {
            try {
                if (troop.getHash().equals(newSelectedTroopHash) || troop.getGradeLevel().equals(newSelectedTroopHash)) {
                    newSelectedTroop = troop;
                    break;
                }
            } catch (Exception e) {
                log.error("Error occurred:", e);
            }
        }
        if (newSelectedTroop == null) {
            return;
        }
        if (!user.getCurrentYear().equals(VtkUtil.getCurrentGSYear() + "")) {
            Set permis = Permission.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS);
            Troop newSelectedTroopCloned = ((Troop) VtkUtil.deepClone(newSelectedTroop));
            newSelectedTroopCloned.setPermissionTokens(permis);
            newSelectedTroop = newSelectedTroopCloned;
        }
        Troop newSelectedTroopRepoData = getTroopByPath(user, newSelectedTroop.getPath());
        if (newSelectedTroopRepoData == null) {
            createCouncil(user, newSelectedTroop);
        } else {
            newSelectedTroop.setYearPlan(newSelectedTroopRepoData.getYearPlan());
            newSelectedTroop.setCurrentTroop(newSelectedTroopRepoData.getCurrentTroop());
        }
        session.setAttribute("VTK_troop", newSelectedTroop);
        session.setAttribute("VTK_planView_memoPos", null);
        session.setAttribute("vtk_cachable_contacts", null);
    }

    public String bindAssetToYPC(User user, Troop troop, String bindAssetToYPC, String _ypcId, String _assetDesc, String _assetTitle) throws IllegalAccessException, VtkException {
        String vtkErr = "";
        String assetId = bindAssetToYPC;
        String ypcId = _ypcId;
        String assetDesc = java.net.URLDecoder.decode(_assetDesc);
        String assetTitle = java.net.URLDecoder.decode(_assetTitle);
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getUid().equals(ypcId)) {
                Asset asset = new Asset();
                asset.setIsCachable(false);
                asset.setRefId(assetId);
                asset.setDescription(assetDesc);
                asset.setTitle(assetTitle);
                java.util.List<Asset> assets = meetings.get(i).getAssets();
                assets = assets == null ? new java.util.ArrayList() : assets;
                assets.add(asset);
                meetings.get(i).setAssets(assets);
                boolean isUsrUpd = updateTroop(user, troop);
                if (!isUsrUpd) {
                    vtkErr += vtkErr.concat("Warning: You last change was not saved.");
                }
                return vtkErr;
            }
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        if (activities != null) {
            for (int i = 0; i < activities.size(); i++) {
                if (activities.get(i).getUid().equals(ypcId)) {
                    Asset asset = new Asset();
                    asset.setIsCachable(false);
                    asset.setRefId(assetId);
                    asset.setDescription(assetDesc);
                    asset.setTitle(assetTitle);
                    java.util.List<Asset> assets = activities.get(i).getAssets();
                    assets = assets == null ? new java.util.ArrayList() : assets;
                    assets.add(asset);
                    activities.get(i).setAssets(assets);
                    boolean isUsrUpd = updateTroop(user, troop);
                    if (!isUsrUpd) {
                        vtkErr += vtkErr.concat("Warning: You last change was not saved.");
                    }
                    return vtkErr;
                }
            }

        }
        return vtkErr;
    }

    public String editCustActivity(User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws IllegalAccessException, ParseException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
        String vtkErr = "";
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        Activity activity = null;
        for (int i = 0; i < activities.size(); i++) {
            if (activities.get(i).getUid().equals(request.getParameter("editCustActivity"))) {
                activity = activities.get(i);
                break;
            }
        }
        double cost = VtkUtil.convertObjectToDouble(request.getParameter("newCustActivity_cost"));
        activity.setCost(cost);
        activity.setContent(request.getParameter("newCustActivity_txt"));
        activity.setDate(dateFormat4.parse(request.getParameter("newCustActivity_date") + " " + request.getParameter("newCustActivity_startTime") + " " + request.getParameter("newCustActivity_startTime_AP")));
        activity.setEndDate(dateFormat4.parse(request.getParameter("newCustActivity_date") + " " + request.getParameter("newCustActivity_endTime") + " " + request.getParameter("newCustActivity_endTime_AP")));
        activity.setName(request.getParameter("newCustActivity_name"));
        activity.setLocationName(request.getParameter("newCustActivityLocName"));
        activity.setLocationAddress(request.getParameter("newCustActivityLocAddr"));
        boolean isUsrUpd = updateTroop(user, troop);
        if (!isUsrUpd) {
            vtkErr += vtkErr.concat("Warning: You last change was not saved.");
        }
        return vtkErr;
    }

    public void impersonate(User user, Troop nothing, String councilCode, String troopId, HttpSession session) throws IllegalAccessException, VtkException {
        Troop new_troop = getTroopByPath(user, nothing.getPath());
        //new_troop.setTroop(nothing.getTroop());
        //new_troop.setSfTroopId(new_troop.getTroop().getTroopId());
        //new_troop.setSfUserId(user.getApiConfig().getUserId());
        //new_troop.setSfTroopName(new_troop.getTroop().getTroopName());
        //new_troop.setSfTroopAge(new_troop.getTroop().getGradeLevel());
        //new_troop.setSfCouncil(new_troop.getTroop().getCouncilCode() + "");
        session.setAttribute("VTK_troop", new_troop);
        session.setAttribute("VTK_planView_memoPos", null);
        new_troop.setCurrentTroop(session.getId());
    }

    private Cal selectYearPlan_newSched(User user, Troop troop, String yearPlanPath) throws java.lang.IllegalAccessException, VtkYearPlanChangeException {
        Cal cal = new Cal();
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        YearPlan oldPlan = troop.getYearPlan();
        YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);
        if (oldPlan.getSchedule() == null) {
            return null;
        }
        int currMeetingCount = 0;
        if (newYearPlan.getMeetingEvents() != null) {
            currMeetingCount = newYearPlan.getMeetingEvents().size();
        }
        java.util.List<java.util.Date> oldSched = VtkUtil.getStrCommDelToArrayDates(oldPlan.getSchedule().getDates());
        //add all past meeting dates to currMeetingCount. Because they carry over
        int past_meeting_count = (int) oldSched.stream().filter(e -> e.before(Calendar.getInstance().getTime())).count();
        currMeetingCount += past_meeting_count;
        // fever meetings
        if (oldSched.size() > currMeetingCount) {
            for (int i = (oldSched.size() - 1); i >= currMeetingCount; i--) {
                if (oldSched.get(i).after(new java.util.Date())) {
                    oldSched.remove(i); // rm last meeting if in future
                } else if (currMeetingCount > 0) {
                    throw new VtkYearPlanChangeException("Can not change year plan. Dates in the past can not be changed (" + oldSched.get(i) + ")");
                }
            }
        }
        // more meetings
        if (oldSched.size() < currMeetingCount) {
            // meeting freq dates
            String calFreq = oldPlan.getCalFreq();
            if (calFreq == null || calFreq.trim().equals("")) {
                calFreq = "biweekly";
            }
            // add meeting dates
            int oldSched_size = oldSched.size();
            for (int i = oldSched_size; i < currMeetingCount; i++) {
                long newDate = new CalendarUtil().getNextDate(VtkUtil.getStrCommDelToArrayStr(oldPlan.getCalExclWeeksOf()), oldSched.get(oldSched.size() - 1).getTime(), oldPlan.getCalFreq(), false);
                oldSched.add(new java.util.Date(newDate));
            }
        }
        cal.setDates(VtkUtil.getArrayDateToLongComDelim(oldSched));
        cal.setDbUpdate(true);
        return cal;
    }

    private java.util.List<MeetingE> selectYearPlan_newMeetingPlan(User user, Troop troop, YearPlan newYearPlan) throws java.lang.IllegalAccessException {
        // permission to update
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        YearPlan oldPlan = troop.getYearPlan();
        // SORT Meetings - new
        newYearPlan.setMeetingEvents(VtkUtil.sortMeetingsE(newYearPlan.getMeetingEvents()));
        if (oldPlan == null || oldPlan.getSchedule() == null) {
            return newYearPlan.getMeetingEvents();
        }
        // SORT MEETINGS -old
        oldPlan.setMeetingEvents(VtkUtil.sortMeetingsE(oldPlan.getMeetingEvents()));
        // get Number Of Past meetings
        int numOfPastMeetings = 0;
        java.util.List<java.util.Date> dates = VtkUtil.getStrCommDelToArrayDates(oldPlan.getSchedule().getDates());
        for (int i = 0; i < dates.size(); i++) {
            if (dates.get(i).before(new java.util.Date())) {
                numOfPastMeetings++;
            }
        }
        // overwrite first numOfPastMeetings from oldPlan to newPlan; ASSUMING
        // NEW & OLD Meetings are sorted!!!
        if (newYearPlan.getMeetingEvents().size() >= numOfPastMeetings) { // if
            // newYearPlan
            // has
            // at
            // least
            // numOfPastMeetings->
            // overwrite
            for (int i = 0; i < numOfPastMeetings; i++) {
                //newYearPlan.getMeetingEvents().set(i, oldPlan.getMeetingEvents().get(i));
                newYearPlan.getMeetingEvents().add(i, oldPlan.getMeetingEvents().get(i));
            }
        } else if (newYearPlan.getMeetingEvents() == null || newYearPlan.getMeetingEvents().size() == 0) {
            newYearPlan.setMeetingEvents(new java.util.ArrayList<MeetingE>());
            for (int i = 0; i < numOfPastMeetings; i++) {
                newYearPlan.getMeetingEvents().add(oldPlan.getMeetingEvents().get(i));
            }
        }
        return VtkUtil.setToDbUpdate(newYearPlan.getMeetingEvents());
    }

    public void selectYearPlan(User user, Troop troop, String yearPlanPath, String planName) throws java.lang.IllegalAccessException, VtkYearPlanChangeException, VtkException {
        // permission to update
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_ADD_YEARPLAN_ID)) {
            throw new IllegalAccessException();
        }
        YearPlan oldPlan = troop.getYearPlan();
        String orgSchedDates = "";
        if (oldPlan != null && oldPlan.getSchedule() != null) {
            orgSchedDates = oldPlan.getSchedule().getDates();
        }
        YearPlan newYearPlan = addYearPlan(user, troop, yearPlanPath);
        //sched
        if (oldPlan != null) {
            if (oldPlan.getName().equals(planName)) {// reset current yearplan
                if (!userUtil.hasPermission(troop, Permission.PERMISSION_RM_YEARPLAN_ID)) {
                    throw new IllegalAccessException();
                }
                Cal cal = new Cal();
                cal.setDbUpdate(true);
                troop.getYearPlan().setSchedule(cal);
            } else {
                troop.getYearPlan().setSchedule(selectYearPlan_newSched(user, troop, yearPlanPath));
            }
        } else {
            troop.setYearPlan(newYearPlan);
        }
        //meetings
        if (yearPlanPath != null && !yearPlanPath.equals("")) {
            troop.getYearPlan().setMeetingEvents(selectYearPlan_newMeetingPlan(user, troop, newYearPlan));

        } else {
            java.util.List<MeetingE> futureMeetings = getFutureMeetings(user, troop, orgSchedDates);
            java.util.List<MeetingE> pastMeetings = rmFutureMeetings(user, troop, futureMeetings);
            troop.getYearPlan().setMeetingEvents(VtkUtil.setToDbUpdate(pastMeetings));
        }
        if (oldPlan != null) {
            // rm activities
            if (troop.getYearPlan().getActivities() != null) {
                for (int i = 0; i < troop.getYearPlan().getActivities().size(); i++) {
                    Activity activity = troop.getYearPlan().getActivities().get(i);
                    if (activity.getDate() != null && (activity.getDate().getTime() > Calendar.getInstance().getTimeInMillis())) {
                        troopDAO.removeActivity(user, troop, activity);
                    }
                }
            }
        }
        if (yearPlanPath == null || yearPlanPath.trim().equals("")) { // custom
            // year plan
            troop.getYearPlan().setAltered("true");
            if (troop.getYearPlan().getSchedule() != null && (troop.getYearPlan().getSchedule().getDates() == null || "".equals(troop.getYearPlan().getSchedule().getDates()))) {
                troop.getYearPlan().getSchedule().setDates(null);
            }

        } else {
            troop.getYearPlan().setAltered("false");
        }
        troop.getYearPlan().setName(planName);
        troop.getYearPlan().setRefId(yearPlanPath);
        troop.getYearPlan().setDbUpdate(true);
        troopDAO.removeMeetings(user, troop);
        troopDAO.updateTroop(user, troop);

    }

    private java.util.List<MeetingE> rmFutureMeetings(User user, Troop troop, java.util.List<MeetingE> futureMeetings) throws IllegalAccessException {
        java.util.List<MeetingE> pastMeetings = troop.getYearPlan().getMeetingEvents();
        java.util.List<MeetingE> temp = new java.util.ArrayList();
        if (futureMeetings != null) {
            for (MeetingE meeting : futureMeetings) {
                temp.add(meeting);
            }
        }
        for (MeetingE rm : temp) {
            pastMeetings.remove(rm);
        }
        return pastMeetings;
    }

    private java.util.List<MeetingE> getFutureMeetings(User user, Troop troop, String dates) {
        java.util.List<MeetingE> futureMeetings = new java.util.ArrayList<MeetingE>();
        if (troop.getYearPlan().getSchedule() == null || troop.getYearPlan().getSchedule().getDates() == null) {
            return troop.getYearPlan().getMeetingEvents();
        }
        java.util.List<MeetingE> allMeetings = troop.getYearPlan().getMeetingEvents();
        if (allMeetings != null) {
            Collections.sort(allMeetings, new MeetingESortOrderComparator());
        }
        long timeNow = new java.util.Date().getTime();
        int countMeetings = 0;
        dates = "," + dates;
        StringTokenizer t = new java.util.StringTokenizer(dates, ",");
        while (t.hasMoreElements()) {
            long x = Long.parseLong(t.nextToken());
            if (timeNow < x) {
                futureMeetings.add(allMeetings.get(countMeetings));
            }
            countMeetings++;
        }
        return futureMeetings;
    }

    public boolean removeMilestones(User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws java.lang.IllegalAccessException, VtkException {
        java.util.List<Milestone> milestones = troop.getYearPlan().getMilestones();
        for (int i = 0; i < milestones.size(); i++) {
            Milestone m = milestones.get(i);
            if (m.getUid().equals(request.getParameter("removeCouncilMilestones"))) {
                milestones.remove(m);
                boolean isUsrUpd = updateTroop(user, troop);
                return false;
            }
        }
        return true;
    }
}// end class

