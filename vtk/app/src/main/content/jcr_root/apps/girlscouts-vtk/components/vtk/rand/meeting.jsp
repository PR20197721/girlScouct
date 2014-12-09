
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="../include/session.jsp"%>
<%

	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(  user,  troop,  request);
	String mid=planView.getYearPlanComponent().getUid();
	
%>

<html>
 



  <!-- script src="http://fb.me/react-0.12.1.js"></script -->
   <!--script src="http://fb.me/JSXTransformer-0.12.1.js"></script -->
   <!--script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script -->
   <!--script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script -->
  
       <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
    <script src="http://fb.me/react-0.12.1.js"></script>
   <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
     <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
  
    
  <body>
  
  <style>
  	  .meetingHeader{border:5px solid green; background-color:yellow;}
  	
	  li.placeholder { background: rgb(255,240,120);}
	  li.placeholder:before {content: "Drop here"; color: rgb(225,210,90);}
	  ul { list-style: none; margin:0; padding:0}
	  li {padding: 5px; background:#eee}
	  
  
  </style>
 <div id="testDiv"></div>
 <hr/>
 
 
    <div id="content"></div>
    <script type="text/jsx">

var thisMeetingRefId;
var thisMeetingPath;
var MeetingList = React.createClass({
 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
    //setInterval(this.toggle, 1500);
  },
  toggle: function() {
    this.setState({ show: !this.state.show });
  },
  render: function() {
    var commentNodes = this.props.data.map(function (comment ,i ) {


if(comment.uid=='<%=mid%>'){
	thisMeetingRefId	= comment.refId;
	thisMeetingPath = comment.path;
      return (

        <YearPlan  item={comment} key={i} >
			
			<MeetingPlan uid={comment.uid} meetingTitle={comment.meetingInfo.name}
				meetingId={comment.id}
				location={comment.locationRef} blurb={comment.meetingInfo.meetingInfo["meeting short description"].str} />
			<MeetingAssets data={comment.assets} />
			<MeetingActivities data={comment.meetingInfo.activities} />

        </YearPlan>

      );
}
    });
    return (
      <div className="commentList">
        {commentNodes}
      </div>
    );
  }
});







var placeholder = document.createElement("li");
placeholder.className = "placeholder";





var MeetingActivities = React.createClass({
total: 0,

 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
    
  },
	componentDidMount: function(){ 	
		
	},
  toggle: function() {
    this.setState({ show: !this.state.show });
  },
  handleSort: function(newOrder) {
          var newItems = newOrder.map(function(index) {
            return this.props.data[index];
          }.bind(this));
          this.setState({data: newItems});
        },
  render: function() {
	this.total = xx1(this.props.data);
    var commentNodes = this.props.data.map((function (comment ,i ) {
	  
      return (
		    		  	
			<%@include file="meetingActivity.jsp"%>
	    
      );

    }).bind(this));
    return (
	<div>
      
        {commentNodes}

	 	total time: {this.total}
	</div>
    );
  }
});

var ActivityName = React.createClass({
   
    onClick: function() {

		loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid%>&isAgenda='+(this.props.item.activityNumber-1), true, 'Agenda')

 
    },
    render: function() {
        return (
            <a href="#" onClick={this.onClick} className={this.props.selected ? "selected" : ""}>
               {this.props.item.name}
            </a>
        );
    }
});



var MeetingAssets = React.createClass({
 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
   // setInterval(this.toggle, 1500);
  },
  toggle: function() {
    this.setState({ show: !this.state.show });
  },
  render: function() {
    var commentNodes = this.props.data.map(function (comment ,i ) {
      return (
		  <MeetingAsset  item={comment} key={i} refId={comment.refId} title={comment.title} description={comment.description}/>
      		
      );

    });
    return (
      <ul>
        {commentNodes}
      </ul>
    );
  }
});








var YearPlanComponent = React.createClass({
 getInitialState: function() {
    return { show: false };
  },
  render: function() {
    var commentNodes = this.props.data;
    return (
      <div className="commentList">
        {commentNodes}
      </div>
    );
  }
});



var YearPlan = React.createClass({
  render: function() {
    return (
      <div className="comment">
        <h2 className="commentAuthor">
          {this.props.author}
        </h2>
        {this.props.children}
      </div>
    );
  }
});

var MeetingPlan = React.createClass({
  render: function() {
    return (
		<%@include file="meetingHeader.jsp"%>
    );
  }
});

var MeetingAsset = React.createClass({
  render: function() {
    return (
		<%@include file="meetingAsset.jsp"%>
    );
  }
});


var MeetingActivity = React.createClass({
  render: function() {
    return (
		
		 
			<%@include file="meetingActivity.jsp"%>
	
		 
	
    );
  }
});


