package org.girlscouts.vtk.models;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.girlscouts.vtk.ejb.EmailMeetingReminder;
import org.girlscouts.vtk.utils.VtkUtil;

import java.io.Serializable;
import java.util.Set;
import java.util.Date;

@Node
public class Troop implements Serializable {
    @Field(path = true)
    private String path;
    @Bean(autoUpdate = false)
    private YearPlan yearPlan;
    @Field(id = true)
    private String id;
    @Field
    private String sfUserId;
    @Field
    private String sfTroopId;
    @Field
    private String sfTroopName;
    @Field
    private String sfTroopAge;
    @Field
    private String sfCouncil;
    @Field
    private String currentTroop;
    @Field
    private String errCode;
    @Field
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
    private boolean isDbUpdate = false;
    private String participationCode;
    private Council council;

    public Troop() {
        this.type = 0;
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

    public String getParticipationCode() { return participationCode; }

    public void setParticipationCode(String participationCode) { this.participationCode = participationCode; }

    public Council getCouncil() {return council;}

    public void setCouncil(Council council) { this.council = council; }
}