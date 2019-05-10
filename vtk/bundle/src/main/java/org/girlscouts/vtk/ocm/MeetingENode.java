package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.mapper.ocm.NodeToModelMapper;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Node
public class MeetingENode extends YearPlanComponentNode implements Serializable {
    @Collection(autoUpdate = false)
    private List<AssetNode> assets;
    @Collection
    private List<SentEmailNode> sentEmails;
    @Field
    private Date lastAssetUpdate;
    @Bean(autoUpdate = false)
    private AttendanceNode attendance;
    @Bean(autoUpdate = false)
    private AchievementNode achievement;
    @Collection(autoRetrieve = true, autoInsert = true)
    private List<NoteNode> notes;
    @Field
    private String refId;
    @Field
    private String locationRef;
    @Field
    private String cancelled;
    @Field
    private String emlTemplate;
    @Collection
    private List<String> aidPaths;
    @Collection
    private List<String> resoourcePaths;

    public List<AssetNode> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetNode> assets) {
        this.assets = assets;
    }

    public List<SentEmailNode> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<SentEmailNode> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public Date getLastAssetUpdate() {
        return lastAssetUpdate;
    }

    public void setLastAssetUpdate(Date lastAssetUpdate) {
        this.lastAssetUpdate = lastAssetUpdate;
    }

    public AttendanceNode getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceNode attendance) {
        this.attendance = attendance;
    }

    public AchievementNode getAchievement() {
        return achievement;
    }

    public void setAchievement(AchievementNode achievement) {
        this.achievement = achievement;
    }

    public List<NoteNode> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteNode> notes) {
        this.notes = notes;
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

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getEmlTemplate() {
        return emlTemplate;
    }

    public void setEmlTemplate(String emlTemplate) {
        this.emlTemplate = emlTemplate;
    }

    public List<String> getAidPaths() {
        return aidPaths;
    }

    public void setAidPaths(List<String> aidPaths) {
        this.aidPaths = aidPaths;
    }

    public List<String> getResoourcePaths() {
        return resoourcePaths;
    }

    public void setResoourcePaths(List<String> resoourcePaths) {
        this.resoourcePaths = resoourcePaths;
    }
}
