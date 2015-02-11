package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes = "mix:lockable")
public class YearPlan implements Serializable {

	public YearPlan() {

		/*
		 * java.util.List <Milestone> milestones = new java.util.ArrayList();
		 * 
		 * Milestone m= new Milestone(); m.setBlurb("Cookie Sales Start");
		 * m.setDate( new java.util.Date("12/07/2014") ); milestones.add(m);
		 * 
		 * 
		 * m= new Milestone(); m.setBlurb("Troops re-register"); m.setDate( new
		 * java.util.Date("09/30/2014") ); milestones.add(m);
		 * 
		 * 
		 * 
		 * m= new Milestone(); m.setBlurb("Cookie Sales End"); m.setDate( new
		 * java.util.Date("03/29/2015") ); milestones.add(m);
		 * 
		 * 
		 * this.milestones= milestones;
		 */

		calFreq = "biweekly";
	}

	@Field
	private String name, desc, id, refId, altered, resources;
	@Field(path = true)
	String path;
	@Collection
	private java.util.List<MeetingE> meetingEvents;
	@Collection
	private java.util.List<Activity> activities;
	@Bean
	private Cal schedule;
	@Collection
	private java.util.List<Location> locations;

	// cal settings cache
	@Field
	private Long calStartDate;
	@Field
	private String calFreq, calExclWeeksOf;

	/* @Collection */private java.util.List<Milestone> milestones;

	public String getResources() {
		return resources;
	}

	public void setResources(String resources) {
		this.resources = resources;
	}

	public java.util.List<Milestone> getMilestones() {
		return milestones;
	}

	public void setMilestones(java.util.List<Milestone> milestones) {
		// -this.milestones = milestones;
		/*
		 * milestones = new java.util.ArrayList();
		 * 
		 * Milestone m= new Milestone(); m.setBlurb("Cookie Sales Start");
		 * m.setDate( new java.util.Date("12/07/2014") ); milestones.add(m);
		 * 
		 * 
		 * m= new Milestone(); m.setBlurb("Troops re-register"); m.setDate( new
		 * java.util.Date("09/30/2014") ); milestones.add(m);
		 * 
		 * 
		 * 
		 * m= new Milestone(); m.setBlurb("Cookie Sales End"); m.setDate( new
		 * java.util.Date("03/29/2015") ); milestones.add(m);
		 */
		this.milestones = milestones;

		calFreq = "biweekly"; // todo rm

	}

	public String getAltered() {
		return altered;
	}

	public void setAltered(String altered) {
		this.altered = altered;
	}

	public Long getCalStartDate() {
		return calStartDate;
	}

	public void setCalStartDate(Long calStartDate) {
		this.calStartDate = calStartDate;
	}

	public String getCalFreq() {
		return calFreq;
	}

	public void setCalFreq(String calFreq) {
		this.calFreq = calFreq;
	}

	public String getCalExclWeeksOf() {
		return calExclWeeksOf;
	}

	public void setCalExclWeeksOf(String calExclWeeksOf) {
		this.calExclWeeksOf = calExclWeeksOf;
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
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
