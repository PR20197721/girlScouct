



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
 



  <script src="http://fb.me/react-0.12.1.js"></script>
  <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
  <script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script>
 
  <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
  <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
    
    
  <body>
  
  <style>
  	  .meetingHeader{border:5px solid green; background-color:yellow;}
  	
	  li.placeholder { background: rgb(255,240,120);}
	  li.placeholder:before {content: "Drop here"; color: rgb(225,210,90);}
	  ul { list-style: none; margin:0; padding:0}
	  li {padding: 5px; background:#eee}
	  
  
  </style>
 
    <div id="content"></div>
    <script type="text/jsx">
React.initializeTouchEvents(true);


React.initializeTouchEvents(true); 



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
dragStart: function(e) {
console.log(1);
    this.dragged = e.currentTarget;
console.log(22);
console.log( e);
  	//e.dataTransfer.effectAllowed = 'move';


  console.log(2);
    // Firefox requires dataTransfer data to be set
   //-e.dataTransfer.setData("text/html", e.currentTarget);
  },
  dragEnd: function(e) {
console.log( "dragEnd");
    this.dragged.style.display = "block";
    this.dragged.parentNode.removeChild(placeholder);

    // Update data
    var data = this.props.data;//this.state.data;
    var from = Number(this.dragged.dataset.id);
    var to = Number(this.over.dataset.id);
    if(from < to) to--;
    if(this.nodePlacement == "after") to++;
    data.splice(to, 0, data.splice(from, 1)[0]);
    this.setState({data: data});

	 xx(data);
  },
  dragOver: function(e) {
console.log( "dragOver");
    e.preventDefault();
    this.dragged.style.display = "none";
    if(e.target.className == "placeholder") return;
    this.over = e.target;
    // Inside the dragOver method
    var relY = e.clientY - this.over.offsetTop;
    var height = this.over.offsetHeight / 2;
    var parent = e.target.parentNode;
    
    if(relY > height) {
      this.nodePlacement = "after";
      parent.insertBefore(placeholder, e.target.nextElementSibling);
    }
    else if(relY < height) {
      this.nodePlacement = "before"
      parent.insertBefore(placeholder, e.target);
    }
  },
 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
    //setInterval(this.toggle, 1500);
  },
	componentDidMount: function(){ 	
		/*		
			if( this.props.data!=null ){
					this.total = xx1(this.props.data);
			}
		*/
	},
  toggle: function() {
    this.setState({ show: !this.state.show });
  },
	onDrag: function (){
		console.log("dragging.... ");
},
  render: function() {
var onDrag = this.state.dragging ? this.onDrag : null;
	this.total = xx1(this.props.data);
    var commentNodes = this.props.data.map((function (comment ,i ) {
	  
      return (
		    		  	
			<li data-id={i} key={i}  
			draggable="true"
            onDragEnd={this.dragEnd}
            onDragStart={this.dragStart}>
            
			{comment.activityNumber}--{comment.name}-{comment.duration}
			</li>
	    
      );

    }).bind(this));
    return (
	<div>
      <ul onDragOver={this.dragOver}>
        {commentNodes}
      </ul>
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

