package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class ParentEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("Council_Code__c")
    private String councilCode;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("Account__c")
    private String accountCode;

    @SerializedName("Program_Grade_Level__c")
    private String gradeLevel;

    @SerializedName("Name")
    private String troopName;

    @SerializedName("Participation__c")
    private String participationCode;

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getGradeLevel() {
        return gradeLevel;
    }

    public void setGradeLevel(String gradeLevel) {
        this.gradeLevel = gradeLevel;
    }

    public String getTroopName() {
        return troopName;
    }

    public void setTroopName(String troopName) {
        this.troopName = troopName;
    }

    public String getParticipationCode() {
        return participationCode;
    }

    public void setParticipationCode(String participationCode) {
        this.participationCode = participationCode;
    }
}
