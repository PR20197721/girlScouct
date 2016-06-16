package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.dao.YearPlanComponentType;

@Node
public class Note implements Serializable{

	    @Field private String message;
		@Field(path = true) String path;		
		@Field(id = true) String uid;

		public Note() {
			this.uid = "N" + new java.util.Date().getTime() + "_" + Math.random();
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	
}
