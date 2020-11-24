package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TroopEntity {

    @SerializedName("volunteerJobs")
    private List<VolunteerJobsEntity> volunteerJobs;
    @SerializedName("parentAccount")
    private ParentAccountEntity parentAccount;
    @SerializedName("ProgramGradeLevel")
    private String programGradeLevel;
    @SerializedName("Name")
    private String name;
    @SerializedName("Id")
    private String id;
    @SerializedName("CouncilCode")
    private String councilCode;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    public List<VolunteerJobsEntity> getVolunteerJobs() {
        return volunteerJobs;
    }

    public void setVolunteerJobs(List<VolunteerJobsEntity> volunteerJobs) {
        this.volunteerJobs = volunteerJobs;
    }

    public ParentAccountEntity getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(ParentAccountEntity parentAccount) {
        this.parentAccount = parentAccount;
    }

    @Override
    public String toString() {
        return "TroopEntity{" + "volunteerJobs=" + volunteerJobs + ", parentAccount=" + parentAccount + ", programGradeLevel='" + programGradeLevel + '\'' + ", name='" + name + '\'' + ", id='" + id + '\'' + ", councilCode='" + councilCode + '\'' + '}';
    }
}
