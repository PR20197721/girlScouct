package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.dao.YearPlanComponentType;
import java.util.Date;
import java.util.List;

public class MeetingCanceledEntity extends BaseEntity{

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
	private Integer id;
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
    @SerializedName("date")
	private Date date;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
