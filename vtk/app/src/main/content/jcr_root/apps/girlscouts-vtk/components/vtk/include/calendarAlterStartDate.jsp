<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%	
  String startAlterDate = request.getParameter("alterYPStartDate") == null ? "" : request.getParameter("alterYPStartDate");
  if(startAlterDate!=null && !startAlterDate.equals("") ){ %>
  <p>Configure <%=request.getParameter("mCountUpd") %> meeting dates starting on or after <%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, new java.util.Date(Long.parseLong(startAlterDate))) %>:</p>
<%} %>
  <input type="hidden" id="orgDt" name="orgDt" value="<%=( startAlterDate!=null && !startAlterDate.trim().equals("")) ? startAlterDate:( troop.getYearPlan().getCalStartDate()==null ? "" : new java.util.Date(troop.getYearPlan().getCalStartDate()).getTime() ) %>"/>   
  <section class="clearfix">
  <div class="columns small-24 medium-8">
    <div class="row">
          <div class="small-15 medium-19 columns date">
      <input type="text" placeholder="Start Date" id="calStartDt" name="calStartDt" value="<%=( startAlterDate!=null && !startAlterDate.trim().equals("")) ? VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, new java.util.Date( Long.parseLong(startAlterDate))):( troop.getYearPlan().getCalStartDate()==null ? "" : VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, new java.util.Date(troop.getYearPlan().getCalStartDate()))) %>" />
    </div>
    <div class="small-7  medium-5 columns date">
        <label for="calStartDt"><i class="icon-calendar"></i></label>
    </div>
    </div>
  </div>
    <div class="columns small-24 medium-16">
    <div class="row">


    <div class="small-8 medium-5 columns">
        <input type="text" placeholder="Time" id="calTime" value="<%=troop.getYearPlan().getCalStartDate()==null ? (org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_HOUR+":"+org.girlscouts.vtk.models.VTKConfig.CALENDAR_START_TIME_MIN) : VtkUtil.formatDate(VtkUtil.FORMAT_hhmm, new java.util.Date(troop.getYearPlan().getCalStartDate())) %>" />
    </div>
    <div class="small-6  medium-4 columns">
      <select id="calAP">
        <% String AM = "PM";
          if( troop.getYearPlan().getCalStartDate() !=null ){
            AM = VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, new java.util.Date(troop.getYearPlan().getCalStartDate()));
          } 
        %>
        <option value="pm" <%=AM.equals("PM") ? " SELECTED" : "" %>>PM</option>
        <option value="am" <%=AM.equals("AM") ? " SELECTED" : "" %>>AM</option>
      </select>
    </div>
    <div class="small-10   medium-7 columns left">
      <select id="calFreq">
        <option value="weekly" <%= troop.getYearPlan().getCalFreq().equals("weekly") ? " SELECTED" : "" %>>weekly</option>
        <option value="biweekly" <%= troop.getYearPlan().getCalFreq().equals("biweekly") ? " SELECTED" : "" %>>biweekly</option>
        <option value="monthly" <%= troop.getYearPlan().getCalFreq().equals("monthly") ? " SELECTED" : "" %>>monthly</option>
      </select>
    </div>
    </div>
    </div>
  </section>

  <section class="clearfix holidays">
    <p>Do not schedule the meeting the week of:</p>
      <%
      String exlDates = troop.getYearPlan().getCalExclWeeksOf();
      exlDates= exlDates==null ? "" : exlDates.trim();
      String[] split_exclDates = exlDates.split(",");

      java.util.Map<Long, String> holidays = VtkUtil.getVtkHolidays(user, troop);
    
      //sort
      holidays= new java.util.TreeMap(holidays);

      %>
      
    <ul class="small-block-grid-2 medium-block-grid-3">
     <%
        java.util.Iterator itr= holidays.keySet().iterator();
        int holidayCount=0;
	    if( exlDates.equals("") ){ //pull from default config
	     while(itr.hasNext()){
	    	 holidayCount++;
	    	 Long holidayDate= (Long)itr.next();
	    	 String holidayDateFmt= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY ,new java.util.Date(holidayDate));
	    	 String holidayTitle = holidays.get(holidayDate);   	 
	      %>  
	      <li>
	         <input type="checkbox" id="chk_<%=holidayCount %>" name="exclDt" value="<%=holidayDateFmt %>" <%=("".equals(exlDates) || exlDates.contains(holidayDateFmt)) ? "CHECKED" : ""  %>/>
           <label style="display:block;" for="chk_<%=holidayCount%>">
            <div style="padding-left: 30px;">
              <span style="margin-left: 5px;"  class="date"><%=holidayDateFmt %></span><span><%=holidayTitle %></span>
              </div>
            </label>
	      </li>
	     <%}
	     }else{ //pull from db %>
	       <%
	       java.util.List<Long> storedHolidayDates = new java.util.ArrayList<Long>();       
	       for(int i=0;i<split_exclDates.length;i++){
	    	  try{
	    	   holidayCount++;
	    	   String holidayDateFmt= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY ,new java.util.Date(split_exclDates[i]));
	           String holidayTitle = holidays.get(new java.util.Date(holidayDateFmt).getTime()) ;
	           storedHolidayDates.add(new java.util.Date(holidayDateFmt).getTime());    	   
	    	   if( split_exclDates[i]==null || split_exclDates[i].equals("")) continue;
		       %>
		         <li>
		            <input type="checkbox" id="chk_<%=(holidayCount) %>" name="exclDt" value="<%=split_exclDates[i] %>" CHECKED/>
                <label style="display:block;" for="chk_<%=holidayCount%>">
                  <div style="padding-left: 30px">
                    <span style="margin-left: 5px;" class="date"><%= split_exclDates[i]%><br><%=holidayTitle ==null ? "Canceled Meeting" : holidayTitle %></span>
                  </div>
                </label>
		         </li>   
		      <%
	    	  }catch(Exception e){e.printStackTrace();}
	       }
	       if( itr!=null )      
	        while( itr.hasNext()){ 
	           try{	
		           long holidayDate = (Long) itr.next();
		           if( storedHolidayDates!=null && !storedHolidayDates.contains(holidayDate) ){
		        	   holidayCount++;
		               String holidayDateFmt= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY ,new java.util.Date(holidayDate));
		               String holidayTitle = holidays.get(holidayDate); 
		               %>
		                <li>
		                    <input type="checkbox" id="chk_<%=holidayCount %>" name="exclDt" value="<%=holidayDateFmt %>" <%=("".equals(exlDates) || exlDates.contains(holidayDateFmt)) ? "CHECKED" : ""  %>/><label for="chk_<%=holidayCount%>"><p><span class="date"><%=holidayDateFmt %></span><span><%=holidayTitle %></span></p></label>
		                </li>
		               <%
		           }//end if
	           }catch(Exception e){e.printStackTrace();}
		    }//end while
     }//end else
       %>
      
    </ul>
  </section>
  <button class="btn right" onclick="buildSched()">Update Calendar</button>
  <div id="calView"></div>
  <script>
   $(function(){
      var currentYear = CurrentYear(new Date());
	   $( "#calStartDt" ).datepicker({
	  	  minDate: currentYear.start(),
  		  maxDate : currentYear.end()
      });
   });
  </script>
