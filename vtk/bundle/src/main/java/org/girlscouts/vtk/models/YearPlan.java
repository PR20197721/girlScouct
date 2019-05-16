package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.List;

public class YearPlan extends JcrNode implements Serializable {
    private String name, desc, id, refId, altered, resources;
    private java.util.List<MeetingE> meetingEvents;
    private java.util.List<Activity> activities;
    private Cal schedule;
    private List<Location> locations;
    private Long calStartDate;
    private String calFreq, calExclWeeksOf;
    private List<Milestone> milestones;
    private List<MeetingCanceled> meetingCanceled;
    private Helper helper;

    public YearPlan() {
        calFreq = "biweekly";
    }

    public Helper getHelper() {
        return helper;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
        this.setDbUpdate(true);
    }

    public java.util.List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(java.util.List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public String getAltered() {
        return altered;
    }

    public void setAltered(String altered) {
        if (altered != null && this.altered != null && !this.altered.equals(altered)) {
            this.setDbUpdate(true);
        }
        this.altered = altered;

    }

    public Long getCalStartDate() {
        return calStartDate;
    }

    public void setCalStartDate(Long calStartDate) {
        this.calStartDate = calStartDate;
        this.setDbUpdate(true);
    }

    public String getCalFreq() {
        return calFreq;
    }

    public void setCalFreq(String calFreq) {
        this.calFreq = calFreq;
        this.setDbUpdate(true);
    }

    public String getCalExclWeeksOf() {
        return calExclWeeksOf;
    }

    public void setCalExclWeeksOf(String calExclWeeksOf) {
        this.calExclWeeksOf = calExclWeeksOf;
        this.setDbUpdate(true);
    }

    public Cal getSchedule() {
        return schedule;
    }

    public void setSchedule(Cal schedule) {
        this.schedule = schedule;
    }

    public java.util.List<Location> getLocations() {
        return locations;
    }

    public void setLocations(java.util.List<Location> locations) {
        this.locations = locations;
    }

    public java.util.List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(java.util.List<Activity> activities) {
        this.activities = activities;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
        this.setDbUpdate(true);
    }

    public java.util.List<MeetingE> getMeetingEvents() {
        return meetingEvents;
    }

    public void setMeetingEvents(java.util.List<MeetingE> meetingEvents) {
        this.meetingEvents = meetingEvents;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setDbUpdate(true);

    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        this.setDbUpdate(true);

    }

    public List<MeetingCanceled> getMeetingCanceled() {
        return meetingCanceled;
    }

    public void setMeetingCanceled(List<MeetingCanceled> meetingCanceled) {
        this.meetingCanceled = meetingCanceled;
    }

}