var CommentBox = React.createClass({

 loadCommentsFromServer: function( isFirst ) {
   $.ajax({
      url: this.props.url + (isFirst==1 ? ("&isFirst="+ isFirst) : ''),
      dataType: 'json',
	  cache: false,
      success: function(data) {
       this.setState({data: data.yearPlan.meetingEvents});
 	   this.setState({yp: data});

      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
 },

  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {

    this.loadCommentsFromServer(1);
    setInterval( this.loadCommentsFromServer, this.props.pollInterval);
  },
  render: function() {

	var x = this.state.data ? this.state.data : 'loading...';
	var y = this.state.yp ? this.state.yp.uid : 'loading year plan...';

    return (
      <div className="commentBox">
        
       
 			<MeetingList data={x} />
       		<YearPlanComponent data={y} />
      </div>
    );
  }
});


	  function cloneWithProps(c) {
        var newInstance = new c.constructor();
        newInstance.construct(c.props);
        return newInstance;
      }





      var SmartSortable = React.createClass({
        getDefaultProps: function() {
          return {component: React.DOM.ul, childComponent: React.DOM.li};
        },

        render: function() {
          var component = this.props.component;
console.log( this.props );
console.log( this.props.component ); 
console.log(this)

          return  null;//this.transferPropsTo(<component />);

 
        },

        componentDidMount: function() {
          jQuery(this.getDOMNode()).sortable({stop: this.handleDrop});
          this.getChildren().forEach(function(child, i) {
            jQuery(this.getDOMNode()).append('<' + this.props.childComponent.componentConstructor.displayName + ' />');
            var node = jQuery(this.getDOMNode()).children().last()[0];
            node.dataset.reactSortablePos = i;
            React.renderComponent(cloneWithProps(child), node);
          }.bind(this));
        },
        componentDidUpdate: function() {
          var childIndex = 0;
          var nodeIndex = 0;
          var children = this.getChildren();
          var nodes = jQuery(this.getDOMNode()).children();
          var numChildren = children.length;
          var numNodes = nodes.length;

          while (childIndex < numChildren) {
            if (nodeIndex >= numNodes) {
console.log(221);
console.log(this.props)
console.log(this.props.childComponent)
              jQuery(this.getDOMNode()).append('<' + this.props.childComponent.componentConstructor.displayName + '/>');
              nodes.push(jQuery(this.getDOMNode()).children().last()[0]);
              nodes[numNodes].dataset.reactSortablePos = numNodes;
              numNodes++;
            }
            React.renderComponent(cloneWithProps(children[childIndex]), nodes[nodeIndex]);
            childIndex++;
            nodeIndex++;
          }
          while (nodeIndex < numNodes) {
            React.unmountComponentAtNode(nodes[nodeIndex]);
            jQuery(nodes[nodeIndex]).remove();
            nodeIndex++;
          }
        },
        componentWillUnmount: function() {
          jQuery(this.getDOMNode()).children().get().forEach(function(node) {
            React.unmountComponentAtNode(node);
          });
        },
        getChildren: function() {
          return this.props.children || [];
        },
        handleDrop: function() {
          var newOrder = jQuery(this.getDOMNode()).children().get().map(function(child, i) {
            var rv = child.dataset.reactSortablePos;
            child.dataset.reactSortablePos = i;
            return rv;
          });
          this.props.onSort(newOrder);
        }
      });



React.render(
<CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={2000} />,
  document.getElementById('content')
);


function xx(activities){
//console.log(1);
//console.log( YearPlan);
//console.log(this.props);
//alert( MeetingPlan.refId );
	var yy='';
	for( var x in activities ){
		//console.log( "*** "+ activities[x].activityNumber);
		yy+= activities[x].activityNumber +",";
	} 
//console.log(yy);
repositionActivity(thisMeetingRefId , yy);
	
}



function xx1(activities){
	var total=0 ;
	for( var x in activities ){	
		var i = activities[x].duration ;
		total += i;		
	} 
	return total;	
}


var App = React.createClass({
        getInitialState: function() {
          return {items: ['test 0', 'test 1', 'test 2'], counter: 3};
        },
        handleSort: function(newOrder) {
          var newItems = newOrder.map(function(index) {
            return this.state.items[index];
          }.bind(this));
          this.setState({items: newItems});
        },
        render: function() {
          var items = this.state.items.map(function(item) {
            return <li key={item}>{item}</li>;
          });
          return (
            <div>
             
              <SmartSortable onSort={this.handleSort}>
                {items}
              </SmartSortable>
             
            </div>
          );
        }
      });
   React.renderComponent(<App />, document.getElementById('testDiv'));

    </script>
    
    <hr/>
    <a href="javascript:void(0)" onclick="revertAgenda( thisMeetingPath )"  class="mLocked">Revert to Original Agenda</a>
    <br/><br/>
    
    	<a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add Agenda Items</a>


<div id="newMeetingAgenda" style="display:none;">

       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment:
	<select id="newCustAgendaDuration">
		<option value="5">5</option>
		<option value="10">10</option>
                <option value="15">15</option>
		<option value="20">20</option>
                <option value="25">25</option>
		<option value="30">30</option>
	</select>
	
	
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	<div class="linkButtonWrapper">
		<input type="button" value="save" onclick="createCustAgendaItem1('<%=planView.getSearchDate().getTime()%>', '1', thisMeetingPath)" class="button linkButton"/>
	</div>

    
  </body>
</html>

