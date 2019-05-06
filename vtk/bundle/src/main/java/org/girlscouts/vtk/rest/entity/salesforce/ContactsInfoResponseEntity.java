package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class ContactsInfoResponseEntity {
    @SerializedName("lstCon")
    private ContactEntity[] contacts;
    @SerializedName("lstCM")
    private CampaignMemberEntity[] campaignMembers;

    public ContactEntity[] getContacts() {
        return contacts;
    }

    public void setContacts(ContactEntity[] contacts) {
        this.contacts = contacts;
    }

    public CampaignMemberEntity[] getCampaignMembers() {
        return campaignMembers;
    }

    public void setCampaignMembers(CampaignMemberEntity[] campaignMembers) {
        this.campaignMembers = campaignMembers;
    }
}
