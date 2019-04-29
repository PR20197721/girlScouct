package org.girlscouts.vtk.rest.entity.vtk;

import com.google.gson.annotations.SerializedName;

public class EmailMeetingReminderEntity extends BaseEntity {

    @SerializedName("to")
	private String to;
    @SerializedName("from")
	private String from;
    @SerializedName("cc")
	private String cc;
    @SerializedName("bcc")
	private String bcc;
    @SerializedName("template")
	private String template;
    @SerializedName("html")
	private String html;
    @SerializedName("subj")
	private String subj;
    @SerializedName("emailToGirlParent")
	private String emailToGirlParent;
    @SerializedName("emailToSelf")
	private String emailToSelf;
    @SerializedName("emailToTroopVolunteer")
	private String emailToTroopVolunteer;
    @SerializedName("meetingId")
	private String meetingId;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getSubj() {
        return subj;
    }

    public void setSubj(String subj) {
        this.subj = subj;
    }

    public String getEmailToGirlParent() {
        return emailToGirlParent;
    }

    public void setEmailToGirlParent(String emailToGirlParent) {
        this.emailToGirlParent = emailToGirlParent;
    }

    public String getEmailToSelf() {
        return emailToSelf;
    }

    public void setEmailToSelf(String emailToSelf) {
        this.emailToSelf = emailToSelf;
    }

    public String getEmailToTroopVolunteer() {
        return emailToTroopVolunteer;
    }

    public void setEmailToTroopVolunteer(String emailToTroopVolunteer) {
        this.emailToTroopVolunteer = emailToTroopVolunteer;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
