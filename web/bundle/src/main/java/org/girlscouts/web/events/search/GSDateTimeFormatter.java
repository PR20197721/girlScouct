package org.girlscouts.web.events.search;
import org.joda.time.format.DateTimeFormatter;
import org.girlscouts.web.events.search.GSDateTime;
import org.girlscouts.web.events.search.GSDateTimeZone;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTimeFormatter{
	public DateTimeFormatter dtf;
	
	public GSDateTimeFormatter(DateTimeFormatter dateTimeFormatter){
		dtf = dateTimeFormatter;
	}
	
	public String print(GSDateTime dateTime){
		return dtf.print(dateTime.dt);
	}
	
	public GSDateTimeFormatter withZone(GSDateTimeZone dtz){
		return new GSDateTimeFormatter(dtf.withZone(dtz.dtz));
	}
}