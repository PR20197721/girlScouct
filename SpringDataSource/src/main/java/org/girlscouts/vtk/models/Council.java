package org.girlscouts.vtk.models;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="Council")
public class Council implements Serializable {

	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int uid;
	
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}

	@Transient
	String path;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "council", cascade=CascadeType.ALL)
	java.util.List<Milestone> milestones;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "council", cascade=CascadeType.ALL)
	java.util.List<Troop> troops;
	
	public Council() {
		
	}
	public Council(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public java.util.List<Milestone> getMilestones() {
		return milestones;
	}

	public void setMilestones(java.util.List<Milestone> milestones) {
		this.milestones = milestones;
	}
}
