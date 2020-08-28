package org.girlscouts.vtk.models;

import org.girlscouts.vtk.utils.MeetingESortOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Troop extends JcrNode {
    private YearPlan yearPlan;
    private String sfUserId;
    private String sfTroopId;
    private String sfTroopName;
    private String sfTroopAge;
    private String sfCouncil;
    private String sfParentId;
    private String irmTroopId;
    private String currentTroop;
    private String errCode;
    private String refId;
    private String troopId;
    private String troopName;
    private String gradeLevel;
    private String councilId;
    private String councilCode;
    private int type;
    private Set<Integer> permissionTokens;
    private String role;
    private boolean isRefresh;
    private Date retrieveTime;
    private EmailMeetingReminder sendingEmail;
    private String participationCode;
    private String councilPath;
    private String hash;
    private Boolean isIRM = Boolean.FALSE;
    private Boolean isSUM = Boolean.FALSE;
    private Map<Date, YearPlanComponent> schedule;
    private Boolean isLoadedManualy = Boolean.FALSE;


    private final Logger log = LoggerFactory.getLogger(getClass());

    public Troop() {
        this.type = 0;
    }

    public Troop(String troopId) {
        this.setId(troopId);
        this.setRetrieveTime(new java.util.Date());
    }

    public java.util.Date getRetrieveTime() {
        return retrieveTime;
    }

    public void setRetrieveTime(java.util.Date retrieveTime) {
        this.retrieveTime = retrieveTime;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
        this.setDbUpdate(true);
    }

    public String getCurrentTroop() {
        return currentTroop;
    }

    public void setCurrentTroop(String currentUser) {
        this.currentTroop = currentUser;
        this.setDbUpdate(true);
    }

    public String getSfCouncil() {
        return sfCouncil;
    }

    public void setSfCouncil(String sfCouncil) {
        this.sfCouncil = sfCouncil;
    }

    public String getSfTroopAge() {
        return sfTroopAge;
    }

    public void setSfTroopAge(String sfTroopAge) {
        this.sfTroopAge = sfTroopAge;
    }

    public EmailMeetingReminder getSendingEmail() {
        return sendingEmail;
    }

    public void setSendingEmail(EmailMeetingReminder sendingEmail) {
        this.sendingEmail = sendingEmail;
    }

    public String getSfTroopName() {
        return sfTroopName;
    }

    public void setSfTroopName(String sfTroopName) {
        this.sfTroopName = sfTroopName;
    }

    public String getSfUserId() {
        return sfUserId;
    }

    public void setSfUserId(String sfUserId) {
        this.sfUserId = sfUserId;
    }

    public String getSfTroopId() {
        return sfTroopId;
    }

    public void setSfTroopId(String sfTroopId) {
        this.sfTroopId = sfTroopId;
    }

    public YearPlan getYearPlan() {
        return yearPlan;
    }

    public void setYearPlan(YearPlan yearPlan) {
        this.yearPlan = yearPlan;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
        this.setDbUpdate(true);
    }

    public String getTroopPath() {
        return getPath();
    }

    public String getTroopId() {
        return troopId;
    }

    public void setTroopId(String troopId) {
        this.troopId = troopId;
    }

    public String getTroopName() {
        return troopName;
    }

    public void setTroopName(String troopName) {
        this.troopName = troopName;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getCouncilId() {
        return councilId;
    }

    public void setCouncilId(String councilId) {
        this.councilId = councilId;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<Integer> getPermissionTokens() {
        return permissionTokens;
    }

    public void setPermissionTokens(Set<Integer> permissionTokens) {
        this.permissionTokens = permissionTokens;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getParticipationCode() {
        return participationCode;
    }

    public void setParticipationCode(String participationCode) {
        this.participationCode = participationCode;
    }

    public String getCouncilPath() {
        return councilPath;
    }

    public void setCouncilPath(String councilPath) {
        this.councilPath = councilPath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getIrmTroopId() {
        return irmTroopId;
    }

    public void setIrmTroopId(String irmTroopId) {
        this.irmTroopId = irmTroopId;
    }

    public Boolean getIsIRM() {
        return isIRM;
    }

    public void setIsIRM(Boolean IRM) {
        this.isIRM = IRM;
    }

    public Boolean getIsSUM() {
        return isSUM;
    }

    public void setIsSUM(Boolean SUM) {
        this.isSUM = SUM;
    }

    public String getSfParentId() {
        return sfParentId;
    }

    public void setSfParentId(String sfParentId) {
        this.sfParentId = sfParentId;
    }

    public Boolean getIsLoadedManualy() {
        return isLoadedManualy;
    }

    public void setIsLoadedManualy(Boolean loadedManualy) {
        this.isLoadedManualy = loadedManualy;
    }

    public void clearSchedule(){
        this.schedule = null;
    }
    public Map<Date, YearPlanComponent> getSchedule(){
        log.debug("Getting schedule for "+ this.getPath());
        /*if(this.schedule != null) {
            log.debug("Existing schedule for "+ this.getPath());
            return this.schedule;
        } else{
        */
        log.debug("Building schedule for "+ this.getPath());
        YearPlan yearPlan = this.getYearPlan();
        if (yearPlan != null) {
            this.schedule = getYearPlanSchedule(yearPlan);
            return this.schedule;
        } else {
            log.debug("No year plan for " + this.getPath());
            return new TreeMap<>();
        }
       // }
    }

    private Map<Date, YearPlanComponent> getYearPlanSchedule(YearPlan plan) {
        Map<Date, YearPlanComponent> schedule = new TreeMap<Date, YearPlanComponent>();
        if (plan != null) {
            try {
                List<Activity> activities = plan.getActivities();
                List<MeetingE> meetingEvents = plan.getMeetingEvents();
                if (meetingEvents != null) {
                    try {
                        Collections.sort(meetingEvents, new MeetingESortOrderComparator());
                    } catch (Exception e) {
                        log.error("Error occurred while sorting meetings for year plan " + plan.getPath() + " : ", e);
                    }
                }
                if (plan.getSchedule() != null) {
                    String calMeeting = plan.getSchedule().getDates();
                    String[] calendarDates = calMeeting.split(",");
                    int numOfDates = calendarDates.length >= meetingEvents.size() ? meetingEvents.size() : calendarDates.length;
                    int maxDates = 500;
                    for (int i = 0; i < numOfDates; i++) {
                        if (i >= maxDates) {
                            break;
                        }
                        Date meetingDate = new Date(Long.parseLong(calendarDates[i]));
                        if (schedule.containsKey(meetingDate)) {
                            while (schedule.containsKey(meetingDate)) {
                                meetingDate = new Date(meetingDate.getTime() + TimeUnit.MILLISECONDS.toMillis(1));
                            }
                        }
                        schedule.put(meetingDate, meetingEvents.get(i));
                    }
                } else { // no dates: create 1976
                    Calendar defaultDateTime = Calendar.getInstance();
                    defaultDateTime.setTime(new Date("1/1/1976"));
                    if (meetingEvents != null) {
                        for (int i = 0; i < meetingEvents.size(); i++) {
                            schedule.put(defaultDateTime.getTime(), meetingEvents.get(i));
                            defaultDateTime.add(Calendar.DATE, 1);
                        }
                    }
                }
                if (activities != null) {
                    for (int i = 0; i < activities.size(); i++) {
                        Date activityDate = activities.get(i).getDate();
                        if (schedule.containsKey(activityDate)) { // add
                            while (schedule.containsKey(activityDate)) {
                                activityDate = new Date(activityDate.getTime() + TimeUnit.MILLISECONDS.toMillis(1));
                            }
                        }
                        schedule.put(activityDate, activities.get(i));
                    }
                }
                if (plan.getMeetingCanceled() != null) {
                    for (int i = 0; i < plan.getMeetingCanceled().size(); i++) {
                        Date cancelledMeetingDateTime = plan.getMeetingCanceled().get(i).getDate();
                        if (schedule.containsKey(cancelledMeetingDateTime)) { // add 2 sec
                            while (schedule.containsKey(cancelledMeetingDateTime)) {
                                cancelledMeetingDateTime = new Date(cancelledMeetingDateTime.getTime() + TimeUnit.MILLISECONDS.toMillis(1));
                            }
                        }
                        schedule.put(cancelledMeetingDateTime, plan.getMeetingCanceled().get(i));
                    }
                }
            } catch (Exception e) {
                log.error("Error occurred: ", e);
                return schedule;
            }
        }
        return schedule;
    }


}