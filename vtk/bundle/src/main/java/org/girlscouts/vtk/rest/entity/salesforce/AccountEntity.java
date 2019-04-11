package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class AccountEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("rC_Bios__Preferred_Contact__c")
    private String sfPreferredContactId;

    @SerializedName("rC_Bios__Preferred_Contact__r")
    private PreferredContact preferredContact;

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

    public String getSfPreferredContactId() {
        return sfPreferredContactId;
    }

    public void setSfPreferredContactId(String sfPreferredContactId) {
        this.sfPreferredContactId = sfPreferredContactId;
    }

    public PreferredContact getPreferredContact() {
        return preferredContact;
    }

    public void setPreferredContact(PreferredContact preferredContact) {
        this.preferredContact = preferredContact;
    }

    public class PreferredContact{
        @SerializedName("Id")
        private String sfId;

        @SerializedName("attributes")
        private AttributesEntity attributes;

        @SerializedName("Email")
        private String email;

        @SerializedName("FirstName")
        private String firstName;

        @SerializedName("LastName")
        private String lastName;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
    }
}
