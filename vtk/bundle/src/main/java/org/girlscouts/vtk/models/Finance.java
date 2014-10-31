package org.girlscouts.vtk.models;

import java.io.Serializable;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrMixinTypes="mix:lockable" )
public class Finance  implements Serializable{

	private static final long serialVersionUID = 8084860336298434137L;

	@Field(path=true) 
	String path;
	
	@Field
	private double startingBalance, troopDues, sponsorshipDonations,
		productSalesProceeds,approvedMoneyEarningActivity, interestOnBankAccount,
		
		gsusaRegistration, serviceActivitiesEvents, councilProgramsCamp,
		troopActivities, troopSupplies, gsStorePurchases;
	
	@Field(id=true) 
	private int financialQuarter;
	
	
	
	public double getStartingBalance() {
		return startingBalance;
	}

	public void setStartingBalance(double startingBalance) {
		this.startingBalance = startingBalance;
	}

	public double getTroopDues() {
		return troopDues;
	}

	public void setTroopDues(double troopDues) {
		this.troopDues = troopDues;
	}

	public double getSponsorshipDonations() {
		return sponsorshipDonations;
	}

	public void setSponsorshipDonations(double sponsorshipDonations) {
		this.sponsorshipDonations = sponsorshipDonations;
	}

	public double getProductSalesProceeds() {
		return productSalesProceeds;
	}

	public void setProductSalesProceeds(double productSalesProceeds) {
		this.productSalesProceeds = productSalesProceeds;
	}

	public double getApprovedMoneyEarningActivity() {
		return approvedMoneyEarningActivity;
	}

	public void setApprovedMoneyEarningActivity(double approvedMoneyEarningActivity) {
		this.approvedMoneyEarningActivity = approvedMoneyEarningActivity;
	}

	public double getInterestOnBankAccount() {
		return interestOnBankAccount;
	}

	public void setInterestOnBankAccount(double interestOnBankAccount) {
		this.interestOnBankAccount = interestOnBankAccount;
	}

	public double getGsusaRegistration() {
		return gsusaRegistration;
	}

	public void setGsusaRegistration(double gsusaRegistration) {
		this.gsusaRegistration = gsusaRegistration;
	}

	public double getServiceActivitiesEvents() {
		return serviceActivitiesEvents;
	}

	public void setServiceActivitiesEvents(double serviceActivitiesEvents) {
		this.serviceActivitiesEvents = serviceActivitiesEvents;
	}

	

	

	public double getCouncilProgramsCamp() {
		return councilProgramsCamp;
	}

	public void setCouncilProgramsCamp(double councilProgramsCamp) {
		this.councilProgramsCamp = councilProgramsCamp;
	}

	public double getTroopSupplies() {
		return troopSupplies;
	}

	public void setTroopSupplies(double troopSupplies) {
		this.troopSupplies = troopSupplies;
	}

	public double getGsStorePurchases() {
		return gsStorePurchases;
	}

	public void setGsStorePurchases(double gsStorePurchases) {
		this.gsStorePurchases = gsStorePurchases;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFinancialQuarter() {
		return financialQuarter;
	}

	public void setFinancialQuarter(int financialQuarter) {
		this.financialQuarter = financialQuarter;
	}

	public double getTroopActivities() {
		return troopActivities;
	}

	public void setTroopActivities(double troopActivities) {
		this.troopActivities = troopActivities;
	}

	
}
