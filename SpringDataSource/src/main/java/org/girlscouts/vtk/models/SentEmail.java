package org.girlscouts.vtk.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

import difflib.DiffUtils;
import difflib.Patch;
import difflib.PatchFailedException;
import difflib.StringUtills;

@Entity
@Table(name="SendEmail")
public class SentEmail implements Serializable {
	
	@Column
	private String addressList,subject, addresses, htmlDiff;
	
	@Column
	private Date sentDate;

	@Transient
	private String path;

	@Id @Column
	private String uid;
		
	private String htmlMsg;//temp
	
	public SentEmail() {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();

	}
	public SentEmail(EmailMeetingReminder emr) {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();
		if(emr.getCc()!=null && !emr.getCc().isEmpty()){
			addresses+=emr.getCc();
		}
		addressList = emr.getEmailToGirlParent()!=null ? "Girls/Parents " : "" ;
		addressList += emr.getEmailToSelf()!=null ? "Self " : "" ;
		addressList += emr.getEmailToTroopVolunteer()!=null ? "Troop Volunteers" : "" ;
		subject=emr.getSubj();
		htmlMsg = emr.getHtml();
        generateDiffString(emr.getTemplate(), emr.getHtml());

	}
	public SentEmail(String path, EmailMeetingReminder emr){
		this(emr);
		this.path = path;
	}

	private void generateDiffString(String template, String copy){
		List<String> original = Arrays.asList(template.split("\r?\n|\r"));
		List<String> revised = Arrays.asList(copy.split("\r?\n|\r"));
		
		Patch patch = DiffUtils.diff(original, revised);
		List<String> diffStrings = DiffUtils.generateUnifiedDiff("", "", original, patch, 0);
		
        htmlDiff = StringUtills.join(diffStrings, "\n");
	}
	
	public String getHtmlMsg(String template){
		List<String >diff = Arrays.asList(this.htmlDiff.split("\n"));
        List<String> original = Arrays.asList(template.split("\r?\n|\r"));

		Patch patch2 = DiffUtils.parseUnifiedDiff(diff);
		try{
			List<String> result = (List<String>) DiffUtils.patch(original, patch2);
			htmlMsg = StringUtills.join(result,"\n");

		}catch(PatchFailedException pfe){
			pfe.printStackTrace();
		}
        return htmlMsg;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid= uid;
	}
	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date date) {
		this.sentDate= date;
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path=path;
	}
	public String getAddressList() {
		return addressList;
	}

	public void setAddressList(String adl) {
		this.addressList=adl;
	}
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subj) {
		this.subject=subj;
	}


	public String getHtmlDiff() {
		return htmlDiff;
	}

	public void setHtmlDiff(String diff) {
		this.htmlDiff=diff;
	}
	
	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String adrs) {
		this.addresses=adrs;
	}
	


	
}
