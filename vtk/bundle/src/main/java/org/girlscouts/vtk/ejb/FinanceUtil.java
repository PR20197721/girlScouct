package org.girlscouts.vtk.ejb;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.TroopDAO;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value=FinanceUtil.class)
public class FinanceUtil {

	@Reference 
	TroopDAO troopDAO;
	
	public Finance getFinances(User user, Troop troop, int qtr){
		return troopDAO.getFinanaces(user, troop, qtr);
	}
	
	
	
	public void updateFinances( User user, Troop troop, java.util.Map<java.lang.String,java.lang.String[]> params){
		Finance finance =new Finance();
		finance.setApprovedMoneyEarningActivity( Double.parseDouble(params.get("amea")[0]) );
		finance.setCouncilProgramsCamp( Double.parseDouble( params.get("")[0] ) );
	}
}
