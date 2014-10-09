package org.girlscouts.vtk.ejb;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.dao.MeetingDAO;
import org.girlscouts.vtk.models.Activity;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;

@Component
@Service(value=CalendarUtil.class)
public class CalendarUtil {
    
	@Reference
	TroopUtil troopUtil;
    
    @Reference
    private UserUtil userUtil;


	public void weeklyCal( java.util.Date startDate ){}
		
	
		
		public java.util.List<org.joda.time.DateTime> genSched( org.joda.time.DateTime date, 
				String freq, java.util.List<org.joda.time.DateTime> exclWeeksOfDate,int NumOfMeetings, String existingSched ){
			
			
			
			//add all exclude dates
			java.util.List <org.joda.time.DateTime> exclDates = new java.util.ArrayList <org.joda.time.DateTime> ();
			for(int i=0;i<exclWeeksOfDate.size();i++ ){
				exclDates.addAll( exclWeek( exclWeeksOfDate.get(i) )  );
			}
			
			
			
			
			java.util.List <org.joda.time.DateTime> sched = 
					new CalendarUtil().byWeeklySched( date , freq, exclDates, NumOfMeetings, existingSched);
			
			
			
			return  sched;
		}
		
		public java.util.List<java.util.Date> toUtilDate( java.util.List <org.joda.time.DateTime> sched)
		{
			
			java.util.List <java.util.Date> toRet= new java.util.ArrayList<java.util.Date>();
			for(int i=0;i<sched.size();i++){
				
				
				
				toRet.add( sched.get(i).toDate());
			}
				
			return toRet;
		}
		
		
		
		
		
		
		//generate sched : by weekly
		public java.util.List <org.joda.time.DateTime> byWeeklySched( org.joda.time.DateTime _startDate, String freq, 
								java.util.List <org.joda.time.DateTime> exclDates, int numOfMeetings, String existingSched ){
			
			org.joda.time.DateTime startDate= new  org.joda.time.DateTime( _startDate.toDateMidnight() );
			
			java.util.List<org.joda.time.DateTime> sched = new java.util.ArrayList();
			
			
			org.joda.time.DateTime date = new org.joda.time.DateTime(startDate);
			//-sched.add(_startDate);
			
			int addedDates =0;
			
			if( existingSched!=null ){
			 java.util.StringTokenizer t= new java.util.StringTokenizer( existingSched , ",");
			 while( t.hasMoreElements()){
				long existSchedDate = Long.parseLong(t.nextToken());
				if( new java.util.Date( existSchedDate ).before( new java.util.Date() ) ){ 
					sched.add(new org.joda.time.DateTime(existSchedDate));
					addedDates++;
				}
			 }
			}
			
			
			//for(int i=1;i<numOfMeetings;i++){
			for(int i=1;i<100;i++){
				
	         
				
				if (!exclDates.contains( date ) ){
					
					java.util.Calendar tmp = java.util.Calendar.getInstance();
					tmp.setTimeInMillis( date.getMillis() );
					tmp.set(java.util.Calendar.HOUR, _startDate.getHourOfDay() );
					tmp.set(java.util.Calendar.MINUTE, _startDate.getMinuteOfHour() );
					//date= new org.joda.time.DateTime(tmp.getTimeInMillis());
					
					sched.add(new org.joda.time.DateTime(tmp.getTimeInMillis()));
					addedDates++;
					
					if( addedDates >= numOfMeetings) return sched;
					
				}
				
				
				   if (freq.equals("weekly")){
		                date= date.plusWeeks(1); 
		                
		            } else if (freq.equals("monthly")) {
		                date= date.plusMonths(1); 
		                
		            } else if (freq.equals("biweekly")) {
		            	date= date.plusWeeks(2);
		                
		            }
				
				
				
				    
				
			}
				
			 
			 return sched;
		}
		
		
		
		
		//exclude full week of this date
		//public java.util.List <org.joda.time.LocalDate> exclWeek( java.util.Date date){
		public java.util.List <org.joda.time.DateTime> exclWeek( org.joda.time.DateTime date){
			
			java.util.List<org.joda.time.DateTime> exclDates = new java.util.ArrayList();
			
			org.joda.time.DateTime now = new org.joda.time.DateTime(date);
			/*
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.MONDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.TUESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.WEDNESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.THURSDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.FRIDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SATURDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SUNDAY) );
*/
			
			
			java.util.Calendar cal = now.toGregorianCalendar();
			cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
			for(int i=0;i<7;i++){
				exclDates.add(  new org.joda.time.DateTime(cal.getTimeInMillis()) );
				cal.add(java.util.Calendar.DATE, 1);
			}
		
			
			return exclDates;
			
		}
		
		
		public void createSched(Troop user, String freq, org.joda.time.DateTime p, String exclDate)throws java.lang.IllegalAccessException{
		
			if( !userUtil.hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_EDIT_MEETING_ID ) ){
				 user.setErrCode("112");
				 //return;
				 throw new IllegalAccessException();
			 }
			
