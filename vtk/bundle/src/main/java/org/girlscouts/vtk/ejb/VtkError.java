package org.girlscouts.vtk.ejb;

public class VtkError {

	public VtkError(){
		this.singleView= true;
		this.errorTime= new java.util.Date().getTime();
		this.id = new java.util.Date().getTime() +"_"+ Math.random();
	}
	
	private String name, errorCode, description, userFormattedMsg, id;
    private boolean singleView;
    private java.util.List<String> targets;
    private long errorTime;
    
    
    public long getErrorTime(){ return this.errorTime; }
    
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
