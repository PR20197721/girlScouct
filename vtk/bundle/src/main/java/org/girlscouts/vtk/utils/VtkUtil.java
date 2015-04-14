package org.girlscouts.vtk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.time.DateUtils;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;

public enum VtkUtil {
	;

	public static boolean isLocation(java.util.List<Location> locations,
			String locationName) {
		if (locations != null && locationName != null) {
			for (int i = 0; i < locations.size(); i++) {
				if (locations.get(i).getName().equals(locationName)) {
					return true;
				}
			}
		}
		return false;

	}

	public static double convertObjectToDouble(Object o) {
		Double parsedDouble = 0.00d;
		if (o != null) {
			try {
				String preParsedCost = ((String) o).replaceAll(",", "")
						.replaceAll(" ", "");
				parsedDouble = Double.parseDouble(preParsedCost);
			} catch (NumberFormatException npe) {
				// do nothing -- leave cost at 0.00
			} catch (ClassCastException cce) {
				// doo nothing -- leave cost at 0.00
			} catch (Exception e) {
				// print error
				e.printStackTrace();
			}
		}
		return parsedDouble;
	}

	public static final String HASH_SEED = "!3Ar#(8\0102-D\033@";

	public final static String doHash(String str)
			throws NoSuchAlgorithmException {
		/*
		 * String plainText = str + "salt";
		 * 
		 * MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
		 * byte[] hash = messageDigest.digest( plainText.getBytes() );
		 * 
		 * return new String(hash);
		 */

		str += HASH_SEED;

		MessageDigest md = MessageDigest.getInstance("MD5"); // SHA-256");// 512");
		md.update(str.getBytes());

		byte byteData[] = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
					.substring(1));
		}

		return sb.toString();

	}
	
	public static final int getMeetingEndTime( Meeting meeting ){
		int total =0;
		for(int i=0; i< meeting.getActivities().size(); i++){
			total += meeting.getActivities().get(i).getDuration();
		}
		
		
		return total;
	}
	
	
	public static final java.util.List<java.util.Date> getStrCommDelToArrayDates( String date ){
		
		java.util.List <java.util.Date> sched = new java.util.ArrayList<java.util.Date>();
		if( date==null || date.equals("")) return sched;
		
		java.util.StringTokenizer t= new java.util.StringTokenizer( date, ",");
		while( t.hasMoreElements()){
			sched.add( new java.util.Date( Long.parseLong( t.nextToken() ) ) );
		}
		return sched;
		
	}
	
    public static final java.util.List<String> getStrCommDelToArrayStr( String date ){
		
		java.util.List <String> sched = new java.util.ArrayList<String>();
		if( date==null || date.equals("")) return sched;
		
		java.util.StringTokenizer t= new java.util.StringTokenizer( date, ",");
		while( t.hasMoreElements()){
			sched.add(  t.nextToken() );
		}
		return sched;
		
	}
    
    public static final String getArrayDateToStringComDelim( java.util.List<java.util.Date> dates){
    	
    	String str ="";
    	
    	for(int i=0;i<dates.size();i++){
    		str+= dates.get(i) +",";
    	}
    	return str;
    }
    
    
    public static final java.util.List<MeetingE> sortMeetingsById( java.util.List<MeetingE> meetings){
    	
    	//for(int i=0;i<meetings.size();i++)
    	//	System.err.println("tatax sorting before: "+ i+" :" +meetings.get(i).getId());
    	
    	
    	Comparator<MeetingE> comp = new BeanComparator("id");
		Collections.sort(meetings, comp);
		
		//for(int i=0;i<meetings.size();i++)
    	//	System.err.println("tatax sorting after: "+ i+" :" +meetings.get(i).getId());
		
    	return meetings;
    	
    }
    
  public static final java.util.List<MeetingE> setToDbUpdate( java.util.List<MeetingE> meetings){
    	if( meetings!=null )
    	 for(int i=0;i<meetings.size();i++)
    		meetings.get(i).setDbUpdate(true);
	  return meetings; 	
  }
  
  public static final String getArrayDateToLongComDelim( java.util.List<java.util.Date> dates){
  	
  	String str ="";
  	
  	for(int i=0;i<dates.size();i++){
  		str+= dates.get(i).getTime() +",";
  	}
  	return str;
  }
  
  public static final Contact getSubContact( Contact contact, int contactType){
	  
	  if( contact.getContacts()!=null )
		  for(Contact subContact: contact.getContacts()){
			  if( subContact.getType()==1)
				  return subContact;
		  }
	  
	  return null;
  }
  
public static final boolean  isSameDate( java.util.Date date1, java.util.Date date2){
	 return DateUtils.isSameDay(date1, date2);
	  
	  
  }
  
  
}
