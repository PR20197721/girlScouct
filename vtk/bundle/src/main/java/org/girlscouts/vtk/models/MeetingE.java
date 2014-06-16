package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class MeetingE extends YearPlanComponent{

	public MeetingE(){super.setType(YearPlanComponentType.MEETING);}
	
	@Field(path=true) String path;
	@Field private String refId; //path to meetingInfo template

	@Field private String locationRef;
	private Meeting meetingInfo; 
	
	@Field private String cancelled;
	
	@Field (id=true)
    private Integer id;
	
	 
	
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public void setLocationRef(String locationRef) {
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
		this.refId = refId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	
}
