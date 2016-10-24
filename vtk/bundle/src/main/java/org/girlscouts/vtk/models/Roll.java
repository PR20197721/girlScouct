package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class Roll implements Serializable {

	@Field(path = true)
	String path;
	@Field(id = true)
	String id;

	public Roll() {
		this.id = "R" + new java.util.Date().getTime() + "_" + Math.random();

	}
}
