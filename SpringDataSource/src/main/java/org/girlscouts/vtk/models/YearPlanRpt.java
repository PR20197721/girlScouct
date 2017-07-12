package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.commons.collections4.Predicate;

public class YearPlanRpt implements Serializable,  Predicate {

	String council, troop, troopAge, troopName;

	public String getCouncil() {
		return council;
	}

	public void setCouncil(String council) {
		this.council = council;
	}

	public String getTroop() {
		return troop;
	}

	public void setTroop(String troop) {
		this.troop = troop;
	}

	public String getTroopAge() {
		return troopAge;
	}

	public void setTroopAge(String troopAge) {
		this.troopAge = troopAge;
	}

	public String getTroopName() {
		return troopName;
	}

	public void setTroopName(String troopName) {
		this.troopName = troopName;
	}

	public boolean evaluate(Object arg0) {
		return ((YearPlanRpt)arg0).getTroopAge().equals( this.getTroopAge() );
		
	}

}
