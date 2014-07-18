package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.AssetComponentType;

@Node
public class Asset {

	public Asset(){ this.uid = "A"+new java.util.Date().getTime() + "_" + Math.random(); this.isCachable=false;}
	public Asset(String path){ this.path= path; this.uid = "A"+new java.util.Date().getTime() + "_" + Math.random(); this.isCachable=false;}
	
	
	@Field private String type, refId, description, title;
	


	@Field (path=true) private String path;
	@Field (id=true) private String uid;
	@Field Boolean isCachable;
	
	
	

	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getIsCachable() {
		return isCachable;
	}
	public void setIsCachable(Boolean isCachable) {
		this.isCachable = isCachable;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	
	
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public AssetComponentType getType(boolean nothing){ return AssetComponentType.valueOf(this.getType());}
	public void setType(AssetComponentType act){ setType( act.toString() );}
	
	
	
	
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	
}
