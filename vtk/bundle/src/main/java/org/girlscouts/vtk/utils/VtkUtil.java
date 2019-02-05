package org.girlscouts.vtk.utils;

import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.time.DateUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.felix.scr.annotations.Properties;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;
import org.apache.sling.api.SlingHttpServletRequest;
import org.girlscouts.vtk.auth.models.ApiConfig;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.ejb.VtkError;
import org.girlscouts.vtk.helpers.ConfigListener;
import org.girlscouts.vtk.helpers.ConfigManager;
import org.girlscouts.vtk.models.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Component(metatype = true, immediate = true)
@Service(value = VtkUtil.class)
@Properties ({
        @Property(name="label", value="Girl Scouts VTK Utils"),
        @Property(name="description", value="Girl Scouts VTK Utils")
})
public class VtkUtil  implements ConfigListener{
	
	@Reference
	ConfigManager configManager;
	
	private static String gsNewYear;
	private static String vtkHolidays[], gsCouncils[];
	private static String gsFinanceYearCutoffDate;
	@SuppressWarnings("rawtypes")
	public void updateConfig(Dictionary configs) {
	
		gsNewYear = (String) configs.get("gsNewYear");
		vtkHolidays= (String[]) configs.get("vtkHolidays");
		gsFinanceYearCutoffDate=  (String)configs.get("gsFinanceYearCutoffDate");
		gsCouncils = (String[]) configs.get("councilMapping");
	}

	@Activate
	public void init() {
		configManager.register(this);
	}
	
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
	public static final SimpleDateFormat FORMAT_YYYYMMdd = new SimpleDateFormat("yyyyMMdd");

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
		if(meeting.getActivities()!=null)
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

	if( user!=null && user.getCurrentYear() !=null ) {
		return "/vtk"+ ("2014".equals(user.getCurrentYear()) ? "" : user.getCurrentYear()) +"/";
	}
	
		
	int currentGSYear= getCurrentGSYear();
	if( currentGSYear==2014)
		return "/vtk/";
	else
		return "/vtk"+ currentGSYear +"/";
	
}

public static String getYearPlanBase_previous(User user, Troop troop){

	if( user!=null && user.getCurrentYear() !=null ) {
		int yr = Integer.parseInt(user.getCurrentYear())-1;
		return "/vtk"+ (yr ==2014 ? "" : yr) +"/";
		
	}
	
		
	int currentGSYear= getCurrentGSYear();
	currentGSYear = currentGSYear-1;
	
	if( currentGSYear==2014)
		return "/vtk/";
	else
		return "/vtk"+ currentGSYear +"/";
	
}



/* TODO: this is used by ReplicationManager. By using this method instead of the static one,
 * ReplicationManager waits VtkUtil to start first.
 */
public String _getYearPlanBase(User user, Troop troop) {
    return VtkUtil.getYearPlanBase(user, troop);

}


/*GS Year starts Aug 1 */
public static int getCurrentGSYear(){
	String _gsNewYear = gsNewYear;
	if( _gsNewYear==null )	_gsNewYear= "0701";
	
	int month = Integer.parseInt( _gsNewYear.substring(0, 2) );
	int date=  Integer.parseInt( _gsNewYear.substring(2) );
	java.util.Calendar now= java.util.Calendar.getInstance();
	//if( now.get(java.util.Calendar.MONTH ) >= java.util.Calendar.AUGUST ) //after Aug 1 -> NEXT YEAR
	if( now.get(java.util.Calendar.MONTH ) >= (month-1)) //after Aug 1 -> NEXT YEAR		
		return now.get(java.util.Calendar.YEAR) ;
	else
		return now.get(java.util.Calendar.YEAR) -1;	
}

/* TODO: this is used by ReplicationManager. By using this method instead of the static one,
 * ReplicationManager waits VtkUtil to start first.
 */
public int _getCurrentGSYear() {
    return VtkUtil.getCurrentGSDate();
}

/*GS Year starts Aug 1 */
public static int getCurrentGSMonth(){
	String _gsNewYear = gsNewYear;
	return  Integer.parseInt( _gsNewYear.substring(0, 2) );
}


