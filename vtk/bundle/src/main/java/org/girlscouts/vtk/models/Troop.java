package org.girlscouts.vtk.models;

import org.girlscouts.vtk.osgi.component.util.VtkUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Troop extends JcrNode implements Serializable {
    private YearPlan yearPlan;
    private String sfUserId;
    private String sfTroopId;
    private String sfTroopName;
    private String sfTroopAge;
    private String sfCouncil;
    private String sfParentId;
    private String irmTroopId;
    private String currentTroop;
    private String errCode;
    private String refId;
    private String troopId;
    private String troopName;
    private String gradeLevel;
    private String councilId;
    private String councilCode;
    private int type;
    private Set<Integer> permissionTokens;
    private String role;
    private boolean isRefresh;
    private Date retrieveTime;
    private EmailMeetingReminder sendingEmail;
    private String participationCode;
    private String councilPath;
    private String hash;
    private Boolean isIRM = Boolean.FALSE;
    private Boolean isSUM = Boolean.FALSE;

    public Troop() {
        this.type = 0;
    }

    public Troop(String troopId) {
        this.setId(troopId);
        this.setRetrieveTime(new java.util.Date());
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
        this.setDbUpdate(true);
    }

    public String getCurrentTroop() {
        return currentTroop;
    }

    public void setCurrentTroop(String currentUser) {
        this.currentTroop = currentUser;
        this.setDbUpdate(true);
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

    public YearPlan getYearPlan() {
        return yearPlan;
    }

    public void setYearPlan(YearPlan yearPlan) {
        this.yearPlan = yearPlan;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
        this.setDbUpdate(true);
    }

    public String getTroopPath() {
        return getPath();
    }

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

    public String getCouncilId() {
        return councilId;
    }

    public void setCouncilId(String councilId) {
        this.councilId = councilId;
    }

    public String getCouncilCode() {
        return councilCode;
    }

    public void setCouncilCode(String councilCode) {
        this.councilCode = councilCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Set<Integer> getPermissionTokens() {
        return permissionTokens;
    }

    public void setPermissionTokens(Set<Integer> permissionTokens) {
        this.permissionTokens = permissionTokens;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getParticipationCode() {
        return participationCode;
    }

    public void setParticipationCode(String participationCode) {
        this.participationCode = participationCode;
    }

    public String getCouncilPath() {
        return councilPath;
    }

    public void setCouncilPath(String councilPath) {
        this.councilPath = councilPath;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getIrmTroopId() {
        return irmTroopId;
    }

    public void setIrmTroopId(String irmTroopId) {
        this.irmTroopId = irmTroopId;
    }

    public Boolean getIsIRM() {
        return isIRM;
    }

    public void setIsIRM(Boolean IRM) {
        isIRM = IRM;
    }

    public Boolean getIsSUM() {
        return isSUM;
    }

    public void setIsSUM(Boolean SUM) {
        isSUM = SUM;
    }

    public String getSfParentId() {
        return sfParentId;
    }

    public void setSfParentId(String sfParentId) {
        this.sfParentId = sfParentId;
    }
}