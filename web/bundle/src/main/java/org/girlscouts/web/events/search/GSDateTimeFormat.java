package org.girlscouts.web.events.search;
import org.girlscouts.web.events.search.GSDateTimeFormatter;
import org.joda.time.format.DateTimeFormat;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTimeFormat{
	public static GSDateTimeFormatter forPattern(String str){
		return new GSDateTimeFormatter(DateTimeFormat.forPattern(str));
	}
}