/*GS Year starts Aug 1 */
public static int getCurrentGSDate(){
	String _gsNewYear = gsNewYear;
	return  Integer.parseInt( _gsNewYear.substring(2) );
}

public static String getNewGSYearDateString() {
    return gsNewYear;
}

public static java.util.Map<Long, String> getVtkHolidays( User user, Troop troop){

	String[] mappings = vtkHolidays;
	Map<Long, String> councilMap = new HashMap<Long, String>();
	if (mappings != null) {
		for (int i = 0; i < mappings.length; i++) {
			String[] configRecord = mappings[i].split("::");
			if (configRecord.length >= 2) {
				try{
					councilMap.put( Long.valueOf( FORMAT_YYYYMMdd.parse( configRecord[0] ).getTime() ), configRecord[1]);
				}catch(Exception e){e.printStackTrace();}
			} else {
				
			}
		}
	}
	return councilMap;
	
}


/**
 * This method makes a "deep clone" of any Java object it is given.
 */
 public static Object deepClone(Object object) {
   try {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     ObjectOutputStream oos = new ObjectOutputStream(baos);
     oos.writeObject(object);
     ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
     ObjectInputStream ois = new ObjectInputStream(bais);
     return ois.readObject();
   }
   catch (Exception e) {
     e.printStackTrace();
     return null;
   }
 }

 
 
 public static boolean hasPermission(Troop troop, int permissionId) {
		java.util.Set<Integer> myPermissionTokens = troop.getTroop().getPermissionTokens();
		if (myPermissionTokens != null && myPermissionTokens.contains(permissionId)) {
			return true;
		}
		return false;
	}
 
 public static User getUser(HttpSession session){
	 
	    org.girlscouts.vtk.auth.models.ApiConfig apiConfig = null;
		try {
			if (session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()) != null) {
				apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
			} else {
			   return null;
			}
		} catch (ClassCastException cce) {
			return null;
		} 
	 
		
	return ((org.girlscouts.vtk.models.User) session
 			.getAttribute(org.girlscouts.vtk.models.User.class
 					.getName()));
 }
 
 public static Troop getTroop( HttpSession session){
	 
	 org.girlscouts.vtk.auth.models.ApiConfig apiConfig = null;
		try {
			if (session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()) != null) {
				apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
			} else {
			   return null;
			}
		} catch (ClassCastException cce) {
			return null;
		} 
		
	 return (Troop) session.getValue("VTK_troop");
 }
 
 public static boolean isValidUrl_withTroop(User user, Troop troop, String uri) {
	 
	if( uri.trim().indexOf("/myvtk/")==-1 ) return true;
 	if( user==null || troop==null || uri==null || uri.trim().equals("") )
 		return false;
 		
 	try{
		String str =uri.substring(uri.indexOf("/myvtk/")+7);
	 	StringTokenizer t = new StringTokenizer( str,"/");
	 	
	 	String cid_tid= t.nextToken();
	 	//String tid= t.nextToken();
	 	
	 	StringTokenizer tt= new StringTokenizer(cid_tid, ".");
	 	String cid= tt.nextToken();
	 	String tid= tt.nextToken();
	 			
	 	if( cid.trim().toLowerCase().equals(troop.getSfCouncil().trim().toLowerCase()) && 
	 			( tid.equals("0") || tid.trim().toLowerCase().equals(troop.getSfTroopId().trim().toLowerCase()) ) ){
	 		return true;
	 	}
	 	
 	}catch(Exception e){e.printStackTrace();}
 return false;	
 }
 
 public static boolean isValidUrl(User user, Troop troop, String uri, String councilName) {
	 
	if( uri.trim().indexOf("/myvtk/")==-1 ) return true;
 	if( user==null || troop==null || uri==null || uri.trim().equals("") )
 		return false;
 		
 	try{
		String str =uri.substring(uri.indexOf("/myvtk/")+7);
	 	StringTokenizer t = new StringTokenizer( str,"/");
	 	
	 	String cid= t.nextToken();
	 	
	 			
	 	if( cid.trim().toLowerCase().equals( councilName ) ){//troop.getSfCouncil().trim().toLowerCase()) ){
	 		return true;
	 	}
	 	
 	}catch(Exception e){e.printStackTrace();}
 return false;	
 }
 

 public static java.util.List<String> countResourseCategories( java.util.Collection<bean_resource> resources ) {
	 java.util.List<String> categories = new java.util.ArrayList<String>();
	 java.util.Iterator <bean_resource>itr = resources.iterator();
	 while( itr.hasNext() ){
		
		 String resource_category = itr.next().getCategoryDisplay();
		 if( !categories.contains(resource_category))
			 categories.add(resource_category);
	 }
	 return categories;
 }
 
 public static java.util.List<VtkError> getVtkErrors(HttpServletRequest request){
	 java.util.List<VtkError> errors=new java.util.ArrayList<VtkError> (); 
	 try{
		 HttpSession session = request.getSession();
		 if( session.getAttribute("fatalError")!=null ){
	         org.girlscouts.vtk.ejb.VtkError err = null;
	         try{ 
	        	 err= (org.girlscouts.vtk.ejb.VtkError) session.getAttribute("fatalError");
	         }catch(Exception e){
	        	 e.printStackTrace();
	         }
	         if( err!=null )
	        	 errors.add( err );
		 }
	   
		 ApiConfig apiConfig= getApiConfig(session);
		 if( apiConfig!=null && apiConfig.getErrors()!=null )
	   		  errors.addAll(apiConfig.getErrors());
	 }catch(Exception e){e.printStackTrace();}
	 return errors;
 }
 
 public static ApiConfig getApiConfig( HttpSession session){
	    org.girlscouts.vtk.auth.models.ApiConfig apiConfig = null;

		try {
			if (session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()) != null) {
				apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
			} else {
			   return null;
			}
		} catch (ClassCastException cce) {
			return null;
		} 

	return apiConfig;
 }
 
 
 public static void setVtkErrors(HttpServletRequest request, java.util.List<VtkError> errors){
	try{
		 HttpSession session = request.getSession();
		 ApiConfig apiConfig= getApiConfig(session);
		 if( apiConfig!=null && apiConfig.getErrors()!=null && errors!=null )
	   		  apiConfig.setErrors( errors );
	 }catch(Exception e){e.printStackTrace();}
	 
 }
 
 public static void rmVtkError(HttpServletRequest request, String vtkErrId){
		try{			
			java.util.List<VtkError> errors =  getVtkErrors( request );
			if( errors==null || errors.size()<=0 ) return;		
			for(int i=0;i<errors.size();i++){	
				VtkError  error = errors.get(i);
				if( error!=null && error.getId()!=null && error.getId().equals(vtkErrId)){					
					errors.remove(i);
					ApiConfig apiConfig= getApiConfig(request.getSession());
					apiConfig.setErrors( errors );
					return;
				}
			}
		}catch(Exception e){e.printStackTrace();}	 
	 }
 
 
