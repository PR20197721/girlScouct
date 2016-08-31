               

                  <div class="small-24 column">
                
                <p style="font-weight:bold;">Select a date for the meetings and "Save" your choice.</p>

                <div class="row">
                     <div class="small-24 medium-8 column">
                          <p>
                            Meetings to schedule on the same day:
                          </p>
                            <ul class="meetings-to-combine-list">
                              
                            </ul>

                            <strong  id="combine-new-time" class="hide">New Date: <span></span></strong>
                     </div>

                     <div class="small-24 medium-12 column end">
                          <form id="frmCalElem2">
                              <!-- <p><strong>Change Date:</strong></p>
                              <span>Select today's date or any future date</span> -->
                              <div id="datepicker2"></div>

                              
                              <div class="alert-error-display hide">
                                Enter a valid time
                              </div>


                              <input type="hidden" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,date) %>" id="cngDate0"  name="cngDate0" class="date calendarField" />
                              <p><strong>Change Time:</strong></p>
                              <section class='row clearfix'>
                                  <div class="column small-6">
                                      <input type="text" id="cngTime0X" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm,date) %>" name="cngTime0X"  />
                                  </div>
                                  <div class="columm small-6 left">
                                      <select id="cngAP0X" name="cngAP0X" class="ampm">
                                          <option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option> 
                                          <option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
                                      </select>
                                  </div>
                              </section>
                          </form>
                          <span  id="cngDate0ErrMsg2"></span>
                     </div>
                </div>


                <div class="row">
                    <div class="vtk-meeting-calendar-foot column small-24 column">
                      <div class="row">
                          <input  class="button tiny right" type="button" value="Save" onclick="doCombine()"/>
                           <input class="button tiny right" type="button" onclick="tabsVtk.goBack()" value="Back" />
                           <input class="button tiny right"  onclick="closeModal()" type="button"  value="Cancel" />
                          
                      </div>
                   </div>  
                </div>

                
             </div>
             
   

        
        <script>
        $(function() {
            $( "#datepicker2" ).datepicker({
                  defaultDate: new Date ('<%=date%>'),
                  minDate: 0,
                  onSelect: function(dateText, inst) { 
                    
                      var dateAsString = dateText; 
                      var dateAsObject = $(this).datepicker( 'getDate' ); 


                      $('#combine-new-time').show().children('span').html(dateAsString);

          
                      selectedTime.set(new Date(dateAsString).getTime())
                      
                      document.getElementById("cngDate0").value = dateAsString;
                      
                   }
            });
        });
        </script>
   