package org.girlscouts.vtk.ejb;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.FinanceConfiguration;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.codehaus.jackson.map.ObjectMapper;

@Component
@Service(value = FinanceUtil.class)
public class FinanceUtil {

	@Reference
	TroopDAO troopDAO;

	public Finance getFinances(User user, Troop troop, int qtr) {
		return troopDAO.getFinanaces(user, troop, qtr);
	}

	public void updateFinances(User user, Troop troop,
			java.util.Map<String, String[]> params) {
		Finance finance = new Finance();
		
		String path = "vtk/" + troop.getSfCouncil() + "/troops/"
				+ troop.getId() + "/finances";
		
		

		System.err.println("Started updating finance");
		System.err.println("Quarter is: " + params.get("qtr")[0]);
		Set<String> keySet = params.keySet();
		System.err.println("Params are:");
		for(String temp : keySet){
			System.err.println("Param: " + temp);
			String[] tempArray = params.get(temp);
			for(String temp2: tempArray){
				System.err.println("Content: " + temp2);
			}
		}
		
		int quarter = Integer.parseInt(params.get("qtr")[0]);
		
		
		troopDAO.setFinances(user, troop, path, quarter, params);

		// TODO NOTIFY Council here

	}
	
	public FinanceConfiguration getFinanceConfig(User user, Troop troop) {
		//return troopDAO.getFinanaceConfiguration(user, troop);
		FinanceConfiguration stubConfig = new FinanceConfiguration();
		List<String> income = new ArrayList<String>();
		income.add("Beginning Balance");
		income.add("Troop Dues");
		income.add("Sponsorship/Donations");
		income.add("Product Sales Proceeds");
		income.add("Approved Money-Earnings Activities");
		income.add("Interest on Bank Accounts");
		
		List<String> expenses = new ArrayList<String>();
		expenses.add("GSUSA Registrations");
		expenses.add("Service Activities/Events");
		expenses.add("Council Programs/Camp");
		expenses.add("Troop Activities");
		expenses.add("Troop Supplies");
		expenses.add("GS Store Purchase");
		
		stubConfig.setExpenseFields(expenses);
		stubConfig.setIncomeFields(income);
		
		return stubConfig;
		
	}

	public void updateFinanceConfiguration(User user, Troop troop,
			java.util.Map<java.lang.String, java.lang.String[]> params) {
		FinanceConfiguration financeConfig = new FinanceConfiguration();
		
		
		
		
		
		financeConfig.setPath("/vtk/" + troop.getSfCouncil() + "/troops/"
				+ troop.getId() + "/finance_config/");

		troopDAO.setFinanceConfiguration(user, troop, financeConfig);

		// TODO NOTIFY Council here

	}
	
	
}


