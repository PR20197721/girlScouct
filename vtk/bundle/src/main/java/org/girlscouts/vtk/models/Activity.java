package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class Activity extends YearPlanComponent implements Serializable {

	public Activity() {
		this.uid = "A" + new java.util.Date().getTime() + "_" + Math.random();
		super.setType(YearPlanComponentType.ACTIVITY);
		this.cost = 0.00;
		this.isEditable = true;
	}

	public Activity(String name, String content, java.util.Date date,
			java.util.Date endDate, String locationName,
			String locationAddress, double cost) {
		this.name = name;
		this.content = content;
		this.date = date;
		this.endDate = endDate;
		this.locationName = locationName;
		this.locationAddress = locationAddress;
		super.setType(YearPlanComponentType.ACTIVITY);
		this.uid = "A" + new java.util.Date().getTime() + "_" + Math.random();
		this.cost = cost;
		this.isEditable = true;
		
	}

	@Field(path = true)	String path;
	@Field	private String name, activityDescription;
	@Field	private int duration, activityNumber;
	@Field	private String materials, steps;
	@Field	private java.util.Date endDate, date;
	@Field	private String content, id, refUid;
	@Field	private String locationName, locationAddress;
	@Field private String locationRef;// depricated
	@Collection java.util.List<Asset> assets;
	@Field(id = true) String uid;
	@Field Double cost;
	@Field Boolean isEditable;
	@Field private String cancelled;
	@Field private String registerUrl;
	@Field private String emlTemplate;
	@Collection java.util.List<SentEmail> sentEmails;
	private boolean isDbUpdate=false;
	@Field String img;
	
	//outdoor info
	@Field Boolean isOutdoor= false, isOutdoorAvailable=false;
	@Field String activityDescription_outdoor, name_outdoor;
	
	@Bean( autoUpdate = false)  
	Attendance attendance;

	
	public String getRegisterUrl() {
		return registerUrl;
	}

	public void setRegisterUrl(String registerUrl) {
		if( ( registerUrl!=null && this.registerUrl!= null && !this.registerUrl.equals(registerUrl) ) || 
				(registerUrl!=null && this.registerUrl==null) )
			isDbUpdate=true;
		
			
		this.registerUrl = registerUrl;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		if( (cancelled!=null && this.cancelled==null ) ||
				this.cancelled!=null && cancelled!=null && !this.cancelled.equals(cancelled))
			isDbUpdate=true;
		this.cancelled = cancelled;
	}

	public String getRefUid() {
		return refUid;
	}

	public void setRefUid(String refUid) {
		if( (this.refUid==null && refUid!=null) ||
				(this.refUid!=null && refUid!=null && !this.refUid.equals(refUid)) )
			isDbUpdate=true;
		this.refUid = refUid;
	}

	public Boolean getIsEditable() {
		
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		if( (this.isEditable==null && isEditable!=null) ||
				(this.isEditable!=null && isEditable!=null && this.isEditable.booleanValue()!=isEditable.booleanValue()) )
		isDbUpdate=true;
		this.isEditable = isEditable;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
	
		if( ( cost!=null && this.cost!=null && this.cost.doubleValue()!= cost.doubleValue() ) ||
				(this.cost==null && cost!=null) )
			isDbUpdate=true;
		
		
		this.cost = cost;
	}

	public String getUid() {
		if (uid == null)
			this.uid = "A" + new java.util.Date().getTime() + "_"
					+ Math.random();
		return uid;
	}

	public void setUid(String uid) {
		if( (this.refUid==null && refUid!=null) ||
				(this.refUid!=null && refUid!=null && !this.refUid.equals(refUid)) )
			isDbUpdate=true;
		this.uid = uid;
	}

	public java.util.List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(java.util.List<Asset> assets) {
		if( (this.assets==null && assets!=null) ||
				(this.assets!=null && assets!=null && !this.assets.equals(assets)) )
			isDbUpdate=true;
		this.assets = assets;
	}
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		if( (this.locationName==null && locationName!=null) ||
				(this.locationName!=null && locationName!=null && !this.locationName.equals(locationName)) )
			isDbUpdate=true;
		this.locationName = locationName;
	}

	public String getLocationAddress() {
		return locationAddress;
	}

	public void setLocationAddress(String locationAddress) {
		if( (this.locationAddress==null && locationAddress!=null) ||
				(this.locationAddress!=null && locationAddress!=null && !this.locationAddress.equals(locationAddress)) )
			isDbUpdate=true;
		this.locationAddress = locationAddress;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public void setLocationRef(String locationRef) {
		if( (this.locationRef==null && locationRef!=null) ||
				(this.locationRef!=null && locationRef!=null && !this.locationRef.equals(locationRef)) )
			isDbUpdate=true;
		this.locationRef = locationRef;
	}

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		if( (this.date==null && date!=null) ||
				(this.date!=null && date!=null && !this.date.equals(date)) )
			isDbUpdate=true;
		this.date = date;
	}

	public java.util.Date getEndDate() {
		return endDate;
	}

	public void setEndDate(java.util.Date endDate) {
		if( (this.endDate==null && endDate!=null) ||
				(this.endDate!=null && endDate!=null && !this.endDate.equals(endDate)) )
			isDbUpdate=true;
		this.endDate = endDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if( (this.content==null && content!=null) ||
				(this.content!=null && content!=null && !this.content.equals(content)) )
			isDbUpdate=true;
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		if( (this.id==null && id!=null) ||
				(this.id!=null && id!=null && !this.id.equals(id)) )
			isDbUpdate=true;
		this.id = id;
	}

	public String getPath() {

		return path;
	}

	public void setPath(String path) {
		if( (this.path==null && path!=null) ||
				(this.path!=null && path!=null && !this.path.equals(path)) )
			isDbUpdate=true;
		this.path = path;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		if( (this.activityDescription==null && activityDescription!=null) ||
				(this.activityDescription!=null && activityDescription!=null && !this.activityDescription.equals(activityDescription)) )
			isDbUpdate=true;
		this.activityDescription = activityDescription;
	}

	public String getMaterials() {
		return materials;
	}

	public void setMaterials(String materials) {
		if( (this.materials==null && materials!=null) ||
				(this.materials!=null && materials!=null && !this.materials.equals(materials)) )
			isDbUpdate=true;
		this.materials = materials;
	}

	public String getSteps() {
		return steps;
	}

	public void setSteps(String steps) {
		if( (this.steps==null && steps!=null) ||
				(this.steps!=null && steps!=null && !this.steps.equals(steps)) )
			isDbUpdate=true;
		this.steps = steps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if( (this.name==null && name!=null) ||
				(this.name!=null && name!=null && !this.name.equals(name)) )
			isDbUpdate=true;
		this.name = name;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		if( this.duration!=duration) 
			isDbUpdate=true;
		this.duration = duration;
	}

	public int getActivityNumber() {
		return activityNumber;
	}

	public void setActivityNumber(int activityNumber) {
		if(activityNumber!= this.activityNumber)
			isDbUpdate=true;
		this.activityNumber = activityNumber;

	}

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}
	
	public java.util.List<SentEmail> getSentEmails() {
		return sentEmails;
	}

	public void setSentEmails(java.util.List<SentEmail> emails) {
		this.sentEmails = emails;
	}
	
	public String getEmlTemplate() {
		return emlTemplate;
	}

	public void setEmlTemplate(String template) {
		this.emlTemplate =  template;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Boolean getIsOutdoor() {
		
		return isOutdoor ==null ? false : isOutdoor;
	}

	public void setIsOutdoor(Boolean isOutdoor) {
		this.isOutdoor = isOutdoor;
	}

	public String getActivityDescription_outdoor() {
		return activityDescription_outdoor;
	}

	public void setActivityDescription_outdoor(String activityDescription_outdoor) {
		this.activityDescription_outdoor = activityDescription_outdoor;
	}

	public Boolean getIsOutdoorAvailable() {
		return isOutdoorAvailable ==null ? false : isOutdoorAvailable;
	}

	public void setIsOutdoorAvailable(Boolean isOutdoorAvailable) {
		this.isOutdoorAvailable = isOutdoorAvailable;
	}

	public String getName_outdoor() {
		return name_outdoor;
	}

	public void setName_outdoor(String name_outdoor) {
		this.name_outdoor = name_outdoor;
	}

	public Attendance getAttendance() {
		return attendance;
	}

	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
	}

	
	
	
	
}
