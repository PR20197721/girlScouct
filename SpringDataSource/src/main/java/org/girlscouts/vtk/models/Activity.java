package org.girlscouts.vtk.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "Activity")
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

	@Transient String path;
	@Column	private String name, activityDescription;
	@Column	private int duration, activityNumber;
	@Column	private String materials, steps;
	@Column	private java.util.Date endDate, date;
	@Column	private String content, id, refUid;
	@Column	private String locationName, locationAddress;
	@Column private String locationRef;// depricated

	//@OneToMany
	@Transient
	java.util.List<Asset> assets;
	
	@Column @Id String uid;
	@Column Double cost;
	@Column Boolean isEditable;
	@Column private String cancelled;
	@Column private String registerUrl;
	@Column private String emlTemplate;
	
	//@OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.yearplan", cascade=CascadeType.ALL)
	@Transient
	java.util.List<SentEmail> sentEmails;
	
	private boolean isDbUpdate=false;
	@Column String img;
	
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
}
