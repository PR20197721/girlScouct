package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class Activity extends YearPlanComponent{

	public Activity(){
		this.uid= "A"+new java.util.Date().getTime()+ "_"+ Math.random();
		super.setType(YearPlanComponentType.ACTIVITY);
		this.cost=0.00;
		}
	
	public Activity(String name, String content, java.util.Date date, java.util.Date endDate,
			String locationName, String locationAddress, double cost){
		this.name= name;
		this.content= content;
		this.date= date;
		this.endDate= endDate;
		this.locationName= locationName;
		this.locationAddress = locationAddress;
		super.setType(YearPlanComponentType.ACTIVITY);
		this.uid= "A"+new java.util.Date().getTime() + "_"+ Math.random();
		this.cost= cost;
	}
	
	@Field(path=true) String path;
	@Field private String name, activityDescription;
	@Field private int duration, activityNumber;
	@Field private String materials, steps;
	@Field private java.util.Date endDate, date;
	@Field private String content, id;
	
	@Field private String locationName, locationAddress; 
	
	@Field private String locationRef;//depricated
	@Collection java.util.List<Asset> assets;
	@Field(id=true) String uid;
	@Field Double cost;

	
	
	
	

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getUid() {
		if(uid==null)
			this.uid= "A"+new java.util.Date().getTime()+"_"+ Math.random();
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public java.util.List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(java.util.List<Asset> assets) {
		this.assets = assets;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		this.locationAddress = locationAddress;
	}

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
