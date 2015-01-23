package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.Calendar;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.ejb.EmailMeetingReminder;

@Node(jcrMixinTypes = "mix:lockable")
public class Troop implements Serializable {

	public Troop() {
	}

	public Troop(String path, String troopId) {
		this.id = troopId;
		this.path = path + troopId;
		this.setRetrieveTime(new java.util.Date());
	}

	public Troop(String troopId) {
		this.id = troopId;
		this.setRetrieveTime(new java.util.Date());
	}

	@Field(id = true)
	private String id;
	@Field(path = true)
	String path;
	@Bean
	YearPlan yearPlan;
	// -private ApiConfig apiConfig;
	private org.girlscouts.vtk.salesforce.Troop troop;
	@Field
	private String sfUserId, sfTroopId, sfTroopName, sfTroopAge, sfCouncil;
	@Field(jcrName = "jcr:lastModified")
	private Calendar lastModified;
	@Field
	private String currentTroop;
	@Field
	private String errCode, refId;
	private boolean isRefresh; // reload yearPlan from DB. case: someone
								// modified plan:lock
	private java.util.Date retrieveTime;
	private EmailMeetingReminder sendingEmail; // tmp

	public java.util.Date getRetrieveTime() {
		return retrieveTime;
	}

	public void setRetrieveTime(java.util.Date retrieveTime) {
		this.retrieveTime = retrieveTime;
	}

	public boolean isRefresh() {
		return isRefresh;
	}

	public void setRefresh(boolean isRefresh) {
		this.isRefresh = isRefresh;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getCurrentTroop() {
		return currentTroop;
	}

	public void setCurrentTroop(String currentUser) {
		this.currentTroop = currentUser;
	}

	public String getSfCouncil() {
		return sfCouncil;
	}

	public void setSfCouncil(String sfCouncil) {
		this.sfCouncil = sfCouncil;
	}

	public String getSfTroopAge() {
		return sfTroopAge;
	}

	public void setSfTroopAge(String sfTroopAge) {
		this.sfTroopAge = sfTroopAge;
	}

	public EmailMeetingReminder getSendingEmail() {
		return sendingEmail;
	}

	public void setSendingEmail(EmailMeetingReminder sendingEmail) {
		this.sendingEmail = sendingEmail;
	}

	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;

	}

	public String getSfTroopName() {
		return sfTroopName;
	}

	public void setSfTroopName(String sfTroopName) {
		this.sfTroopName = sfTroopName;
	}

	public String getSfUserId() {
		return sfUserId;
	}

	public void setSfUserId(String sfUserId) {
		this.sfUserId = sfUserId;
	}

	public String getSfTroopId() {
		return sfTroopId;
	}

	public void setSfTroopId(String sfTroopId) {
		this.sfTroopId = sfTroopId;
	}

	public org.girlscouts.vtk.salesforce.Troop getTroop() {
		return troop;
	}

	public void setTroop(org.girlscouts.vtk.salesforce.Troop troop) {
		this.troop = troop;
	}

	/*
	 * public ApiConfig getApiConfig() { return apiConfig; } public void
	 * setApiConfig(ApiConfig apiConfig) { this.apiConfig = apiConfig; }
	 */
	public YearPlan getYearPlan() {
		return yearPlan;
	}

	public void setYearPlan(YearPlan yearPlan) {
		this.yearPlan = yearPlan;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

}
