package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Troop {

	@Field(path=true) String path;
	@Field String uid, sFTroopId, sFTroopName;
	@Bean YearPlan yearPlan;
	@Field(id=true) String id;

	
	public Troop(){
		this.id= "T"+new java.util.Date().getTime()+"_"+ Math.random();
		
		yearPlan = new YearPlan();
		yearPlan.setDesc("teststst");
	}
	
	public YearPlan getYearPlan() {
		return yearPlan;
	}

	public void setYearPlan(YearPlan yearPlan) {
		this.yearPlan = yearPlan;
	}

	
	
	public String getId() {
		if(id==null)
			this.id= "T"+new java.util.Date().getTime()+"_"+ Math.random();
		return id;
	}


	public void setId(String id) {
		if( this.id==null)
			this.id= "T"+new java.util.Date().getTime()+"_"+ Math.random();
		this.id = id;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getUid() {
		return uid;
	}


	public void setUid(String uid) {
		this.uid = uid;
	}


	public String getsFTroopId() {
		return sFTroopId;
	}


	public void setsFTroopId(String sFTroopId) {
		this.sFTroopId = sFTroopId;
	}


	public String getsFTroopName() {
		return sFTroopName;
	}


	public void setsFTroopName(String sFTroopName) {
		this.sFTroopName = sFTroopName;
	}
	
	
}
