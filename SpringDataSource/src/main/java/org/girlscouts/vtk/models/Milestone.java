package org.girlscouts.vtk.models;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.Type;


@Entity
@Table( name="Milestone")
/*
@AssociationOverrides({
	@AssociationOverride(name = "milestone", 
		joinColumns = @JoinColumn(name = "uid"))
   }) 
   */
public class Milestone extends YearPlanComponent implements Serializable {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "council_uid", nullable = false)
	private Council council;
	

	@Transient
	String path;
	
	@Column
	private String blurb;
	
	@Column
	@Type(type = "org.hibernate.type.NumericBooleanType")
	boolean isShow;
	
	@Column
	private java.util.Date date;
	
	@Id @Column
	String uid;

	public Milestone() {
		this.uid = "M" + new java.util.Date().getTime() + "_" + Math.random();
		super.setType(YearPlanComponentType.MILESTONE);
	}
	public Milestone(String blurb, boolean show, java.util.Date date) {
		this.uid = "M" + new java.util.Date().getTime() + "_" + Math.random();
		super.setType(YearPlanComponentType.MILESTONE);
		this.blurb=blurb;
		this.isShow = new Boolean(show);
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
		return isShow;
	}

	public void setShow(Boolean showInPlans) {
		this.isShow = showInPlans;
	}
	public Council getCouncil() {
		return council;
	}
	public void setCouncil(Council council) {
		this.council = council;
	}
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}

}
