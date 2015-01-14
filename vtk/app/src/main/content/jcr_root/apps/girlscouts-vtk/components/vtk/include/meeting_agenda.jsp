 
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
  
