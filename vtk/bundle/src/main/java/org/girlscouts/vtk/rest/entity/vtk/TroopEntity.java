package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Set;

public class Troop extends BaseEntity{

    @SerializedName("path")
    private String path;
    @SerializedName("yearPlan")
    private YearPlan yearPlan;
    @SerializedName("id")
    private String id;
    @SerializedName("sfUserId")
    private String sfUserId;
    @SerializedName("sfTroopId")
    private String sfTroopId;
    @SerializedName("sfTroopName")
    private String sfTroopName;
    @SerializedName("sfTroopAge")
    private String sfTroopAge;
    @SerializedName("sfCouncil")
    private String sfCouncil;
    @SerializedName("currentTroop")
    private String currentTroop;
    @SerializedName("errCode")
    private String errCode;
    @SerializedName("refId")
    private String refId;
    @SerializedName("troopId")
    private String troopId;
    @SerializedName("troopName")
    private String troopName;
    @SerializedName("gradeLevel")
    private String gradeLevel;
    @SerializedName("councilId")
    private String councilId;
    @SerializedName("councilCode")
    private String councilCode;
    @SerializedName("type")
    private int type;
    @SerializedName("permissionTokens")
    private Set<Integer> permissionTokens;
    @SerializedName("role")
    private String role;
    @SerializedName("isRefresh")
    private boolean isRefresh;
    @SerializedName("retrieveTime")
    private Date retrieveTime;
    @SerializedName("sendingEmail")
    private EmailMeetingReminder sendingEmail;
    @SerializedName("isDbUpdate")
    private boolean isDbUpdate;
    @SerializedName("participationCode")
    private String participationCode;
    @SerializedName("councilPath")
    private String councilPath;
    @SerializedName("hash")
    private String hash;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public YearPlan getYearPlan() {
        return yearPlan;
    }

    public void setYearPlan(YearPlan yearPlan) {
        this.yearPlan = yearPlan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSfTroopName() {
        return sfTroopName;
    }

    public void setSfTroopName(String sfTroopName) {
        this.sfTroopName = sfTroopName;
    }

    public String getSfTroopAge() {
        return sfTroopAge;
    }

    public void setSfTroopAge(String sfTroopAge) {
        this.sfTroopAge = sfTroopAge;
    }

    public String getSfCouncil() {
        return sfCouncil;
    }

    public void setSfCouncil(String sfCouncil) {
        this.sfCouncil = sfCouncil;
    }

    public String getCurrentTroop() {
        return currentTroop;
    }

    public void setCurrentTroop(String currentTroop) {
        this.currentTroop = currentTroop;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
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

    public boolean isRefresh() {
        return isRefresh;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
    }

    public Date getRetrieveTime() {
        return retrieveTime;
    }

    public void setRetrieveTime(Date retrieveTime) {
        this.retrieveTime = retrieveTime;
    }

    public EmailMeetingReminder getSendingEmail() {
        return sendingEmail;
    }

    public void setSendingEmail(EmailMeetingReminder sendingEmail) {
        this.sendingEmail = sendingEmail;
    }

    public boolean isDbUpdate() {
        return isDbUpdate;
    }

    public void setDbUpdate(boolean dbUpdate) {
        isDbUpdate = dbUpdate;
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
}