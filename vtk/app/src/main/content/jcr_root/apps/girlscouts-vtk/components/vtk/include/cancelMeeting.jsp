
             <table>
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                           <td><input type="radio" id="meeting_select" name="meeting_select" value="<%=meetingsToCancel.get(i).getRefId()%>"/></td>
                           <td>
                           <%
                            java.util.Iterator ii= sched.keySet().iterator();
                            while( ii.hasNext()){
                            	java.util.Date dt = (java.util.Date) ii.next();
                            	MeetingE me = (MeetingE)sched.get(dt);
                            	if( me.getRefId().equals( meetingsToCancel.get(i).getRefId())){
                            		%> <%=dt%> <% 
                            	}
                            }
                           %>
                           </td>
                           
                           <td><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></td>
                           </tr>                           
                        <% }
              %>
              </table>   
       
        <input type="button" onclick="fnOpenNormalDialog()" value="Cancel"/>