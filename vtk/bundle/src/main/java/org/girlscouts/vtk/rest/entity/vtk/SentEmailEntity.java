package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SentEmailEntity extends BaseEntity{

    @SerializedName("addressList")
    private String addressList;
    @SerializedName("subject")
    private String subject;
    @SerializedName("addresses")
    private String addresses;
    @SerializedName("htmlDiff")
    private String htmlDiff;
    @SerializedName("sentDate")
    private Date sentDate;
    @SerializedName("path")
    private String path;
    @SerializedName("uid")
    private String uid;
    @SerializedName("htmlMsg")
    private String htmlMsg;

    public String getAddressList() {
        return addressList;
    }

    public void setAddressList(String addressList) {
        this.addressList = addressList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getHtmlDiff() {
        return htmlDiff;
    }

    public void setHtmlDiff(String htmlDiff) {
        this.htmlDiff = htmlDiff;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHtmlMsg() {
        return htmlMsg;
    }

    public void setHtmlMsg(String htmlMsg) {
        this.htmlMsg = htmlMsg;
    }
}
