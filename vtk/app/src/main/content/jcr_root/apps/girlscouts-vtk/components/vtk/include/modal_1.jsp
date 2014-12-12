
<!--/TODO rename this once done -->
<div class="modal-box row">

    <div class="header clearfix">
      <h3 class="columns large-22">MEETING date and locations</h3><a class="columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
    </div>

    <div class="tabs-wrapper">
      <dl class="tabs" data-tab>
        <dd class="active"><a href="#panel1">manage calendar</a></dd>
        <dd><a href="#panel2">manage location</a></dd>
        <dd><a href="#panel3">manage activity</a></dd>
      </dl>
      <div class="tabs-content">
        <div class="content active clearfix" id="panel1">
          <form>
            <section class="clearfix">
              <p>Set up your meeting date and times:</p>
              <div class="large-4 columns">
                <input type="text" placeholder="Start Date" />
              </div>
              <div class="large-2 columns">
                <a href="#" title="calendar"><i class="icon-calendar"></i></a>
              </div>
              <div class="large-4 columns">
                <input type="text" placeholder="Time" />
              </div>
              <div class="large-3 columns">
                <select>
                  <option value="0">AM</option>
                  <option value="1">PM</option>
                </select>
              </div>
              <div class="large-4 columns left">
                <select>
                  <option value="0">Frequency</option>
                  <option value="1">Once a day</option>
                </select>
              </div>
            </section>
            <section>
              <p>Do not schedule the meeting the week of:</p>
              <input id="checkbox1" type="checkbox" /><label for="checkbox1"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
              <input id="checkbox2" type="checkbox" /><label for="checkbox2"><p><span class="date">12/12/14</span><span>Another Holiday</span></p></label>
              <input id="checkbox3" type="checkbox" /><label for="checkbox3"><p><span class="date">12/12/14</span><span>Holiday Name</span></p></label>
              <input id="checkbox4" type="checkbox" /><label for="checkbox4"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
              <input id="checkbox5" type="checkbox" /><label for="checkbox5"><p><span class="date">12/12/14</span><span>Thanksgiving</span></p></label>
            </section>
            <button class="btn right">create calendar</button>
          </form>
        </div>
        <div class="content clearfix" id="panel2">
          <p>This is the second panel of the basic tab example. This is the second panel of the basic tab example.</p>
        </div>
        <div class="content clearfix" id="panel3">
          <p>This is the third panel of the basic tab example. This is the third panel of the basic tab example.</p>
        </div>
    </div>
  </div><!--/tab wrapper-->
</div><!--/modal-->