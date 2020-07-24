package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class AffiliationsEntity {
    @SerializedName("Type")
    private String type;
    @SerializedName("StartDate")
    private String startDate;
    @SerializedName("ProgramGradeLevel")
    private String programGradeLevel;
    @SerializedName("Name")
    private String name;
    @SerializedName("EndDate")
    private String endDate;
    @SerializedName("CouncilCode")
    private String councilCode;
    @SerializedName("AccountId")
    private String accountId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getProgramGradeLevel() {
        return programGradeLevel;
    }

    public void setProgramGradeLevel(String programGradeLevel) {
        this.programGradeLevel = programGradeLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
