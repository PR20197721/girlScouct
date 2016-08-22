
             
             <table class="list-of-meeting-calendar combine-meeting">
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                           <td>

                        <!--    <input type="checkbox"  name="meeting_combine" value="<%=meetingsToCancel.get(i).getUid()%>" <%=i==0 ? "SELECTED" : "" %>/></td> -->
                             <input type="checkbox" name="_tag_m" id="y" value="later">
                            <label for="y"><span></span><p></p></label>

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



              <div class="row">

              <div class="small-24 column">
            <input type="button" value="save" id="saveCalElem" class="button btn right">  <input ttype="button" onclick="doCombine()" value="Combine Meetings"  class="button btn right"> 
            <div id="dialog-confirm"></div>
          </div>
             </div>
       
        
        <!-- <input type="button" onclick="doCombine()" value="Combine Meetings"/> -->
        
        <script>
        function doCombine(){
        	var mids = "";
        	var checkboxes = document.getElementsByName("meeting_combine");
        	  var checkboxesChecked = [];
        	 
        	  for (var i=0; i<checkboxes.length; i++) {
        	     
        	     if (checkboxes[i].checked) {
        	        console.log(checkboxes[i].value);
        	        mids += checkboxes[i].value+",";
        	     }
        	  }
        	  console.log("mids: "+ mids);
        	  addCalendar(mids);
        }
        
        
        function addCalendar(mids){
        	console.log("addCalendar....");
        	$.ajax({
                url: '/content/girlscouts-vtk/controllers/vtk.include.combineMeetingsAddCal.html',
                type: 'GET',
                data: {
                    
                    mids: mids,
                    a: Date.now()
                },
                success: function(result) {
                	console.log("succ "+ result);
                    document.getElementById("combineMeetings").innerHtml=result;
                }
            });
        }
        </script>