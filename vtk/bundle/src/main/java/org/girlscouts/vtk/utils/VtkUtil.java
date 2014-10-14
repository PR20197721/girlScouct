package org.girlscouts.vtk.utils;

import org.girlscouts.vtk.models.Location;

public enum VtkUtil {;

	public static boolean isLocation(java.util.List<Location> locations, String locationName){
		if( locations!=null && locationName!=null ) {
			for(int i=0;i< locations.size();i++) {
				if( locations.get(i).getName().equals(locationName) ) {
					return true;
				}
			}
		}
		return false;
		
	}


}
