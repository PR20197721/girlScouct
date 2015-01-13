<div id="modal_agenda" class="reveal-modal" data-reveal>
  <div class="header clearfix">
    <h3 class="columns large-22">Agenda</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="content clearfix active row">
    <form class="clearfix">
      <section class="clearfix">
        <p>Add New Agenda Item</p>
        <div class="large-8 columns">
          <input type="text" placeholder="Enter Ageda Item Name" />
        </div>
        <div class="large-8 columns">
          <select id="newCustAgendaDuration" value="Time Allotment">
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="15">15</option>
            <option value="20">20</option>
            <option value="25">25</option>
            <option value="30">30</option>
          </select>
        </div>
        <div class="large-8 columns left">
          <input type="text" placeholder="Description" />
        </div>
      </section>
      <button onclick="createCustAgendaItem2('<%=planView.getSearchDate().getTime()%>', '1', thisMeetingPath)" class="button btn right">Save</button>
    </form>
  </div>
</div>