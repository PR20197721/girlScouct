package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class AssetEntity extends BaseEntity {

    @SerializedName("type")
    private String type;
    @SerializedName("description")
    private String description;
    @SerializedName("title")
    private String title;
    @SerializedName("docType")
    private String docType;
    @SerializedName("refId")
    private String refId;
    @SerializedName("path")
    private String path;
    @SerializedName("isCachable")
    private Boolean isCachable;
    @SerializedName("uid")
    private String uid;
    @SerializedName("isOutdoorRelated")
    private Boolean isOutdoorRelated;
    @SerializedName("isDbUpdate")
    private Boolean isDbUpdate;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getCachable() {
        return isCachable;
    }

    public void setCachable(Boolean cachable) {
        isCachable = cachable;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Boolean getOutdoorRelated() {
        return isOutdoorRelated;
    }

    public void setOutdoorRelated(Boolean outdoorRelated) {
        isOutdoorRelated = outdoorRelated;
    }

    public Boolean getDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(Boolean dbUpdate) {
        isDbUpdate = dbUpdate;
    }
}
