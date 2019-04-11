package org.girlscouts.vtk.rest.entity.salesforce;

import com.google.gson.annotations.SerializedName;

public class ContactsInfoResponseEntity {

    @SerializedName("lstCon")
    private ContactEntity[] contacts;

    public ContactEntity[] getContacts() {
        return contacts;
    }

    public void setContacts(ContactEntity[] contacts) {
        this.contacts = contacts;
    }
}
