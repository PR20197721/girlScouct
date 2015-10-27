package org.girlscouts.vtk.ejb;

public class VtkError {

	private String name, errorCode, description, userFormattedMsg;

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
}
