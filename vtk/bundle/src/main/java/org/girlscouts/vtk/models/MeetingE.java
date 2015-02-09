package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node(jcrMixinTypes = "mix:lockable")
public class MeetingE extends YearPlanComponent implements Serializable {

	public MeetingE() {
		this.uid = "M" + new java.util.Date().getTime();
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

	@Collection
	java.util.List<Asset> assets;
	@Field
	java.util.Date lastAssetUpdate;

	
	
	
	public java.util.Date getLastAssetUpdate() {
		return lastAssetUpdate;
	}

	public void setLastAssetUpdate(java.util.Date lastAssetUpdate) {
		this.lastAssetUpdate = lastAssetUpdate;
		isUpdated=true;
	}

	public java.util.List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(java.util.List<Asset> assets) {
		this.assets = assets;
		isUpdated=true;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
		if (uid == null)
			this.uid = "M" + new java.util.Date().getTime() + "_"
					+ Math.random();
		isUpdated=true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		isUpdated=true;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
		isUpdated=true;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public void setLocationRef(String locationRef) {
		this.locationRef = locationRef;
		isUpdated=true;
	}

	public Meeting getMeetingInfo() {
		return meetingInfo;
	}

	public void setMeetingInfo(Meeting meetingInfo) {
		this.meetingInfo = meetingInfo;
		isUpdated=true;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
		isUpdated=true;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		isUpdated=true;
	}

	
	
	private boolean isUpdated;

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
}
