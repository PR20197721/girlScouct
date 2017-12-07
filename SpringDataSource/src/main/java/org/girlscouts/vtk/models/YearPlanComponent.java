package org.girlscouts.vtk.models;

import java.io.Serializable;


public class YearPlanComponent implements Serializable {

	private java.util.Date date;
	private YearPlanComponentType type;
	private String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

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
