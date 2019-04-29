package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class JcrCollectionHoldString extends BaseEntity {

    @SerializedName("str")
	private String str;

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

}
