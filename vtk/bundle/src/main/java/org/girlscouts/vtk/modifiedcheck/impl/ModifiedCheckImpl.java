package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;

@Component
@Service
public class ModifiedCheckImpl implements ModifiedChecker {

	private PassiveExpiringMap modifiedContainer;
	public ModifiedCheckImpl(){modifiedContainer = new PassiveExpiringMap(25000); }
	public boolean isModified(String sessionId, String yearplanId) {
		String modified_sId = (String) modifiedContainer.get(yearplanId);
		if( modified_sId!=null && !modified_sId.equals(sessionId) ){
			//if(modifiedContainer!=null && (modifiedContainer.get(yearplanId) !=null) ){
			System.err.println("yes");
			return true;
		}
		
		return false;
	}

	

	
	 public void setModified(String sessionId, String path ){
	 //   System.err.println( "SetModif: "+ path );	
		
	    if( path==null || !path.endsWith("/yearPlan") ) return;
	    /*
	    	if( modifiedContainer==null )
	    		modifiedContainer = new PassiveExpiringMap(11000);
	    	*/
	    	System.err.println("inserting -"+ path +"- into modifiedContainer...." + sessionId);
	    	
	    	modifiedContainer.put(path , sessionId);
	    	System.err.println("Checking container size: "+ modifiedContainer.size() +" : "+ modifiedContainer.get(path) );
	    }




	
}
