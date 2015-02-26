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

	public Finance getFinances(Troop troop, int qtr, String currentYear) {
		return troopDAO.getFinances(troop, qtr, currentYear);
	}

	public void updateFinances(Troop troop, String currentYear, java.util.Map<String, String[]> params) {
		Finance finance = new Finance();
		Set<String> keySet = params.keySet();
		for(String temp : keySet){
			String[] tempArray = params.get(temp);
		}
		int quarter = Integer.parseInt(params.get("qtr")[0]);
		troopDAO.setFinances(troop, quarter, currentYear, params);
		// TODO NOTIFY Council here
	}
	
	public FinanceConfiguration getFinanceConfig(Troop troop, String currentYear) {
		return troopDAO.getFinanceConfiguration(troop, currentYear);
	}

	public void updateFinanceConfiguration(Troop troop, String currentYear, java.util.Map<java.lang.String, java.lang.String[]> params) {
		FinanceConfiguration financeConfig = new FinanceConfiguration();
		String expenses = params.get(Finance.EXPENSES)[0];
		String income = params.get(Finance.INCOME)[0];
		String period = params.get(Finance.PERIOD)[0];
		troopDAO.setFinanceConfiguration(troop, currentYear, income, expenses, period);
		// TODO NOTIFY Council here
	}
}


