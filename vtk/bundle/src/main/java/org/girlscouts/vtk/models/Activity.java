package org.girlscouts.vtk.models;

import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Activity extends YearPlanComponent implements Serializable {
    private List<Asset> assets;
    private Double cost;
    private Boolean isEditable;
    private List<SentEmail> sentEmails;
    private String img;
    private Attendance attendance;
    private int duration;
    private Boolean outdoor = false;
    private Boolean global = false;
    private List<Activity> multiactivities;
    private String subtitle;
    private Boolean isSelected = false;
    private String name, activityDescription;
    private int activityNumber;
    private String materials, steps;
    private Date endDate, date;
    private String content, refUid;
    private String locationName, locationAddress;
    private String locationRef;// depricated
    private String cancelled;
    private String registerUrl;
    private String emlTemplate;

    private Date regOpenDate, regCloseDate;
    private String adultFee;
    private String girlFee;
    private String priceRange;
    private String grades;
    private String maxAttend;
    private String minAttend;
    private String progType;
    private String programCode;
    private String regDisplay;
    private String region;
    private String timezone;
    private String level;

    public Activity() {
        super.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
        super.setType(YearPlanComponentType.ACTIVITY);
        this.cost = 0.00;
        this.isEditable = true;
    }

    public Activity(String name, String content, java.util.Date date, java.util.Date endDate, String locationName, String locationAddress, double cost) {
        this.name = name;
        this.content = content;
        this.date = date;
        this.endDate = endDate;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        super.setType(YearPlanComponentType.ACTIVITY);
        super.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
        this.cost = cost;
        this.isEditable = true;

    }

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        if ((registerUrl != null && this.registerUrl != null && !this.registerUrl.equals(registerUrl)) || (registerUrl != null && this.registerUrl == null)) {
            setDbUpdate(true);
        }
        this.registerUrl = registerUrl;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        if ((cancelled != null && this.cancelled == null) || this.cancelled != null && cancelled != null && !this.cancelled.equals(cancelled)) {
            setDbUpdate(true);
        }
        this.cancelled = cancelled;
    }

    public String getRefUid() {
        return refUid;
    }

    public void setRefUid(String refUid) {
        if ((this.refUid == null && refUid != null) || (this.refUid != null && refUid != null && !this.refUid.equals(refUid))) {
            setDbUpdate(true);
        }
        this.refUid = refUid;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        if ((this.isEditable == null && isEditable != null) || (this.isEditable != null && isEditable != null && this.isEditable.booleanValue() != isEditable.booleanValue())) {
            setDbUpdate(true);
        }
        this.isEditable = isEditable;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        if ((cost != null && this.cost != null && this.cost.doubleValue() != cost.doubleValue()) || (this.cost == null && cost != null)) {
            setDbUpdate(true);
        }
        this.cost = cost;
    }

    public void setUid(String uid) {
        if (uid == null) {
            super.setUid("A" + new java.util.Date().getTime() + "_" + Math.random());
        } else {
            super.setUid(uid);
        }

    }

    public java.util.List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(java.util.List<Asset> assets) {
        if ((this.assets == null && assets != null) || (this.assets != null && assets != null && !this.assets.equals(assets))) {
            setDbUpdate(true);
        }
        this.assets = assets;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        if ((this.locationName == null && locationName != null) || (this.locationName != null && locationName != null && !this.locationName.equals(locationName))) {
            setDbUpdate(true);
        }
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        if ((this.locationAddress == null && locationAddress != null) || (this.locationAddress != null && locationAddress != null && !this.locationAddress.equals(locationAddress))) {
            setDbUpdate(true);
        }
        this.locationAddress = locationAddress;
    }

    public String getLocationRef() {
        return locationRef;
    }

    public void setLocationRef(String locationRef) {
        if ((this.locationRef == null && locationRef != null) || (this.locationRef != null && locationRef != null && !this.locationRef.equals(locationRef))) {
            setDbUpdate(true);
        }
        this.locationRef = locationRef;
    }

    public java.util.Date getDate() {
        return date;
    }

    public void setDate(java.util.Date date) {
        if ((this.date == null && date != null) || (this.date != null && date != null && !this.date.equals(date))) {
            setDbUpdate(true);
        }
        this.date = date;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        if ((this.endDate == null && endDate != null) || (this.endDate != null && endDate != null && !this.endDate.equals(endDate))) {
            setDbUpdate(true);
        }
        this.endDate = endDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if ((this.content == null && content != null) || (this.content != null && content != null && !this.content.equals(content))) {
            setDbUpdate(true);
        }
        this.content = content;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        if ((this.activityDescription == null && activityDescription != null) || (this.activityDescription != null && activityDescription != null && !this.activityDescription.equals(activityDescription))) {
            setDbUpdate(true);
        }
        this.activityDescription = activityDescription;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        if ((this.materials == null && materials != null) || (this.materials != null && materials != null && !this.materials.equals(materials))) {
            setDbUpdate(true);
        }
        this.materials = materials;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        if ((this.steps == null && steps != null) || (this.steps != null && steps != null && !this.steps.equals(steps))) {
            setDbUpdate(true);
        }
        this.steps = steps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if ((this.name == null && name != null) || (this.name != null && name != null && !this.name.equals(name))) {
            setDbUpdate(true);
        }
        this.name = name;
    }

    public int getActivityNumber() {
        return activityNumber;
    }

    public void setActivityNumber(int activityNumber) {
        if (activityNumber != this.activityNumber) {
            setDbUpdate(true);
        }
        this.activityNumber = activityNumber;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Attendance getAttendance() {
        return attendance;
    }

    public void setAttendance(Attendance attendance) {
        this.attendance = attendance;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (this.duration != duration) {
            setDbUpdate(true);
        }
        this.duration = duration;
    }

    public java.util.List<Activity> getMultiactivities() {
        return multiactivities;
    }

    public void setMultiactivities(java.util.List<Activity> multiactivities) {
        this.multiactivities = multiactivities;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Boolean getIsSelected() {
        return isSelected == null ? false : isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return "Activity{" + "path=" + this.getPath() + '}';
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

    public String getRegDisplay() {
        return regDisplay;
    }

    public void setRegDisplay(String regDisplay) {
        this.regDisplay = regDisplay;
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