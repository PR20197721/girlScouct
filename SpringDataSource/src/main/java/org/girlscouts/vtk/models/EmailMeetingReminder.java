package org.girlscouts.vtk.models;

public class EmailMeetingReminder {

	private String to, from, cc, bcc, template, html, subj, emailToGirlParent,
			emailToSelf, emailToTroopVolunteer, meetingId;

	public EmailMeetingReminder(String to, String from, String bcc,
			String subj, String html) {
		this.to = to;
		this.from = from;
		this.bcc = bcc;
		this.subj = subj;
		this.html = html;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getSubj() {
		return subj;
	}

	public void setSubj(String subj) {
		this.subj = subj;
	}

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

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

	public void addTo(String to) {
		if (to != null && !to.isEmpty()) {
			if (this.bcc == null || this.bcc.isEmpty()) {
				this.bcc = to;
			} else {
				this.bcc += ";" + to;
			}
		}
	}

}
