 <ul>
        	{this.props.data.map((function(item, i) {
        		return <li key={item.activityNumber} className="row" id={item.activityNumber} >
				    <div class="wrapper clearfix">
						<div className="large-3 medium-3 small-4 columns push-2">
					        <span>{item.activityNumber}</span>
				        </div>
						<div className="large-17 columns push-1">
							<ActivityName item={item} key={item.uid} selected={item.uid} itemSelected={this.setSelectedItem} activityNumber={item.activityNumber - 1} />
			      		</div>
						<div className="large-3 columns">
							<span>:{item.duration}</span>
      					</div>
					</div>
                </li>;
				}))	
			}	
        </ul>