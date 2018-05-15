package org.girlscouts.web.events.search;
import org.joda.time.DateTimeZone;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTimeZone{
	public DateTimeZone dtz;
	
	public static GSDateTimeZone UTC = new GSDateTimeZone(DateTimeZone.UTC);
	
	public GSDateTimeZone(DateTimeZone dateTimeZone){
		dtz = dateTimeZone;
	}
	
	public String getShortName(long instant){
		return dtz.getShortName(instant);
	}
	
	public static GSDateTimeZone forID(String str){
		return new GSDateTimeZone(DateTimeZone.forID(str));
	}
	
}