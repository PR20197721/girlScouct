package org.girlscouts.vtk.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.time.DateUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.ejb.UserUtil;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.Contact;
import org.girlscouts.vtk.models.Location;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;

@Component
@Service(value = VtkUtil.class)
public class VtkUtil  implements ConfigListener{
	
	@Reference
	ConfigManager configManager;
	
	
	private static String gsNewYear;

	@SuppressWarnings("rawtypes")
	public void updateConfig(Dictionary configs) {
		
System.err.println("tata xxx T");		
		gsNewYear = (String) configs.get("gsNewYear");
	}
	
	public void caca(){System.err.println(" tata xxx 1 : "+ gsNewYear);}
	
	// do not use these objects explicitly as they are not thread safe
	// use the two synchronized  parseDate and formatDate utility methods below
	public static final SimpleDateFormat FORMAT_MMddYYYY = new SimpleDateFormat("MM/dd/yyyy");
	public static final SimpleDateFormat FORMAT_hhmm_AMPM = new SimpleDateFormat("hh:mm a");
	public static final SimpleDateFormat FORMAT_hhmm = new SimpleDateFormat("hh:mm");
	public static final SimpleDateFormat FORMAT_AMPM = new SimpleDateFormat("a");
	public static final SimpleDateFormat FORMAT_MONTH = new SimpleDateFormat("MMM");
	public static final SimpleDateFormat FORMAT_DAY_OF_MONTH = new SimpleDateFormat("d");
	public static final SimpleDateFormat FORMAT_MONTH_DAY = new SimpleDateFormat("MMM d");
	public static final SimpleDateFormat FORMAT_MMMM_dd_hhmm_AMPM = new SimpleDateFormat("MMMM dd hh:mm a");
	public static final SimpleDateFormat FORMAT_MEETING_REMINDER = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
	public static final SimpleDateFormat FORMAT_CALENDAR_DATE = new SimpleDateFormat( "MMM dd, yyyy hh:mm a");
	public static final SimpleDateFormat FORMAT_FULL = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	public static final SimpleDateFormat FORMAT_Md = new SimpleDateFormat("M/d");
	public static final SimpleDateFormat FORMAT_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

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
    	if( meetings==null ) return meetings;
    	Comparator<MeetingE> comp = new BeanComparator("id");
		Collections.sort(meetings, comp);
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

public static String formatDate(SimpleDateFormat f, Date d) {
    synchronized(f) {
        return f.format(d);
    }
}
public static Date parseDate(SimpleDateFormat f, String d) throws ParseException{
    synchronized(f) {
		return f.parse(d);
    }
}

public static String getCouncilInClient(HttpServletRequest request){
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		 for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals("vtk_referer_council")) {
						return cookies[i].getValue();
			}
		}
	}
	return null;
}

public static String getYearPlanBase(User user, Troop troop){

	/*
	String ypBase= "/vtk";
	java.util.Calendar now= java.util.Calendar.getInstance();
	if( now.get(java.util.Calendar.MONTH ) >= java.util.Calendar.AUGUST ) //after Aug 1 -> NEXT YEAR
		ypBase += now.get(java.util.Calendar.YEAR) +1;
	else
		ypBase += now.get(java.util.Calendar.YEAR);
	
	return ypBase+"/";
	*/
	
	int currentGSYear= getCurrentGSYear();
	if( currentGSYear==2014)
		return "/vtk/";
	else
		return "/vtk"+ currentGSYear +"/";
	
	//return "/vtk"+ getCurrentGSYear() +"/";
	
}



/*GS Year starts Aug 1 */
public static int getCurrentGSYear(){
	
	System.err.println ("tatat222222 :" +gsNewYear) ;
	if( gsNewYear==null )
		gsNewYear = new VtkUtil().getX();
	System.err.println ("tatat333333 :" +gsNewYear) ;
	
	java.util.Calendar now= java.util.Calendar.getInstance();
	if( now.get(java.util.Calendar.MONTH ) >= java.util.Calendar.AUGUST ) //after Aug 1 -> NEXT YEAR
		return now.get(java.util.Calendar.YEAR) ;
	else
		return now.get(java.util.Calendar.YEAR) -1;	
}


private String getX(){
	return gsNewYear;
}

}//end class
