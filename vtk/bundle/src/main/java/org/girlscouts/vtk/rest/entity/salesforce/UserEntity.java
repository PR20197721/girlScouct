package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class UserEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("ContactId")
    private String sfContactId;

    @SerializedName("Council_ID__c")
    private String sfCouncilId;

    @SerializedName("Email")
    private String email;

    @SerializedName("Phone")
    private String phone;

    @SerializedName("FirstName")
    private String firstName;

    @SerializedName("LastName")
    private String lastName;

    @SerializedName("Name")
    private String name;

    @SerializedName("SU_AccessUser__c")
    private boolean isServiceUserManager;

    @SerializedName("Active_DP__c")
    private boolean isActive;

    @SerializedName("isParent__c")
    private boolean isParent;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    @SerializedName("Contact")
    private Contact contact;

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

    public String getSfCouncilId() {
        return sfCouncilId;
    }

    public void setSfCouncilId(String sfCouncilId) {
        this.sfCouncilId = sfCouncilId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServiceUserManager() {
        return isServiceUserManager;
    }

    public void setServiceUserManager(boolean serviceUserManager) {
        isServiceUserManager = serviceUserManager;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
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

    public class Contact {
        @SerializedName("Id")
        private String sfId;

        @SerializedName("AccountId")
        private String sfAccountId;

        @SerializedName("OwnerId")
        private String sfOwnerId;

        @SerializedName("attributes")
        private AttributesEntity attributes;

        @SerializedName("Owner")
        private Owner owner;

        @SerializedName("rC_Bios__Preferred_Contact__c")
        private boolean isPreferredContact;

        @SerializedName("VTK_Admin__c")
        private boolean isVtkAdmin;

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

        public String getSfOwnerId() {
            return sfOwnerId;
        }

        public void setSfOwnerId(String sfOwnerId) {
            this.sfOwnerId = sfOwnerId;
        }

        public AttributesEntity getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesEntity attributes) {
            this.attributes = attributes;
        }

        public Owner getOwner() {
            return owner;
        }

        public void setOwner(Owner owner) {
            this.owner = owner;
        }

        public boolean isPreferredContact() {
            return isPreferredContact;
        }

        public void setPreferredContact(boolean preferredContact) {
            isPreferredContact = preferredContact;
        }

        public boolean isVtkAdmin() {
            return isVtkAdmin;
        }

        public void setVtkAdmin(boolean vtkAdmin) {
            isVtkAdmin = vtkAdmin;
        }

        public class Owner {
            @SerializedName("Id")
            private String sfId;

            @SerializedName("AccountId")
            private String sfAccountId;

            @SerializedName("Council_Code__c")
            private String councilCode;

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

            public String getCouncilCode() {
                return councilCode;
            }

            public void setCouncilCode(String councilCode) {
                this.councilCode = councilCode;
            }
        }

    }
}
