package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
@Node
public class User {

	@Field(path=true) String path;
	@Field(id=true) String id;

	
	public User(){
		this.id= "U"+new java.util.Date().getTime()+"_"+ Math.random();
		
	}
}
