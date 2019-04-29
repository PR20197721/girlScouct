package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class Note extends BaseEntity{

    @SerializedName("message")
    private String message;
    @SerializedName("path")
    private String path;
    @SerializedName("uid")
    private String uid;
    @SerializedName("createTime")
    private Long createTime;
    @SerializedName("createdByUserName")
    private String createdByUserName;
    @SerializedName("createdByUserId")
    private String createdByUserId;
    @SerializedName("refId")
    private String refId; //meetingId, activity, anything else
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }
}
