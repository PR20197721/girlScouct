              <p>
                Select the mmeting you'd like to schedule for the same day
              </p>
             
             <table class="list-of-meeting-calendar combine-meeting yearMeetingList">
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
                          <td class="vtk_age_level <%= meetingsToCancel.get(i).getMeetingInfo().getLevel() %>"><%= meetingsToCancel.get(i).getMeetingInfo().getLevel().charAt(0) %></td>
                           </tr>                           
                        <% }
              %>
              </table>   



              <div class="row">

              <div class="small-24 column">
          
            <div id="dialog-confirm"></div>  <input ttype="button" onclick="checkListIsOneIsChecked('_tag_m',{yes: function(){tabsVtk.goto('combine-meeting-time')},no:function(){ modalCalendar.alert('','Need to select a least one field to continue') }})" value="Continue"  class="button btn right"> 
            <input type="button" value="cancel" onclick="cancelModal()" class="button btn right"> 
            <div id="dialog-confirm"></div>
          </div>
             </div>
       
        
        <!-- <input type="button" onclick="doCombine()" value="Combine Meetings"/> -->
        
        <script>

        var selectedTime = (function(){
          
            var _selectedTime;
  
            function set(value){
                _selectedTime = value;
            }

            function get(){
              return _selectedTime || '1476722888234';
            }

            return {
              set: set,
              get: get
            }
        })(); 

        function doCombine(){
        	var mids = "";
        	var checkboxes = document.getElementsByName("_tag_m");
        	  var checkboxesChecked = [];
        	 
        	  for (var i=0; i<checkboxes.length; i++) {
        	     
        	     if (checkboxes[i].checked) {
        	       
        	        mids += checkboxes[i].value+",";
        	     }
        	  }
        	  addCalendar(mids);
        }



        

        
        function addCalendar(mids){


        	console.log("addCalendar....");
        	$.ajax({
                url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
                type: 'GET',
                data: {
                	act:'combineCal',
                    dt:selectedTime.get(),
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