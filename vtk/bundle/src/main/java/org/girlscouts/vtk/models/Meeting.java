package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;




@Node(jcrMixinTypes="mix:lockable" )
public class Meeting extends YearPlanComponent {

	
	@Field(path=true) String path;
	@Field private String id, name;
	@Field private String  level, blurb, cat;
	@Field private String aidTags,resources, agenda;
	@Field private Integer position;
	
	@Collection private java.util.List <Activity> activities;
	
	
    @Collection private java.util.Map<String, JcrCollectionHoldString> meetingInfo;
	
   


public Integer getPosition() {
		return position;
	}


	public void setPosition(Integer position) {
		this.position = position;
	}


public java.util.Map<String, JcrCollectionHoldString> getMeetingInfo() {
	return meetingInfo;
}


public void setMeetingInfo(java.util.Map<String, JcrCollectionHoldString> meetingInfo) {
	this.meetingInfo = meetingInfo;
}



	public java.util.List<Activity> getActivities() {
		return activities;
	}



	public void setActivities(java.util.List<Activity> activities) {
		this.activities = activities;
	}



	public String getAidTags() {
		return aidTags;
	}



	public void setAidTags(String aidTags) {
		this.aidTags = aidTags;
	}



	public String getResources() {
		return resources;
	}



	public void setResources(String resources) {
		this.resources = resources;
	}



	public String getAgenda() {
		return agenda;
	}



	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}



	public Meeting(){this.path="/meeting";}
	
	

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}

	public String getCat() {
		return cat;
	}

	public void setCat(String cat) {
		this.cat = cat;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
}
