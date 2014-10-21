package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.girlscouts.vtk.auth.models.ApiConfig;

public class User implements Serializable{

	private ApiConfig apiConfig;
	private java.util.Set<Integer> permissions;
	
	
	public java.util.Set<Integer> getPermissions() {
		return permissions;
	}

	public void setPermissions(java.util.Set<Integer> permissions) {
		this.permissions = permissions;
	}

	public ApiConfig getApiConfig() {
		return apiConfig;
	}

	public void setApiConfig(ApiConfig apiConfig) {
		this.apiConfig = apiConfig;
	}

}
