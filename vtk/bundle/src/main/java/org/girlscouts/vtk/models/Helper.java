package org.girlscouts.vtk.models;

public class Helper {

	private long currentDate, nextDate, prevDate;
	private java.util.ArrayList<String> permissions;
	
	
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

}
