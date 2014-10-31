package org.girlscouts.vtk.ejb;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.models.Finance;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value=FinanceUtil.class)
public class FinanceUtil {

	public Finance getFinances(User user, Troop troop, int qtr){
		Finance finance=null;
		
		return finance;
	}
	
	
	
	public void updateFinances( User user, Troop troop, java.util.Map<java.lang.String,java.lang.String[]> params){
		
	}
}
