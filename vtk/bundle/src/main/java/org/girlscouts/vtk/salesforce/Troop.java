package org.girlscouts.vtk.salesforce;

public class Troop {

    private int type;

    public Troop(){ this.type= 0; }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
	
	private String troopId, troopName, gradeLevel,  councilId;

	private int councilCode;
	
	
	
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
