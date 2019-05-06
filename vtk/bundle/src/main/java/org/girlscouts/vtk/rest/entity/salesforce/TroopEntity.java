package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class TroopEntity {
    @SerializedName("Id")
    private String sfId;
    @SerializedName("ParentId")
    private String sfParentId;
    @SerializedName("Job_Code__c")
    private String jobCode;
    @SerializedName("Parent")
    private ParentEntity parentEntity;
    @SerializedName("attributes")
    private AttributesEntity attributes;

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public String getSfParentId() {
        return sfParentId;
    }

    public void setSfParentId(String sfParentId) {
        this.sfParentId = sfParentId;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public ParentEntity getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(ParentEntity parentEntity) {
        this.parentEntity = parentEntity;
    }

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }
}
