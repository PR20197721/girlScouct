<li data-id={i} key={i}  
			draggable="true"
            onDragEnd={this.dragEnd}
            onDragStart={this.dragStart}
              
             onTouchStart={this.dragStart} 
             onTouchEnd={this.dragEnd} 
             >
            
			<img className="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll-small.png" width="21" height="34"/>
			<ActivityName item={comment} key={comment.uid} selected={comment.uid} itemSelected={this.setSelectedItem} activityNumber={comment.activityNumber} />
			---{comment.duration}
	</li>
	
	