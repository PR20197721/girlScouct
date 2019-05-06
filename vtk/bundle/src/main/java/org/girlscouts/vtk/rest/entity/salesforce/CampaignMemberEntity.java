package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class CampaignMemberEntity {
    @SerializedName("Id")
    private String sfId;
    @SerializedName("ContactId")
    private String sfContactId;
    @SerializedName("attributes")
    private AttributesEntity attributes;
    @SerializedName("Display_Renewal__c")
    private boolean isDisplayRenewal;
    @SerializedName("Membership__c")
    private String sfMembershipId;
    @SerializedName("Membership__r")
    private Membership membership;

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

    public AttributesEntity getAttributes() {
        return attributes;
    }

    public void setAttributes(AttributesEntity attributes) {
        this.attributes = attributes;
    }

    public boolean isDisplayRenewal() {
        return isDisplayRenewal;
    }

    public void setDisplayRenewal(boolean displayRenewal) {
        isDisplayRenewal = displayRenewal;
    }

    public String getSfMembershipId() {
        return sfMembershipId;
    }

    public void setSfMembershipId(String sfMembershipId) {
        this.sfMembershipId = sfMembershipId;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public class Membership {
        @SerializedName("Id")
        private String sfId;
        @SerializedName("Membership_Year__c")
        private Integer membershipYear;
        @SerializedName("attributes")
        private AttributesEntity attributes;

        public String getSfId() {
            return sfId;
        }

        public void setSfId(String sfId) {
            this.sfId = sfId;
        }

        public Integer getMembershipYear() {
            return membershipYear;
        }

        public void setMembershipYear(Integer membershipYear) {
            this.membershipYear = membershipYear;
        }

        public AttributesEntity getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesEntity attributes) {
            this.attributes = attributes;
        }
    }

}
