package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserInfoResponseEntity {
    @SerializedName("SMSOptIn")
    private boolean smsOptIn;
    @SerializedName("RenewalDate")
    private String renewalDate;
    @SerializedName("ProductAccess")
    private ProductAccessEntity productAccess;
    @SerializedName("PrimaryCouncil")
    private String primaryCouncil;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("MobilePhone")
    private String mobilePhone;
    @SerializedName("LastName")
    private String lastName;
    @SerializedName("IsParent")
    private boolean isParent;
    @SerializedName("GlobalId")
    private String globalId;
    @SerializedName("FirstName")
    private String firstName;
    @SerializedName("EmailOptIn")
    private boolean emailOptIn;
    @SerializedName("Email")
    private String email;
    @SerializedName("Affiliations")
    private List<AffiliationsEntity> affiliations;
    @SerializedName("ActiveSince")
    private String activeSince;
    @SerializedName("ReturnStatus")
    private String returnStatus;
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("PrimaryContact")
    private PrimaryContactEntity primaryContact;

    public boolean isSmsOptIn() {
        return smsOptIn;
    }

    public void setSmsOptIn(boolean smsOptIn) {
        this.smsOptIn = smsOptIn;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public ProductAccessEntity getProductAccess() {
        return productAccess;
    }

    public void setProductAccess(ProductAccessEntity productAccess) {
        this.productAccess = productAccess;
    }

    public String getPrimaryCouncil() {
        return primaryCouncil;
    }

    public void setPrimaryCouncil(String primaryCouncil) {
        this.primaryCouncil = primaryCouncil;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<AffiliationsEntity> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationsEntity> affiliations) {
        this.affiliations = affiliations;
    }

    public String getActiveSince() {
        return activeSince;
    }

    public void setActiveSince(String activeSince) {
        this.activeSince = activeSince;
    }

    public String getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(String returnStatus) {
        this.returnStatus = returnStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public PrimaryContactEntity getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(PrimaryContactEntity primaryContact) {
        this.primaryContact = primaryContact;
    }

    @Override
    public String toString() {
        return "UserInfoResponseEntity{" + "smsOptIn=" + smsOptIn + ", renewalDate='" + renewalDate + '\'' + ", productAccess=" + productAccess + ", primaryCouncil='" + primaryCouncil + '\'' + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", lastName='" + lastName + '\'' + ", isParent=" + isParent + ", globalId='" + globalId + '\'' + ", firstName='" + firstName + '\'' + ", emailOptIn=" + emailOptIn + ", email='" + email + '\'' + ", affiliations=" + affiliations + ", activeSince='" + activeSince + '\'' + ", returnStatus='" + returnStatus + '\'' + ", errorMessage='" + errorMessage + '\'' + ", primaryContact=" + primaryContact + '}';
    }
}
