package org.girlscouts.vtk.models;

import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MeetingE extends YearPlanComponent implements Serializable {
    boolean isAnyOutdoorActivityInMeeting = false, isAnyOutdoorActivityInMeetingAvailable = false, isAnyGlobalActivityInMeeting = false, isAnyGlobalActivityInMeetingAvailable = false, isAllMultiActivitiesSelected = false;
    private List<Asset> assets;
    private List<SentEmail> sentEmails;
    private Date lastAssetUpdate;
    private Attendance attendance;
    private Achievement achievement;
    private List<Note> notes;
    private String refId; // path to meetingInfo template
    private String locationRef;
    private Meeting meetingInfo;
    private String cancelled;
    private String emlTemplate;
    private List<String> aidPaths;
    private List<String> resourcePaths;

    public MeetingE() {
        super.setUid("M" + new java.util.Date().getTime() + "_" + Math.random()); // better to be impossible than unlikely
        super.setType(YearPlanComponentType.MEETING);
    }

    public List<String> getAidPaths() {
        return aidPaths;
    }

    public void setAidPaths(List<String> aidPaths) {
        this.aidPaths = aidPaths;
    }

    public List<String> getResourcePaths() {
        return resourcePaths;
    }

    public void setResourcePaths(List<String> resourcePaths) {
        this.resourcePaths = resourcePaths;
    }

    public boolean isAllMultiActivitiesSelected() {
        return isAllMultiActivitiesSelected;
    }

    public void setAllMultiActivitiesSelected(boolean isAllMultiActivitiesSelected) {
        this.isAllMultiActivitiesSelected = isAllMultiActivitiesSelected;
    }

    public java.util.Date getLastAssetUpdate() {
        return lastAssetUpdate;
    }

    public void setLastAssetUpdate(java.util.Date lastAssetUpdate) {
        if ((lastAssetUpdate != null && this.lastAssetUpdate != null && !this.lastAssetUpdate.equals(lastAssetUpdate)) || (lastAssetUpdate != null && this.lastAssetUpdate == null)) {
            setDbUpdate(true);
        }
        this.lastAssetUpdate = lastAssetUpdate;

    }

    public java.util.List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(java.util.List<Asset> assets) {
        if ((assets != null && this.assets != null && !this.assets.equals(assets)) || (assets != null && this.assets == null)) {
            setDbUpdate(true);
        }
        this.assets = assets;

    }

    public void setUid(String uid) {
        if (uid == null) {
            super.setUid("M" + new java.util.Date().getTime() + "_" + Math.random());
        } else {
            super.setUid(uid);
        }

    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        if ((cancelled != null && this.cancelled != null && !this.cancelled.equals(cancelled)) || (cancelled != null && this.cancelled == null)) {
            setDbUpdate(true);
        }
        this.cancelled = cancelled;

    }

    public void setSortOrder(Integer order) {
        if ((order != null && this.getSortOrder() != null && !this.getSortOrder().equals(order)) || (order != null && this.getSortOrder() == null)) {
            setDbUpdate(true);
        }
        super.setSortOrder(order);
    }

    public String getLocationRef() {
        return locationRef;
    }

    public void setLocationRef(String locationRef) {
        if ((locationRef != null && this.locationRef != null && !this.locationRef.equals(locationRef)) || (locationRef != null && this.locationRef == null)) {
            setDbUpdate(true);
        }
        this.locationRef = locationRef;

    }

    public Meeting getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(Meeting meetingInfo) {
        this.meetingInfo = meetingInfo;

    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        if ((refId != null && this.refId != null && !this.refId.equals(refId)) || (refId != null && this.refId == null)) {
            setDbUpdate(true);
        }
        this.refId = refId;

    }

    public java.util.List<SentEmail> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(java.util.List<SentEmail> emails) {
        this.sentEmails = emails;
    }

    public String getEmlTemplate() {
        return emlTemplate;
    }

    public void setEmlTemplate(String template) {
        this.emlTemplate = template;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public java.util.List<Note> getNotes() {
        return notes;
    }

    public void setNotes(java.util.List<Note> notes) {
        this.notes = notes;
    }

    public boolean isAnyOutdoorActivityInMeeting() {
        return isAnyOutdoorActivityInMeeting;
    }

    public void setAnyOutdoorActivityInMeeting(boolean isAnyOutdoorActivityInMeeting) {
        this.isAnyOutdoorActivityInMeeting = isAnyOutdoorActivityInMeeting;
    }

    public boolean isAnyOutdoorActivityInMeetingAvailable() {
        return isAnyOutdoorActivityInMeetingAvailable;
    }

    public void setAnyOutdoorActivityInMeetingAvailable(boolean isAnyOutdoorActivityInMeetingAvailable) {
        this.isAnyOutdoorActivityInMeetingAvailable = isAnyOutdoorActivityInMeetingAvailable;
    }

    public boolean isAnyGlobalActivityInMeeting() {
        return isAnyGlobalActivityInMeeting;
    }

    public void setAnyGlobalActivityInMeeting(boolean isAnyGlobalActivityInMeeting) {
        this.isAnyGlobalActivityInMeeting = isAnyGlobalActivityInMeeting;
    }

    public boolean isAnyGlobalActivityInMeetingAvailable() {
        return isAnyGlobalActivityInMeetingAvailable;
    }

    public void setAnyGlobalActivityInMeetingAvailable(boolean isAnyGlobalActivityInMeetingAvailable) {
        this.isAnyGlobalActivityInMeetingAvailable = isAnyGlobalActivityInMeetingAvailable;
    }

    @Override
    public String toString() {
        return "MeetingE{" + "path='" + this.getPath()  + '}';
    }
}
