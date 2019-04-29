package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class HelperEntity extends BaseEntity{

    @SerializedName("currentDate")
	private long currentDate;
    @SerializedName("nextDate")
    private long nextDate;
    @SerializedName("prevDate")
    private long prevDate;
    @SerializedName("permissions")
	private ArrayList<String> permissions;
    @SerializedName("achievementCurrent")
	private int achievementCurrent;
    @SerializedName("attendanceCurrent")
    private int attendanceCurrent;
    @SerializedName("attendanceTotal")
    private int attendanceTotal;
    @SerializedName("SfTroopAge")
	private String SfTroopAge;

    public long getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(long currentDate) {
        this.currentDate = currentDate;
    }

    public long getNextDate() {
        return nextDate;
    }

    public void setNextDate(long nextDate) {
        this.nextDate = nextDate;
    }

    public long getPrevDate() {
        return prevDate;
    }

    public void setPrevDate(long prevDate) {
        this.prevDate = prevDate;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public int getAchievementCurrent() {
        return achievementCurrent;
    }

    public void setAchievementCurrent(int achievementCurrent) {
        this.achievementCurrent = achievementCurrent;
    }

    public int getAttendanceCurrent() {
        return attendanceCurrent;
    }

    public void setAttendanceCurrent(int attendanceCurrent) {
        this.attendanceCurrent = attendanceCurrent;
    }

    public int getAttendanceTotal() {
        return attendanceTotal;
    }

    public void setAttendanceTotal(int attendanceTotal) {
        this.attendanceTotal = attendanceTotal;
    }

    public String getSfTroopAge() {
        return SfTroopAge;
    }

    public void setSfTroopAge(String sfTroopAge) {
        SfTroopAge = sfTroopAge;
    }
}
