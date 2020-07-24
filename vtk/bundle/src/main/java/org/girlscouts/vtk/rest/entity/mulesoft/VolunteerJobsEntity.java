package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class VolunteerJobsEntity {
    @SerializedName("StartDate")
    private String startDate;
    @SerializedName("Name")
    private String name;
    @SerializedName("JobCode")
    private String jobCode;
    @SerializedName("EndDate")
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobCode() {
        return jobCode;
    }

    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
