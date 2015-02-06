package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.commons.io.FileUtils;
import org.girlscouts.vtk.dao.AssetComponentType;
import org.girlscouts.vtk.ejb.EmailMeetingReminder;
import org.girlscouts.vtk.difflib.*;

import java.util.Arrays;
import java.util.Date;
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

	}
	public SentEmail(String path){
		this.path = path;
		this.uid="MR" + new Date().getTime() + "_" + Math.random();
		this.sentDate=new Date();
		
	}

//	@Field
//	private org.girlscouts.vtk.difflib.Patch patch;
	
	@Field
	private String addressList,subject, htmlMsg, addresses;
	
	@Field
	private Date sentDate;

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
	public String getHtmlMsg() {
		return htmlMsg;
	}

	public void setHtmlMsg(String body) {
		this.htmlMsg=body;
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

	
}
