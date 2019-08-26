package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.MultiValueCollectionConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Node
public class MeetingNode extends YearPlanComponentNode implements Serializable {
    @Field
    private String name;
    @Field
    private String level, blurb, cat;
    @Field
    private String aidTags, resources, agenda, req, reqTitle;
    @Collection(collectionConverter= MultiValueCollectionConverterImpl.class)
    private List<String> aidPaths;
    @Field
    private Integer position = 0;
    @Field
    private Boolean isAchievement;
    @Collection
    private List<ActivityNode> activities;
    @Collection
    private Map<String, JcrCollectionHoldStringNode> meetingInfo;
    @Field
    private String meetingPlanType, meetingPlanTypeAlt;
    @Field
    private String catTags, catTagsAlt;

    public Integer getPosition() {
        return position == null ? 0 : position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public List<ActivityNode> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityNode> activities) {
        this.activities = activities;
    }

    public Map<String, JcrCollectionHoldStringNode> getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(Map<String, JcrCollectionHoldStringNode> meetingInfo) {
        this.meetingInfo = meetingInfo;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsAchievement() {
        return isAchievement;
    }

    public void setIsAchievement(Boolean isAchievement) {
        this.isAchievement = isAchievement;
    }

    public String getMeetingPlanType() {
        return meetingPlanType;
    }

    public void setMeetingPlanType(String meetingPlanType) {
        this.meetingPlanType = meetingPlanType;
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

    public String getMeetingPlanTypeAlt() {
        return meetingPlanTypeAlt;
    }

    public void setMeetingPlanTypeAlt(String meetingPlanTypeAlt) {
        this.meetingPlanTypeAlt = meetingPlanTypeAlt;
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

    public Boolean getAchievement() {
        return isAchievement;
    }

    public void setAchievement(Boolean achievement) {
        isAchievement = achievement;
    }

    public List<String> getAidPaths() {
        return aidPaths;
    }

    public void setAidPaths(List<String> aidPaths) {
        this.aidPaths = aidPaths;
    }
}
