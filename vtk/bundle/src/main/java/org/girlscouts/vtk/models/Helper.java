package org.girlscouts.vtk.models;

public class Helper {

	private long currentDate, nextDate, prevDate;
	private java.util.ArrayList<String> permissions;
	private int achievementCurrent=0, attendanceCurrent=0, attendanceTotal=0;
	private String SfTroopAge;
	
	public java.util.ArrayList<String> getPermissions() {
		return permissions;
	}
	public void setPermissions(java.util.ArrayList<String> permissions) {
		this.permissions = permissions;
	}
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
