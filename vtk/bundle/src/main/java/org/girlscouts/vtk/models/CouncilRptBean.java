package org.girlscouts.vtk.models;

import java.util.List;

public class CouncilRptBean {

    private String yearPlanName, libPath, ageGroup, yearPlanPath, troopId, troopName;
    private boolean isAltered, isActivity;
    private List<Contact> troopLeaders;


    public List<Contact> getTroopLeaders() {
        return troopLeaders;
    }

    public void setTroopLeaders(List<Contact> troopLeaders) {
        this.troopLeaders = troopLeaders;
    }

    public String getTroopName() {
        return troopName;
    }

    public void setTroopName(String troopName) {
        this.troopName = troopName;
    }

    public String getYearPlanName() {
        return yearPlanName;
    }

    public void setYearPlanName(String yearPlanName) {
        this.yearPlanName = yearPlanName;
    }

    public String getLibPath() {
        return libPath;
    }

    public void setLibPath(String libPath) {
        this.libPath = libPath;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public boolean isAltered() {
        return isAltered;
    }

    public void setAltered(boolean isAltered) {
        this.isAltered = isAltered;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean isActivity) {
        this.isActivity = isActivity;
    }

    public String getYearPlanPath() {
        return yearPlanPath;
    }

    public void setYearPlanPath(String yearPlanPath) {
        this.yearPlanPath = yearPlanPath;
    }

    public String getTroopId() {
        return troopId;
    }

    public void setTroopId(String troopId) {
        this.troopId = troopId;
    }

}
