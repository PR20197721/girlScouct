<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/meeting_react1.jsp -->
<cq:defineObjects />

<%@include file="include/session.jsp"%>
<%
  String activeTab = "planView";
    boolean showVtkNav = true;
    
	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(user, troop, request);
	String mid = planView.getYearPlanComponent().getUid();
	java.util.Date searchDate = new java.util.Date(planView.getSearchDate().getTime());


%>


<!-- <script src="http://code.jquery.com/jquery-1.9.1.js"></script> -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>


<%@include file="include/tab_navigation.jsp"%>
<!--page wrapper-->
<div id="panelWrapper" class="row content meeting-detail">
  <!--/utility links and download, print, help links-->
  <%@include file="include/utility_nav.jsp"%>
  <!--/meeting title and navigation-->
  
<!-- 
  <%@include file="include/meeting_maininfo.jsp"%>
  <%@include file="include/meeting_planning.jsp"%>
  <%@include file="include/meeting_communication.jsp"%>
  <%@include file="include/meeting_aids.jsp"%>
  <%@include file="include/meeting_agenda.jsp"%>
  <!--/TODO this is for text only-->
  <%@include file="include/modal_1.jsp"%>
-->



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
      <section className="column large-20 medium-20 large-centered medium-centered">
 		 <h6>meeting aids</h6>
 			 <ul className="large-block-grid-2 medium-block-grid-2 small-block-grid-1">
        		{commentNodes}
     				<li><a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);" title="Add meeting aids"><i className="icon-button-circle-plus"></i> Add Meeting Aids</a></li>
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
		<div>
		<%@include file="include/meeting_navigator.jsp"%>
 		<%@include file="include/meeting_maininfo.jsp"%>
		<%@include file="include/meeting_planning.jsp"%>
		<%@include file="include/meeting_communication.jsp"%>
		</div>
    );
  }
});

var MeetingAsset = React.createClass({
  render: function() {
    return (
		<%@include file="include/meeting_aids.jsp"%>
    );
  }
});


var MeetingActivity = React.createClass({
  render: function() {
    return (
			<div>meetingactivity</div>
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
		return <section className="column large-20 medium-20 large-centered medium-centered">
  					<h6>meeting agenda</h6>
  					<p>Select and agenda item to view details, edit duration and delete. Drag and drop to reorder.</p>
 						<SortableListItems1 key="{this.props.data}"  data={this.props.data}  onReorder={this.onReorder}/>
				</section>;
	}
});


var SortableListItems1 = React.createClass({
  render: function() {

   return (
		<%@include file="include/meeting_agenda.jsp"%>
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
    
    
<!--/TODO this is for text only-->
  <%@include file="include/modal_1.jsp"%>
</div>

