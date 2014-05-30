package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.girlsscout.vtk.dao.YearPlanComponentType;

public class YearPlanComponent {

	private java.util.Date date;
	private  YearPlanComponentType type;

	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public YearPlanComponentType getType() {
		return type;
	}

	public void setType(YearPlanComponentType type) {
		this.type = type;
	}
	
	
	
}
