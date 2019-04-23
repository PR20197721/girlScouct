package org.girlscouts.vtk.models;

import org.girlscouts.vtk.auth.models.ApiConfig;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    private ApiConfig apiConfig;
    private String sid;
    private String currentYear;
    private int currentFinanceYear;
    private int gSFinanceCutOffDate;
    private String name;
    private String email;
    private String phone;
    private String mobilePhone;
    private String assistantPhone;
    private String homePhone;
    private String contactId;
    private String sfUserId;
    private String firstName;
    private String lastName;
    private boolean isAdmin;
    private boolean isServiceUnitManager;
    private String adminCouncilId;
    private List<Troop> troops;

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

    public int getCurrentFinanceYear() {
        return currentFinanceYear;
    }

    public void setCurrentFinanceYear(int currentFinanceYear) {
        this.currentFinanceYear = currentFinanceYear;
    }

    public int getgSFinanceCutOffDate() {
        return gSFinanceCutOffDate;
    }

    public void setgSFinanceCutOffDate(int gSFinanceCutOffDate) {
        this.gSFinanceCutOffDate = gSFinanceCutOffDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getAssistantPhone() {
        return assistantPhone;
    }

    public void setAssistantPhone(String assistantPhone) {
        this.assistantPhone = assistantPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getSfUserId() {
        return sfUserId;
    }

    public void setSfUserId(String sfUserId) {
        this.sfUserId = sfUserId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getAdminCouncilId() {
        return adminCouncilId;
    }

    public void setAdminCouncilId(String adminCouncilId) {
        this.adminCouncilId = adminCouncilId;
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public void setTroops(List<Troop> troops) {
        this.troops = troops;
    }

    public boolean isServiceUnitManager() {
        return isServiceUnitManager;
    }

    public void setServiceUnitManager(boolean serviceUnitManager) {
        isServiceUnitManager = serviceUnitManager;
    }
}
