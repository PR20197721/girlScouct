
             
             <table class="list-of-meeting-calendar combine-meeting">
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                          
                         
                           <%
                            java.util.Iterator ii= sched.keySet().iterator();
                            while( ii.hasNext()){
                                java.util.Date dt = (java.util.Date) ii.next();
                                MeetingE me = (MeetingE)sched.get(dt);
                                if( me.getRefId().equals( meetingsToCancel.get(i).getRefId())){
                                    %>
                                     <td>
			                             <input type="checkbox" name="_tag_m" id="y<%=meetingsToCancel.get(i).getUid() %>" value="<%=dt.getTime()%>">
			                             <label for="y<%=meetingsToCancel.get(i).getUid() %>"><span></span><p></p></label>
                                     </td>
                                     <td> <%=dt%> </td>
                                   <% 
                                }
                            }
                           %>
                           
                           
                           <td><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></td>
                           </tr>                           
                        <% }
              %>
              </table>   



              <div class="row">

              <div class="small-24 column">
            <input type="button" value="save" id="saveCalElem" class="button btn right">  <input ttype="button" onclick="tabsVtk.goto('combine-meeting-time')" value="Combine Meetings"  class="button btn right"> 
            <div id="dialog-confirm"></div>
          </div>
             </div>
       
        
        <!-- <input type="button" onclick="doCombine()" value="Combine Meetings"/> -->
        
        <script>
        function doCombine(){
        	var mids = "";
        	var checkboxes = document.getElementsByName("_tag_m");
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
                url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
                type: 'GET',
                data: {
                	act:'combineCal',
                    dt:'1476722888234',
                    mids: mids,
                    a: Date.now()
                },
                success: function(result) {
                	//console.log("succ "+ result);
                    //document.getElementById("combineMeetings").innerHtml=result;
                    
                    //close window
                    alert("done..");
                }
            });
        }
        </script>