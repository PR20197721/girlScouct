package org.girlscouts.vtk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	
	public static final String HASH_SEED = "!3Ar#(8\0102-D\033@";
	public final static String doHash(String str) throws NoSuchAlgorithmException{
		/*
		String plainText = str + "salt";

		MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		byte[] hash = messageDigest.digest( plainText.getBytes() );

      return new String(hash);
      */
		
		str+= HASH_SEED ;
		System.out.println("########### str = " + str);
        MessageDigest md = MessageDigest.getInstance("MD5"); //SHA-256");// 512");
        md.update(str.getBytes());
 
        byte byteData[] = md.digest();
 
       
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
  return sb.toString();

	}
}
