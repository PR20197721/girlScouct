package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class Milestone extends YearPlanComponent implements Serializable {

	@Field(path = true)
	String path;
	@Field
	private String blurb;
	@Field
	Boolean show;
	@Field
	private java.util.Date date;
	@Field(id = true)
	String uid;

	public Milestone() {
		this.uid = "M" + new java.util.Date().getTime() + "_" + Math.random();
		super.setType(YearPlanComponentType.MILESTONE);
	}
	public Milestone(String blurb, boolean show, java.util.Date date) {
		this.uid = "M" + new java.util.Date().getTime() + "_" + Math.random();
		super.setType(YearPlanComponentType.MILESTONE);
		this.blurb=blurb;
		this.show = new Boolean(show);
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getBlurb() {
		return blurb;
	}

	public void setBlurb(String blurb) {
		this.blurb = blurb;
	}


	public java.util.Date getDate() {
		return date;
	}

	public void setDate(java.util.Date date) {
		this.date = date;
	}

	public String getUid() {
		if (uid == null)
			this.uid = "M" + new java.util.Date().getTime() + "_"
					+ Math.random();
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public Boolean getShow() {
		return show;
	}

	public void setShow(Boolean showInPlans) {
		this.show = showInPlans;
	}

}
