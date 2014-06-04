package org.girlscouts.vtk.ejb;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.dao.UserDAO;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.user.User;

@Component
@Service(value=CalendarUtil.class)
public class CalendarUtil {
    @Reference
    UserDAO userDAO;

	public void weeklyCal( java.util.Date startDate ){}
		
	

		
		
		
		public java.util.List<org.joda.time.DateTime> genSched( org.joda.time.DateTime date, 
				String freq, java.util.List<org.joda.time.DateTime> exclWeeksOfDate ){
			
			
			
			//add all exclude dates
			java.util.List <org.joda.time.DateTime> exclDates = new java.util.ArrayList <org.joda.time.DateTime> ();
			for(int i=0;i<exclWeeksOfDate.size();i++ )
				exclDates.addAll( exclWeek( exclWeeksOfDate.get(i) )  );
			
			
			java.util.List <org.joda.time.DateTime> sched = 
					new CalendarUtil().byWeeklySched( date , freq, exclDates);
			
			
			
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
		public java.util.List <org.joda.time.DateTime> byWeeklySched( org.joda.time.DateTime startDate, String freq, 
								java.util.List <org.joda.time.DateTime> exclDates ){
			
			java.util.List<org.joda.time.DateTime> sched = new java.util.ArrayList();
			
			
			org.joda.time.DateTime date = new org.joda.time.DateTime(startDate);
			sched.add(date);
			
			while( date.getYear() == startDate.getYear() ){
				
	            if (freq.equals("weekly")){
	                date= date.plusWeeks(1); 
	            } else if (freq.equals("monthly")) {
	                date= date.plusMonths(1);
	            }
				
				
				if( date.getYear() != startDate.getYear() ) break;
				
				if (!exclDates.contains( date ) )
					sched.add(date);
				
				    
				
			}
				
			 
			 return sched;
		}
		
		
		
		
		//exclude full week of this date
		//public java.util.List <org.joda.time.LocalDate> exclWeek( java.util.Date date){
		public java.util.List <org.joda.time.DateTime> exclWeek( org.joda.time.DateTime date){
			
			java.util.List<org.joda.time.DateTime> exclDates = new java.util.ArrayList();
			
			org.joda.time.DateTime now = new org.joda.time.DateTime(date);
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.MONDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.TUESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.WEDNESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.THURSDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.FRIDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SATURDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SUNDAY) );

		
			
			return exclDates;
			
		}
		
		
		public void createSched(User user, String freq, org.joda.time.DateTime p, String exclDate){
		
			
			java.util.List <org.joda.time.DateTime>exclDt= new java.util.ArrayList<org.joda.time.DateTime>();
			java.util.StringTokenizer t= new java.util.StringTokenizer( exclDate, ",");
			while( t.hasMoreElements() )
				exclDt.add( new org.joda.time.DateTime( new java.util.Date(t.nextToken())) );
			
			java.util.List <org.joda.time.DateTime> sched = new CalendarUtil().genSched( p, freq, exclDt );

			
			YearPlan plan = user.getYearPlan();
			Cal calendar = plan.getSchedule();
			if(calendar==null) calendar= new Cal();
			calendar.fmtDate(sched);

			plan.setSchedule(calendar);
			user.setYearPlan(plan);
			
			userDAO.updateUser(user);
		}
		
		public void updateSched(User user, String meetingPath, String time, String date, String ap, 
				String isCancelledMeeting, long currDate){
			
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
				if( meeting.getPath().equals( meetingPath ) )
					meeting.setCancelled(isCancelledMeeting);
			}
			
			userDAO.updateUser(user);
			
		}
	
}
