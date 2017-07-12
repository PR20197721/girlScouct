package org.girlscouts.vtk.models;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="YearPlan")
public class YearPlan implements Serializable {

	public YearPlan() {
		calFreq = "biweekly";
	}

	@Id @Column
	private String id;
	
	@Column
	private String name, description, refId, altered, resources;
	@Transient String path;
	

	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "yearplan", cascade=CascadeType.ALL)
	@Transient //@OneToMany //Collection(autoUpdate = false)
	private java.util.List<MeetingE> meetingEvents;
	
	@Transient //@OneToMany //Collection(autoUpdate = false)
	private java.util.List<Activity> activities;
	
	@Transient //@OneToMany //Bean(autoUpdate = false)
	private Cal schedule;
	
	@Transient //@OneToMany //Collection(autoUpdate = false)
	private java.util.List<Location> locations;
	
	@Column
	private Long calStartDate;
	
	@Column
	private String calFreq, calExclWeeksOf;
	
	@Transient
	private boolean isDbUpdate = false;
	
	@Transient 
	private java.util.List<Milestone> milestones;
	
	@Transient //@OneToMany //Collection(autoUpdate = false)
	private java.util.List<MeetingCanceled> meetingCanceled;
	
	@Transient 
	private Helper helper;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "troop_uid", nullable = false)
	private Troop troop;
	
	public Helper getHelper() {
		return helper;
	}

	public void setHelper(Helper helper) {
		this.helper = helper;
	}

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
		isDbUpdate = true;

	}

	public java.util.List<Milestone> getMilestones() {
		return milestones;
	}

	public void setMilestones(java.util.List<Milestone> milestones) {
		this.milestones = milestones;
	}

	public String getAltered() {
		return altered;
	}

	public void setAltered(String altered) {

		if (altered != null && this.altered != null
				&& !this.altered.equals(altered)) {
			isDbUpdate = true;

		}

		this.altered = altered;

	}

	public Long getCalStartDate() {
		return calStartDate;
	}

	public void setCalStartDate(Long calStartDate) {
		this.calStartDate = calStartDate;
		isDbUpdate = true;

	}

	public String getCalFreq() {
		return calFreq;
	}

	public void setCalFreq(String calFreq) {
		this.calFreq = calFreq;
		isDbUpdate = true;

	}

	public String getCalExclWeeksOf() {
		return calExclWeeksOf;
	}

	public void setCalExclWeeksOf(String calExclWeeksOf) {
		this.calExclWeeksOf = calExclWeeksOf;
		isDbUpdate = true;

	}

	public Cal getSchedule() {
		return schedule;
	}

	public void setSchedule(Cal schedule) {
		this.schedule = schedule;
	}

	public java.util.List<Location> getLocations() {
		return locations;
	}

	public void setLocations(java.util.List<Location> locations) {
		this.locations = locations;
	}

	public java.util.List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(java.util.List<Activity> activities) {
		this.activities = activities;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
		isDbUpdate = true;

	}

	public java.util.List<MeetingE> getMeetingEvents() {
		return meetingEvents;
	}

	public void setMeetingEvents(java.util.List<MeetingE> meetingEvents) {
		this.meetingEvents = meetingEvents;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if (this.path != null && path != null && !path.equals(this.path)) {
			isDbUpdate = true;

		}
		this.path = path;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		isDbUpdate = true;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		isDbUpdate = true;

	}



	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}

	public java.util.List<MeetingCanceled> getMeetingCanceled() {
		return meetingCanceled;
	}

	public void setMeetingCanceled(
			java.util.List<MeetingCanceled> meetingCanceled) {
		this.meetingCanceled = meetingCanceled;
	}

	public Troop getTroop() {
		return troop;
	}

	public void setTroop(Troop troop) {
		this.troop = troop;
	}

}
