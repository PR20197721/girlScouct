
             <table class="list-of-meeting-calendar cancel-meeting">
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                           <td>


                             <input type="radio" value="<%=meetingsToCancel.get(i).getRefId()%>" id="x" name="meeting_select" /><label for="x"><p> </p></label>

                            <!--  <input type="radio" id="meeting_select" name="meeting_select" value="<%=meetingsToCancel.get(i).getRefId()%>"/> -->


                           </td>
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
       
        <!-- <input type="button" onclick="fnOpenNormalDialog()" value="Cancel"/> -->



          <div class="row">
            <div class="small-24 column">
            <input type="button" value="save" id="saveCalElem" class="button btn right">  <input type="button" value="cancel" id="cancelCalElem" onclick="fnOpenNormalDialog()" class="button btn right"> 
            <div id="dialog-confirm"></div>
        </div>
          </div>