public static void cngYear(HttpServletRequest request, User user, Troop troop){
	
	String yr = request.getParameter("cngYear");
	
	if( yr!=null && yr.equals( getCurrentGSYear()+"") ) 
		return;
	else if( yr==null && user.getCurrentYear().equals( getCurrentGSYear()+""))
		return;
	
	String newYear = yr ==null ? user.getCurrentYear() : yr;	
	user.setCurrentYear( newYear );
	
    
}


public static boolean isAnyOutdoorActivitiesInMeeting(Meeting meeting){
	if( meeting ==null || meeting.getActivities()==null  ){
		return false;
	}
	
	Activity activity= 
			meeting.getActivities().stream()
			.filter( x -> (x.getMultiactivities() !=null))
			 .flatMap(x ->  x.getMultiactivities().stream() )
			.filter(a -> a.getOutdoor() && (a.getIsSelected()!=null && a.getIsSelected()) )
			.findAny()
		    .orElse(null);
	
	return (activity == null) ? false: true;
}


public static boolean isAnyOutdoorActivitiesInMeetingAvailable(Meeting meeting){
	if( meeting ==null || meeting.getActivities()==null  ){
		return false;
	}
	
	Activity activity= 
			meeting.getActivities().stream()
			.filter( x -> (x.getMultiactivities() !=null))
			 .flatMap(x ->  x.getMultiactivities().stream() )
			.filter(a -> a.getOutdoor() )
			.findAny()
		    .orElse(null);
	
	return (activity == null) ? false: true;
}

