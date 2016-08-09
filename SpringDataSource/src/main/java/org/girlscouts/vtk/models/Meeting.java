package org.girlscouts.vtk.models;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="Meeting")
public class Meeting extends YearPlanComponent implements Serializable {

	public Meeting() {
		this.path = "/meeting";
		this.position= 0;
	}
	
	@Transient
	String path;
	
	@Column
	private String id, name, level, blurb, cat, aidTags, resources, agenda, meetingPlanType,catTags;
	
	@Column
	private Integer position=0;
	
	@Column private Boolean isAchievement; 
	
	@OneToMany
	private java.util.List<Activity> activities;

	@OneToMany
	private java.util.Map<String, JcrCollectionHoldString> meetingInfo;


	
	
	
	
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
