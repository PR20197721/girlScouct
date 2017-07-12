package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.*;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.utils.VtkUtil;

@Entity
@Table(name="Troop")
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

	@Id @Column
	private String id;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "council_uid", nullable = false)
	private Council council;
	
	@Transient
	String path;
	
	//-@OneToOne //(autoUpdate = false)
	
	@Transient //@OneToOne(fetch = FetchType.LAZY, mappedBy = "troop", cascade=CascadeType.ALL)
	private YearPlan yearPlan;
	
	private org.girlscouts.vtk.salesforce.Troop troop;
	
	@Column
	private String sfUserId, sfTroopId, sfTroopName, sfTroopAge, sfCouncil;
	
	@Column
	private String currentTroop;
	@Column
	private String errCode, refId;
	private boolean isRefresh; // reload yearPlan from DB. case: someone
								// modified plan:lock
	private java.util.Date retrieveTime;
	
	@Transient
	private EmailMeetingReminder sendingEmail; // tmp
	private boolean isDbUpdate = false;

	public boolean isDbUpdate() {
		return isDbUpdate;
	}

	public void setDbUpdate(boolean isDbUpdate) {
		this.isDbUpdate = isDbUpdate;
	}

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
		isDbUpdate = true;

	}

	public String getCurrentTroop() {
		return currentTroop;
	}

	public void setCurrentTroop(String currentUser) {
		this.currentTroop = currentUser;
		isDbUpdate = true;

	}

	public String getSfCouncil() {
		return sfCouncil;
	}

	public void setSfCouncil(String sfCouncil) {
		this.sfCouncil = sfCouncil;
		// isDbUpdate=true;

	}

	public String getSfTroopAge() {
		return sfTroopAge;
	}

	public void setSfTroopAge(String sfTroopAge) {
		this.sfTroopAge = sfTroopAge;
		// isDbUpdate=true;

	}

	public EmailMeetingReminder getSendingEmail() {
		return sendingEmail;
	}

	public void setSendingEmail(EmailMeetingReminder sendingEmail) {
		this.sendingEmail = sendingEmail;
	}

	public String getSfTroopName() {
		return sfTroopName;
	}

	public void setSfTroopName(String sfTroopName) {
		this.sfTroopName = sfTroopName;
		// isDbUpdate=true;

	}

	public String getSfUserId() {
		return sfUserId;
	}

	public void setSfUserId(String sfUserId) {
		this.sfUserId = sfUserId;
		// isDbUpdate=true;

	}

	public String getSfTroopId() {
		return sfTroopId;
	}

	public void setSfTroopId(String sfTroopId) {
		this.sfTroopId = sfTroopId;
		// isDbUpdate=true;

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
		isDbUpdate = true;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		isDbUpdate = true;

	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
		isDbUpdate = true;

	}

	public String getTroopPath() {
		return VtkUtil.getYearPlanBase(null, null).substring(1) + this.getSfCouncil() + "/troops/" + this.getId();
	}

	public String getCouncilPath() {
		return VtkUtil.getYearPlanBase(null, null).substring(1) + this.getSfCouncil();
	}

	public Council getCouncil() {
		return council;
	}

	public void setCouncil(Council council) {
		this.council = council;
	}
	
	
}
