package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.utils.VtkUtil;

@Node
public class UserGlobConfig implements Serializable {

	public UserGlobConfig() {
		this.path = "/"+VtkUtil.getYearPlanBase(null, null)+"/global-settings";
		this.masterSalesForceToken = "00DZ000000MheRX!ARwAQDgR9t1li56MwGcEk1rb18yI7Zzdvs4f29hVb2vAmQ.gurnQYCRWc4A9zmVFvJUByVOU1RimEGsnVtfCxcAwNNLEEbkK";
		this.masterSalesForceRefreshToken = "5Aep861ZcIr522KYf4_XY9iYeazzzXIeCIfJKTh98MNzEMpJSTLefHjeMvhqx2esGh5vTGrg0G9dox.LydHIgVN";
	}

	@Field(path = true)
	private String path;
	@Field(id = true)
	private String uid;

	// on sched ; separ by '|'
	@Field
	private String vacationDates;

	// temp aperio
	@Field
	private String masterSalesForceToken, masterSalesForceRefreshToken;

	public String getMasterSalesForceToken() {
		return masterSalesForceToken;
	}

	public void setMasterSalesForceToken(String masterSalesForceToken) {
		this.masterSalesForceToken = masterSalesForceToken;
	}

	public String getMasterSalesForceRefreshToken() {
		return masterSalesForceRefreshToken;
	}

	public void setMasterSalesForceRefreshToken(
			String masterSalesForceRefreshToken) {
		this.masterSalesForceRefreshToken = masterSalesForceRefreshToken;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getVacationDates() {
		return vacationDates;
	}

	public void setVacationDates(String vacationDates) {
		this.vacationDates = vacationDates;
	}

}
