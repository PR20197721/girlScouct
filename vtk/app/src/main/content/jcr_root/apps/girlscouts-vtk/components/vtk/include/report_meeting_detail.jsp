      <%   
      MeetingE meeting = (MeetingE)ypc;
      int attendanceCurrent=0, attendanceTotal=0, 
    		  achievementCurrent=0;
      if( meeting!=null ){
	      Attendance attendance = meetingUtil.getAttendance( user,  _troop,  meeting.getPath()+"/attendance");
	      if( attendance !=null && attendance.getUsers()!=null ){
	    	  StringTokenizer act = new StringTokenizer( attendance.getUsers(), ",");
	          attendanceCurrent = act.countTokens();
	          attendanceTotal= attendance.getTotal();
	          while( act.hasMoreTokens())
	        	  distinctGirl.add(act.nextToken() );
	      }
	
	      Achievement achievement = meetingUtil.getAchievement( user,  _troop, meeting.getPath()+"/achievement");
	      if( achievement !=null && achievement.getUsers()!=null ){
	    	  StringTokenizer act= new StringTokenizer( achievement.getUsers(), ",");
	          achievementCurrent = act.countTokens();
	          while( act.hasMoreTokens())
                  distinctGirl.add(act.nextToken() );
	      } 
	      /*
	      if( _troop.getYearPlan().isAltered()!=null && _troop.getYearPlan().isAltered().equals("true") && 
	    		  meeting.getMeetingInfo()!=null && meeting.getRefId().contains("/lib/")){
	    	  
	    	  
	    	  String mid= meeting.getRefId();
	    	  mid= mid.substring( mid.lastIndexOf("/") );
	    	  mid= mid.substring( mid.indexOf("_"));
	    	  
	    	  String orgPath = "/content/girlscouts-vtk/meetings/myyearplan/"+ ageGroup +"/"+ mid ;
	          Meeting orgMeeting = yearPlanUtil.getMeeting(user, orgPath );
	    	  if(  meeting.getMeetingInfo().getActivities().size()!= orgMeeting.getActivities().size() )
	    		  meeting.getMeetingInfo().getActivities().size() - orgMeeting.getActivities().size()
	    		  
	      }
	      */
      }

      badges_earned += achievementCurrent;
      
      %>
      
      
          <tr>
            <td><%= FORMAT_MMddYYYY.format(date) %></td>
            <td><%=meeting.getMeetingInfo()==null ? "" : meeting.getMeetingInfo().getName() %></td>
            <td><%=attendanceCurrent == 0 ? "" : attendanceCurrent %></td>
            <td><p class="<%= (achievementCurrent > 0) ? "check" : "" %>"></p></td>
          </tr>