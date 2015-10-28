package org.girlscouts.vtk.ejb;

public class VtkError {

	public VtkError(){
		this.singleView= true;
	}
	
	private String name, errorCode, description, userFormattedMsg;
    private boolean singleView;
    private java.util.List<String> targets;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUserFormattedMsg() {
		return userFormattedMsg;
	}

	public void setUserFormattedMsg(String userFormattedMsg) {
		this.userFormattedMsg = userFormattedMsg;
	}

	public boolean isSingleView() {
		return singleView;
	}

	public void setSingleView(boolean singleView) {
		this.singleView = singleView;
	}

	public java.util.List<String> getTargets() {
		return targets;
	}

	public void setTargets(java.util.List<String> targets) {
		this.targets = targets;
	}
	
	public void addTarget(String target){
		if( targets==null)
			targets=new java.util.ArrayList<String>();
		targets.add( target );
	}
	
	
}
