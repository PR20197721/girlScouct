package org.girlscouts.vtk.models;

import java.io.Serializable;

import javax.persistence.*;


@Entity
@Table(name="MeetingE")
public class MeetingE extends YearPlanComponent implements Serializable {
	// This class wraps the web meeting object to implement VTK meeting structure

	public MeetingE() {
		this.uid = "M" + new java.util.Date().getTime(); // better to be impossible than unlikely
		super.setType(YearPlanComponentType.MEETING);
	}

	@Transient
	String path;
	
	@Column
	private String refId; // path to meetingInfo template

	@Column
	private String locationRef;
	private Meeting meetingInfo;

	@Column
	private String cancelled;

	@Column
	private Integer id;

	@Column @Id
	String uid;
	
	@Column
	private String emlTemplate;

	//-@OneToMany
	@Transient
	java.util.List<Asset> assets;
	
	//-@OneToMany
	@Transient
	java.util.List<SentEmail> sentEmails;
	
	@Column
	java.util.Date lastAssetUpdate;
	
	
	//-@OneToMany //( autoUpdate = false)  
	@Transient
	Attendance attendance;
	
	//-@OneToMany //( autoUpdate = false) 
	@Transient
	Achievement achievement;
	
	//@Bean( autoUpdate = false)  
	//-@OneToMany /*(autoRetrieve =true, autoInsert =true) */ 
	@Transient
	java.util.List<Note> notes;
	
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
