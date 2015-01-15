 <div class="header clearfix">
    <h3 class="columns large-22">Agenda</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content row">
      <div class="setupCalendar">
        <h3>Agenda Item: Activity name</h3>
        <div class="columns small-4">
          <select>
            <option value="Time Allotment" selected>Time Allotment</option>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="15">15</option>
            <option value="20">20</option>
            <option value="25">25</option>
            <option value="30">30</option>
          </select>
        </div>
        <section class="columns small-20">
          <button onclick="location.reload();" class="btn button">Save and Back to meeting</button>
          <button class="btn button">Delete This Agenda Item</button>
        </section>
        <div class="clearfix columns small-24">
          <%=request.getParameter("mid") %>
        </div>
      </div>
    </div>
  </div>