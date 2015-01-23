package org.girlscouts.vtk.ejb;

import org.girlscouts.vtk.models.Asset;

public class EmailMeetingReminder {

	private String to, from, cc, bcc, html, subj, sentDate,
	emailToGirlParent, emailToSelf, emailToTroopVolunteer,
	meetingId;

	private java.util.List<Asset> assets;



	public EmailMeetingReminder(String to, String from , String cc, String subj, String html){
		this.to= to;
		this.from=from;
		this.cc=cc;
		this.subj= subj;
		this.html=html;
	}


	public java.util.List<Asset> getAssets() {
		return assets;
	}





	public void setAssets(java.util.List<Asset> assets) {
		this.assets = assets;
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
	public void setSentDate(String date){
		sentDate = date;
	}
	public String getSentDate(){
		return sentDate;
	}

	public void addTo(String to){
		if(to!=null && !to.isEmpty()){
			if(this.to == null){
				this.to = to;
			}else{
				this.to+=";"+to;
			}
		}
	}










}



