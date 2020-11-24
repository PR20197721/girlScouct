package org.girlscouts.vtk.rest.entity.mulesoft;

import com.google.gson.annotations.SerializedName;

public class PrimaryContactEntity {

    @SerializedName("LastName")
    private String lastName;
    @SerializedName("GlobalId")
    private String globalId;
    @SerializedName("FirstName")
    private String firstName;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("MobilePhone")
    private String mobilePhone;
    @SerializedName("SMSOptIn")
    private boolean smsOptIn;
    @SerializedName("Email")
    private String email;
    @SerializedName("EmailOptIn")
    private boolean emailOptIn;

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

    public boolean isSmsOptIn() {
        return smsOptIn;
    }

    public void setSmsOptIn(boolean smsOptIn) {
        this.smsOptIn = smsOptIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    @Override
    public String toString() {
        return "PrimaryContactEntity{" + "lastName='" + lastName + '\'' + ", globalId='" + globalId + '\'' + ", firstName='" + firstName + '\'' + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", smsOptIn=" + smsOptIn + ", email='" + email + '\'' + ", emailOptIn=" + emailOptIn + '}';
    }
}
