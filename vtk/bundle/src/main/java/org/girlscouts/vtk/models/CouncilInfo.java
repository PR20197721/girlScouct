package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;

@Node
public class CouncilInfo implements Serializable {

	@Field(path = true)
	String path;
	@Collection
	java.util.List<Milestone> milestones;
	
	public CouncilInfo() {
		
	}
	public CouncilInfo(String path) {
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
