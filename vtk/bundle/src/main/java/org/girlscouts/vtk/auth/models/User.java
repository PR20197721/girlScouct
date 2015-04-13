package org.girlscouts.vtk.auth.models;

import java.io.Serializable;

public class User implements Serializable {

	private String name, email, phone, mobilePhone, assistantPhone, homePhone,
			contactId, sfUserId;

	// tmp
	// private String ageLevel="brownie";

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	/*
	 * public String getAgeLevel() { return ageLevel; } public void
	 * setAgeLevel(String ageLevel) { this.ageLevel = ageLevel; }
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getAssistantPhone() {
		return assistantPhone;
	}

	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getSfUserId() {
		return sfUserId;
	}

	public void setSfUserId(String sfUserId) {
		this.sfUserId = sfUserId;
	}
}
