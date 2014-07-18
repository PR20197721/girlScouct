package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class UserGlobConfig {

	
	@Field (path=true) private String path;
	@Field (id=true) private String uid;
	
	
	//on sched ; separ by '|'
	@Field private String vacationDates;
	
	
	
		
	
	
	public UserGlobConfig(){
		this.path= "/vtk/global-settings";
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



	public String getVacationDates() {
		return vacationDates;
	}

	public void setVacationDates(String vacationDates) {
		this.vacationDates = vacationDates;
	}
	
	
	
	
	
}
