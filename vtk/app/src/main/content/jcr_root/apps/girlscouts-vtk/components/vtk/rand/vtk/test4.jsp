

<% 

			java.util.List<org.joda.time.DateTime> exclDates = new java.util.ArrayList();
			
			org.joda.time.DateTime now = new org.joda.time.DateTime("2014-10-12");
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.MONDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.TUESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.WEDNESDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.THURSDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.FRIDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SATURDAY) );
			exclDates.add( now.withDayOfWeek(org.joda.time.DateTimeConstants.SUNDAY) );
			
			out.println(now.dayOfWeek().withMinimumValue() +" : "+ now.dayOfWeek().withMaximumValue());
			
			for(int i=0;i<exclDates.size();i++)
				%><%=exclDates.get(i)%><% 
				
				
				out.println("<hr/>" + now.toGregorianCalendar().getFirstDayOfWeek() );
				
				java.util.Calendar cal = java.util.Calendar.getInstance();
				cal.setTime(new java.util.Date("10/11/2014") );
				
				cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.SUNDAY);
				for(int i=0;i<7;i++){
					
					
					out.println( "*** "+cal.getFirstDayOfWeek()  +" : "+ cal.getTime());
					cal.add(java.util.Calendar.DATE, 1);
				}
%>

<hr/><%= exclDates%>