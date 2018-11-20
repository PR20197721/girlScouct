package org.girlscouts.vtk.ejb;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.girlscouts.vtk.auth.permission.Permission;
import org.girlscouts.vtk.models.Cal;
import org.girlscouts.vtk.models.Meeting;
import org.girlscouts.vtk.models.MeetingE;
import org.girlscouts.vtk.models.Troop;
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.utils.VtkException;
import org.girlscouts.vtk.utils.VtkUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Service(value = CalendarUtil.class)
public class CalendarUtil {

	private final Logger log = LoggerFactory.getLogger("vtk");

	@Reference
	TroopUtil troopUtil;

	@Reference
	private UserUtil userUtil;

	@Reference
	MeetingUtil meetingUtil;

	private java.text.SimpleDateFormat fmtDate = new java.text.SimpleDateFormat(
			"MM/dd/yyyy");

	public void weeklyCal(java.util.Date startDate) {
	}

	public java.util.List<java.util.Date> toUtilDate(
			java.util.List<org.joda.time.DateTime> sched) {
		java.util.List<java.util.Date> toRet = new java.util.ArrayList<java.util.Date>();
		for (int i = 0; i < sched.size(); i++) {
			toRet.add(sched.get(i).toDate());
		}
		return toRet;
	}

	// generate sched : by weekly
	public java.util.List<org.joda.time.DateTime> byWeeklySched(
			org.joda.time.DateTime _startDate, String freq,
			java.util.List<org.joda.time.DateTime> exclDates,
			int numOfMeetings, String existingSched) {
		org.joda.time.DateTime startDate = new org.joda.time.DateTime(
				_startDate.toDateMidnight());
		java.util.List<org.joda.time.DateTime> sched = new java.util.ArrayList();
		org.joda.time.DateTime date = new org.joda.time.DateTime(startDate);
		int addedDates = 0;
		if (existingSched != null) {
			java.util.StringTokenizer t = new java.util.StringTokenizer(
					existingSched, ",");
			while (t.hasMoreElements()) {
				long existSchedDate = Long.parseLong(t.nextToken());
				if (new java.util.Date(existSchedDate)
						.before(new java.util.Date())) {
					sched.add(new org.joda.time.DateTime(existSchedDate));
					addedDates++;
				}
			}
		}

		for (int i = 1; i < 100; i++) {
			if (!exclDates.contains(date)) {
				java.util.Calendar tmp = java.util.Calendar.getInstance();
				tmp.setTimeInMillis(date.getMillis());
				tmp.set(java.util.Calendar.HOUR, _startDate.getHourOfDay());
				tmp.set(java.util.Calendar.MINUTE, _startDate.getMinuteOfHour());
				sched.add(new org.joda.time.DateTime(tmp.getTimeInMillis()));
				addedDates++;
				if (addedDates >= numOfMeetings)
					return sched;
			}

			if (freq.equals("weekly")) {
				date = date.plusWeeks(1);
			} else if (freq.equals("monthly")) {
				date = date.plusMonths(1);
			} else if (freq.equals("biweekly")) {
				date = date.plusWeeks(2);
			}
		}
		return sched;
	}

	public java.util.List<String> exclWeek( org.joda.time.DateTime date ) {
		java.util.List<String> exclDates = new java.util.ArrayList();
		org.joda.time.DateTime now = new org.joda.time.DateTime(date);
		java.util.Calendar cal = now.toGregorianCalendar();
		cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
		for (int i = 0; i < 7; i++) {
			exclDates.add(fmtDate.format(cal.getTime()));
			cal.add(java.util.Calendar.DATE, 1);
		}
		return exclDates;

	}

