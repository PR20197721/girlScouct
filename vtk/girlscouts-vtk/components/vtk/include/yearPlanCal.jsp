<div class="column small-24 medium-12 large-8">
                    <input type="radio" value="change" id="cngRadio" CHECKED onchange="tabsVtk.goto('calendar-meeting')" name="goto" /><label for="cngRadio"><p>Change Date / Time</p></label>
   <form id="frmCalElem">
                <p><strong>Change Date:</strong></p>
                <span>Select today's date or any future date</span>
                <div id="datepicker"></div>
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
            <span  id="cngDate0ErrMsg"></span>
        </div>