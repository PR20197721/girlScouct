package org.girlscouts.vtk.osgi.component.util;

import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.exception.VtkException;
import org.girlscouts.vtk.models.*;
import org.girlscouts.vtk.osgi.component.dao.*;
import org.girlscouts.vtk.osgi.service.GirlScoutsSalesForceService;
import org.girlscouts.vtk.utils.ActivityDateComparator;
import org.girlscouts.vtk.utils.ActivityNumberComparator;
import org.girlscouts.vtk.utils.MeetingESortOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Service(MeetingUtil.class)
public class MeetingUtil {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference
    private TroopUtil troopUtil;
    @Reference
    private MeetingDAO meetingDAO;
    @Reference
    private ActivityDAO activityDAO;
    @Reference
    private UserUtil userUtil;
    @Reference
    private YearPlanUtil yearPlanUtil;
    @Reference
    private TroopDAO troopDAO;
    @Reference
    private MeetingAidUtil meetingAidUtil;
    @Reference
    private GirlScoutsSalesForceService gsSalesForceService;

    public java.util.List<MeetingE> updateMeetingPos(java.util.List<MeetingE> orgMeetings, java.util.List<Integer> newPoss) {
        java.util.List<MeetingE> newMeeting = new java.util.ArrayList<MeetingE>();// orgMeetings.size());
        try {
            for (int i = 0; i < orgMeetings.size(); i++) {
                newMeeting.add(orgMeetings.get(i)); // TODO
            }
            for (int i = 0; i < orgMeetings.size(); i++) {
                int newpos = newPoss.indexOf(i + 1);
                if (newpos == i) {
                    continue;
                }
                MeetingE meeting = orgMeetings.get(i);
                meeting.setSortOrder(newpos);
                meeting.setDbUpdate(true);
                newMeeting.set(newpos, meeting);

            }
        } catch (Exception e) {
            log.error("ERROR : MeetingUtil.updateMeetingPos", e);
            newMeeting = orgMeetings;
        }
        return newMeeting;

    }

    public java.util.Map getYearPlanSched(User user, Troop troop, YearPlan plan, boolean meetingPlanSpecialSort) throws IllegalAccessException, VtkException {
        return getYearPlanSched(user, troop, plan, meetingPlanSpecialSort, false);
    }

    public Map getYearPlanSched(User user, Troop troop, YearPlan plan, boolean meetingPlanSpecialSort, boolean isLoadMeetingInfo) throws IllegalAccessException, VtkException {
        if (plan == null) {
            return new java.util.TreeMap();
        }
        if (plan.getSchedule() != null || plan.getActivities() == null || plan.getActivities().size() <= 0) {
            // set meetingInfos if isLoadMeetingInfo
            if (isLoadMeetingInfo) {
                java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
                if (meetingEs != null) {
                    for (int i = 0; i < meetingEs.size(); i++) {
                        MeetingE meetingE = meetingEs.get(i);
                        Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingE.getRefId());
                        meetingE.setMeetingInfo(meetingInfo);
                    }
                }
                plan.setMeetingEvents(meetingEs);
                // load meetingCanceled
                if (plan.getMeetingCanceled() != null) {
                    for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {
                        MeetingCanceled meetingCanceled = plan.getMeetingCanceled().get(i);
                        Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingCanceled.getRefId());
                        meetingCanceled.setMeetingInfo(meetingInfo);
                    }
                }
            }

            // Check for empty schedule
            yearPlanUtil.checkEmptySchedule(user, troop);
            plan.setSchedule(troop.getYearPlan().getSchedule());

