package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service
public class ModifiedCheckImpl implements ModifiedChecker {
	private PassiveExpiringMap modifiedContainer;
	private static final Logger log = LoggerFactory.getLogger(ModifiedCheckImpl.class);

	public ModifiedCheckImpl(){
		modifiedContainer = new PassiveExpiringMap(25000);
	}
	public boolean isModified(String sessionId, String yearplanId) {
		String modified_sId = (String) modifiedContainer.get(yearplanId);
		if( modified_sId!=null && !modified_sId.equals(sessionId) ){
			log.debug("isModified : true");
			return true;
		}
		return false;
	}
	public void setModified(String sessionId, String path ){
		if( path==null || !path.endsWith("/yearPlan")){
			return;
		}
		log.debug("inserting -"+ path +"- into modifiedContainer...." + sessionId);
                modifiedContainer.put(path , sessionId);
		log.debug("Checking container size: "+ modifiedContainer.size() +" : "+ modifiedContainer.get(path) );
	}
}
