package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Node
public class ActivityNode extends YearPlanComponentNode implements Serializable {
    @Collection
    private List<AssetNode> assets;
    @Field
    private Double cost;
    @Field
    private Boolean isEditable;
    @Collection
    private List<SentEmailNode> sentEmails;
    @Field
    private String img;
    @Bean(autoUpdate = true)
    private AttendanceNode attendance;
    @Field(jcrDefaultValue = "0")
    private int duration;
    @Field(jcrDefaultValue = "false")
    private Boolean outdoor;
    @Field(jcrDefaultValue = "false")
    private Boolean global;
    @Collection
    private List<ActivityNode> multiactivities;
    @Field
    private String subtitle;
    @Field(jcrDefaultValue = "false")
    private Boolean isSelected;
    @Field
    private String name, activityDescription;
    @Field
    private int activityNumber;
    @Field
    private String materials, steps;
    @Field
    private Date endDate;
    @Field
    private String content, refUid;
    @Field
    private String locationName, locationAddress;
    @Field
    private String locationRef;
    @Field
    private String cancelled;
    @Field
    private String registerUrl;
    @Field
    private String emlTemplate;
    @Field
    private Date regOpenDate;
    @Field
    private Date regCloseDate;
    @Field
    private String adultFee;
    @Field
    private String priceRange;
    @Field
    private String girlFee;
    @Field
    private String grades;
    @Field
    private String maxAttend;
    @Field
    private String minAttend;
    @Field
    private String progType;
    @Field
    private String programCode;
    @Field
    private String regDisplay;
    @Field
    private String region;
    @Field
    private String timezone;
    @Field
    private String level;

    public List<AssetNode> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetNode> assets) {
        this.assets = assets;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean editable) {
        isEditable = editable;
    }

    public List<SentEmailNode> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<SentEmailNode> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public AttendanceNode getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceNode attendance) {
        this.attendance = attendance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Boolean getOutdoor() {
        return outdoor;
    }

    public void setOutdoor(Boolean outdoor) {
        this.outdoor = outdoor;
    }

    public Boolean getGlobal() {
        return global;
    }

    public void setGlobal(Boolean global) {
        this.global = global;
    }

    public List<ActivityNode> getMultiactivities() {
        return multiactivities;
    }

    public void setMultiactivities(List<ActivityNode> multiactivities) {
        this.multiactivities = multiactivities;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }

    public int getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(int activityNumber) {
        this.activityNumber = activityNumber;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRefUid() {
        return refUid;
    }

    public void setRefUid(String refUid) {
        this.refUid = refUid;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
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

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getEmlTemplate() {
        return emlTemplate;
    }

    public void setEmlTemplate(String emlTemplate) {
        this.emlTemplate = emlTemplate;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Date getRegOpenDate() {
        return regOpenDate;
    }

    public void setRegOpenDate(Date regOpenDate) {
        this.regOpenDate = regOpenDate;
    }

    public Date getRegCloseDate() {
        return regCloseDate;
    }

    public void setRegCloseDate(Date regCloseDate) {
        this.regCloseDate = regCloseDate;
    }

    public String getAdultFee() {
        return adultFee;
    }

    public void setAdultFee(String adultFee) {
        this.adultFee = adultFee;
    }

    public String getGirlFee() {
        return girlFee;
    }

    public void setGirlFee(String girlFee) {
        this.girlFee = girlFee;
    }

    public String getGrades() {
        return grades;
    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public String getMaxAttend() {
        return maxAttend;
    }

    public void setMaxAttend(String maxAttend) {
        this.maxAttend = maxAttend;
    }

    public String getMinAttend() {
        return minAttend;
    }

    public void setMinAttend(String minAttend) {
        this.minAttend = minAttend;
    }

    public String getProgType() {
        return progType;
    }

    public void setProgType(String progType) {
        this.progType = progType;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public String getRegDisplay() {
        return regDisplay;
    }

    public void setRegDisplay(String regDisplay) {
        this.regDisplay = regDisplay;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}