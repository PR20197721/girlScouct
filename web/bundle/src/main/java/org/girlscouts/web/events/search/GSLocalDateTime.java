package org.girlscouts.web.events.search;
import org.joda.time.LocalDateTime;
import org.girlscouts.web.events.search.GSDateTimeFormatter;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSLocalDateTime{
	public LocalDateTime ldt;
	
	public GSLocalDateTime(LocalDateTime dateTime){
		ldt = dateTime;
	}
	
	public static GSLocalDateTime parse(String str, GSDateTimeFormatter dtf){
		return new GSLocalDateTime(LocalDateTime.parse(str,dtf.dtf));
	}
	
	public long year(){
		return ldt.year().get();
	}
	
	public long getYear(){
		return ldt.year().get();
	}
	
	public long dayOfYear(){
		return ldt.dayOfYear().get();
	}
	
	public int monthOfYear(){
		return ldt.monthOfYear().get();
	}
	
	public int dayOfMonth(){
		return ldt.dayOfMonth().get();
	}
	
	public int hourOfDay(){
		return ldt.hourOfDay().get();
	}
	
	public int minuteOfHour(){
		return ldt.minuteOfHour().get();
	}
}