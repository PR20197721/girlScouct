   
   
                <div class="small-24 medium-8 column">
                    [names here]
                </div>

                <div class="small-24 medium-12 column end">
                    <form id="frmCalElem2">
                        <!-- <p><strong>Change Date:</strong></p>
                        <span>Select today's date or any future date</span> -->
                        <div id="datepicker2"></div>
                        <input type="hidden" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY,date) %>" id="cngDate0"  name="cngDate0" class="date calendarField" />
                        <p><strong>Change Time:</strong></p>
                        <section class='row clearfix'>
                            <div class="column small-6">
                                <input type="text" id="cngTime0" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm,date) %>" name="cngDate0"  />
                            </div>
                            <div class="columm small-6 left">
                                <select id="cngAP0" name="cngAP0" class="ampm">
                                    <option value="pm" <%= AP.equals("PM") ? "SELECTED" : "" %>>PM</option> 
                                    <option value="am" <%= AP.equals("AM") ? "SELECTED" : "" %>>AM</option>
                                </select>
                            </div>
                        </section>
                    </form>
                    <span  id="cngDate0ErrMsg2"></span>
                </div>


                <div class="vtk-meeting-calendar-foot column small-24 column">
                    <div class="row">
                               ??
                    </div>
                </div>  
            
             
   
   
   
        <input type="button"  value="Cancel" />
        <input type="button" onclick="tabsVtk.goBack()" value="Back" />
        <input type="button" value="Save" onclick="doCombine()"/>
        
        <script>
        $(function() {
            $( "#datepicker2" ).datepicker({
                  defaultDate: new Date ('<%=date%>'),
                  minDate: 0,
                  onSelect: function(dateText, inst) { 
                      var dateAsString = dateText; 
                      var dateAsObject = $(this).datepicker( 'getDate' ); 
                      
                      document.getElementById("cngDate0").value =dateAsString;
                      
                   }
            });
        });
        </script>
   