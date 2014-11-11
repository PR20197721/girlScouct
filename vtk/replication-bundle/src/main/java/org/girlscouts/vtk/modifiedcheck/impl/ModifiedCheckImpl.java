package org.girlscouts.vtk.modifiedcheck.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.modifiedcheck.ModifiedChecker;

@Component
@Service
public class ModifiedCheckImpl implements ModifiedChecker {

	public boolean isModified(String sessionId, String yearplanId) {
		return false;
	}

	public void setModified(String sessionId, String yearplanId) {
		// TODO Auto-generated method stub
		
	}

}
