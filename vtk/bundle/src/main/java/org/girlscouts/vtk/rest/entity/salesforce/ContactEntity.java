package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class ContactEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("AccountId")
    private String sfAccountId;

    @SerializedName("rC_Bios__Preferred_Mailing_Address__c")
    private String sfPreferredMailingAddressID;

    @SerializedName("Email")
    private String email;

    @SerializedName("Name")
    private String name;

    @SerializedName("Email_Opt_In__c")
    private boolean isEmailOptIn;

    @SerializedName("Text_Phone_Opt_In__c")
    private boolean isTextPhoneOptIn;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("rC_Bios__Role__c")
    private String role;

    @SerializedName("Secondary_Role__c")
    private String secondaryRole;

    @SerializedName("rC_Bios__Age__c")
    private int age;

    @SerializedName("Birthdate")
    private String birthdate;

    @SerializedName("MailingStreet")
    private String mailingStreet;

    @SerializedName("MailingCity")
    private String mailingCity;

    @SerializedName("MailingState")
    private String mailingState;

    @SerializedName("MailingPostalCode")
    private String mailingPostalCode;

    @SerializedName("MailingCountry")
    private String mailingCountry;

    @SerializedName("of_Girl_Years__c")
    private int girlYears;

    @SerializedName("of_Adult_Years__c")
    private int adultYears;

    @SerializedName("rC_Bios__Preferred_Mailing_Address__r")
    private PreferredMailingAddress preferredMailingAddress;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("Account")
    private AccountEntity account;

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public String getSfAccountId() {
        return sfAccountId;
    }

    public void setSfAccountId(String sfAccountId) {
        this.sfAccountId = sfAccountId;
    }

    public String getSfPreferredMailingAddressID() {
        return sfPreferredMailingAddressID;
    }

    public void setSfPreferredMailingAddressID(String sfPreferredMailingAddressID) {
        this.sfPreferredMailingAddressID = sfPreferredMailingAddressID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmailOptIn() {
        return isEmailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        isEmailOptIn = emailOptIn;
    }

    public boolean isTextPhoneOptIn() {
        return isTextPhoneOptIn;
    }

    public void setTextPhoneOptIn(boolean textPhoneOptIn) {
        isTextPhoneOptIn = textPhoneOptIn;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSecondaryRole() {
        return secondaryRole;
    }

    public void setSecondaryRole(String secondaryRole) {
        this.secondaryRole = secondaryRole;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getMailingStreet() {
        return mailingStreet;
    }

    public void setMailingStreet(String mailingStreet) {
        this.mailingStreet = mailingStreet;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public String getMailingState() {
        return mailingState;
    }

    public void setMailingState(String mailingState) {
        this.mailingState = mailingState;
    }

    public String getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void setMailingPostalCode(String mailingPostalCode) {
        this.mailingPostalCode = mailingPostalCode;
    }

    public String getMailingCountry() {
        return mailingCountry;
    }

    public void setMailingCountry(String mailingCountry) {
        this.mailingCountry = mailingCountry;
    }

    public int getGirlYears() {
        return girlYears;
    }

    public void setGirlYears(int girlYears) {
        this.girlYears = girlYears;
    }

    public int getAdultYears() {
        return adultYears;
    }

    public void setAdultYears(int adultYears) {
        this.adultYears = adultYears;
    }

    public PreferredMailingAddress getPreferredMailingAddress() {
        return preferredMailingAddress;
    }

    public void setPreferredMailingAddress(PreferredMailingAddress preferredMailingAddress) {
        this.preferredMailingAddress = preferredMailingAddress;
    }

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public void setAccount(AccountEntity account) {
        this.account = account;
    }

    public class PreferredMailingAddress {

        @SerializedName("Id")
        private String sfId;

        @SerializedName("attributes")
        private AttributesEntity attributes;

        @SerializedName("rC_Bios__County__c")
        private String county;

        public String getSfId() {
            return sfId;
        }

        public void setSfId(String sfId) {
            this.sfId = sfId;
        }

        public AttributesEntity getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesEntity attributes) {
            this.attributes = attributes;
        }

        public String getCounty() {
            return county;
        }

        public void setCounty(String county) {
            this.county = county;
        }
    }
}
