            <p style="font-weight:bold;">
              Select the meeting you want to cancel and "Save" your choice.
            </p>

             <table class="list-of-meeting-calendar cancel-meeting yearMeetingList">
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


                                       <input type="radio" name="_tag_t" id="x<%=meetingsToCancel.get(i).getUid() %>" value="<%=meetingsToCancel.get(i).getUid()%>"  />
                                            <label for="x<%=meetingsToCancel.get(i).getUid() %>"><span></span><p> </p></label>



                                      </td>
                                   
                                     
                                      <td> 
                                    <%=VtkUtil.formatDate(VtkUtil.FORMAT_CALENDAR_DATE,  dt )%>
                                     </td>
                                    
                                    <td><%= meetingsToCancel.get(i).getMeetingInfo().getName()%></td>
                                     <td class="vtk_age_level <%= meetingsToCancel.get(i).getMeetingInfo().getLevel() %>"><%= meetingsToCancel.get(i).getMeetingInfo().getLevel().charAt(0) %></td>
                                   
                                    </tr>
                                    <% 
                                }
                            }
                           
                        }  %>
              </table>   
       




          <div class="row">
            <div class="small-24 column">
            <input type="button" value="save" id="saveCalElem" onclick="saveCalElem()" class="cancel-meetings-button inactive-button button btn right">  <input type="button" value="cancel" id="cancelCalElem" onclick="cancelModal()" class="button btn right"> 
            <div id="dialog-confirm"></div>
        </div>
          </div>
<script>

function checkSaveButton(name,className,number){

  function changeButton(){
      
      var _array = [].slice.call(document.getElementsByName(name));
      var _state = 0;
      
      var _arrayOfChecked = _array.filter(function(e){
          if(e.checked){
              return e;
          }
      });
    
      if(number){
          _state = number;
      }

      if(_arrayOfChecked.length > _state){
        $(className).removeClass('inactive-button');
      }else{
         $(className).addClass('inactive-button');
      } 
  }  

  $('[name='+name+']').on('change',changeButton)     
}

checkSaveButton('_tag_t','.cancel-meetings-button')



</script>
