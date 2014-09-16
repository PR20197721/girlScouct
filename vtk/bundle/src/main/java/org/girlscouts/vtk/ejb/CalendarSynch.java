package org.girlscouts.vtk.ejb;


	
	import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.LocationDAO;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.YearPlanComponent;
import org.girlscouts.vtk.models.user.User;

	import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

	public class CalendarSynch {

		
	
		
	 public static void main(String[] args) throws IOException, ValidationException, ParserException {
	  
	  String calFile = "/Users/akobovich/mycalendar.ics";
	  
	  //Creating a new calendar
	  net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
	  calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
	  calendar.getProperties().add(Version.VERSION_2_0);
	  calendar.getProperties().add(CalScale.GREGORIAN);
	  
	  
	  
	  //Creating an event
	  java.util.Calendar cal = java.util.Calendar.getInstance();
	  cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
	  cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

	  VEvent christmas = new VEvent(new Date(cal.getTime()), "Christmas Day");
	  
	  // initialise as an all-day event..
	  christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
	  
	  UidGenerator uidGenerator = new UidGenerator("1");
	  christmas.getProperties().add(uidGenerator.generateUid());

	  calendar.getComponents().add(christmas);
	  
	  
	  
	  
	  
	  //Saving an iCalendar file
	  FileOutputStream fout = new FileOutputStream(calFile);

	  CalendarOutputter outputter = new CalendarOutputter();
	  outputter.setValidating(false);
	  outputter.output(calendar, fout);
	  
	  //Now Parsing an iCalendar file
	  FileInputStream fin = new FileInputStream(calFile);

	  CalendarBuilder builder = new CalendarBuilder();

	  calendar = builder.build(fin);
	  
	  //Iterating over a Calendar
	  for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
	      Component component = (Component) i.next();

	      for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
	          Property property = (Property) j.next();
	      }
	  }
	 }
	
	 
	 
	 public net.fortuna.ical4j.model.Calendar yearPlanCal(User user )throws Exception{
		 
		 java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
			
		  String calFile = "/Users/akobovich/mycalendar.ics";
		  
		 
	  
	  //Creating a new calendar
	  net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
	  calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
	  calendar.getProperties().add(Version.VERSION_2_0);
	  calendar.getProperties().add(CalScale.GREGORIAN);
	  
		  
		  
		  
		  java.util.Iterator itr = sched.keySet().iterator();
		  while( itr.hasNext() ){
			  java.util.Date dt= (java.util.Date) itr.next();
			  YearPlanComponent _comp= (YearPlanComponent) sched.get(dt);
			  
			  Calendar cal = java.util.Calendar.getInstance();
			  cal.setTime(dt);
			  
			  String desc= "", location="";
			  
			  switch( _comp.getType() ){
					case ACTIVITY :
						Activity a = ((Activity) _comp);
						desc = ((Activity) _comp).getName();
						location= a.getLocationName() ==null ? "" : a.getLocationName() +" "+
								a.getLocationAddress() ==null ? "" : a.getLocationAddress();
						break;
					
					case MEETING :
						Meeting meetingInfo =  new MeetingDAOImpl().getMeeting(  ((MeetingE) _comp).getRefId() );
						desc = meetingInfo.getName();		
						location= getLocation( user, ((MeetingE) _comp).getLocationRef());
						break;
				}       	
			  
			  
			
		  
		  
		  //Creating an event
		  //java.util.Calendar cal = java.util.Calendar.getInstance();
		  //cal.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
		  //cal.set(java.util.Calendar.DAY_OF_MONTH, 25);

		  VEvent christmas = new VEvent(new Date(cal.getTime()), desc);
		  
		  
		  System.err.println("Location: "+ location);
		  christmas.getProperties().add(new Location(location));
		  
		  // initialise as an all-day event..
		  christmas.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
		  christmas.getProperties().getProperty(Property.LOCATION).getParameters().add(Value.TEXT);
		  
		  
		  UidGenerator uidGenerator = new UidGenerator("1");
		  christmas.getProperties().add(uidGenerator.generateUid());

		  calendar.getComponents().add(christmas);
		  
		  
		  
		  
		  //Saving an iCalendar file
		  FileOutputStream fout = new FileOutputStream(calFile);

		  CalendarOutputter outputter = new CalendarOutputter();
		  outputter.setValidating(false);
		  outputter.output(calendar, fout);
		  
		  
		  
		 
		  }//end while
		  return calendar;
	 }
	 
	 private String getLocation(User user, String locationId){
		 
		 String fmtLocation = "";
		 try{
			 if( user!=null && user.getYearPlan()!=null && user.getYearPlan().getLocations()!=null)
			  for(int i=0;i<user.getYearPlan().getLocations().size();i++)
				 if( user.getYearPlan().getLocations().get(i).getUid().equals( locationId)){
					 String lName= user.getYearPlan().getLocations().get(i).getLocatinName();
					 String lAddress = user.getYearPlan().getLocations().get(i).getLocationAddress();
					 fmtLocation = (lName==null ? "" : lName) +" " +
							 (lAddress==null ? "" : lAddress);
					
				 }
		 }catch(Exception e){e.printStackTrace();}
		 return fmtLocation;
	 }
}
