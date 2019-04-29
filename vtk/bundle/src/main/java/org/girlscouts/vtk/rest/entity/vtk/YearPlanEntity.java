package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YearPlanEntity extends BaseEntity{

    @SerializedName("path")
    private String path;
    @SerializedName("name")
    private String name;
    @SerializedName("desc")
    private String desc;
    @SerializedName("id")
    private String id;
    @SerializedName("refId")
    private String refId;
    @SerializedName("altered")
    private String altered;
    @SerializedName("resources")
    private String resources;
    @SerializedName("meetingEvents")
    private List<MeetingEEntity> meetingEvents;
    @SerializedName("activities")
    private List<ActivityEntity> activities;
    @SerializedName("schedule")
    private CalEntity schedule;
    @SerializedName("locations")
    private List<LocationEntity> locations;
    @SerializedName("calStartDate")
    private Long calStartDate;
    @SerializedName("calFreq")
    private String calFreq;
    @SerializedName("calExclWeeksOf")
    private String calExclWeeksOf;
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;
    @SerializedName("milestones")
    private List<MilestoneEntity> milestones;
    @SerializedName("meetingCanceled")
    private List<MeetingCanceled> meetingCanceled;
    @SerializedName("helper")
    private HelperEntity helper;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getAltered() {
        return altered;
    }

    public void setAltered(String altered) {
        this.altered = altered;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public List<MeetingEEntity> getMeetingEvents() {
        return meetingEvents;
    }

    public void setMeetingEvents(List<MeetingEEntity> meetingEvents) {
        this.meetingEvents = meetingEvents;
    }

    public List<ActivityEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityEntity> activities) {
        this.activities = activities;
    }

    public CalEntity getSchedule() {
        return schedule;
    }

    public void setSchedule(CalEntity schedule) {
        this.schedule = schedule;
    }

    public List<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationEntity> locations) {
        this.locations = locations;
    }

    public Long getCalStartDate() {
        return calStartDate;
    }

    public void setCalStartDate(Long calStartDate) {
        this.calStartDate = calStartDate;
    }

    public String getCalFreq() {
        return calFreq;
    }

    public void setCalFreq(String calFreq) {
        this.calFreq = calFreq;
    }

    public String getCalExclWeeksOf() {
        return calExclWeeksOf;
    }

    public void setCalExclWeeksOf(String calExclWeeksOf) {
        this.calExclWeeksOf = calExclWeeksOf;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }

    public List<MilestoneEntity> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<MilestoneEntity> milestones) {
        this.milestones = milestones;
    }

    public List<MeetingCanceled> getMeetingCanceled() {
        return meetingCanceled;
    }

    public void setMeetingCanceled(List<MeetingCanceled> meetingCanceled) {
        this.meetingCanceled = meetingCanceled;
    }

    public HelperEntity getHelper() {
        return helper;
    }

    public void setHelper(HelperEntity helper) {
        this.helper = helper;
    }
}
