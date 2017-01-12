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
	     
      }

      badges_earned += achievementCurrent;
      
      %>
      
      
          <tr>
            <td><%= (date==null || date.before(new java.util.Date("1/1/2000"))) ? "" : VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, date) %></td>
            <td><%=meeting.getMeetingInfo()==null ? "" : meeting.getMeetingInfo().getName() %></td>
            <td><%=attendanceCurrent == 0 ? "" : attendanceCurrent %></td>
            <td><p class="<%= (achievementCurrent > 0) ? "check" : "" %>"></p></td>
          </tr>
