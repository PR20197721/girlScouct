package org.girlscouts.vtk.auth.permission;

public enum RollType {
	/*
	 * 
	 * 
		DS (Direct Secondary)
		DO (Direct Occasional)
		IP (Indirect Primary)
		IS (Indirect Secondary)
	 */
	
	DP("DP"), CouncilAdmin("CA"), PA("PA"),
	DS ("DS"),
	DO ("DO"),
	IP ("IP"),
	IS ("IS");

	String value = null;

	RollType(String x) {
		value = x;
	}

	public String getRollType() {
		return this.value;
	}
}
