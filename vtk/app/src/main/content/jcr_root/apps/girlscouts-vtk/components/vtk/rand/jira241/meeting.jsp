
<%
	String myProjLoc = "jira241";
%>



<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="../../include/session.jsp"%>
<%
	org.girlscouts.vtk.models.PlanView planView = meetingUtil
			.planView1(user, troop, request);
	String mid = planView.getYearPlanComponent().getUid();
	java.util.Date searchDate = new java.util.Date(planView
			.getSearchDate().getTime());
%>

<html>

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script
	src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>


<body>

	<style>
.meetingHeader {
	border: 5px solid green;
	background-color: yellow;
}

li.placeholder {
	background: rgb(255, 240, 120);
}

li.placeholder:before {
	content: "Drop here";
	color: rgb(225, 210, 90);
}

ul {
	list-style: none;
	margin: 0;
	padding: 0
}

li {
	padding: 5px;
	background: #eee
}
</style>


	<%@include file="meetingMain.jsp"%>

	<div id="panelWrapper"></div>
	<script type="text/jsx">

var thisMeetingRefId;
var thisMeetingPath;
var thisMeetingImg="tata";
var thisMeetingDate="a";


var MeetingList = React.createClass({
 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
  },
  toggle: function() {
    this.setState({ show: !this.state.show });
  },

  render: function() {

	var scheduleDates = this.props.schedule.dates;	
    var commentNodes = this.props.data.map(function (comment ,i ) {

if(comment.uid=='<%=mid%>'){
	
if( scheduleDates !=null ){
	var scheduleDatesArray = scheduleDates.split(',');
	thisMeetingDate =  scheduleDatesArray[comment.id] ;

}
	thisMeetingRefId  = comment.refId;
	thisMeetingPath  = comment.path;
	thisMeetingImg   = "/content/dam/girlscouts-vtk/local/icon/meetings/"+ comment.meetingInfo.id +".png";	
	thisMeetingDate = new Date( Number(thisMeetingDate) );
      return (
        <YearPlan  item={comment} key={i} >		
			<MeetingPlan meetingModMONTH={moment(thisMeetingDate).format('MMMM')} meetingModDAY={moment(thisMeetingDate).format('DD')} meetingModHOUR={moment(thisMeetingDate).format('h:mm a')} uid={comment.uid} meetingTitle={comment.meetingInfo.name}
				meetingId={comment.id} meetingGlobalId={thisMeetingImg}
				location={comment.locationRef} blurb={comment.meetingInfo.meetingInfo["meeting short description"].str} />			
			<MeetingAssets data={comment.assets} />
			<SortableList1 data={comment.meetingInfo.activities}/>
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
      <section class="column large-20 medium-20 large-centered medium-centered">
  		<h6>meeting aids</h6>
  			<ul class="large-block-grid-2">
        		{commentNodes}
			</ul>
		
	 </section>  
      
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
		this.setState({data:data.yearPlan});
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

var x;
var sched;
if( this.state.data.meetingEvents!=null){
	 x =  this.state.data.meetingEvents;
     sched = this.state.data.schedule;
	 return (
 			<MeetingList data={x} schedule={sched} /> 
    );
}else{
	return <div>loading...</div>;
}
  }
});



var SortableList1 = React.createClass({

	getInitialState: function() {
		return {data: this.props.data};
	},

	onReorder: function (order) {
	
	},

    render: function () {
		return <SortableListItems1 key="{this.props.data}"  data={this.props.data}  onReorder={this.onReorder}/>;
	}
});


var SortableListItems1 = React.createClass({
  render: function() {

   return (
		<%@include file="meetingActivity1.jsp"%>
    );
	
  },
  componentDidMount: function() {
    	var dom = $(this.getDOMNode());
    	var onReorder = this.props.onReorder;
        dom.sortable({
        	stop: function (event, ui) {
        		var order = dom.sortable("toArray", {attribute: "id"});
   			    var yy  = order.toString().replace('"','');

				repositionActivity1(thisMeetingRefId , yy);

        	}
        });	
    }
});



function repositionActivity1(meetingPath,newVals ){
		
	var x =$.ajax({
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RearrangeActivity&mid='+meetingPath+'&isActivityCngAjax='+ newVals, // JQuery loads serverside.php
		data: '', 
		dataType: 'html', 
		success: function (data) { 

			//location.reload();
		},
		error: function (data) { 
		}
	});
} 





React.render(
<CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={10000} />,
  document.getElementById('panelWrapper')
);


    </script>

	<hr />
	<a href="javascript:void(0)" onclick="revertAgenda( thisMeetingPath )"
		class="mLocked">Revert to Original Agenda</a>
	<br />
	<br />

	<a href="javascript:void(0)"
		onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add
		Agenda Items</a>


	<div id="newMeetingAgenda" style="display: none;">

		<h1>Add New Agenda Item</h1>

		Enter Agenda Item Name:<br /> <input type="text"
			id="newCustAgendaName" value="" /> <br />Time Allotment: <select
			id="newCustAgendaDuration">
			<option value="5">5</option>
			<option value="10">10</option>
			<option value="15">15</option>
			<option value="20">20</option>
			<option value="25">25</option>
			<option value="30">30</option>
		</select> <br />Description:
		<textarea id="newCustAgendaTxt"></textarea>
		<br /> <br />
		<div class="linkButtonWrapper">
			<input type="button" value="save"
				onclick="createCustAgendaItem2('<%=planView.getSearchDate().getTime()%>', '1', thisMeetingPath)"
				class="button linkButton" />
		</div>

		<div id="myActivity">activity</div>
</body>
</html>

