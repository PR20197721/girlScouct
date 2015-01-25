package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value = FinanceUtil.class)
public class FinanceUtil {

	@Reference
	TroopDAO troopDAO;

	public Finance getFinances(User user, Troop troop, int qtr) {
		return troopDAO.getFinanaces(user, troop, qtr);
	}

	public void updateFinances(User user, Troop troop,
			java.util.Map<java.lang.String, java.lang.String[]> params) {
		Finance finance = new Finance();
		finance.setApprovedMoneyEarningActivity(Double.parseDouble(params
				.get("amea")[0]));
		finance.setCouncilProgramsCamp(Double.parseDouble(params
				.get("council_pc")[0]));
		finance.setFinancialQuarter(Integer.parseInt(params.get("qtr")[0]));
		finance.setGsStorePurchases(Double.parseDouble(params
				.get("gs_store_purchase")[0]));
		finance.setGsusaRegistration(Double.parseDouble(params
				.get("gsusa_registrations")[0]));
		finance.setInterestOnBankAccount(Double.parseDouble(params
				.get("bank_interest")[0]));
		finance.setProductSalesProceeds(Double.parseDouble(params
				.get("product_sales_proceeds")[0]));
		finance.setServiceActivitiesEvents(Double.parseDouble(params
				.get("service_ae")[0]));
		finance.setSponsorshipDonations(Double.parseDouble(params
				.get("sponsorship_donations")[0]));
		finance.setStartingBalance(Double.parseDouble(params
				.get("starting_balance")[0]));
		finance.setTroopActivities(Double.parseDouble(params
				.get("troop_activities")[0]));
		finance.setTroopDues(Double.parseDouble(params.get("troop_dues")[0]));
		finance.setTroopSupplies(Double.parseDouble(params
				.get("troop_supplies")[0]));
		finance.setPath("/vtk/" + troop.getSfCouncil() + "/troops/"
				+ troop.getId() + "/finances/" + finance.getFinancialQuarter());

		troopDAO.setFinances(user, troop, finance);

		// TODO NOTIFY Council here

	}
}
