package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class JcrNode implements Serializable {

	public JcrNode(String path) {
		this.path = path;
	}

	@Field(path = true)
	String path;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
