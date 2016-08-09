package org.girlscouts.vtk.models;

import java.util.List;

public class PlanView {

	private MeetingE meeting;
	private List<Asset> aidTags;
	private java.util.Date searchDate;
	//meeting length in minutes
	private int meetingLength;
	private long prevDate, nextDate;
	private int currInd, meetingCount;
	private YearPlanComponent yearPlanComponent;

	public MeetingE getMeeting() {
		return meeting;
	}

	public void setMeeting(MeetingE meeting) {
		this.meeting = meeting;
	}

	public List<Asset> getAidTags() {
		return aidTags;
	}

	public void setAidTags(List<Asset> aidTags) {
		this.aidTags = aidTags;
	}

	public java.util.Date getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(java.util.Date searchDate) {
		this.searchDate = searchDate;
	}

	public long getPrevDate() {
		return prevDate;
	}

	public void setPrevDate(long prevDate) {
		this.prevDate = prevDate;
	}

	public long getNextDate() {
		return nextDate;
	}

	public void setNextDate(long nextDate) {
		this.nextDate = nextDate;
	}

	public int getCurrInd() {
		return currInd;
	}

	public void setCurrInd(int currInd) {
		this.currInd = currInd;
	}

	public int getMeetingCount() {
		return meetingCount;
	}

	public void setMeetingCount(int meetingCount) {
		this.meetingCount = meetingCount;
	}

	public YearPlanComponent getYearPlanComponent() {
		return yearPlanComponent;
	}

	public void setYearPlanComponent(YearPlanComponent yearPlanComponent) {
		this.yearPlanComponent = yearPlanComponent;
	}
	//meeting length in minutes
	public int getMeetingLength() {
		return meetingLength;
	}

	public void setMeetingLength(int meetingLength) {
		this.meetingLength = meetingLength;
	}


}
