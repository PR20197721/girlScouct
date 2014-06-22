package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Asset {

	public Asset(){ this.uid = "A"+new java.util.Date().getTime() + "_" + Math.random(); }
	public Asset(String path){ this.path= path; this.uid = "A"+new java.util.Date().getTime() + "_" + Math.random(); }
	
	
	@Field private String type;
	@Field (path=true) private String path;
	@Field (id=true) private String uid;
	
	
	

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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
	
	
	
}
