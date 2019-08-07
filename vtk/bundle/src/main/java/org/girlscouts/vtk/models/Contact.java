package org.girlscouts.vtk.models;

import java.util.List;

public class Contact extends JcrNode implements java.io.Serializable, Comparable<Contact> {
    private static final long serialVersionUID = -9143046810103196285L;
    private Integer age, type;
    private String email, phone, firstName, lastName, address, address1, city, state, zip, suite, role, dob, country, contactId;
    private List<Contact> contacts;
    private String accountId;
    private boolean renewalDue, emailOptIn, txtOptIn;
    private Integer membershipYear_girl, membershipYear_adult;
    private Integer membershipYear;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Integer getMembershipYear_girl() {
        return membershipYear_girl;
    }

    public void setMembershipYear_girl(Integer membershipYear_girl) {
        this.membershipYear_girl = membershipYear_girl;
    }

    public Integer getMembershipYear_adult() {
        return membershipYear_adult;
    }

    public void setMembershipYear_adult(Integer membershipYear_adult) {
        this.membershipYear_adult = membershipYear_adult;
    }

    public boolean isEmailOptIn() {
        return emailOptIn;
    }

    public void setEmailOptIn(boolean emailOptIn) {
        this.emailOptIn = emailOptIn;
    }

    public boolean isTxtOptIn() {
        return txtOptIn;
    }

    public void setTxtOptIn(boolean txtOptIn) {
        this.txtOptIn = txtOptIn;
    }

    public Integer getMembershipYear() {
        return membershipYear;
    }

    public void setMembershipYear(Integer membershipYear) {
        this.membershipYear = membershipYear;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public java.util.List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(java.util.List<Contact> contacts) {
        this.contacts = contacts;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public boolean isRenewalDue() {
        return renewalDue;
    }

    public void setRenewalDue(boolean renewalDue) {
        this.renewalDue = renewalDue;
    }

    public int compareTo(Contact other) {
        if (this.getId() == null && (other == null || other.getId() == null)) {
            return 0;
        } else if (other == null || other.getId() == null) {
            return 1;
        } else if (this.getId() == null) {
            return -1;
        }
        return getId().compareTo(other.getId());
    }
}
