package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Council {

	public Council(String path){ 
		this.path= path;
		/*
		for(int i=0;i<10000;i++){
			
			if( troops==null ) troops= new java.util.ArrayList<Troop>();
			Troop troop= new Troop();
			troop.setsFTroopId("test");
			troop.setsFTroopName("testName");
			troop.setUid("CACA"+ new java.util.Date() );
			//troop.setId("T"+ new java.util.Date().getTime()+"_"+ Math.random() );
			troops.add( troop );
		}
		*/
	}
	
	@Field(path=true) String path;
	//@Collection	private java.util.List <Milestone> milestones;
	
	
	@Collection private java.util.List <Troop> troops;
	
	
	
	

	public java.util.List<Troop> getTroops() {
		return troops;
	}

	public void setTroops(java.util.List<Troop> troops) {
		this.troops = troops;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/*
	public java.util.List<Milestone> getMilestones() {
		return milestones;
	}

	public void setMilestones(java.util.List<Milestone> milestones) {
		this.milestones = milestones;
	}
	*/
	
	
}
