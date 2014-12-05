
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

var thisMeetingRefId;
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
      return (
        <YearPlan  item={comment} key={i} >
			
			<MeetingPlan uid={comment.uid} meetingTitle={comment.meetingInfo.name}
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
dragStart: function(e) {

    this.dragged = e.currentTarget;
    e.dataTransfer.effectAllowed = 'move';
    
    // Firefox requires dataTransfer data to be set
    e.dataTransfer.setData("text/html", e.currentTarget);
  },
  dragEnd: function(e) {

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
console.log("final");
console.log( data);
console.log( this.props)
 xx(data);
  },
  dragOver: function(e) {

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
  toggle: function() {
    this.setState({ show: !this.state.show });
  },
  render: function() {
    var commentNodes = this.props.data.map((function (comment ,i ) {
      return (
		    		  	
			        
	    <%@include file="meetingActivity.jsp"%>
      );

    }).bind(this));
    return (
      <ul onDragOver={this.dragOver}>
        {commentNodes}
      </ul>
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
console.log(1);
console.log( YearPlan);
console.log(this.props);
//alert( MeetingPlan.refId );
	var yy='';
	for( var x in activities ){
		console.log( "*** "+ activities[x].activityNumber);
		yy+= activities[x].activityNumber +",";
	} 
console.log(yy);
repositionActivity(thisMeetingRefId , yy);
	
}


	
    </script>
    
    
   
  </body>
</html>

