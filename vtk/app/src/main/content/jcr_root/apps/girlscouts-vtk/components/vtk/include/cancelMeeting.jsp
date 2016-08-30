            <p>
              Select the meeting you want to cancel and "Save" your choice.
            </p>

             <table class="list-of-meeting-calendar cancel-meeting yearMeetingList">
             <%
                        for(int i=0;i<meetingsToCancel.size();i++) {%>
                           <tr>
                           <td>


                            <input type="radio" name="_tag_t" id="x<%=meetingsToCancel.get(i).getUid() %>" value="<%=meetingsToCancel.get(i).getRefId()%>"  />
                                 <label for="x<%=meetingsToCancel.get(i).getUid() %>"><span></span><p> </p></label>


                     <!--         <input type="radio" value="<%=meetingsToCancel.get(i).getRefId()%>" id="x" name="meeting_select" /><label for="x"><p> </p></label> -->

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
                            <td class="vtk_age_level <%= meetingsToCancel.get(i).getMeetingInfo().getLevel() %>"><%= meetingsToCancel.get(i).getMeetingInfo().getLevel().charAt(0) %></td>
                          
                           </tr>                           
                        <% }
              %>
              </table>   
       




          <div class="row">
            <div class="small-24 column">
            <input type="button" value="save" id="saveCalElem" onclick="saveCalElem()" class="cancel-meetings-button inactive-button button btn right">  <input type="button" value="cancel" id="cancelCalElem" onclick="fnOpenNormalDialog()" class="button btn right"> 
            <div id="dialog-confirm"></div>
        </div>
          </div>
<script>

function checkSaveButton(name,className){
  function changeButton(){
      
      var _array = [].slice.call(document.getElementsByName(name));
    
      var _if = _array.some(function(e){
        return e.checked;
      })

      if(_if){
        $(className).removeClass('inactive-button');
      }else{
         $(className).addClass('inactive-button');
      } 
  }  

  $('[name='+name+']').on('change',changeButton)     
}

checkSaveButton('_tag_t','.cancel-meetings-button')



</script>
