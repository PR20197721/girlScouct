package org.girlscouts.common.events.search;
import org.joda.time.DateTimeZone;
import java.util.TimeZone;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTimeZone{
	public DateTimeZone dtz;
	private String timeZoneId;
	
	
	public static GSDateTimeZone UTC = new GSDateTimeZone(DateTimeZone.UTC, "UTC");
	
	public GSDateTimeZone(DateTimeZone dateTimeZone, String timeZoneId){
		dtz = dateTimeZone;
		this.timeZoneId = timeZoneId;
		
	}
	
	public String getShortName(long instant){
		return TimeZone.getTimeZone(this.timeZoneId).getDisplayName(false, TimeZone.SHORT);
	}
	
	public static GSDateTimeZone forID(String str){
		return new GSDateTimeZone(DateTimeZone.forID(str), str);
	}
	
}