package org.girlsscout.vtk.models.user;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlsscout.vtk.models.YearPlan;

@Node
public class User {

	//@Collection private java.util.List <YearPlan> yearPlans;
	@Field private String id, refId;
	@Field(path=true) String path;
	@Bean YearPlan yearPlan;
	
	
	
	
	
	
	public YearPlan getYearPlan() {
		return yearPlan;
	}
	public void setYearPlan(YearPlan yearPlan) {
		this.yearPlan = yearPlan;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	
	
}
