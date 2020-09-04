package org.girlscouts.vtk.models;


public class PrimaryGuardian {

    private String lastName;
    private String globalId;
    private String firstName;
    private String phone;
    private String mobilePhone;
    private boolean smsOptIn;
    private String email;
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
        return "PrimaryGuardian{" + "lastName='" + lastName + '\'' + ", globalId='" + globalId + '\'' + ", firstName='" + firstName + '\'' + ", phone='" + phone + '\'' + ", mobilePhone='" + mobilePhone + '\'' + ", smsOptIn=" + smsOptIn + ", email='" + email + '\'' + ", emailOptIn=" + emailOptIn + '}';
    }
}
