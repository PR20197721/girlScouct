package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlsscout.vtk.dao.YearPlanComponentType;

@Node
public class Activity extends YearPlanComponent{

	public Activity(){super.setType(YearPlanComponentType.ACTIVITY);}
	
	public Activity(String name, String content, java.util.Date date, java.util.Date endDate){
		this.name= name;
		this.content= content;
		this.date= date;
		this.endDate= endDate;
		super.setType(YearPlanComponentType.ACTIVITY);
	}
	
	@Field(path=true) String path;
	@Field private String name, activityDescription;
	@Field private int duration, activityNumber;
	@Field private String materials, steps;
	
	@Field private java.util.Date endDate, date;
	@Field private String content, id;
	
	//@Field private String 
	
	@Field private String locationRef;//depricated
	
	
	public String getLocationRef() {
		return locationRef;
	}
	public void setLocationRef(String locationRef) {
		this.locationRef = locationRef;
	}
	public java.util.Date getDate() {
		return date;
	}
	public void setDate(java.util.Date date) {
		this.date = date;
	}
	public java.util.Date getEndDate() {
		return endDate;
	}
	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPath() {
		
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getActivityDescription() {
		return activityDescription;
	}
	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}
	public String getMaterials() {
		return materials;
	}
	public void setMaterials(String materials) {
		this.materials = materials;
	}
	public String getSteps() {
		return steps;
	}
	public void setSteps(String steps) {
		this.steps = steps;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getActivityNumber() {
		return activityNumber;
	}
	public void setActivityNumber(int activityNumber) {
		this.activityNumber = activityNumber;
		/*
		if( path==null )
			path="/myActivity/"+activityNumber;
	
	//System.err.println("Activity path: "+ path);
	*/
	}
	
	
}
