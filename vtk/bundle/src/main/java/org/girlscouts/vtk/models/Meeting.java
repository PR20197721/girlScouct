package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Meeting extends YearPlanComponent implements Serializable {
    private String name;
    private String level, blurb, cat;
    private String aidTags, resources, agenda, req, reqTitle;
    private Integer position = 0;
    private Boolean isAchievement;
    private List<Activity> activities;
    private Map<String, JcrCollectionHoldString> meetingInfo;
    private String meetingPlanType, meetingPlanTypeAlt;
    private String catTags, catTagsAlt;
    private List<String> aidPaths;
    private List<String> resourcePaths;
    private Boolean isArchived;

    public Meeting() {
        setPath("/meeting");
        this.position = 0;
    }

    public static Object getStaticValue(final String className, final String fieldName) throws SecurityException, NoSuchFieldException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // Get the private field
        final java.lang.reflect.Field field = Class.forName(className).getDeclaredField(fieldName);
        // Allow modification on the field
        field.setAccessible(true);
        // Return the Obect corresponding to the field
        return field.get(Class.forName(className));
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

    public Integer getPosition() {
        return position == null ? 0 : position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public java.util.Map<String, JcrCollectionHoldString> getMeetingInfo() {
        return meetingInfo;
    }

    public void setMeetingInfo(java.util.Map<String, JcrCollectionHoldString> meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    public java.util.List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(java.util.List<Activity> activities) {
        this.activities = activities;
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

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean archived) {
        isArchived = archived;
    }
    public Boolean getArchived() {
        return isArchived;
    }

    public void setArchived(Boolean archived) {
        isArchived = archived;
    }

    @Override
    public String toString() {
        return "Meeting{" + "path='" + this.getPath() + '\'' + '}';
    }

}
