package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class MembersEntity {
    @SerializedName("YearsAsGirl")
    private int yearsAsGirl;
    @SerializedName("YearsAsAdult")
    private int yearsAsAdult;
    @SerializedName("SMSOptIn")
    private boolean smsOptIn;
    @SerializedName("Role")
    private RoleEntity role;
    @SerializedName("PromptForRenewal")
    private boolean promptForRenewal;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("MostRecentMembershipYear")
    private String mostRecentMembershipYear;
    @SerializedName("MobilePhone")
    private String mobilePhone;
    @SerializedName("LastName")
    private String lastName;
    @SerializedName("GlobalId")
    private String globalId;
    @SerializedName("FirstName")
    private String firstName;
    @SerializedName("EmailOptIn")
    private boolean emailOptIn;
    @SerializedName("Email")
    private String email;
    @SerializedName("Birthdate")
    private String birthdate;
    @SerializedName("Age")
    private int age;
    @SerializedName("Address")
    private AddressEntity address;

    public int getYearsAsGirl() {
        return yearsAsGirl;
    }

    public void setYearsAsGirl(int yearsAsGirl) {
        this.yearsAsGirl = yearsAsGirl;
    }

    public int getYearsAsAdult() {
        return yearsAsAdult;
    }

    public void setYearsAsAdult(int yearsAsAdult) {
        this.yearsAsAdult = yearsAsAdult;
    }

    public boolean isSmsOptIn() {
        return smsOptIn;
    }

    public void setSmsOptIn(boolean smsOptIn) {
        this.smsOptIn = smsOptIn;
    }

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public boolean isPromptForRenewal() {
        return promptForRenewal;
    }

    public void setPromptForRenewal(boolean promptForRenewal) {
        this.promptForRenewal = promptForRenewal;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMostRecentMembershipYear() {
        return mostRecentMembershipYear;
    }

    public void setMostRecentMembershipYear(String mostRecentMembershipYear) {
        this.mostRecentMembershipYear = mostRecentMembershipYear;
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }
}
