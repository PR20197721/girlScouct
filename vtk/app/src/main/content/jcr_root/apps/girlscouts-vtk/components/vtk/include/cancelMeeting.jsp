<div class="column small-24 large-10 medium-10">
            <input type="radio" value="cancel" id="cclRadio" name="cancel" /><label for="cclRadio"><p>Cancel Meeting</p></label>
            <p>Select meeting plan you would like to cancel:</p>
            
             
             <table>
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                           <td><input type="radio" id="meeting_select" name="" value="<%=meetingsToCancel.get(i).getRefId()%>" <%=i==0 ? "SELECTED" : "" %>/></td>
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
        </div>
        
        <input type="button" onclick="fnOpenNormalDialog()" value="Cancel"/>