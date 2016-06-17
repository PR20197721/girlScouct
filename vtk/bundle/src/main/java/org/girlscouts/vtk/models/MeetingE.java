package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class MeetingE extends YearPlanComponent implements Serializable {
	// This class wraps the web meeting object to implement VTK meeting structure

	public MeetingE() {
		this.uid = "M" + new java.util.Date().getTime(); // better to be impossible than unlikely
		super.setType(YearPlanComponentType.MEETING);
	}

	@Field(path = true)
	String path;
	
	@Field
	private String refId; // path to meetingInfo template

	@Field
	private String locationRef;
	private Meeting meetingInfo;

	@Field
	private String cancelled;

	@Field
	private Integer id;

	@Field(id = true)
	String uid;
	
	@Field
	private String emlTemplate;

	@Collection ( autoUpdate = false) 
	java.util.List<Asset> assets;
	
	@Collection
	java.util.List<SentEmail> sentEmails;
	
	@Field
	java.util.Date lastAssetUpdate;
	
	
	@Bean( autoUpdate = false)  
	Attendance attendance;
	@Bean( autoUpdate = false) 
	Achievement achievement;
	
	//@Bean( autoUpdate = false)  
	@Collection java.util.List<Note> notes;
	
    private boolean isDbUpdate=false;
	public java.util.Date getLastAssetUpdate() {
		return lastAssetUpdate;
	}

	public void setLastAssetUpdate(java.util.Date lastAssetUpdate) {
		if( (lastAssetUpdate !=null && this.lastAssetUpdate!=null && !this.lastAssetUpdate.equals(lastAssetUpdate)  )	||
				(lastAssetUpdate!=null && this.lastAssetUpdate==null) )
			isDbUpdate=true;
		this.lastAssetUpdate = lastAssetUpdate;
	
	}

	public java.util.List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(java.util.List<Asset> assets) {
		if( (assets !=null && this.assets!=null && !this.assets.equals(assets)  )	||
				(assets!=null && this.assets==null) )
			isDbUpdate=true;
		this.assets = assets;
		
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
		if (uid == null)
			this.uid = "M" + new java.util.Date().getTime() + "_"
					+ Math.random();
		
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		if( (id !=null && this.id!=null && !this.id.equals(id)  )	||
				(id!=null && this.id==null) )
			isDbUpdate=true;
		this.id = id;
		
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		if( (cancelled !=null && this.cancelled!=null && !this.cancelled.equals(cancelled)  )	||
				(cancelled!=null && this.cancelled==null) )
			isDbUpdate=true;
		this.cancelled = cancelled;
		
	}

	public String getLocationRef() {
		return locationRef;
	}

	public void setLocationRef(String locationRef) {
		if( (locationRef !=null && this.locationRef!=null && !this.locationRef.equals(locationRef)  )	||
				(locationRef!=null && this.locationRef==null) )
			isDbUpdate=true;
		
		this.locationRef = locationRef;
		
	}

	public Meeting getMeetingInfo() {
		return meetingInfo;
	}

	public void setMeetingInfo(Meeting meetingInfo) {
		
		this.meetingInfo = meetingInfo;
		
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		if( (refId !=null && this.refId!=null && !this.refId.equals(refId)  )	||
				(refId!=null && this.refId==null) )
			isDbUpdate=true;
		this.refId = refId;
		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		
		if( (path !=null && this.path!=null && !this.path.equals(path)  )	||
				(path!=null && this.path==null) )
			isDbUpdate=true;
		
		this.path = path;
		
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
	
	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}

	public Attendance getAttendance() {
		return attendance;
	}

	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
	}

	public Achievement getAchievement() {
		return achievement;
	}

	public void setAchievement(Achievement achievement) {
		this.achievement = achievement;
	}

	public java.util.List<Note> getNotes() {
		return notes;
	}

	public void setNotes(java.util.List<Note> notes) {
		this.notes = notes;
	}


	
	
	
	
}