public static String sortDates(String dates){
	java.util.List<Date> container = new java.util.ArrayList();
	java.util.StringTokenizer t= new StringTokenizer( dates, ",");
	while( t.hasMoreElements()){
	
		java.util.Date date = new java.util.Date( Long.parseLong(t.nextToken()  ) );
		container.add( date );
	}
	
	java.util.Collections.sort(container);
	String toRet = "";
	for(int i=0;i<container.size();i++){
		toRet += container.get(i).getTime() +",";
	}
	return toRet;
}

public static MeetingE findMeetingById(java.util.List<MeetingE>meetings, String mid){//, String aid, boolean isOutdoor){

	if( meetings==null || mid==null ) return null;
	
	return meetings.stream()
		    .filter( meeting -> mid.equals( meeting.getUid()))
		    .findAny()
		    .orElse(null);
	
}

public static Activity findActivityByPath(java.util.List<Activity>activities, String aid){

	if( activities==null || aid ==null) return null;
	
	return activities.stream()
			 .filter( activity -> aid.equals( activity.getPath() ) )
			    .findAny()
			    .orElse(null);
		
}




public static void changePermission(User user, Troop troop, int chngPerm){
	
	switch (chngPerm) {
	case 2:
		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));
		break;
	case 11:

		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_LEADER_PERMISSIONS));
		break;
	case 12:

		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_MEMBER_2G_PERMISSIONS));
		break;
	case 13:
		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_MEMBER_1G_PERMISSIONS));
		break;

	case 14:
		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_MEMBER_NO_TROOP_PERMISSIONS));
		break;

	case 15:
		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_MEMBER_TROOP_PERMISSIONS));
		break;

	default:
		troop.getTroop()
				.setPermissionTokens(
						Permission
								.getPermissionTokens(Permission.GROUP_GUEST_PERMISSIONS));

		break;
	}
}


public static  java.util.List<Meeting>  sortMeetings (java.util.List<Meeting> meetings){
	Collections.sort(meetings,
            java.util.Comparator.comparing(Meeting::getLevel)
                   .thenComparing(Meeting::getName) );
	return meetings;
}

