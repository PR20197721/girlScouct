package org.girlsscout.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class YearPlan {

	
	@Field private String name, desc, id, refId;
	@Field(path=true) String path;
	@Collection private java.util.List <MeetingE> meetingEvents;
	@Collection private java.util.List <Activity> activities;
	@Bean private Cal schedule;
	@Collection private java.util.List <Location> locations;
	
	
	
	
	
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
