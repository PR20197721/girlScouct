package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.commons.lang.StringUtils;
import org.girlscouts.vtk.ejb.EmailMeetingReminder;
import org.girlscouts.vtk.difflib.*;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@Node(jcrMixinTypes = "mix:lockable")
public class SentEmail implements Serializable {
	
	@Field
	private String addressList,subject, addresses, htmlDiff;
	
	@Field
	private Date sentDate;

	@Field(path = true)
	private String path;

	@Field(id = true)
	private String uid;
		
	private String htmlMsg;//temp
	
	public SentEmail() {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();

	}
	public SentEmail(EmailMeetingReminder emr) {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();
//		if(emr.getTo()!=null && !emr.getTo().isEmpty()){
//			addresses=emr.getTo()+";";
//		}
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
		List<String> original = new LinkedList<String>();
		List<String> revised = new LinkedList<String>();

		original.addAll(Arrays.asList(template.split("\r?\n|\r")));
		revised.addAll(Arrays.asList(copy.split("\r?\n|\r")));
		
		
		org.girlscouts.vtk.difflib.Patch patch = org.girlscouts.vtk.difflib.DiffUtils.diff(original, revised);
		List<String> diffStrings = org.girlscouts.vtk.difflib.DiffUtils.generateUnifiedDiff("", "", original, patch, 0);	
        String []dfs = new String[diffStrings.size()];
        htmlDiff = StringUtils.join(diffStrings.toArray(dfs), "\n");
   
	}
	
	public String getHtmlMsg(String template) throws org.girlscouts.vtk.difflib.PatchFailedException {
		List<String >diff = new LinkedList<String>();
		String[] diffStrings = this.htmlDiff.split("\n");
		diff = Arrays.asList(diffStrings);
		org.girlscouts.vtk.difflib.Patch patch2 = org.girlscouts.vtk.difflib.DiffUtils.parseUnifiedDiff(diff);
        List<String> original = new LinkedList<String>();
		original.addAll(Arrays.asList(template.split("\r?\n|\r")));
        List<String> result = (List<String>) org.girlscouts.vtk.difflib.DiffUtils.patch(original, patch2);

        String []dsf = new String[result.size()];
        htmlMsg = StringUtils.join( result.toArray(dsf), "\n");		
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
