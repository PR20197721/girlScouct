package org.girlscouts.web.events.search;

import java.util.HashMap;
import java.util.Map;

public class Event {

	 private String year;
	 private String month;
	 private boolean isValid=true;
	 
	 Map<String,String> errorCode = new HashMap<String,String>();
	 
	 public void setYear(String year){
		 
		 this.year = year;
	 }
	 
	 public String getYear(){
		 return  year;
	 }
	 
    public void getMonth(String month){
		 
		 this.month = month;
	 }
	 
	 public String getMonth(){
		 return  month;
	 }
	 
	 
	 public boolean isValid(){
		 if(year!=null && month==null){
			 isValid = false;
			 errorCode.put("monthError","Please Provide Year and Month" );
			 
		 }
		 if(month!=null && year==null){
			 errorCode.put("yearError","Please Provide Year and Month" );
			 isValid = false;
			 
		 }
		 return isValid;
	 }
	 
}
