package org.girlscouts.vtk.ejb;

public class EmailMeetingReminder {

	private String to, from, cc, bcc, html, subj,
		emailToGirlParent, emailToSelf, emailToTroopVolunteer;
	
	public EmailMeetingReminder(String to, String from , String cc, String subj, String html){
		this.to= to;
		this.from=from;
		this.cc=cc;
		this.subj= subj;
		this.html=html;
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
	
	
	
	
	
	
	
	
	
	
}



