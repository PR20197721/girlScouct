package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes="mix:lockable" )
public class Roll {


		@Field(path=true) String path;
		@Field(id=true) String id;
		
		public Roll(){
			this.id= "R"+new java.util.Date().getTime()+"_"+ Math.random();
			
		}
	}


