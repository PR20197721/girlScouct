package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParentEntity that = (ParentEntity) o;
        return Objects.equals(sfId, that.sfId) && Objects.equals(councilCode, that.councilCode) && Objects.equals(attributes, that.attributes) && Objects.equals(accountCode, that.accountCode) && Objects.equals(gradeLevel, that.gradeLevel) && Objects.equals(troopName, that.troopName) && Objects.equals(participationCode, that.participationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sfId, councilCode, attributes, accountCode, gradeLevel, troopName, participationCode);
    }

    @Override
    public String toString() {
        return "{Id='" + sfId + '\'' + ", Council_Code__c='" + councilCode + '\'' + ", attributes=" + attributes + ", Account__c='" + accountCode + '\'' + ", Program_Grade_Level__c='" + gradeLevel + '\'' + ", Name='" + troopName + '\'' + ", Participation__c='" + participationCode + '\'' + '}';
    }
}