	public boolean updateSched(User user, Troop troop, String meetingPath,
			String time, String date, String ap, String isCancelledMeeting,
			long currDate) throws java.lang.IllegalAccessException,
			VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat(
				"MM/dd/yyyy hh:mm a");
		YearPlan plan = troop.getYearPlan();
		Cal cal = plan.getSchedule();
		java.util.Date newDate = null;
		try {
			newDate = dateFormat4.parse(date + " " + time + " " + ap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//String sched = cal.getDates();
		String sched = VtkUtil.sortDates(cal.getDates());
		if ((sched == null || sched.contains(newDate.getTime() + ""))
				&& !(("" + currDate).equals(newDate.getTime() + ""))) {
			log.error("CalendarUtil.updateSched error: DUP DATE: date already exist in cal");
			return false;
		}
			
		java.util.List<MeetingE> meetings = schedMeetings(plan.getMeetingEvents(), sched);

		
		
		for(int i=0;i<100;i++){
			if ((sched == null || sched.contains(newDate.getTime() +""))) {
				java.util.Calendar c = Calendar.getInstance();
				c.setTimeInMillis(newDate.getTime());
				c.add(java.util.Calendar.SECOND, 1);
				newDate= c.getTime();
			}
		}
			
		sched = sched.replace("" + currDate, newDate.getTime() + "");
		
		
		updateSchedMeetings( meetings, currDate, newDate.getTime() );
	
		
		//sort meetings by Date
		Comparator<MeetingE> comp = new BeanComparator("date");
		if (meetings != null)
			Collections.sort(meetings, comp);
		
		
		for(int i=0;i<meetings.size();i++){
			if( meetings.get(i).getId() != (i) ){
				meetings.get(i).setId((i));
				meetings.get(i).setDbUpdate(true);
			}
		}
		
		cal.setDates(sched);
		cal.setDbUpdate(true);
		troopUtil.updateTroop(user, troop);

		return true;
	}

	public void resetCal(User user, Troop troop)
			throws java.lang.IllegalAccessException, VtkException {
		troop.getYearPlan().setSchedule(null);
		troopUtil.updateTroop(user, troop);
	}

	public java.util.Date getNextAvailDate(
			java.util.List<org.joda.time.DateTime> exclWeeks, long fromDate,
			int freq) {
		return null;
	}

	private int weekOfMonth = 0, dayOfWeek = 0;

	public String getSchedDates(User user, Troop troop, String freq,
			org.joda.time.DateTime newStartDate, String exclDate,
			long oldFromDate) throws java.lang.IllegalAccessException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();



		// org Dates
		String dates = "";
		if (troop.getYearPlan().getSchedule() == null) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(new java.util.Date("1/1/1977"));
			oldFromDate = cal.getTimeInMillis();
			if (troop.getYearPlan().getMeetingEvents() != null)
				for (int i = 0; i < troop.getYearPlan().getMeetingEvents()
						.size(); i++) {
					dates = dates + (cal.getTimeInMillis()) + ",";
					cal.add(java.util.Calendar.DATE, 1);
				}
		} else{
			
			dates = troop.getYearPlan().getSchedule().getDates();

		}//end else
		
		if (!dates.startsWith(","))
			dates = "," + dates;
		if (!dates.endsWith(","))
			dates = dates + ",";

		// ALL excl dates
		java.util.List<String> exclDates = getExclDates(exclDate);

		// current cal
		Calendar newCalDate = java.util.Calendar.getInstance();
		newCalDate.setTimeInMillis(newStartDate.getMillis());
		weekOfMonth = getWeekOfMonth(newCalDate.getTime().getTime());// -1;
		dayOfWeek = newCalDate.get(Calendar.DAY_OF_WEEK);
		long maxYearPlanDate = 0;
		String newDates = "";
		StringTokenizer t = new StringTokenizer(dates, ",");
		while (t.hasMoreElements()) {
			java.util.Date dt = new java.util.Date(
					Long.parseLong(t.nextToken()));
			if ((dt.getTime() == oldFromDate)) {
				long newDate = getNextDate(exclDates,
						newCalDate.getTimeInMillis(), freq, true);
				if (maxYearPlanDate == 0) {
					maxYearPlanDate = getMaxYearPlanDate(newDate);
				}
				if (newDate > maxYearPlanDate)
					break;
				newCalDate.setTimeInMillis(newDate);
				newDates += "," + newDate;
			} else if (dt.getTime() > oldFromDate) {
				long newDate = getNextDate(exclDates,
						newCalDate.getTimeInMillis(), freq, false);
				if (maxYearPlanDate == 0) {
					maxYearPlanDate = getMaxYearPlanDate(newDate);
				}
				if (newDate > maxYearPlanDate)
					break;
				newCalDate.setTimeInMillis(newDate);
				newDates += "," + newDate;
			} else {
				if (maxYearPlanDate == 0) {
					maxYearPlanDate = getMaxYearPlanDate(dt.getTime());
				}
				if (dt.getTime() > maxYearPlanDate)
					break;
				newDates += "," + dt.getTime();
			}
		}
		dates = newDates;
		if (dates.startsWith(","))
			dates = dates.substring(1);
		if (dates.endsWith(","))
			dates = dates.substring(0, dates.length() - 1);
		
