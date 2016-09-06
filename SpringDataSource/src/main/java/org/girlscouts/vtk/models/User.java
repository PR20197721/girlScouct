package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.girlscouts.vtk.auth.models.ApiConfig;

public class User implements Serializable {

	private ApiConfig apiConfig;

	private String sid;// my http sessionId
	private String currentYear; // could be uniq id -> String

	public ApiConfig getApiConfig() {
		return apiConfig;
	}

	public void setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}

}
