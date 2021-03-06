package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;
import org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType;

import java.util.Date;
import java.util.List;

public class ActivityEntity extends BaseEntity {
    @SerializedName("path")
    private String path;
    @SerializedName("name")
    private String name;
    @SerializedName("activityDescription")
    private String activityDescription;
    @SerializedName("activityNumber")
    private int activityNumber;
    @SerializedName("materials")
    private String materials;
    @SerializedName("steps")
    private String steps;
    @SerializedName("endDate")
    private Date endDate;
    @SerializedName("date")
    private Date date;
    @SerializedName("content")
    private String content;
    @SerializedName("id")
    private String id;
    @SerializedName("sortOrder")
    private Integer sortOrder;
    @SerializedName("refUid")
    private String refUid;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("locationAddress")
    private String locationAddress;
    @SerializedName("locationRef")
    private String locationRef;
    @SerializedName("assets")
    private List<AssetEntity> assets;
    @SerializedName("uid")
    private String uid;
    @SerializedName("cost")
    private Double cost;
    @SerializedName("isEditable")
    private Boolean isEditable;
    @SerializedName("cancelled")
    private String cancelled;
    @SerializedName("registerUrl")
    private String registerUrl;
    @SerializedName("emlTemplate")
    private String emlTemplate;
    @SerializedName("sentEmails")
    private List<SentEmailEntity> sentEmails;
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;
    @SerializedName("img")
    private String img;
    @SerializedName("attendance")
    private AttendanceEntity attendance;
    @SerializedName("duration")
    private int duration;
    @SerializedName("outdoor")
    private Boolean outdoor;
    @SerializedName("global")
    private Boolean global;
    @SerializedName("multiactivities")
    private List<ActivityEntity> multiactivities;
    @SerializedName("subtitle")
    private String subtitle;
    @SerializedName("isSelected")
    private Boolean isSelected;
    @SerializedName("type")
    private YearPlanComponentType type;
    @SerializedName("regOpenDate")
    private Date regOpenDate;
    @SerializedName("regCloseDate")
    private Date regCloseDate;
    @SerializedName("adultFee")
    private String adultFee;
    @SerializedName("girlFee")
    private String girlFee;
    @SerializedName("priceRange")
    private String priceRange;
    @SerializedName("grades")
    private String grades;
    @SerializedName("maxAttend")
    private String maxAttend;
    @SerializedName("minAttend")
    private String minAttend;
    @SerializedName("progType")
    private String progType;
    @SerializedName("programCode")
    private String programCode;
    @SerializedName("regDisplay")
    private String regDisplay;
    @SerializedName("region")
    private String region;
    @SerializedName("timezone")
    private String timezone;
    @SerializedName("level")
    private String level;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<AssetEntity> getAssets() {
        return assets;
    }

    public void setAssets(List<AssetEntity> assets) {
        this.assets = assets;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Boolean getEditable() {
        return isEditable;
    }

    public void setEditable(Boolean editable) {
        isEditable = editable;
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

    public List<SentEmailEntity> getSentEmails() {
        return sentEmails;
    }

    public void setSentEmails(List<SentEmailEntity> sentEmails) {
        this.sentEmails = sentEmails;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public AttendanceEntity getAttendance() {
        return attendance;
    }

    public void setAttendance(AttendanceEntity attendance) {
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

    public List<ActivityEntity> getMultiactivities() {
        return multiactivities;
    }

    public void setMultiactivities(List<ActivityEntity> multiactivities) {
        this.multiactivities = multiactivities;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public YearPlanComponentType getType() {
        return type;
    }

    public void setType(YearPlanComponentType type) {
        this.type = type;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}