		return dates;
	}

	public YearPlan getSched(User user, Troop troop, String freq,
			org.joda.time.DateTime newStartDate, String exclDate,
			long oldFromDate) throws java.lang.IllegalAccessException {
		String dates = getSchedDates(user, troop, freq, newStartDate, exclDate,
				oldFromDate);
		YearPlan plan = troop.getYearPlan();
		plan.setCalFreq(freq);
		if (dates != null && !dates.contains(",") && dates.length() > 3)
			plan.setCalStartDate(Long.parseLong(dates));
		else
			plan.setCalStartDate(Long.parseLong(dates.substring(0,
					dates.indexOf(","))));
		plan.setCalExclWeeksOf(exclDate);
		
		Cal calendar = plan.getSchedule();
		if (calendar == null)
			calendar = new Cal();
		calendar.setDates(dates);
		calendar.setDbUpdate(true);
		plan.setSchedule(calendar);
		Comparator<MeetingE> comp = new BeanComparator("id");
		Collections.sort(plan.getMeetingEvents(), comp);
		return plan;
	}

	public void createSched(User user, Troop troop, String freq,
			org.joda.time.DateTime newStartDate, String exclDate,
			long oldFromDate) throws java.lang.IllegalAccessException,
			VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();
		
	
		YearPlan plan = getSched(user, troop, freq, newStartDate, exclDate, oldFromDate);
		troop.setYearPlan(plan);
		
		// if sched dates > meetings = rm last N meetings
		meetingUtil.rmExtraMeetingsNotOnSched(user, troop);
	
		troopUtil.updateTroop(user, troop);
	
	}

	private java.util.List<String> getExclDates(String exclDate) {
		java.util.List<String> exclDates = new java.util.ArrayList<String>();
		java.util.StringTokenizer t = new java.util.StringTokenizer(exclDate,
				",");
		while (t.hasMoreElements()) {
			exclDates.addAll(exclWeek(new DateTime(new java.util.Date(t
					.nextToken()).getTime())));
		}
		return exclDates;
	}

	public long getNextDate(List<String> exclDates, long theDate, String freq,
			boolean isUseCurrDate) {
		long nextDate = theDate;
		if (!isUseCurrDate) {
			org.joda.time.DateTime date = new org.joda.time.DateTime(theDate);
			if (freq.equals("weekly")) {
				date = date.plusWeeks(1);
			} else if (freq.equals("monthly")) {
				DateTime _date = new org.joda.time.DateTime(
						getMonthlyNextDate(date));
				if (_date.isBefore(new java.util.Date().getTime()))
					date = date.plusMonths(1);
				else
					date = _date;
			} else if (freq.equals("biweekly")) {
				date = date.plusWeeks(2);
			}
			nextDate = date.getMillis();
		}

		if (nextDate <= 0
				|| (nextDate > new java.util.Date().getTime())
				&& !exclDates.contains(fmtDate.format(new java.util.Date(
						nextDate))))
			return nextDate;
		else
			return getNextDate(exclDates, nextDate, freq, false);
	}

	private int getWeekOfMonth(long date) {
		Calendar cal = java.util.Calendar.getInstance();
		cal.setTimeInMillis(date);
		int dayOfTheWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
		int toRet = 1;
		Calendar tmp = java.util.Calendar.getInstance();
		tmp.setTimeInMillis(date);
		tmp.set(java.util.Calendar.DATE, 1);
		while (tmp.getTimeInMillis() != date) {
			if (tmp.get(java.util.Calendar.DAY_OF_WEEK) == dayOfTheWeek)
				toRet++;
			tmp.add(java.util.Calendar.DATE, 1);
		}
		return toRet;
	}

	private long getMonthlyNextDate(org.joda.time.DateTime date) {

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTimeInMillis(date.getMillis());
		cal.add(java.util.Calendar.MONTH, 1);
		cal.set(java.util.Calendar.DATE, 1);
		int month = cal.get(java.util.Calendar.MONTH);
		int count = 0;
		long lastdt = 0;
		while (cal.get(java.util.Calendar.MONTH) == month) {
			if (dayOfWeek == cal.get(java.util.Calendar.DAY_OF_WEEK)) {
				count++;
				lastdt = cal.getTimeInMillis();
			}
			if (count == weekOfMonth
					&& dayOfWeek == cal.get(java.util.Calendar.DAY_OF_WEEK)) {
				return cal.getTimeInMillis();
			}
			cal.add(java.util.Calendar.DATE, 1);
		}
		return lastdt;
	}

	public long getMaxYearPlanDate(long startYearPlanDate) {

		java.util.Calendar _startYearPlanDate = java.util.Calendar
				.getInstance();
		_startYearPlanDate.setTimeInMillis(startYearPlanDate);
		
		java.util.Calendar sepThisYear = java.util.Calendar.getInstance();
		sepThisYear.set(java.util.Calendar.DAY_OF_MONTH,
				VtkUtil.getCurrentGSDate()); 
		sepThisYear.set(java.util.Calendar.MONTH,
				VtkUtil.getCurrentGSMonth() - 1);
		sepThisYear.set(java.util.Calendar.YEAR,
				_startYearPlanDate.get(java.util.Calendar.YEAR));

		// if sep1 of this year is after startYearPlanDate, use this year
		if ((!VtkUtil.isSameDate(sepThisYear.getTime(),
				_startYearPlanDate.getTime()))
				&& sepThisYear.getTimeInMillis() > _startYearPlanDate
						.getTimeInMillis()) {
			return sepThisYear.getTimeInMillis();
		} else {

			java.util.Calendar now = java.util.Calendar.getInstance();
			int maxYear = now.get(java.util.Calendar.YEAR);

			if ((now.get(java.util.Calendar.MONTH) > VtkUtil
					.getCurrentGSMonth() - 1)
					|| ((now.get(java.util.Calendar.MONTH) == VtkUtil
							.getCurrentGSMonth() - 1) && (now
							.get(java.util.Calendar.DATE) >= VtkUtil
							.getCurrentGSDate())))

				maxYear += 1;
			sepThisYear.set(java.util.Calendar.YEAR, maxYear);

			return sepThisYear.getTimeInMillis();
		}

	}

	public boolean isEventPastGSYear(User user, Troop troop) {
		if (troop.getYearPlan() == null
				|| troop.getYearPlan().getSchedule() == null
				|| troop.getYearPlan().getSchedule().getDates()==null || "".equals(troop.getYearPlan().getSchedule().getDates() ))
			return true;

		boolean isPast = false;
		try {
			Cal cal = troop.getYearPlan().getSchedule();

			java.util.List<java.util.Date> sched = VtkUtil
					.getStrCommDelToArrayDates(cal.getDates());

			// sort here -sched
			Collections.sort(sched);

			String freq = troop.getYearPlan().getCalFreq();
			long lastDate = sched.get(sched.size() - 1).getTime();
			java.util.List<String> exclDates = VtkUtil
					.getStrCommDelToArrayStr(troop.getYearPlan()
							.getCalExclWeeksOf());
			long nextDate = getNextDate(exclDates, lastDate, freq, false);
			java.util.Calendar nextDateCal = java.util.Calendar.getInstance();
			nextDateCal.setTimeInMillis( sched.get(sched.size()-1 ).getTime() ); 

			java.util.Calendar cutOffDate = java.util.Calendar.getInstance();
			cutOffDate.setTime(sched.get(0));

			int currentGSDate = VtkUtil.getCurrentGSDate();
			int currentGSMonth = VtkUtil.getCurrentGSMonth();
			
		    if (nextDateCal.get(java.util.Calendar.MONTH) > currentGSMonth - 1)
				cutOffDate.add(java.util.Calendar.YEAR, 1);
			cutOffDate.set(java.util.Calendar.MONTH, currentGSMonth - 1);
			cutOffDate.set(java.util.Calendar.DATE, currentGSDate);
			
			if (nextDate <= cutOffDate.getTime().getTime())
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isPast;

	}
	
	public boolean updateDate(User user, Troop troop, long currDate, long newDate) throws java.lang.IllegalAccessException,
			VtkException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		java.text.SimpleDateFormat dateFormat4 = new java.text.SimpleDateFormat(
				"MM/dd/yyyy hh:mm a");
		YearPlan plan = troop.getYearPlan();
		Cal cal = plan.getSchedule();		
		String sched = VtkUtil.sortDates(cal.getDates());

StringTokenizer t1 = new StringTokenizer(sched, ",");
while( t1.hasMoreElements()){
	String tt= t1.nextToken();
	
}

		java.util.List<MeetingE> meetings = schedMeetings(plan.getMeetingEvents(), sched);
		
		for(int i=0;i<100;i++){
			if ((sched == null || sched.contains(newDate +""))) {
				java.util.Calendar c = Calendar.getInstance();
				c.setTimeInMillis(newDate);
				c.add(java.util.Calendar.SECOND, 1);
				newDate= c.getTimeInMillis();
			}
		}
		sched = sched.replace("" + currDate, newDate + "");
		
		
		updateSchedMeetings( meetings, currDate, newDate );
		
		
		
		//sort meetings by Date
		Comparator<MeetingE> comp = new BeanComparator("date");
		if (meetings != null)
			Collections.sort(meetings, comp);
		
		
		
		
		for(int i=0;i<meetings.size();i++){
			if( meetings.get(i).getId() != i ){
				meetings.get(i).setId(i);
				meetings.get(i).setDbUpdate(true);
			}
		}
		
		
		cal.setDates(sched);

		
StringTokenizer t = new StringTokenizer(sched, ",");
while( t.hasMoreElements()){
	String tt= t.nextToken();
	
}

		cal.setDbUpdate(true);
troop.getYearPlan().setSchedule(cal);		
		troopUtil.updateTroop(user, troop);
		return true;
	}
	
	
private java.util.List<MeetingE> updateSchedMeetings( java.util.List<MeetingE> meetings, long currDate, long newDate ){
	for(int i=0;i<meetings.size();i++){
	
		if(  meetings.get(i).getDate().getTime()== currDate )
			meetings.get(i).setDate( new java.util.Date( newDate) );
	}
	return meetings;
}


public  java.util.List<MeetingE> schedMeetings(java.util.List<MeetingE> meetings, String sched){

	
	return VtkUtil.schedMeetings(meetings, sched);
}


public void combineMeeting(User user, Troop troop, String currDates, String _dt) throws java.lang.IllegalAccessException, java.text.ParseException, org.girlscouts.vtk.utils.VtkException{
	
	java.util.StringTokenizer t= new java.util.StringTokenizer( currDates, ",");
	while( t.hasMoreElements()){
		   long currDate = Long.parseLong(t.nextToken());
		   java.util.Date dt = VtkUtil.parseDate( VtkUtil.FORMAT_FULL, _dt );
		   updateDate( user,  troop,  currDate,  dt.getTime() );
	}
}
}//edn class
