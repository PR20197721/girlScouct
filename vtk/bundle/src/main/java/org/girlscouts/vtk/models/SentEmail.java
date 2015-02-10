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
	
	public SentEmail() {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();

	}
	public SentEmail(EmailMeetingReminder emr) {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();
		if(emr.getTo()!=null && !emr.getTo().isEmpty()){
			addresses=emr.getTo()+";";
		}
		if(emr.getCc()!=null && !emr.getCc().isEmpty()){
			addresses+=emr.getCc();
		}
		addressList = emr.getEmailToGirlParent()!=null ? "Girls/Parents " : "" ;
		addressList += emr.getEmailToSelf()!=null ? "Self " : "" ;
		addressList += emr.getEmailToTroopVolunteer()!=null ? "Troop Volunteers" : "" ;
		subject=emr.getSubj();
		htmlMsg = emr.getHtml();
        diff = generateDiffString(emr.getTemplate(), emr.getHtml());

	}
	public SentEmail(String path){
		this.path = path;
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();
		
	}

	private String htmlMsg;
	
	@Field
	private String addressList,subject, addresses, htmlDiff;
	
	
	@Field
	private Date sentDate;

	@Field(path = true)
	private String path;

	@Field(id = true)
	private String uid;
	
	@Collection
	private List<JcrCollectionHoldString> diff;

	
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
	public String getHtmlMsg(String template) throws org.girlscouts.vtk.difflib.PatchFailedException {
		//List<String> diff = Arrays.asList(StringUtils.split(htmlDiff,"\n"));
		List<String >diff = new LinkedList<String>();
        for (int i = 0; i < this.diff.size(); i++) {
        	diff.add(this.diff.get(i).getStr());
		}
		org.girlscouts.vtk.difflib.Patch patch2 = org.girlscouts.vtk.difflib.DiffUtils.parseUnifiedDiff(diff);
        List<String> original = new LinkedList<String>();
		original.addAll(Arrays.asList(template.split("\r?\n|\r")));
        List<String> result = (List<String>) org.girlscouts.vtk.difflib.DiffUtils.patch(original, patch2);

        String []dsf = new String[result.size()];
        htmlMsg = StringUtils.join( result.toArray(dsf), "\n");		
        return htmlMsg;
	}

//	public void setHtmlMsg(String body) {
//		this.htmlMsg=body;
//	}
//	
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
//	public org.girlscouts.vtk.difflib.Patch getPatch() {
//		return patch;
//	}
//
//	public void setPatch(org.girlscouts.vtk.difflib.Patch patch) {
//		this.patch=patch;
//	}
	
	public List<JcrCollectionHoldString> getDiff(){
		return diff;
	}
	
	public void setDiff(List<JcrCollectionHoldString> diff){
		this.diff = diff;
	}
	
	
	
	private List<JcrCollectionHoldString> generateDiffString(String template, String copy){
		List<String> original = new LinkedList<String>();
		List<String> revised = new LinkedList<String>();

		original.addAll(Arrays.asList(template.split("\r?\n|\r")));
		revised.addAll(Arrays.asList(copy.split("\r?\n|\r")));
		
		
		org.girlscouts.vtk.difflib.Patch patch = org.girlscouts.vtk.difflib.DiffUtils.diff(original, revised);
		List<String> diffStrings = org.girlscouts.vtk.difflib.DiffUtils.generateUnifiedDiff("", "", original, patch, 0);	
		diff = new LinkedList<JcrCollectionHoldString>();
        for (int i = 0; i < diffStrings.size(); i++) {
        	diff.add(new JcrCollectionHoldString(diffStrings.get(i)));
		}
        String []dsf = new String[diffStrings.size()];
        htmlDiff = StringUtils.join( diffStrings.toArray(dsf), "\n");
		return diff;
   
	}

	
}
