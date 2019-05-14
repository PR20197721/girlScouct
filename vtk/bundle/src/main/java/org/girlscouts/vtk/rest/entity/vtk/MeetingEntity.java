package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.util.List;
import java.util.Map;

public class MeetingEntity extends BaseEntity {
    @SerializedName("type")
    private YearPlanComponentType type;
    @SerializedName("path")
    private String path;
    @SerializedName("id")
    private String id;
    @SerializedName("sortOrder")
    private Integer sortOrder;
    @SerializedName("name")
    private String name;
    @SerializedName("level")
    private String level;
    @SerializedName("blurb")
    private String blurb;
    @SerializedName("cat")
    private String cat;
    @SerializedName("aidTags")
    private String aidTags;
    @SerializedName("resources")
    private String resources;
    @SerializedName("agenda")
    private String agenda;
    @SerializedName("req")
    private String req;
    @SerializedName("reqTitle")
    private String reqTitle;
    @SerializedName("position")
    private Integer position;
    @SerializedName("isAchievement")
    private Boolean isAchievement;
    @SerializedName("activities")
    private List<ActivityEntity> activities;
    @SerializedName("meetingInfo")
    private Map<String, JcrCollectionHoldStringEntity> meetingInfo;
    @SerializedName("meetingPlanType")
    private String meetingPlanType;
    @SerializedName("meetingPlanTypeAlt")
    private String meetingPlanTypeAlt;
    @SerializedName("catTags")
    private String catTags;
    @SerializedName("catTagsAlt")
    private String catTagsAlt;

    public YearPlanComponentType getType() {
        return type;
    }

    public void setType(YearPlanComponentType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getAidTags() {
        return aidTags;
    }

    public void setAidTags(String aidTags) {
        this.aidTags = aidTags;
    }

    public String getResources() {
        return resources;
    }

    public void setResources(String resources) {
        this.resources = resources;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public String getReq() {
        return req;
    }

    public void setReq(String req) {
        this.req = req;
    }

    public String getReqTitle() {
        return reqTitle;
    }

    public void setReqTitle(String reqTitle) {
        this.reqTitle = reqTitle;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Boolean getAchievement() {
        return isAchievement;
    }

    public void setAchievement(Boolean achievement) {
        isAchievement = achievement;
    }

    public List<ActivityEntity> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityEntity> activities) {
        this.activities = activities;
    }

    public Map<String, JcrCollectionHoldStringEntity> getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(Map<String, JcrCollectionHoldStringEntity> meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public String getMeetingPlanType() {
        return meetingPlanType;
    }

    public void setMeetingPlanType(String meetingPlanType) {
        this.meetingPlanType = meetingPlanType;
    }

    public String getMeetingPlanTypeAlt() {
        return meetingPlanTypeAlt;
    }

    public void setMeetingPlanTypeAlt(String meetingPlanTypeAlt) {
        this.meetingPlanTypeAlt = meetingPlanTypeAlt;
    }

    public String getCatTags() {
        return catTags;
    }

    public void setCatTags(String catTags) {
        this.catTags = catTags;
    }

    public String getCatTagsAlt() {
        return catTagsAlt;
    }

    public void setCatTagsAlt(String catTagsAlt) {
        this.catTagsAlt = catTagsAlt;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
