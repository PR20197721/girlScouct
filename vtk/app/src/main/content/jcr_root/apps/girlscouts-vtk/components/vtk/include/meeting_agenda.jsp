 <div class="sortable-list">
   <ul>
      {this.props.data.map((function(item, i) {
      return <li className="row"  key={item.activityNumber} id={item.activityNumber}>
        <div className="wrapper clearfix">
          <div className="large-3 medium-3 small-3 columns small-push-1 large-push-2">
            <span>{item.activityNumber}</span>
          </div>
            <div className="large-17 columns medium-17 small-17 small-push-1 large-push-1">
              <ActivityName item={item} key={item.uid} selected={item.uid} itemSelected={this.setSelectedItem} activityNumber={item.activityNumber - 1} /> 
            </div>
            <div className="large-3 medium-3 small-3 columns">
              <span>:{item.duration}</span>
            </div>
          </div>
        </li>;
  				}))	
  			}
  </ul>
  <strong><a data-reveal-id="modal_agenda"><i className="icon-button-circle-plus"></i> Add Agenda Item modal</a></strong>
  <br/>
  <a href="javascript:loadModal('#tataAgenda', true, 'Agenda', false);" title="Add meeting agenda"><i className="icon-button-circle-plus"></i> Add Agenda Item</a>
  <%@include file="modals/modal_agenda.jsp"%>
</div>