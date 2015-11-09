 
   <ul>
      {this.props.data.map((function(item, i) {
      return <li className="row"  key={item.activityNumber} id={item.activityNumber}>
        <div className="wrapper clearfix">
          <div className="large-3 medium-3 small-3 columns small-push-1 large-push-2">
            <span>{  moment('<%=planView.getSearchDate()%>').format('YYYY') <1978 ? item.activityNumber : moment( getAgendaTime( item.duration )).format("h:mm")} </span>
          </div>
            <div className="large-17 columns medium-17 small-17 small-push-1 large-push-1">
              <ActivityName item={item} key={item.uid} selected={item.uid} itemSelected={this.setSelectedItem} activityNumber={item.activityNumber - 1} /> 
            </div>
            <div className="large-3 medium-3 small-3 columns">
              <span>:{item.duration<10 ? ("0"+item.duration) : item.duration}</span>
            </div>
          </div>
        </li>;
  				}))	
  			}
  			
  </ul>
  
