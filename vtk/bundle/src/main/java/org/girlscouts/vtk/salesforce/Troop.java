package org.girlscouts.vtk.salesforce;

import java.io.Serializable;
import java.util.Set;

public class Troop implements Serializable {

	/**
	 * 
	 * type =0 stand type =1 no troops, just get cauncil id
	 */

	public Troop() {
		this.type = 0;
	}

	private String troopId, troopName, gradeLevel, councilId;

	private int councilCode, type;
	private Set<Integer> permissionTokens;
	private String role; //userRole from Salesforce Job_Code__c
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<Integer> getPermissionTokens() {
		return permissionTokens;
	}

	public void setPermissionTokens(Set<Integer> permissionTokens) {
		this.permissionTokens = permissionTokens;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTroopId() {
		return troopId;
	}

	public void setTroopId(String troopId) {
		this.troopId = troopId;
	}

	public String getTroopName() {
		return troopName;
	}

	public void setTroopName(String troopName) {
		this.troopName = troopName;
	}

	public String getGradeLevel() {
		return gradeLevel;
	}

	public void setGradeLevel(String gradeLevel) {
		this.gradeLevel = gradeLevel;
	}

	public int getCouncilCode() {
		return councilCode;
	}

	public void setCouncilCode(int councilCode) {
		this.councilCode = councilCode;
	}

	public String getCouncilId() {
		return councilId;
	}

	public void setCouncilId(String councilId) {
		this.councilId = councilId;
	}

}
