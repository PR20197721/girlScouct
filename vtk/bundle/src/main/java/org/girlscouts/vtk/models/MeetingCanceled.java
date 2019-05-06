package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MeetingCanceled extends MeetingE implements Serializable {
    private List<Asset> assets;
    private List<SentEmail> sentEmails;
    private Date lastAssetUpdate;
    private String refId; // path to meetingInfo template
    private String locationRef;
    private Meeting meetingInfo;
    private String cancelled;
    private String emlTemplate;
    private Date date;

    public MeetingCanceled() {
        super.setType(YearPlanComponentType.MEETINGCANCELED);
        this.setUid("MC" + new java.util.Date().getTime());
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
            this.setUid("MC" + new java.util.Date().getTime() + "_" + Math.random());
        }else{
            this.setUid(uid);
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

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        this.date = date;
    }

}
