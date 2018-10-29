package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.girlscouts.vtk.auth.models.ApiConfig;

public class User implements Serializable {

	private ApiConfig apiConfig;
	private String sid;// my http sessionId
	private String currentYear; // could be uniq id -> String
	private int currentFinanceYear; //ex: 2017
	private int gSFinanceCutOffDate;//config fin year cut off date
	
	public int getCurrentFinanceYear() {
		return currentFinanceYear;
	}

	public void setCurrentFinanceYear(int currentFinanceYear) {
		this.currentFinanceYear = currentFinanceYear;
	}

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

	public int getgSFinanceCutOffDate() {
		return gSFinanceCutOffDate;
	}

	public void setgSFinanceCutOffDate(int gSFinanceCutOffDate) {
		this.gSFinanceCutOffDate = gSFinanceCutOffDate;
	}

}
