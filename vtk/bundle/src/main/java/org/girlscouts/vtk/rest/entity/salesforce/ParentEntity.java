package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class ParentEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("Council_Code__c")
    private int councilCode;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("Account__c")
    private String accountCode;

    @SerializedName("Program_Grade_Level__c")
    private String gradeLevel;

    @SerializedName("Name")
    private String troopName;


    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public int getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(int councilCode) {
        this.councilCode = councilCode;
    }

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }
}
