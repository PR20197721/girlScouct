package org.girlscouts.web.events.search;
import org.joda.time.DateTimeUtils;

//CQ 5.6.1 comes with an old version of Joda. This allows us to use newer functionality
public class GSDateTimeUtils{
	public static long currentTimeMillis(){
		return DateTimeUtils.currentTimeMillis();
	}
}