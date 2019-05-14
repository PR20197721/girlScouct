package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.util.Date;
import java.util.List;

public class MeetingEEntity extends BaseEntity {
    @SerializedName("path")
    private String path;
    @SerializedName("refId")
    private String refId;
    @SerializedName("locationRef")
    private String locationRef;
    @SerializedName("meetingInfo")
    private MeetingEntity meetingInfo;
    @SerializedName("cancelled")
    private String cancelled;
    @SerializedName("id")
    private String id;
    @SerializedName("sortOrder")
    private Integer sortOrder;
    @SerializedName("uid")
    private String uid;
    @SerializedName("emlTemplate")
    private String emlTemplate;
    @SerializedName("assets")
    private List<AssetEntity> assets;
    @SerializedName("sentEmails")
    private List<SentEmailEntity> sentEmails;
    @SerializedName("lastAssetUpdate")
    private Date lastAssetUpdate;
    @SerializedName("attendance")
    private AttendanceEntity attendance;
    @SerializedName("achievement")
    private AchievementEntity achievement;
    @SerializedName("notes")
    private List<NoteEntity> notes;
    @SerializedName("isAnyOutdoorActivityInMeeting")
    private boolean isAnyOutdoorActivityInMeeting;
    @SerializedName("isAnyOutdoorActivityInMeetingAvailable")
    private boolean isAnyOutdoorActivityInMeetingAvailable;
    @SerializedName("isAnyGlobalActivityInMeeting")
    private boolean isAnyGlobalActivityInMeeting;
    @SerializedName("isAnyGlobalActivityInMeetingAvailable")
    private boolean isAnyGlobalActivityInMeetingAvailable;
    @SerializedName("isAllMultiActivitiesSelected")
    private boolean isAllMultiActivitiesSelected;
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;
    @SerializedName("type")
    private YearPlanComponentType type;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getLocationRef() {
        return locationRef;
    }

    public void setLocationRef(String locationRef) {
        this.locationRef = locationRef;
    }

    public MeetingEntity getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(MeetingEntity meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmlTemplate() {
        return emlTemplate;
    }

    public void setEmlTemplate(String emlTemplate) {
        this.emlTemplate = emlTemplate;
    }

    public List<AssetEntity> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetEntity> assets) {
        this.assets = assets;
    }

    public List<SentEmailEntity> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<SentEmailEntity> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public Date getLastAssetUpdate() {
        return lastAssetUpdate;
    }

    public void setLastAssetUpdate(Date lastAssetUpdate) {
        this.lastAssetUpdate = lastAssetUpdate;
    }

    public AttendanceEntity getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity attendance) {
        this.attendance = attendance;
    }

    public AchievementEntity getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementEntity achievement) {
        this.achievement = achievement;
    }

    public List<NoteEntity> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteEntity> notes) {
        this.notes = notes;
    }

    public boolean isAnyOutdoorActivityInMeeting() {
        return isAnyOutdoorActivityInMeeting;
    }

    public void setAnyOutdoorActivityInMeeting(boolean anyOutdoorActivityInMeeting) {
        isAnyOutdoorActivityInMeeting = anyOutdoorActivityInMeeting;
    }

    public boolean isAnyOutdoorActivityInMeetingAvailable() {
        return isAnyOutdoorActivityInMeetingAvailable;
    }

    public void setAnyOutdoorActivityInMeetingAvailable(boolean anyOutdoorActivityInMeetingAvailable) {
        isAnyOutdoorActivityInMeetingAvailable = anyOutdoorActivityInMeetingAvailable;
    }

    public boolean isAnyGlobalActivityInMeeting() {
        return isAnyGlobalActivityInMeeting;
    }

    public void setAnyGlobalActivityInMeeting(boolean anyGlobalActivityInMeeting) {
        isAnyGlobalActivityInMeeting = anyGlobalActivityInMeeting;
    }

    public boolean isAnyGlobalActivityInMeetingAvailable() {
        return isAnyGlobalActivityInMeetingAvailable;
    }

    public void setAnyGlobalActivityInMeetingAvailable(boolean anyGlobalActivityInMeetingAvailable) {
        isAnyGlobalActivityInMeetingAvailable = anyGlobalActivityInMeetingAvailable;
    }

    public boolean isAllMultiActivitiesSelected() {
        return isAllMultiActivitiesSelected;
    }

    public void setAllMultiActivitiesSelected(boolean allMultiActivitiesSelected) {
        isAllMultiActivitiesSelected = allMultiActivitiesSelected;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }

    public YearPlanComponentType getType() {
        return type;
    }

    public void setType(YearPlanComponentType type) {
        this.type = type;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
