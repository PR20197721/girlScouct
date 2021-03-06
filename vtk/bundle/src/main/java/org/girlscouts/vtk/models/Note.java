package org.girlscouts.vtk.models;

import java.io.Serializable;

public class Note extends JcrNode implements Serializable {
    private Long createTime;
    private String createdByUserName;
    private String createdByUserId;
    private String refId; //meetingId, activity, anything else
    private String message;

    public Note() {
        super.setUid("N" + new java.util.Date().getTime() + "_" + Math.random());
        this.createTime = new java.util.Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.setDbUpdate(true);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
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

}
