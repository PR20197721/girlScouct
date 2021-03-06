<div id="modal_agenda" class="reveal-modal" data-reveal>
  <div class="header clearfix">
    <h3 class="columns large-22">Agenda</h3>
    <a class="close-reveal-modal columns large-2" href="#"><span style="color: black; font-size: 22px; padding-right: 10px; font-weight: normal;">X</span></a>
  </div>
  <div class="scroll">
    <div class="content clearfix">
      <form>
        <section class="clearfix row">
          <h4 class="column">Add New Agenda Item</h4>
          <div class="large-8 columns">
            <input type="text" placeholder="Enter Agenda Item Name" id="newCustAgendaName"/>
          </div>
          <div class="large-8 columns">
            <select id="newCustAgendaDuration">
              <option value="0" selected>Time Allotment</option>
              <option value="5">5</option>
              <option value="10">10</option>
              <option value="15">15</option>
              <option value="20">20</option>
              <option value="25">25</option>
              <option value="30">30</option>
              <option value="35">35</option>
              <option value="40">40</option>
              <option value="45">45</option>
              <option value="50">50</option>
              <option value="55">55</option>
              <option value="60">60</option>
            </select>
          </div>
          <div class="large-8 columns left">
            <input type="text" placeholder="Description" id="newCustAgendaTxt"/>
          </div>
        </section>
        <input type="button" onclick="createCustAgendaItem2(moment(thisMeetingDate).valueOf(), '1', thisMeetingPath)" class="button btn right" value="Save"/>
      </form>
    </div>
  </div>
</div>