public static java.util.List<MeetingE> schedMeetings(java.util.List<MeetingE> meetings, String sched){

	
	//sort meetings by Date
	Comparator<MeetingE> comp = new BeanComparator("id");
			if (meetings != null)
				Collections.sort(meetings, comp);
	
	int count=0;
	java.util.StringTokenizer t= new StringTokenizer( sched, ",");
	while( t.hasMoreElements()){
	    if( meetings.size() <= count){ System.err.println("CalendarUtil.schedMeetings Found extra sched date. Num of Dates > meetings."); break;}
		java.util.Date date = new java.util.Date( Long.parseLong(t.nextToken()  ) );
		meetings.get(count).setDate(date);
		count ++;
	 
	}
	
	return meetings;
}

	public static Map<String, Long> countUniq(java.util.List<String> container){
		return container.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
	}
	
	/* 
	 * GS Finance Year 
	 * IF NOT CONFIG, then let it fail
	 * */
	public static int getCurrentGSFinanceYear(){
		java.util.Calendar cutOffDate= java.util.Calendar.getInstance();
		cutOffDate.setTime(new java.util.Date(gsFinanceYearCutoffDate) );
		
		java.util.Calendar now = java.util.Calendar.getInstance();
		
		if( now.getTimeInMillis() < cutOffDate.getTimeInMillis())
			return cutOffDate.get(java.util.Calendar.YEAR) -1 ;
		else
			return cutOffDate.get(java.util.Calendar.YEAR);
			
	}
	
	public static String formatAgeGroup(String sf_age_group){
		return ( sf_age_group ==null || sf_age_group.indexOf("-") ==-1 ) ? sf_age_group :
			sf_age_group.substring( sf_age_group.indexOf("-")+1 ).toLowerCase();
	}
	
	public static List<String> getDistinctMeetingPlanTypes(List<Meeting> meetings){
		
		List<String> meetingTypes = 
				meetings.stream()
			              .map(e -> e.getMeetingPlanType() )
			              .collect(Collectors.toList());
	
		return meetingTypes.stream().distinct().sorted().collect(Collectors.toList());
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
	
	public static Map<String, List<Meeting>> sortMeetingByMeetingType(List<Meeting> meetings, List<String> meetingPlanTypes){
		Map<String, List<Meeting>> orderedMeetingsByType = new TreeMap();
		
		for( String meetingType: meetingPlanTypes){ 
  		  List<Meeting> meetingsByType = meetings.stream()
			    .filter( meeting -> meetingType.equals( meeting.getMeetingPlanType()))
			    .collect(Collectors.toList());
  		  
  		    //sort by name
  		  	Collections.sort(meetingsByType,
  	            java.util.Comparator.comparing(Meeting::getName) );
		
  		  orderedMeetingsByType.put( meetingType, meetingsByType );
		}
		return orderedMeetingsByType;
	}
	
	public static  void  sortMeetingsByName (java.util.List<Meeting> meetings){
		Collections.sort(meetings,
	            java.util.Comparator.comparing(Meeting::getName) );
		
	}
	

	public static String Stem(String text, String language)throws Exception{
        StringBuffer result = new StringBuffer();
        if (text!=null && text.trim().length()>0){
            StringReader tReader = new StringReader(text);
            Analyzer analyzer = new SnowballAnalyzer(Version.LUCENE_35,language);
            TokenStream tStream = analyzer.tokenStream("contents", tReader);
            TermAttribute term = tStream.addAttribute(TermAttribute.class);

            try {
                while (tStream.incrementToken()){
                    result.append(term.term());
                    result.append(" ");
                }
            } catch (IOException ioe){
                System.out.println("Error: "+ioe.getMessage());
            }
        }

        // If, for some reason, the stemming did not happen, return the original text
        if (result.length()==0)
            result.append(text);
        return result.toString().trim();
    }

	public static boolean isRenewMembership(int membershipYear){
		
            Calendar rightNow = Calendar.getInstance();
   		
    		if( membershipYear == rightNow.get(Calendar.YEAR) &&
          		( rightNow.get(Calendar.MONTH) > 2 &&  rightNow.get(Calendar.MONTH) < 9)
          	){
          		return true;
			}
			return false;
		
	}
	
public  static JSONObject getJsonFromRequest(SlingHttpServletRequest request) throws IOException{
		
		HttpServletRequest _request = (HttpServletRequest ) request;
	    StringBuilder sb = new StringBuilder();
	    BufferedReader br = _request.getReader();
	
	    String str;
	    while( (str = br.readLine()) != null ){
	        sb.append(str);
	    }
	

	   JSONObject jsonObject = null;
	   try{ 
		   jsonObject=  (JSONObject) JSONValue.parse( sb.toString() );
	   }catch(Exception e){e.printStackTrace();}
	   
	   return jsonObject;
	}


	public static  String  formatLevel (User user, Troop troop){
		if( troop==null || troop.getTroop()==null || troop.getTroop().getGradeLevel()==null) return "";
		String level = troop.getTroop().getGradeLevel().toLowerCase();
		// The field in SF is 1-Brownie, we need brownie
		if (level.contains("-")) {
			level = level.split("-")[1];
		}
		return level;
	}

public static List<Meeting> filterUniqMeetingByPath( List<Meeting> meetings){
	return ( List<Meeting> )meetings.stream().filter(distinctByKey(Meeting::getPath))
			.collect(Collectors.toList());
}

public static String fmtHtml(String _html){
	String formattedString;

	// Clean the HTML of things that will break the PDF parser.
	try {
		_html = PDFHtmlFormatter.format(_html);
	}catch(Exception e){
		e.printStackTrace();
	}

	try{		
		String CSS="";
		CSSResolver cssResolver = new StyleAttrCSSResolver();
        CssFile cssFile = XMLWorkerHelper.getCSS(new ByteArrayInputStream(CSS.getBytes()));
        cssResolver.addCss(cssFile);
 
        // HTML
        HtmlPipelineContext htmlContext = new HtmlPipelineContext(null);
        htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());

        // Pipelines
        ElementList elements = new ElementList();
        ElementHandlerPipeline pdf = new ElementHandlerPipeline(elements, null);
        HtmlPipeline html = new HtmlPipeline(htmlContext, pdf);
        CssResolverPipeline css = new CssResolverPipeline(cssResolver, html);
        
        XMLWorker worker = new XMLWorker(css, true);
        XMLParser p = new XMLParser(worker);
		p.parse(new ByteArrayInputStream(_html.getBytes()));
        
		formattedString = _html;
	}catch(Exception e){
		System.err.println("VtkUtil.fmtHtml: Found error parsing html. Html not well formatted."+ _html);
		formattedString = Jsoup.parse( _html ).text();
	}
	
	return (formattedString ==null || "".equals(formattedString.trim())) ? _html : formattedString;
}

