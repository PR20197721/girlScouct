package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Meeting extends YearPlanComponent implements Serializable {

	public Meeting() {
		this.path = "/meeting";
		this.position= 0;
	}
	
	@Field(path = true)
	String path;
	@Field
	private String id, name;
	@Field
	private String level, blurb, cat;
	@Field
	private String aidTags, resources, agenda;
	@Field
	private Integer position=0;
	@Field private Boolean isAchievement; 

	
	@Collection
	private java.util.List<Activity> activities;

	@Collection
	private java.util.Map<String, JcrCollectionHoldString> meetingInfo;

	
	@Field
	private String meetingPlanType;
	
	
	@Field
	private String catTags;
	
	
	
	
	public Integer getPosition() {
		return position ==null ? 0 : position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public java.util.Map<String, JcrCollectionHoldString> getMeetingInfo() {
		return meetingInfo;
	}

	public void setMeetingInfo(
			java.util.Map<String, JcrCollectionHoldString> meetingInfo) {
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

	public Boolean getIsAchievement() {
		return isAchievement;
	}

	public void setIsAchievement(Boolean isAchievement) {
		this.isAchievement = isAchievement;
	}

	

	public String getMeetingPlanType() {
		return meetingPlanType;
	}

	public void setMeetingPlanType(String meetingPlanType) {
		this.meetingPlanType = meetingPlanType;
	}

	public String getCatTags() {
		return catTags;
	}

	public void setCatTags(String catTags) {
		this.catTags = catTags;
	}

	

	
	

	

	





	
	

	
	



	

	

	

	

	

	

	

	

	
	
}
