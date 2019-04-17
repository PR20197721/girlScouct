package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class TroopLeaderEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("ContactId")
    private String sfContactId;

    @SerializedName("CampaignId")
    private String sfCampaignId;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("Contact")
    private Contact contact;

    public class Contact{
        @SerializedName("Id")
        private String sfId;

        @SerializedName("attributes")
        private AttributesEntity attributes;

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

    public String getSfId() {
        return sfId;
    }

    public void setSfId(String sfId) {
        this.sfId = sfId;
    }

    public String getSfContactId() {
        return sfContactId;
    }

    public void setSfContactId(String sfContactId) {
        this.sfContactId = sfContactId;
    }

    public String getSfCampaignId() {
        return sfCampaignId;
    }

    public void setSfCampaignId(String sfCampaignId) {
        this.sfCampaignId = sfCampaignId;
    }

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
