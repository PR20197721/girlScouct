package org.girlscouts.vtk.ejb;

import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
import org.girlscouts.vtk.models.User;
import org.girlscouts.vtk.models.YearPlan;
import org.girlscouts.vtk.models.YearPlanComponent;
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

	private java.text.SimpleDateFormat fmtDate = new java.text.SimpleDateFormat(
			"MM/dd/yyyy");

	public void weeklyCal(java.util.Date startDate) {
	}

	/*
	 * public java.util.List<org.joda.time.DateTime> genSched(
	 * org.joda.time.DateTime date, String freq,
	 * java.util.List<org.joda.time.DateTime> exclWeeksOfDate,int NumOfMeetings,
	 * String existingSched ){
	 * 
	 * 
	 * 
	 * //add all exclude dates java.util.List <org.joda.time.DateTime> exclDates
	 * = new java.util.ArrayList <org.joda.time.DateTime> (); for(int
	 * i=0;i<exclWeeksOfDate.size();i++ ){ exclDates.addAll( exclWeek(
	 * exclWeeksOfDate.get(i) ) ); }
	 * 
	 * 
	 * 
	 * 
	 * java.util.List <org.joda.time.DateTime> sched = new
	 * CalendarUtil().byWeeklySched( date , freq, exclDates, NumOfMeetings,
	 * existingSched);
	 * 
	 * 
	 * 
	 * return sched; }
	 */
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
				// date= new org.joda.time.DateTime(tmp.getTimeInMillis());

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

	// exclude full week of this date
	// public java.util.List <org.joda.time.LocalDate> exclWeek( java.util.Date
	// date){
	// -public java.util.List <org.joda.time.DateTime> exclWeek(
	// org.joda.time.DateTime date){
	public java.util.List<String> exclWeek(org.joda.time.DateTime date) {

		java.util.List<String> exclDates = new java.util.ArrayList();

		org.joda.time.DateTime now = new org.joda.time.DateTime(date);

		java.util.Calendar cal = now.toGregorianCalendar();
		cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
		for (int i = 0; i < 7; i++) {
			// exclDates.add( new org.joda.time.DateTime(cal.getTimeInMillis())
			// );
			exclDates.add(fmtDate.format(cal.getTime()));
			cal.add(java.util.Calendar.DATE, 1);
		}

		return exclDates;

	}

	/*
	 * //vtk1 -dont use public void createSched_OLD(User user, Troop troop,
	 * String freq, org.joda.time.DateTime p, String exclDate)throws
	 * java.lang.IllegalAccessException{
	 * 
	 * 
	 * if( troop!= null && ! userUtil.hasPermission(troop,
	 * Permission.PERMISSION_EDIT_MEETING_ID ) ) throw new
	 * IllegalAccessException();
	 * 
	 * if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
	 * troop.setErrCode("112"); throw new java.lang.IllegalAccessException(); }
	 * 
	 * java.util.List <org.joda.time.DateTime>exclDt= new
	 * java.util.ArrayList<org.joda.time.DateTime>(); java.util.StringTokenizer
	 * t= new java.util.StringTokenizer( exclDate, ","); while(
	 * t.hasMoreElements() ){ exclDt.add( new org.joda.time.DateTime( new
	 * java.util.Date(t.nextToken()) )); }
	 * 
	 * String existSched = troop.getYearPlan().getSchedule()== null ? "" :
	 * troop.getYearPlan().getSchedule().getDates();
	 * 
	 * java.util.List <org.joda.time.DateTime> sched = new
	 * CalendarUtil().genSched( p, freq, exclDt,
	 * troop.getYearPlan().getMeetingEvents().size(), existSched); YearPlan plan
	 * = troop.getYearPlan(); plan.setCalFreq(freq); plan.setCalStartDate(
	 * p.getMillis() ); plan.setCalExclWeeksOf(exclDate);
	 * 
	 * Cal calendar = plan.getSchedule(); if(calendar==null) calendar= new
	 * Cal(); calendar.fmtDate(sched);
	 * 
	 * plan.setSchedule(calendar); troop.setYearPlan(plan);
	 * 
	 * 
	 * //sort
	 * 
	 * Comparator<MeetingE> comp = new BeanComparator("id"); Collections.sort(
	 * troop.getYearPlan().getMeetingEvents(), comp);
	 * 
	 * troopUtil.updateTroop(user, troop); }
	 */
	public boolean updateSched(User user, Troop troop, String meetingPath,
			String time, String date, String ap, String isCancelledMeeting,
			long currDate) throws java.lang.IllegalAccessException {

		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_UPDATE_MEETING_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}

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

		String sched = cal.getDates();
		if ((sched == null || sched.contains(newDate.getTime() + ""))
				&& !(("" + currDate).equals(newDate.getTime() + ""))) {
			log.error("CalendarUtil.updateSched error: DUP DATE: date already exist in cal");
			return false;
		}

		sched = sched.replace("" + currDate, newDate.getTime() + "");
		cal.setDates(sched);

		java.util.List<MeetingE> meetings = troop.getYearPlan()
				.getMeetingEvents();
		for (int i = 0; i < meetings.size(); i++) {
			MeetingE meeting = meetings.get(i);
			if (meeting.getPath().equals(meetingPath)) {
				meeting.setCancelled(isCancelledMeeting);
				troop.getYearPlan().setAltered("true");

			}
		}

		troopUtil.updateTroop(user, troop);
		return true;
	}

	public void resetCal(User user, Troop troop)
			throws java.lang.IllegalAccessException {

		troop.getYearPlan().setSchedule(null);
		troopUtil.updateTroop(user, troop);
	}

	/*
	 * public java.util.List <org.joda.time.DateTime> getExclWeeks(String
	 * bannedDates){
	 * 
	 * 
	 * //banned dates to list java.util.List
	 * <org.joda.time.DateTime>exclWeeksOfDate = new java.util.ArrayList();
	 * StringTokenizer t= new StringTokenizer( bannedDates, ","); while(
	 * t.hasMoreElements() ) exclWeeksOfDate.add( new
	 * org.joda.time.DateTime(t.nextToken()) );
	 * 
	 * //banned weeks to list java.util.List <org.joda.time.DateTime> exclDates
	 * = new java.util.ArrayList <org.joda.time.DateTime> (); for(int
	 * i=0;i<exclWeeksOfDate.size();i++ ){ exclDates.addAll( new
	 * CalendarUtil().exclWeek( exclWeeksOfDate.get(i) ) ); }
	 * 
	 * return exclDates; }
	 */
	public java.util.Date getNextAvailDate(
			java.util.List<org.joda.time.DateTime> exclWeeks, long fromDate,
			int freq) {

		return null;

	}

	/*
	 * public boolean chnSchedDatesFwd(User user, Troop troop, long fromDate,
	 * long newDate, int freq) throws IllegalAccessException{ boolean isChg =
	 * false; String dates = "," + troop.getYearPlan().getSchedule().getDates()
	 * + ","; StringTokenizer t= new StringTokenizer( dates, ",");
	 * 
	 * Calendar newCalDate = new java.util.GregorianCalendar();
	 * newCalDate.setTimeInMillis(newDate);
	 * 
	 * java.util.List <org.joda.time.DateTime> exclWeeks= new
	 * CalendarUtil().getExclWeeks( troop.getYearPlan().getCalExclWeeksOf() );
	 * 
	 * while( t.hasMoreElements() ){ java.util.Date dt = new java.util.Date(
	 * Long.parseLong( t.nextToken() ) ); new
	 * CalendarUtil().getNextAvailDate(exclWeeks, fromDate, freq) ; if(
	 * (dt.getTime() == fromDate) ){ dates.replace(""+dt.getTime(), ""+newDate
	 * ); newCalDate.add( java.util.Calendar.DATE, freq); }else if( dt.getTime()
	 * > fromDate ){ dates.replace(""+dt.getTime(),
	 * ""+newCalDate.getTimeInMillis() ); newCalDate.add(
	 * java.util.Calendar.DATE, freq); } }
	 * 
	 * dates = dates.replace(",,", ","); if( dates.startsWith(",") ) dates=
	 * dates.substring(1); if( dates.endsWith(",") ) dates= dates.substring(0,
	 * dates.length()-1 );
	 * 
	 * //setFreg year //TODO
	 * 
	 * //-SAVE troopUtil.updateTroop(user, troop); return isChg; }
	 */
	public void createSched(User user, Troop troop, String freq,
			org.joda.time.DateTime newStartDate, String exclDate,
			long oldFromDate) throws java.lang.IllegalAccessException {
		if (troop != null
				&& !userUtil.hasPermission(troop,
						Permission.PERMISSION_EDIT_MEETING_ID))
			throw new IllegalAccessException();

		if (!userUtil.isCurrentTroopId(troop, user.getSid())) {
			troop.setErrCode("112");
			throw new java.lang.IllegalAccessException();
		}

		// org Dates
		String dates = "";
		if (troop.getYearPlan().getSchedule() == null) {
			java.util.Calendar cal = java.util.Calendar.getInstance();
			cal.setTime(new java.util.Date("1/1/1977"));
			oldFromDate = cal.getTimeInMillis();
			for (int i = 0; i < troop.getYearPlan().getMeetingEvents().size(); i++) {

				dates = dates + (cal.getTimeInMillis()) + ",";
				cal.add(java.util.Calendar.DATE, 1);
			}
		} else
			dates = troop.getYearPlan().getSchedule().getDates();

		if (!dates.startsWith(","))
			dates = "," + dates;
		if (!dates.endsWith(","))
			dates = dates + ",";

		// ALL excl dates
		java.util.List<String> exclDates = getExclDates(exclDate);

		// current cal
		Calendar newCalDate = java.util.Calendar.getInstance();
		newCalDate.setTimeInMillis(newStartDate.getMillis());

		StringTokenizer t = new StringTokenizer(dates, ",");
		while (t.hasMoreElements()) {
			java.util.Date dt = new java.util.Date(
					Long.parseLong(t.nextToken()));
			if ((dt.getTime() == oldFromDate)) {
				long newDate = getNextDate(exclDates,
						newCalDate.getTimeInMillis(), freq, true);

				dates = dates.replace("," + dt.getTime() + ",", "," + newDate
						+ ",");
				newCalDate.setTimeInMillis(newDate);
			} else if (dt.getTime() > oldFromDate) {

				long newDate = getNextDate(exclDates,
						newCalDate.getTimeInMillis(), freq, false);
				dates = dates.replace("," + dt.getTime() + ",", "," + newDate
						+ ",");
				newCalDate.setTimeInMillis(newDate);

			}
		}
		if (dates.startsWith(","))
			dates = dates.substring(1);
		if (dates.endsWith(","))
			dates = dates.substring(0, dates.length() - 1);

		YearPlan plan = troop.getYearPlan();
		plan.setCalFreq(freq);
		plan.setCalStartDate(Long.parseLong(dates.substring(0,
				dates.indexOf(","))));
		plan.setCalExclWeeksOf(exclDate);

		Cal calendar = plan.getSchedule();
		if (calendar == null)
			calendar = new Cal();
		calendar.setDates(dates);
		// calendar.fmtDate(dates);
		plan.setSchedule(calendar);

		troop.setYearPlan(plan);

		// sort

		Comparator<MeetingE> comp = new BeanComparator("id");
		Collections.sort(troop.getYearPlan().getMeetingEvents(), comp);

		troopUtil.updateTroop(user, troop);

	}

	private java.util.List<String> getExclDates(String exclDate) {
		java.util.List<String> exclDates = new java.util.ArrayList<String>();
		java.util.StringTokenizer t = new java.util.StringTokenizer(exclDate,
				",");
		while (t.hasMoreElements()) {

			// exclDates.addAll( exclWeek( new DateTime( new
			// java.util.Date(t.nextToken()).getTime() )) );
			exclDates.addAll(exclWeek(new DateTime(new java.util.Date(t
					.nextToken()).getTime())));

		}
		return exclDates;
	}

	private long getNextDate(List<String> exclDates, long theDate, String freq,
			boolean isUseCurrDate) {

		long nextDate = theDate;

		if (!isUseCurrDate) {
			org.joda.time.DateTime date = new org.joda.time.DateTime(theDate);
			if (freq.equals("weekly")) {
				date = date.plusWeeks(1);

			} else if (freq.equals("monthly")) {
				//date = date.plusMonths(1);
				int x= date.getDayOfWeek();
				int prevMonth = new DateTime(date.getMillis()).plusMonths(1).getMonthOfYear();
				date= date.plusMonths(1).withDayOfWeek(x); 	    
				if(date.getMonthOfYear() != prevMonth ) date = date.minusWeeks(1);

			} else if (freq.equals("biweekly")) {
				date = date.plusWeeks(2);

			}
			nextDate = date.getMillis();
		}
		if (!exclDates.contains(fmtDate.format(new java.util.Date(nextDate))))
			return nextDate;
		else
			return getNextDate(exclDates, nextDate, freq, false);

	}

}
