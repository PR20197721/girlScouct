package org.girlscouts.vtk.auth.permission;

public enum RollType {
	DP("DP");

	String value = null;

	RollType(String x) {
		value = x;
	}

	public String getRollType() {
		return this.value;
	}
}
