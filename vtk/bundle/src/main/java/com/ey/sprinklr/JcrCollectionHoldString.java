package com.ey.sprinklr;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node
public class JcrCollectionHoldString implements Comparable, Serializable {

	public JcrCollectionHoldString() {
	}

	public JcrCollectionHoldString(String str) {
		this.str = str;
	}

	@Field
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