			java.util.List <org.joda.time.DateTime>exclDt= new java.util.ArrayList<org.joda.time.DateTime>();
			java.util.StringTokenizer t= new java.util.StringTokenizer( exclDate, ",");
			while( t.hasMoreElements() ){
				exclDt.add( new org.joda.time.DateTime( new java.util.Date(t.nextToken()) ));
			}
			
			String existSched = user.getYearPlan().getSchedule()== null ? "" : user.getYearPlan().getSchedule().getDates();
			
			java.util.List <org.joda.time.DateTime> sched = new CalendarUtil().genSched( p, freq, exclDt,  user.getYearPlan().getMeetingEvents().size(),
					existSched);
			YearPlan plan = user.getYearPlan();
			plan.setCalFreq(freq);
			plan.setCalStartDate( p.getMillis() );
			plan.setCalExclWeeksOf(exclDate);
			
			Cal calendar = plan.getSchedule();
			if(calendar==null) calendar= new Cal();
			calendar.fmtDate(sched);

			plan.setSchedule(calendar);
			user.setYearPlan(plan);
			
			
			//sort
			
			Comparator<MeetingE> comp = new BeanComparator("id");
		    Collections.sort( user.getYearPlan().getMeetingEvents(), comp);
			
			troopUtil.updateTroop(user);
		}
		
		public void updateSched(Troop user, String meetingPath, String time, String date, String ap, 
				String isCancelledMeeting, long currDate)throws java.lang.IllegalAccessException{
			
			if( !userUtil.hasAccess(user, user.getCurrentTroop(), Permission.PERMISSION_UPDATE_MEETING_ID ) ){
				 user.setErrCode("112");
				// return;
				 throw new IllegalAccessException();
			 }
			
			java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat("MM/dd/yyyy hh:mm a");
			
			YearPlan plan = user.getYearPlan();
			Cal cal = plan.getSchedule();
			
			
			
			java.util.Date newDate = null;
			try{ newDate =dateFormat4.parse( date +" "+time + " "+ap); }catch(Exception e){e.printStackTrace();}
			
			String sched = cal.getDates();
			sched = sched.replace(""+currDate, newDate.getTime()+"");
			cal.setDates(sched);
			
			
			
			java.util.List<MeetingE>meetings = user.getYearPlan().getMeetingEvents();
			for(int i=0;i<meetings.size();i++){
				MeetingE meeting = meetings.get(i);
				if( meeting.getPath().equals( meetingPath ) ){
					meeting.setCancelled(isCancelledMeeting);
					user.getYearPlan().setAltered("true");
					
				}
			}
			
			troopUtil.updateTroop(user);
			
		}
		
		public void resetCal(Troop user)throws java.lang.IllegalAccessException{
			
			user.getYearPlan().setSchedule(null);
			troopUtil.updateTroop(user);
		}
	
}
