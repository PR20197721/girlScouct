package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Council implements Serializable {
	public Council() {
	}

	public Council(String path) {
		this.path = path;
	}

	@Field(path = true)
	String path;
/*
	@Collection
	private java.util.List<Troop> troops;
	
	public java.util.List<Troop> getTroops() {
		return troops;
	}

	public void setTroops(java.util.List<Troop> troops) {
		this.troops = troops;
	}
	*/
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