            return getYearPlanSched(plan);
        }
        // if no sched and activ -> activ on top
        java.util.Map orgSched = getYearPlanSched(plan);
        java.util.Map container = null;
        if (troop != null && troop.getSfTroopAge() != null && (troop.getSfTroopAge().toLowerCase().contains("cadette") || troop.getSfTroopAge().toLowerCase().contains("senior") || troop.getSfTroopAge().toLowerCase().contains("ambassador"))) {
            container = new TreeMap();
        } else {
            container = new LinkedHashMap();
        }
        java.util.Iterator itr = orgSched.keySet().iterator();
        while (itr.hasNext()) {
            java.util.Date date = (java.util.Date) itr.next();
            YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);
            switch (_comp.getType()) {
                case ACTIVITY:
                    Activity activity = (Activity) _comp;
                    container.put(date, activity);
                    break;
            }
        }
        // now set meetings & etc
        itr = orgSched.keySet().iterator();
        boolean heal = false;
        while (itr.hasNext()) {
            java.util.Date date = (java.util.Date) itr.next();
            YearPlanComponent _comp = (YearPlanComponent) orgSched.get(date);
            switch (_comp.getType()) {
                case MEETINGCANCELED:
                    MeetingCanceled meetingCanceled = (MeetingCanceled) _comp;
                    Meeting meetingInfoCan = yearPlanUtil.getMeeting(user, troop, meetingCanceled.getRefId());
                    meetingCanceled.setMeetingInfo(meetingInfoCan);
                    container.put(date, meetingCanceled);
                    break;
                case MEETING:
                    MeetingE meetingE = (MeetingE) _comp;
                    if (isLoadMeetingInfo) {
                        Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingE.getRefId());
                        meetingE.setMeetingInfo(meetingInfo);
                    }
                    int maxLook = 0;
                    while (container.containsKey(date)) {
                        date = new Date(date.getTime() + 5l);
                        heal = true;
                        maxLook++;
                        if (maxLook > 500) {
                            break;
                        }
                    }
                    container.put(date, meetingE);
                    break;
                case MILESTONE:
                    Milestone milestone = (Milestone) _comp;
                    container.put(date, milestone);
                    break;

            }
        }
        return container;

    }

    public java.util.Map getYearPlanSched(YearPlan plan) {
        if (plan == null) {
            return null;
        }
        java.util.Map<java.util.Date, YearPlanComponent> sched = null;
        try {
            sched = new java.util.TreeMap<java.util.Date, YearPlanComponent>();
            List<Activity> activities = plan.getActivities();
            java.util.List<MeetingE> meetingEs = plan.getMeetingEvents();
            if (meetingEs != null) {
                Collections.sort(meetingEs, new MeetingESortOrderComparator());
            }
            if (plan.getSchedule() != null) {
                String calMeeting = plan.getSchedule().getDates();
                StringTokenizer t = new StringTokenizer(calMeeting, ",");
                int count = 0;
                while (t.hasMoreElements()) {
                    try {
                        java.util.Date dt = new java.util.Date(Long.parseLong((t.nextToken().replace("\n", "").replace("\r", "").trim())));
                        int maxLook = 0;
                        if (sched.containsKey(dt)) {
                            dt = new Date(dt.getTime() + 5l);
                            maxLook++;
                            if (maxLook > 500) {
                               break;
                            }
                        }
                        if (meetingEs.size() > count) {
                            sched.put(dt, meetingEs.get(count));
                        }
                    } catch (Exception e) {
                        log.error("Error occurred: ", e);
                    }
                    count++;

                }
                int counter = 0;
                java.util.Iterator itr = sched.keySet().iterator();
                while (itr.hasNext()) {
                    sched.put((java.util.Date) itr.next(), meetingEs.get(counter));
                    counter++;
                }

            } else { // no dates: create 1976
                Calendar tmp = java.util.Calendar.getInstance();
                tmp.setTime(new java.util.Date("1/1/1976"));
                if (meetingEs != null) {
                    for (int i = 0; i < meetingEs.size(); i++) {
                        sched.put(tmp.getTime(), meetingEs.get(i));
                        tmp.add(java.util.Calendar.DATE, 1);
                    }
                }
            }
            if (activities != null) {
                for (int i = 0; i < activities.size(); i++) {
                    long tmp = activities.get(i).getDate().getTime();
                    if (sched.containsKey(activities.get(i).getDate())) { // add
                        // 2
                        // sec
                        tmp = tmp + TimeUnit.MILLISECONDS.toMillis(1);
                    }
                    sched.put(new java.util.Date(tmp), activities.get(i));

                }
            }
            if (plan.getMeetingCanceled() != null) {
                for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {
                    long tmp = plan.getMeetingCanceled().get(i).getDate().getTime();
                    if (sched.containsKey(plan.getMeetingCanceled().get(i).getDate())) { // add 2 sec
                        tmp = tmp + TimeUnit.MILLISECONDS.toMillis(1);
                    }
                    sched.put(new java.util.Date(tmp), plan.getMeetingCanceled().get(i));

                }
            }

        } catch (Exception e) {
            log.error("Error occurred: ", e);
            return null;
        }
        return sched;
    }

    public Troop changeMeetingPositions(User user, Troop troop, String newPositions) throws IllegalAccessException, VtkException {
        java.util.List<Integer> newMeetingSetup = new java.util.ArrayList();
        java.util.StringTokenizer t = new java.util.StringTokenizer(newPositions, ",");
        while (t.hasMoreElements()) {
            newMeetingSetup.add(Integer.parseInt(t.nextToken()));
        }
        java.util.List<MeetingE> rearangedMeetings = null;
        try {
            rearangedMeetings = updateMeetingPos(troop.getYearPlan().getMeetingEvents(), newMeetingSetup);
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        YearPlan plan = troop.getYearPlan();
        plan.setMeetingEvents(rearangedMeetings);
        plan.setAltered("true");
        plan.setDbUpdate(true);
        troop.setYearPlan(plan);
        troopUtil.updateTroop(user, troop);
        return troop;
    }

    public void createCustomAgenda(User user, Troop troop, String name, String meetingPath, int duration, long _startTime, String txt) throws IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(_startTime);
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE m = meetings.get(i);
            if (m.getPath().equals(meetingPath)) {
                Meeting meeting = m.getMeetingInfo();
                Activity activity = new Activity();
                activity.setDuration(duration);
                activity.setActivityNumber(meeting.getActivities().size() + 1);
                activity.setOutdoor(false);
                activity.setGlobal(false);
                activity.setVirtual(false);
                activity.setName(name);
                Activity subActivity = new Activity();
                subActivity.setOutdoor(false);
                subActivity.setGlobal(false);
                subActivity.setVirtual(false);
                //subActivity.setName(name);
                subActivity.setActivityNumber(1);
                subActivity.setActivityDescription(txt);
                List<Activity> subActivities = new ArrayList<Activity>();
                subActivities.add(subActivity);
                activity.setMultiactivities(subActivities);
                List<Activity> activities = meeting.getActivities();
                activities.add(activity);
                meeting.setActivities(activities);
                meeting = meetingDAO.createOrUpdateMeeting(user, troop, m, meeting);
                Cal cal = troop.getYearPlan().getSchedule();
                if (cal != null) {
                    cal.addDate(startTime.getTime());
                }

            }
        }
    }

    public void rmCustomActivity(User user, Troop troop, String activityPath) throws IllegalStateException, IllegalAccessException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_RM_ACTIVITY_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getPath().equals(activityPath)) {
                activities.remove(activity);
                troopDAO.removeActivity(user, troop, activity);
            }
        }

    }

    public void swapMeetings(User user, Troop troop, String fromPath, String toPath) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE meeting = meetings.get(i);
            if (meeting.getPath().equals(fromPath)) {
                if (meeting.getAssets() != null) {
                    for (int y = 0; y < meeting.getAssets().size(); y++) {
                        troopDAO.removeAsset(user, troop, meeting.getAssets().get(y));
                    }
                }
                if (meeting.getAttendance() != null) {
                    meetingDAO.removeAttendance(user, troop, meeting.getAttendance());
                    meeting.setAttendance(null);
                }
                if (meeting.getAchievement() != null) {
                    meetingDAO.removeAchievement(user, troop, meeting.getAchievement());
                    meeting.setAchievement(null);
                }
                meeting.setRefId(toPath);
                meeting.setAssets(null);
                meeting.setLastAssetUpdate(null); // auto load assets for new
                // meeting
            }
        }
        troop.getYearPlan().setAltered("true");
        troopUtil.updateTroop(user, troop);
    }

    public void rearrangeActivity(User user, Troop troop, String meetingPath, String _newPoss) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        // TOREDO
        java.util.List<Integer> newPoss = new java.util.ArrayList();
        StringTokenizer t = new StringTokenizer(_newPoss, ",");
        while (t.hasMoreElements()) {
            newPoss.add(Integer.parseInt(t.nextToken()));
        }
        Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingPath);
        java.util.List<Activity> orgActivities = meetingInfo.getActivities();
        orgActivities = sortActivity(orgActivities);
        java.util.List<Activity> newActivity = new java.util.ArrayList<Activity>();
        for (int i = 0; i < orgActivities.size(); i++) {
            newActivity.add(null);
        }
        for (int i = 0; i < orgActivities.size(); i++) {
            Activity activity = orgActivities.get(i);
            int newpos = newPoss.indexOf(i + 1);
            activity.setActivityNumber(newpos + 1);
            newActivity.set(newpos, activity);
        }
        // save activities to meeting
        meetingInfo.setActivities(newActivity);
        // create custom meeting
        MeetingE meetingE = getMeeting(troop.getYearPlan().getMeetingEvents(), meetingPath);
        meetingDAO.createOrUpdateMeeting(user, troop, meetingE, meetingInfo);
    }

    public MeetingE getMeeting(java.util.List<MeetingE> meetings, String meetingPath) {
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getRefId().equals(meetingPath)) {
                return meetings.get(i);
            }
        }
        return null;
    }
    public void addMeetings(User user, Troop troop, String[] meetings) throws java.lang.IllegalAccessException, VtkException {
        for (int i = 0; i < meetings.length; i++) {
            addMeetings(user, troop, meetings[i]);
        }
    }
    public void addMeetings(User user, Troop troop, String newMeetingPath) throws java.lang.IllegalAccessException, VtkException {
        addMeetings(user, troop, newMeetingPath, null);
    }

    public void addMeetings(User user, Troop troop, String newMeetingPath, String startDate) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        if (troop.getYearPlan().getMeetingEvents() == null) {
            troop.getYearPlan().setMeetingEvents(new ArrayList());
        }
        MeetingE meeting = new MeetingE();
        meeting.setRefId(newMeetingPath);
        int meetingsNum = troop.getYearPlan().getMeetingEvents().size();
        meeting.setId(String.valueOf(meetingsNum));
        meeting.setSortOrder(meetingsNum);
        meeting.setDbUpdate(true);
        troop.getYearPlan().getMeetingEvents().add(meeting);
        if (troop.getYearPlan().getSchedule() != null) {
            java.util.List<java.util.Date> sched = VtkUtil.getStrCommDelToArrayDates(troop.getYearPlan().getSchedule().getDates());
            if (!troop.getYearPlan().getSchedule().getDates().trim().equals("")) {
                long newDate = new CalendarUtil().getNextDate(VtkUtil.getStrCommDelToArrayStr(troop.getYearPlan().getCalExclWeeksOf()), sched.get(sched.size() - 1).getTime(), troop.getYearPlan().getCalFreq(), false);
                sched.add(new java.util.Date(newDate));
                troop.getYearPlan().getSchedule().setDates(VtkUtil.getArrayDateToLongComDelim(sched));
            }
        } else if (startDate != null) {
            Cal cal = new Cal();
            cal.setDbUpdate(true);
            cal.setDates(startDate + ",");
            troop.getYearPlan().setSchedule(cal);
        }
        troop.getYearPlan().setAltered("true");
        troopUtil.updateTroop(user, troop);

    }

    public void rmAgenda(User user, Troop troop, String agendaPathToRm, String fromMeetingPath) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_REMOVE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
            if (troop.getYearPlan().getMeetingEvents().get(i).getPath().equals(fromMeetingPath)) {
                MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
                Meeting meetingInfo = meeting.getMeetingInfo();
                List<Activity> activities = meetingInfo.getActivities();
                for (int y = 0; y < activities.size(); y++) {
                    if (activities.get(y).getPath().equals(agendaPathToRm)) {
                        activities.remove(y);
                        Collections.sort(activities, new ActivityNumberComparator());
                        for (int ii = 0; ii < activities.size(); ii++) {
                            activities.get(ii).setActivityNumber(ii + 1);
                        }
                        meetingDAO.createOrUpdateMeeting(user, troop, meeting, meetingInfo);
                        troopUtil.updateTroop(user, troop);
                        return;
                    }

                }

            }

        }
    }

    public void editAgendaDuration(User user, Troop troop, int duration, String activityPath, String meetingPath) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
            if (troop.getYearPlan().getMeetingEvents().get(i).getPath().equals(meetingPath)) {
                MeetingE meeting = troop.getYearPlan().getMeetingEvents().get(i);
                Meeting meetingInfo = meeting.getMeetingInfo();
                List<Activity> activities = meetingInfo.getActivities();
                for (int y = 0; y < activities.size(); y++) {
                    if (activities.get(y).getPath().equals(activityPath)) {
                        Activity activity = activities.get(y);
                        activity.setDuration(duration);
                        meetingDAO.createOrUpdateMeeting(user, troop, meeting, meetingInfo);
                        troopUtil.updateTroop(user, troop);
                        return;
                    }
                }
            }
        }
    }

    public void reverAgenda(User user, Troop troop, String meetingPath) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        MeetingE meeting = null;
        for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {
            if (troop.getYearPlan().getMeetingEvents().get(i).getPath().equals(meetingPath)) {
                meeting = troop.getYearPlan().getMeetingEvents().get(i);
            }
        }
        String[] split = meeting.getRefId().split("/");
        String file = split[(split.length - 1)];
        file = file.substring(0, file.indexOf("_"));
        String ageLevel = troop.getGradeLevel();
        try {
            ageLevel = ageLevel.substring(ageLevel.indexOf("-") + 1).toLowerCase().trim();
        } catch (Exception e) {
            log.error("Error occurred: ", e);
        }
        java.util.List<Meeting> __meetings = meetingDAO.getAllMeetings(user, troop, ageLevel);
        for (int i = 0; i < __meetings.size(); i++) {
            Meeting __meeting = __meetings.get(i);
            if (__meeting.getPath().endsWith(file)) {
                swapMeetings(user, troop, meetingPath, __meeting.getPath());
                return;
            }
        }

    }

    public void addAids(User user, Troop troop, String aidId, String meetingId, String assetName, String docType) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE meeting = meetings.get(i);
            if (meeting.getUid().equals(meetingId)) {
                Asset dbAsset = meetingAidUtil.getAsset(aidId);
                Asset asset = new Asset();
                asset.setRefId(aidId);
                asset.setType(AssetComponentType.AID.toString());
                asset.setTitle(assetName);
                asset.setDocType(docType);
                if (dbAsset != null) {
                    asset.setDescription(dbAsset.getDescription());
                    asset.setIsOutdoorRelated(dbAsset.getIsOutdoorRelated());
                    asset.setIsGlobalRelated(dbAsset.getIsGlobalRelated());
                    asset.setIsVirtualRelated(dbAsset.getIsVirtualRelated());
                }
                java.util.List<Asset> assets = meeting.getAssets();
                assets = assets == null ? new java.util.ArrayList() : assets;
                boolean isDuplicate = false;
                for (int y = 0; y < assets.size(); y++) {
                    if (assets.get(y).getRefId().equals(aidId)) {
                        isDuplicate = true;
                    }
                }
                if (isDuplicate) {
                    return;
                }
                assets.add(asset);
                meeting.setAssets(assets);
                troopUtil.updateTroop(user, troop);
                return;
            }
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getUid().equals(meetingId)) {
                Asset asset = new Asset();
                asset.setRefId(aidId);
                asset.setType("AID");
                java.util.List<Asset> assets = activity.getAssets();
                assets = assets == null ? new java.util.ArrayList() : assets;
                assets.add(asset);
                activity.setAssets(assets);
                troop.getYearPlan().setAltered("true");
                troopUtil.updateTroop(user, troop);
                return;
            }
        }

    }

    public void addResource(User user, Troop troop, String aidId, String meetingId, String assetName, String docType) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_CREATE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE meeting = meetings.get(i);
            if (meeting.getUid().equals(meetingId)) {
                Asset asset = new Asset();
                asset.setRefId(aidId);
                asset.setType(AssetComponentType.RESOURCE.toString());
                asset.setTitle(assetName);
                asset.setDocType(docType);
                java.util.List<Asset> assets = meeting.getAssets();
                assets = assets == null ? new java.util.ArrayList() : assets;
                boolean isAsset = false;
                for (int y = 0; y < assets.size(); y++) {
                    if (assets.get(y).getRefId().equals(aidId)) {
                        isAsset = true;
                    }
                }
                if (isAsset) {
                    return;
                }
                assets.add(asset);
                meeting.setAssets(assets);
                troopUtil.updateTroop(user, troop);
                return;
            }
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getUid().equals(meetingId)) {
                Asset asset = new Asset();
                asset.setRefId(aidId);
                asset.setType(AssetComponentType.RESOURCE.toString());
                java.util.List<Asset> assets = activity.getAssets();
                assets = assets == null ? new java.util.ArrayList() : assets;
                assets.add(asset);
                activity.setAssets(assets);
                troopUtil.updateTroop(user, troop);
                return;
            }
        }

    }

    public void rmAsset(User user, Troop troop, String aidId, String meetingId) throws java.lang.IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_REMOVE_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE meeting = meetings.get(i);
            if (meeting.getUid().equals(meetingId)) {
                java.util.List<Asset> assets = meeting.getAssets();
                for (int y = 0; y < assets.size(); y++) {
                    if (assets.get(y).getRefId().equals(aidId)) {
                        troopDAO.removeAsset(user, troop, assets.get(y));
                        assets.remove(y);
                    }
                }
                return;
            }
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getUid().equals(meetingId)) {
                java.util.List<Asset> assets = activity.getAssets();
                for (int y = 0; y < assets.size(); y++) {
                    if (assets.get(y).getUid().equals(aidId)) {
                        assets.remove(y);
                    }
                }
                troopUtil.updateTroop(user, troop);
                return;
            }
        }

    }

    public java.util.List<Activity> sortActivity(java.util.List<Activity> _activities) {
        Collections.sort(_activities, new ActivityNumberComparator());
        return _activities;

    }

    public java.util.List<Activity> sortActivityByDate(java.util.List<Activity> _activities) {
        Collections.sort(_activities, new ActivityDateComparator());
        return _activities;

    }

    public PlanView planView(User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws Exception {
        return planView(user, troop, request, false);

    }

    public PlanView planView(User user, Troop troop, javax.servlet.http.HttpServletRequest request, boolean isUpdateAssetInDb) throws Exception {
        PlanView planView = planView1(user, troop, request);
        if (planView == null) {
            return null;
        }
        YearPlanComponent _comp = planView.getYearPlanComponent();
        if (_comp == null) {
            return null;
        }
        MeetingE meetingEvent = null;
        List<Asset> assets = null;
        Meeting meetingInfo = null;
        if (_comp.getType() == YearPlanComponentType.MEETING || _comp.getType() == YearPlanComponentType.MEETINGCANCELED) {
            meetingEvent = (MeetingE) _comp;
            int meetingCount = 0;
            if (_comp.getType() == YearPlanComponentType.MEETING) {
                meetingCount = troop.getYearPlan().getMeetingEvents().indexOf(_comp) + 1;
            } else if (_comp.getType() == YearPlanComponentType.MEETINGCANCELED) {
                meetingCount = troop.getYearPlan().getMeetingCanceled().indexOf(_comp) + 1;
            }
            meetingInfo = yearPlanUtil.getMeeting(user, troop, meetingEvent.getRefId());
            assets = meetingAidUtil.getMeetingAids(meetingInfo, meetingEvent);
            meetingEvent.setMeetingInfo(meetingInfo);
            java.util.List<Activity> _activities = null;
            if (meetingInfo != null && meetingInfo.getActivities() != null) {
                _activities = meetingInfo.getActivities();
            }
            meetingEvent.setLastAssetUpdate(new java.util.Date());
            meetingEvent.setAssets(assets);
            //end
            int meetingLength = 0;
            if(_activities != null) {
                for (Activity _agenda : _activities) {
                    meetingLength += _agenda.getDuration();
                }
            }
            planView.setMeetingCount(meetingCount);
            planView.setMeetingLength(meetingLength);

        }
        if (meetingEvent != null) {
            meetingEvent.setMeetingInfo(meetingInfo);
        }
        planView.setMeeting(meetingEvent);
        planView.setAidTags(assets);
        return planView;
    }

    public PlanView planView1(User user, Troop troop, javax.servlet.http.HttpServletRequest request) throws IllegalAccessException, VtkException {
        PlanView planView = new PlanView();
        HttpSession session = request.getSession();
        java.util.Map<java.util.Date, YearPlanComponent> sched = null;
        if (troop.getYearPlan() != null) {
            sched = getYearPlanSched(user, troop, troop.getYearPlan(), false, false);
        }
        if (sched == null || (sched.size() == 0)) {
            return null;
        }
        java.util.List<java.util.Date> dates = new java.util.ArrayList<java.util.Date>(sched.keySet());
        long nextDate = 0, prevDate = 0;
        java.util.Date searchDate = null;
        if ((request.getParameter("elem") != null && !request.getParameter("elem").equals("first")) || (request.getAttribute("elem") != null && !request.getAttribute("elem").equals("first"))) {
            String elem = request.getParameter("elem");
            if (elem == null) {
                elem = (String) request.getAttribute("elem");
            }
            searchDate = new java.util.Date(Long.parseLong(elem));
        } else if (false) {// session.getValue("VTK_planView_memoPos") !=null ){
            searchDate = new java.util.Date((Long) session.getAttribute("VTK_planView_memoPos"));
        } else {
            if (troop.getYearPlan().getSchedule() == null) {
                searchDate = sched.keySet().iterator().next();
            } else {
                java.util.Iterator itr = sched.keySet().iterator();
                while (itr.hasNext()) {
                    searchDate = (java.util.Date) itr.next();
                    if (searchDate.after(new java.util.Date())) {
                        break;
                    }
                }
            }
        }
        int currInd = dates.indexOf(searchDate);
        if (dates.size() - 1 > currInd) {
            nextDate = dates.get(currInd + 1).getTime();
        }
        if (currInd > 0) {
            prevDate = dates.get(currInd - 1).getTime();
        }
        session.setAttribute("VTK_planView_memoPos", searchDate.getTime());
        YearPlanComponent _comp = sched.get(searchDate);
        planView.setSearchDate(searchDate);
        planView.setPrevDate(prevDate);
        planView.setNextDate(nextDate);
        planView.setCurrInd(currInd);
        planView.setYearPlanComponent(_comp);
        return planView;
    }

    public java.util.List<MeetingE> getMeetingToCancel(User user, Troop troop) throws IllegalAccessException, VtkException {
        java.util.List<MeetingE> meetings = new java.util.ArrayList();
        java.util.Date today = new java.util.Date();
        java.util.Map<java.util.Date, YearPlanComponent> sched = getYearPlanSched(user, troop, troop.getYearPlan(), false, false);
        java.util.Iterator itr = sched.keySet().iterator();
        while (itr.hasNext()) {
            java.util.Date date = (Date) itr.next();
            YearPlanComponent ypc = sched.get(date);
            if (date.after(today) && ypc.getType() == YearPlanComponentType.MEETING) {
                MeetingE MEETING = (MeetingE) ypc;
                Meeting meetingInfo = yearPlanUtil.getMeeting(user, troop, MEETING.getRefId());
                MEETING.setMeetingInfo(meetingInfo);
                meetings.add(MEETING);
            }
        }
        return meetings;
    }

    public boolean rmSchedDate(User user, Troop troop, long dateToRm) throws IllegalAccessException, VtkException {
        boolean isRemoved = false;
        String dates = troop.getYearPlan().getSchedule().getDates();
        dates = "," + dates + ",";
        dates = dates.replace("," + dateToRm + ",", ",");
        dates = dates.replace(",,", ",");
        if (dates.startsWith(",")) {
            dates = dates.substring(1);
        }
        if (dates.endsWith(",")) {
            dates = dates.substring(0, dates.length() - 1);
        }
        troop.getYearPlan().getSchedule().setDates(dates);
        if ("".equals(dates)) {
            //rm sched. no dates
            troop.getYearPlan().getSchedule().setDates(null);
        }
        troopUtil.updateTroop(user, troop);
        isRemoved = true;
        return isRemoved;
    }

    public boolean rmMeeting(User user, Troop troop, String meetingUid) throws IllegalAccessException {
        if (!userUtil.hasPermission(troop, Permission.PERMISSION_REMOVE_MEETING_ID)) {
            return false;
        }
        boolean isRemoved = false;
        boolean isRmDt = false;
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        if (troop.getYearPlan().getSchedule() != null) {
            meetings = VtkUtil.schedMeetings(meetings, troop.getYearPlan().getSchedule().getDates());
        }
        for (int i = 0; i < meetings.size(); i++) {
            if (meetings.get(i).getUid().equals(meetingUid)) {
                try {
                    if (meetings.get(i).getDate() == null)//more meetings than dates.Ex: scheduling from start
                    {
                        isRmDt = true;
                    } else {
                        isRmDt = rmSchedDate(user, troop, meetings.get(i).getDate().getTime());
                    }
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
                if (isRmDt) {
                    troopDAO.removeMeeting(user, troop, meetings.get(i));
                    meetings.remove(i);
                }
            }
        }
        if (isRmDt) {
            isRemoved = true;
        }
        return isRemoved;
    }

    public boolean updateAttendance(User user, Troop troop, javax.servlet.http.HttpServletRequest request) {
        //MEETING or Attendance
        String eventType = request.getParameter("eType");
        String YEAR_PLAN_EVENT = "meetingEvents";
        if (eventType != null && eventType.equals("ACTIVITY")) {
            YEAR_PLAN_EVENT = "activities";
        }
        String mid = request.getParameter("mid");
        String[] attendances = null;
        if (request.getParameter("attendance") != null) {
            int i = 0;
            StringTokenizer t = new StringTokenizer(request.getParameter("attendance"), ",");
            while (t.hasMoreElements()) {
                if (attendances == null) {
                    attendances = new String[t.countTokens()];
                }
                attendances[i] = t.nextToken();
                i++;
            }
        }
        java.util.List<org.girlscouts.vtk.models.Contact> contacts = gsSalesForceService.getContactsForTroop(user.getApiConfig(), troop);
        contacts = contacts.stream().filter(e -> "GIRL".equals(e.getRole().trim().toUpperCase())).collect(java.util.stream.Collectors.toList());
        String path = troop.getPath() + "/yearPlan/" + YEAR_PLAN_EVENT + "/" + mid + "/attendance";
        java.util.List<String> Attendances = new java.util.ArrayList<String>();
        Attendance ATTENDANCES = getAttendance(user, troop, path);
        if (ATTENDANCES == null) {
            ATTENDANCES = new Attendance();
            ATTENDANCES.setPath(path);
        }
        if (ATTENDANCES != null && ATTENDANCES.getUsers() != null) {
            StringTokenizer t = new StringTokenizer(ATTENDANCES.getUsers(), ",");
            while (t.hasMoreElements()) {
                Attendances.add(t.nextToken());
            }
        }
        java.util.List<String> CURRENT_CONTACT_LIST = new java.util.ArrayList<String>();
        for (int i = 0; i < contacts.size(); i++) {
            CURRENT_CONTACT_LIST.add(contacts.get(i).getId());
        }
        // add
        if (attendances != null) {
            for (int i = 0; i < attendances.length; i++) {
                if (!Attendances.contains(attendances[i])) {
                    Attendances.add(attendances[i]);
                }
            }
        }
        // rm
        java.util.List<String> attendances_toRm = new java.util.ArrayList<String>();
        for (int i = 0; i < Attendances.size(); i++) {
            String contactId = Attendances.get(i);
            boolean isExists = false;
            if (CURRENT_CONTACT_LIST.contains(contactId)) {
                if (attendances != null) {
                    for (int y = 0; y < attendances.length; y++) {
                        if (attendances[y].equals(contactId)) {
                            isExists = true;
                        }
                    }
                }
            }
            if (!isExists) {
                attendances_toRm.add(contactId);
            }
        }
        for (int i = 0; i < attendances_toRm.size(); i++) {
            Attendances.remove(attendances_toRm.get(i));
        }
        String _attendances = "";
        if (Attendances != null) {
            for (int i = 0; i < Attendances.size(); i++) {
                _attendances += Attendances.get(i) + ",";
            }
        }
        ATTENDANCES.setUsers(_attendances);
        //count the number of contacts that are not null
        int cTotal = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() != null) {
                cTotal++;
            }
        }
        ATTENDANCES.setTotal(cTotal);
        setAttendance(user, troop, mid, ATTENDANCES);
        if (troop.getYearPlan().getActivities() != null && troop.getYearPlan().getActivities().size() > 0) {
            //update activity
            Activity _thisActivity = troop.getYearPlan().getActivities().stream().filter(_activity -> _activity.getPath().equals(path.substring(0, path.lastIndexOf("/")))).findAny().orElse(null);
            if (_thisActivity != null) {
                _thisActivity.setAttendance(ATTENDANCES);
            }
        }
        return false;
    }

    public Attendance getAttendance(User user, Troop troop, String mid) {
        return meetingDAO.getAttendance(user, troop, mid);
    }

    public boolean setAttendance(User user, Troop troop, String mid, Attendance attendance) {
        return meetingDAO.setAttendance(user, troop, mid, attendance);
    }

    public Achievement getAchievement(User user, Troop troop, String mid) {
        return meetingDAO.getAchievement(user, troop, mid);
    }

    public boolean setAchievement(User user, Troop troop, String mid, Achievement achievement) {
        return meetingDAO.setAchievement(user, troop, mid, achievement);
    }

    public boolean updateAchievement(User user, Troop troop, HttpServletRequest request) {
        String mid = request.getParameter("mid");
        if (request.getParameter("achievement") != null) {
            String achievementPath = troop.getPath() + "/yearPlan/meetingEvents/" + mid + "/achievement";
            Achievement achievement = getAchievement(user, troop, achievementPath);
             if (achievement == null) {
                achievement = new Achievement();
                achievement.setPath(achievementPath);
            }
            Set<String> achievers = new HashSet<String>();
            List<String> contactIds = new ArrayList<String>();
            List<Contact> contacts = gsSalesForceService.getContactsForTroop(user.getApiConfig(), troop);
            for (int i = 0; i < contacts.size(); i++) {
                contactIds.add(contacts.get(i).getId());
            }
            StringTokenizer t = new StringTokenizer(request.getParameter("achievement"), ",");
            while (t.hasMoreElements()) {
                achievers.add(t.nextToken());
            }
            Set<String> achieverIdToRm = new HashSet<String>();
            for(String achieverId:achievers){
                if(!contactIds.contains(achieverId)){
                    achieverIdToRm.add(achieverId);
                }
            }
            achievers.removeAll(achieverIdToRm);
            StringBuilder achieversStr = new StringBuilder();
            if (achievers != null) {
                for (String achiever:achievers) {
                    achieversStr.append(achiever+ ",");
                }
            }
            achievement.setUsers(achieversStr.toString());
            achievement.setTotal(contacts.size());
            setAchievement(user, troop, mid, achievement);
        }
        return false;
    }

    public void saveEmail(User user, Troop troop, String meetingId) {
        java.util.List<MeetingE> meetings = troop.getYearPlan().getMeetingEvents();
        for (int i = 0; i < meetings.size(); i++) {
            MeetingE meeting = meetings.get(i);
            if (meeting.getUid().equals(meetingId)) {
                try {
                    SentEmail email = new SentEmail(troop.getSendingEmail());
                    java.util.List<SentEmail> emails = meeting.getSentEmails();
                    emails = emails == null ? new java.util.ArrayList<SentEmail>() : emails;
                    emails.add(email);
                    meeting.setSentEmails(emails);
                    if (meeting.getEmlTemplate() == null) {
                        meeting.setEmlTemplate(troop.getSendingEmail().getTemplate());
                    }
                    meetingDAO.updateMeetingEvent(user, troop, meeting);
                    return;
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
            }
        }
        java.util.List<Activity> activities = troop.getYearPlan().getActivities();
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            if (activity.getUid().equals(meetingId)) {
                try {
                    SentEmail email = new SentEmail(troop.getSendingEmail());
                    java.util.List<SentEmail> emails = activity.getSentEmails();
                    emails = emails == null ? new java.util.ArrayList<SentEmail>() : emails;
                    emails.add(email);
                    activity.setSentEmails(emails);
                    if (activity.getEmlTemplate() == null) {
                        activity.setEmlTemplate(troop.getSendingEmail().getTemplate());
                    }
                    activity = activityDAO.updateActivity(user, troop, activity);
                    return;
                } catch (Exception e) {
                    log.error("Error occurred: ", e);
                }
            }
        }

    }

    public void createCustomYearPlan(User user, Troop troop, String mids) throws IllegalAccessException, VtkYearPlanChangeException, VtkException {
        String potentialFirstDate = VtkUtil.getYearPlanStartDate(troop);
        troopUtil.selectYearPlan(user, troop, "", "Custom Year Plan");
        StringTokenizer t = new StringTokenizer(mids, ",");
        while (t.hasMoreElements()) {
            addMeetings(user, troop, t.nextToken(), potentialFirstDate);
        }
    }

    public void rmExtraMeetingsNotOnSched(User user, Troop troop) throws IllegalAccessException {
        String dates = troop.getYearPlan().getSchedule().getDates();
        StringTokenizer t = new StringTokenizer(dates, ",");
        int meetingDatesCount = t.countTokens();
        while (meetingDatesCount < troop.getYearPlan().getMeetingEvents().size()) {
            rmMeeting(user, troop, troop.getYearPlan().getMeetingEvents().get(troop.getYearPlan().getMeetingEvents().size() - 1).getUid());
        }

    }

    public MeetingE getMeetingE(User user, Troop troop, String meetingEpath) throws IllegalAccessException, VtkException {
        if (troop != null && !userUtil.hasPermission(troop, Permission.PERMISSION_VIEW_MEETING_ID)) {
            throw new IllegalAccessException();
        }
        return meetingDAO.getMeetingE(user, troop, meetingEpath);
    }

    public java.util.List<Note> getNotes(User user, Troop troop, String path) throws IllegalAccessException, VtkException {
        return meetingDAO.getNotes(user, troop, path);
    }

    public java.util.List<Note> getNotesByMid(User user, Troop troop, String mid) throws IllegalAccessException, VtkException {
        MeetingE meeting = VtkUtil.findMeetingById(troop.getYearPlan().getMeetingEvents(), mid);
        return meetingDAO.getNotes(user, troop, meeting.getUid());//meeting.getPath() );
    }

    public boolean updateNote(User user, Troop troop, Note note) throws IllegalAccessException {
        return meetingDAO.updateNote(user, troop, note);
    }

    public Note getNote(User user, Troop troop, String noteId) throws IllegalAccessException, VtkException {
        return meetingDAO.getNote(user, troop, noteId);
    }

    public boolean rmNote(User user, Troop troop, String noteId) throws IllegalAccessException, VtkException {
        log.debug("inRmNote MeetingUtil: " + noteId);
        return meetingDAO.rmNote(user, troop, noteId);
    }

    public void createOrUpdateCustomMeeting(User user, Troop troop, MeetingE meetingE, Meeting meetingInfo) throws IllegalAccessException, VtkException {
        meetingDAO.createOrUpdateMeeting(user, troop, meetingE, meetingInfo);
        troopUtil.updateTroop(user, troop);
    }

    public List<Note> addNote(User user, Troop troop, String mid, String message) throws java.lang.IllegalAccessException, VtkException {
        if (mid == null || message == null || message.trim().equals("")) {
            return null;
        }
        List<Note> notes = getNotesByMid(user, troop, mid);
        MeetingE meeting = VtkUtil.findMeetingById(troop.getYearPlan().getMeetingEvents(), mid);
        if (notes == null) {
            notes = new java.util.ArrayList<Note>();
        }
        Note note = new Note();
        note.setMessage(message);
        note.setCreatedByUserId(user.getApiConfig().getUser().getSfUserId());
        note.setCreatedByUserName(user.getApiConfig().getUser().getName());
        note.setCreateTime(new java.util.Date().getTime());
        note.setRefId(meeting.getUid());
        note.setPath(meeting.getPath() + "/notes/" + note.getUid());
        notes.add(note);
        meeting.setNotes(notes);
        troopUtil.updateTroop(user, troop);
        return notes;

    }

    public boolean editNote(User user, Troop troop, String noteId, String msg) throws java.lang.IllegalAccessException, VtkException {
        boolean isEdit = false;
        Note note = getNote(user, troop, noteId);
        if (note != null && msg != null && !msg.equals("")) {
            note.setMessage(msg);
            isEdit = updateNote(user, troop, note);
        }//edn if
        return isEdit;
    }

    public Set<String> getOutdoorMeetings(User user, Troop troop) throws IllegalAccessException {
        return meetingDAO.getOutdoorMeetings(user, troop);
    }

    public Set<String> getGlobalMeetings(User user, Troop troop) throws IllegalAccessException {
        return meetingDAO.getGlobalMeetings(user, troop);
    }

    public Set<String> getVirtualMeetings(User user, Troop troop) throws IllegalAccessException {
        return meetingDAO.getVirtualMeetings(user, troop);
    }

    public List<Meeting> getMeetings(User user, Troop troop, String level) throws IllegalAccessException {
        List<Meeting> meetings = meetingDAO.getMeetings(user, troop, level);
        Collections.sort(meetings, java.util.Comparator.comparing(Meeting::getMeetingPlanType));
        return meetings;
    }

    public List<Meeting> getAllActiveMeetings(User user, Troop troop, String level) throws IllegalAccessException {
        List<Meeting> meetings = meetingDAO.getAllActiveMeetings(user, troop, level);
        Collections.sort(meetings, java.util.Comparator.comparing(Meeting::getMeetingPlanType));
        return meetings;
    }

    public void setSelectedSubActivity(User user, Troop troop, String meetingPath, String activityPath, String subActivityPath) throws IllegalAccessException, IllegalStateException, VtkException {
        List<MeetingE> meetingEs = troop.getYearPlan().getMeetingEvents();
        for (MeetingE meetingE:meetingEs) {
            if (meetingPath.equals(meetingE.getPath())) {
                List<Activity> activities = meetingE.getMeetingInfo().getActivities();
                if (activities != null) {
                    for (int y = 0; y < activities.size(); y++) {
                        if (activityPath.equals(activities.get(y).getPath())) {
                            List<Activity> subActivities = activities.get(y).getMultiactivities();
                            for (int z = 0; z < subActivities.size(); z++) {
                                if (subActivityPath.equals(subActivities.get(z).getPath())) {
                                    subActivities.get(z).setIsSelected(true);
                                    activities.get(y).setDbUpdate(true);
                                } else {
                                    subActivities.get(z).setIsSelected(false);
                                }//end else
                            }//edn for z
                            meetingDAO.createOrUpdateMeeting(user, troop, meetingE, meetingE.getMeetingInfo());
                        }//edn if
                    }//edn for y
                }
            }//edn if
        }//edn for i
    }

    public boolean canDeleteMeeting(PlanView planView, MeetingE meeting, User user, Troop troop) {
        // Validate non null input.
        if (meeting == null || planView == null || troop == null) {
            log.error("Invalid Input - Cannot determine if meeting can be deleted from null values.");
            // Assume we can't delete to be safe.
            return false;
        }
        // Always allow meetings in the future to be deleted.
        if (System.currentTimeMillis() < planView.getSearchDate().getTime()) {
            return true;
        }
        String path = troop.getPath() + "/yearPlan/meetingEvents/" + meeting.getUid();
        Attendance attendance = this.getAttendance(user, troop, path + "/attendance");
        Achievement achievement = this.getAchievement(user, troop, path + "/achievement");
        boolean hasAttendanceUsers = attendance == null ? false : Optional.ofNullable(attendance.getUsers()).map(StringUtils::isNotBlank).orElse(false);
        // Either attendance users *or* achievment users means that the meeting cannot be removed until the users are removed first.
        return !(hasAttendanceUsers || (achievement == null ? false : Optional.ofNullable(achievement.getUsers()).map(StringUtils::isNotBlank).orElse(false)));
    }

}// edn class