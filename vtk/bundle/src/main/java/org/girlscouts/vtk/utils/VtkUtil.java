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

	public static double convertObjectToDouble(Object o) {
		Double parsedDouble = 0.00d;
		if (o != null) {
			try{
				String preParsedCost = ((String) o).replaceAll(",", "").replaceAll(" ", "");
				parsedDouble = Double.parseDouble(preParsedCost);
			} catch (NumberFormatException npe) {
				// do nothing -- leave cost at 0.00
			} catch (ClassCastException cce) {
				// doo nothing -- leave cost at 0.00
			}catch(Exception e){
				// print error
				e.printStackTrace();
			}
		}
		return parsedDouble;
	}

}
