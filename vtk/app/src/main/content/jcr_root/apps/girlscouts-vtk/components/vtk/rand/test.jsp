<%

java.util.Calendar cal = java.util.Calendar.getInstance();
cal.setTime( new java.util.Date("1/1/1076") );

java.util.Calendar cal1 = java.util.Calendar.getInstance();


out.println( cal.before( cal1 ));
%>