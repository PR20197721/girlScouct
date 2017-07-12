              <p style="font-weight:bold;">
                Select the meetings you'd like to schedule for the same day
              </p>

             <table class="list-of-meeting-calendar combine-meeting yearMeetingList">
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {
                            java.util.Iterator ii= sched.keySet().iterator();
                            while( ii.hasNext()){
                                java.util.Date dt = (java.util.Date) ii.next();
                                if( dt.before( new java.util.Date() ) || org.girlscouts.vtk.models.Activity.class.isInstance(sched.get(dt)) )
                                    {continue;}
                                MeetingE me = (MeetingE)sched.get(dt);
                                if( me.getType() ==org.girlscouts.vtk.dao.YearPlanComponentType.MEETING )
                                 if( me.getRefId().equals( meetingsToCancel.get(i).getRefId())){
                                    %>
                                     <tr>
                                     <td>
			                             <input type="checkbox" name="_tag_m" id="y<%=meetingsToCancel.get(i).getUid() %>" value="<%=dt.getTime()%>">
			                             <label for="y<%=meetingsToCancel.get(i).getUid() %>"><span></span><p></p></label>
                                     </td>
                                     <td> <%=VtkUtil.formatDate(VtkUtil.FORMAT_CALENDAR_DATE,  dt )%> </td>
                                      <td><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></td>
			                          <td class="vtk_age_level <%= meetingsToCancel.get(i).getMeetingInfo().getLevel() %>"><%= meetingsToCancel.get(i).getMeetingInfo().getLevel().charAt(0) %></td>
			                           </tr>
                                   <%
                                }
                            }
                           }
              %>
              </table>



              <div class="row">

              <div class="small-24 column">

            <div id="dialog-confirm"></div>
            <input type="button" onclick="continueCombine()" value="Continue"  class="combine-meetings-button inactive-button button btn right">
            <input type="button" value="cancel" onclick="cancelModal()" class="button btn right">
            <div id="dialog-confirm"></div>
          </div>
             </div>



        <script>


        function tableToJson(table,orden){
            var finalObject = {};
            var $table = $(table);
            var $tr = $table.children('tbody').children('tr');


            $tr.each(function(idx, el){
              finalObject[idx] = {}

              $(el).children('td').each(function(idx2,el2){
                  if(orden[idx2] !== 'input'){
                    finalObject[idx][orden[idx2]] = $(el2).text();
                  }else{
                    finalObject[idx][orden[idx2]] = $(el2).children('input');
                  }
              })


            })

            return finalObject;
        }


        function continueCombine(){

          $('.meetings-to-combine-list').html('');

          var objectInfo = tableToJson('.combine-meeting',['input','time','name','level']);
          for (var lineX in objectInfo){
              if(objectInfo[lineX].input[0].checked){
               $('.meetings-to-combine-list').append('<li>'+objectInfo[lineX].name+'</li>')
              }
          }

          tabsVtk.goto('combine-meeting-time');
        }


        checkSaveButton('_tag_m','.combine-meetings-button', 1)

        var selectedTime = function(){

            var _selectedTime;

            function set(value){
                _selectedTime = value;
            }

            function get(){
              return _selectedTime;
            }

            return {
              set: set,
              get: get
            }
        };


        var sTimeCombine = new selectedTime();



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
        	var hour = $('#cngTime0X').val() +' '+  $('#cngAP0X').val();
            var my= moment( moment( sTimeCombine.get()).format("MM/DD/YYYY") + " " + hour , "MM/DD/YYYY HH:mm a");
            var dt = my.format('l') +" "+ hour;

              if(my.isValid()){
                  $.ajax({
                      url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
                      type: 'GET',
                      data: {
                        act:'combineCal',
                          dt:dt,
                          mids: mids,
                          a: Date.now()
                      },
                      success: function(result) {

                          cancelModal();
                      }
                  });
              }else{
                $('.alert-error-display').show();
              }



        }
        </script>