public static Activity findSelectedActivity(java.util.List<Activity> activities){

	if( activities==null ) return null;
	
	return activities.stream()
		    .filter( activity -> (activity.getIsSelected() ) )
		    .findAny()
		    .orElse(null);
}

/*
 * return Date object with date, time, hour in local time but EST time (VTK default timezone)
 * EX: SF time 2018-04-03T23:00:00.000Z timezone (GMT-05:00) Central Daylight Time (America/Chicago)
 * returns 2018-04-03T23:00:00.000-05:00
 */
public static String getSFActivityDate(String eventStartDateStr, String sfTimeZoneLabel){
	String fmtDate = null;
	try{
		String timeZone= "America/New_York"; 
		if( sfTimeZoneLabel!=null && !"".equals(sfTimeZoneLabel) ){
			timeZone = sfTimeZoneLabel.substring(sfTimeZoneLabel.lastIndexOf("(")+1, sfTimeZoneLabel.length()-1);
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		TimeZone UTC_TZ = TimeZone.getTimeZone("UTC");
	    dateFormat.setTimeZone(UTC_TZ);
		java.util.Date UTC_DATE;
		try{
			UTC_DATE = dateFormat.parse(eventStartDateStr);
		}catch (Exception e){
			e.printStackTrace();
			UTC_DATE = dateFormat2.parse(eventStartDateStr);
		}
		
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        dateFormat.setTimeZone(tz);
        
        fmtDate = dateFormat.format(UTC_DATE) ;
       
	}catch(Exception e){e.printStackTrace();}
	return fmtDate;
	
}

public static boolean isAllMultiActivitiesSelected(java.util.List<Activity> activities){

	if( activities==null ) return false;
	boolean isFoundMultiActivityNotSelected= false;
	
	java.util.List<Activity> multiActivities = getMeetingMultiActivities( activities );
	
	//no multi-activities
	if( multiActivities ==null || multiActivities.size()==0 ) return true;
	
	for(Activity _activity : multiActivities){
		Activity multiActivity = _activity.getMultiactivities().stream()
			    .filter( activity -> activity.getIsSelected() )
			    .findAny()
			    .orElse(null);
		if( multiActivity ==null ){
			isFoundMultiActivityNotSelected = true;
			return false;
		}
			   
	}
     return isFoundMultiActivityNotSelected ? false : true;
}

public static java.util.List<Activity> getMeetingMultiActivities( java.util.List<Activity> activities ){
	return activities.stream()
		    .filter( activity -> activity.getMultiactivities().size()>1 )
		    .collect(Collectors.toList());
}

public static String getYearPlanStartDate(Troop troop){
	
	if( troop==null || troop.getYearPlan()==null || troop.getYearPlan().getSchedule() ==null || troop.getYearPlan().getSchedule().getDates() ==null || "".equals(troop.getYearPlan().getSchedule().getDates()) )
		return null;
	
	String startDate=null;
	try{
		StringTokenizer td = new StringTokenizer(troop.getYearPlan().getSchedule().getDates(), ",");
		startDate =  td.nextToken();
	}catch(Exception e){e.printStackTrace();}
	return startDate;	
 }


public static List<String> getCouncils(){
	List <String> councilCodes = new ArrayList<String>();
	if (gsCouncils != null) {
		for (int i = 0; i < gsCouncils.length; i++) {
			String[] configRecord = gsCouncils[i].split("::");
			if (configRecord.length >= 2) {
				councilCodes.add(configRecord[0]);
			} else {
				System.err.println("Malformatted council mapping record: " + gsCouncils[i]);
			}
		}
	} 
	return councilCodes;
}
}//end class
