package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;

@Component
@Service
public class ModifiedCheckImpl implements ModifiedChecker {

	private PassiveExpiringMap modifiedContainer;
	
	public boolean isModified(String sessionId, String yearplanId) {
		if(modifiedContainer.get(yearplanId)!=null )
			return true;
		
		return false;
	}

	

	
	 public void setModified(String sessionId, String path ){
	    System.err.println( "SetModif: "+ path );	
		 if( path==null || !path.endsWith("/jcr:lastModified") ) return;
	    	
	    	if( modifiedContainer==null )
	    		modifiedContainer = new PassiveExpiringMap(60000);
	    	
	    	modifiedContainer.put(path , null);
	    }




	
}
