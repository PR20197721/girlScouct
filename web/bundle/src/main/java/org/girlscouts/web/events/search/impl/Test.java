package org.girlscouts.web.events.search.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test {
	
	public static void main(String [] args) throws ParseException{
		String month = "Jan";
		String year = "2014";
		String mthYr = month+" "+year;
		System.out.println(mthYr);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Calendar c = Calendar.getInstance();
		c.set(2014,1,3);
		
		
		System.out.println(Calendar.DAY_OF_MONTH);
		
		c.set(Calendar.DAY_OF_MONTH,c.getActualMinimum(Calendar.DAY_OF_MONTH));
				
		System.out.println(formatter.format(c.getTime()).toString());		
		c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		System.out.println(formatter.format(c.getTime()).toString());
		
	}
	
	

}
