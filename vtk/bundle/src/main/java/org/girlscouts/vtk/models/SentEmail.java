package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.commons.io.FileUtils;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.ejb.EmailMeetingReminder;
import org.girlscouts.vtk.difflib.*;

import java.util.Date;

@Node(jcrMixinTypes = "mix:lockable")
public class SentEmail implements Serializable {
	
	public SentEmail() {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date().getTime();

	}
	public SentEmail(EmailMeetingReminder emr) {
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date().getTime();
		if(emr.getTo()!=null && !emr.getTo().isEmpty()){
			addresses=emr.getTo()+";";
		}
		if(emr.getCc()!=null && !emr.getCc().isEmpty()){
			addresses+=emr.getCc();
		}
		addressList = emr.getEmailToGirlParent()!=null ? "Girls /Parents " : "" ;
		addressList += emr.getEmailToSelf()!=null ? "Self " : "" ;
		addressList += emr.getEmailToTroopVolunteer()!=null ? "Troop Volunteers" : "" ;
		subject=emr.getSubj();
		patch = emr.getHtml();

	}
	public SentEmail(String path){
		this.path = path;
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date().getTime();
		
	}
	
	@Field
	private String addressList,subject, patch, addresses;
	
	@Field
	private long sentDate;

	@Field(path = true)
	private String path;

	@Field(id = true)
	private String uid;
	

	
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid= uid;
	}
	public Long getSentDate() {
		return sentDate;
	}

	public void setSentDate(Long date) {
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
	public String getPatch() {
		return patch;
	}

	public void setPatch(String patch) {
		this.patch=patch;
	}
	
	public String getAddresses() {
		return addresses;
	}

	public void setAddresses(String adrs) {
		this.addresses=adrs;
	}

	
}
