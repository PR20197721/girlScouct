package org.girlscouts.vtk.models;

import java.io.Serializable;

//@Node
public class JcrCollectionHoldString implements Comparable, Serializable {

	public JcrCollectionHoldString() {
	}

	public JcrCollectionHoldString(String str) {
		this.str = str;
	}

//	@Field
	private String str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
