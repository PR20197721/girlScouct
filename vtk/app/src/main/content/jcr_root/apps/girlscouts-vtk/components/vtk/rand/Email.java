package org.girlscouts.vtk.salesforce;

public class Email {

	private String from, to, txtEmail, htmlEmail, subject, parentId;
	private java.util.List bcc;

	public java.util.List getBcc() {
		return bcc;
	}

	public void setBcc(java.util.List bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTxtEmail() {
		return txtEmail;
	}

	public void setTxtEmail(String txtEmail) {
		this.txtEmail = txtEmail;
	}

	public String getHtmlEmail() {
		return htmlEmail;
	}

	public void setHtmlEmail(String htmlEmail) {
		this.htmlEmail = htmlEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String fmtBcc() {

		if (this.getBcc() == null || this.getBcc().size() <= 0)
			return null;

		String fmtBccs = "";
		for (int i = 0; i < this.getBcc().size(); i++)
			fmtBccs += this.bcc.get(i) + ", ";
		return fmtBccs;
	}

}
