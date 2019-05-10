package org.girlscouts.vtk.ocm;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.Serializable;
import java.util.Date;

@Node
public class SentEmailNode extends JcrNode implements Serializable {
    @Field
    private String addressList;
    @Field
    private String subject;
    @Field
    private String addresses;
    @Field
    private String htmlDiff;
    @Field
    private Date sentDate;
    @Field
    private String htmlMsg;//temp

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

    public String getHtmlMsg() {
        return htmlMsg;
    }

    public void setHtmlMsg(String htmlMsg) {
        this.htmlMsg = htmlMsg;
    }

}
