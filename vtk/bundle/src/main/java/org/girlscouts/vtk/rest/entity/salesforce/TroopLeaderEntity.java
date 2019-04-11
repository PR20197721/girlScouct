package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class TroopLeaderEntity {

    @SerializedName("Id")
    private String sfId;

    @SerializedName("ContactId")
    private int sfContactId;

    @SerializedName("CampaignId")
    private int sfCampaignId;

    @SerializedName("attributes")
    private AttributesEntity attributes;

    public class Contact{
        @SerializedName("Id")
        private String sfId;

        @SerializedName("attributes")
        private AttributesEntity attributes;

        @SerializedName("FirstName")
        private String firstName;

        @SerializedName("LastName")
        private String lastName;
    }
}
