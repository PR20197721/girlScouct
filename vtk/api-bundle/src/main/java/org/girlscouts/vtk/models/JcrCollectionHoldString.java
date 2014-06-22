package org.girlscouts.vtk.models;


import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class JcrCollectionHoldString {

	public JcrCollectionHoldString(){}
	public JcrCollectionHoldString(String str) {this.str=str;}
	
	@Field private String str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}
	
}
