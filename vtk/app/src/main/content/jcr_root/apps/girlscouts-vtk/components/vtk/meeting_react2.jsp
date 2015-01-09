<% 

String mid = planView.getYearPlanComponent().getUid();
%>


<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<script src="http://fb.me/react-with-addons-0.12.1.js"></script>

<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content meeting-detail">
<%@include file="include/utility_nav.jsp"%>



   
  


	<div id="newMeetingAgendaX" style="display: none;">

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

</div>





<div id="theMeeting">
  <script type="text/jsx">


    var thisMeetingRefId;
    var thisMeetingPath;
    var thisMeetingImg="tata";
    var thisMeetingDate="a";
    var isActivNew=0;

    var MeetingList = React.createClass({
      getInitialState: function() {
        return { show: false };
      },
      toggle: function() {
        this.setState({ show: !this.state.show });
      },
      render: function() {
       var scheduleDates = this.props.schedule.dates;
       
       var commentNodes = this.props.data.map(function (comment ,i ) {
       
       if(comment.uid=='<%=mid%>') {
        
        if( scheduleDates !=null ) {
      		var scheduleDatesArray = scheduleDates.split(',');
      		thisMeetingDate =  scheduleDatesArray[comment.id] ;
    	  } 

      	thisMeetingRefId  = comment.refId;
      	thisMeetingPath  = comment.path;
      	thisMeetingImg   = "/content/dam/girlscouts-vtk/local/icon/meetings/"+ comment.meetingInfo.id +".png";
      	thisMeetingDate = new Date( Number(thisMeetingDate) );

          return (
            <YearPlan  item={comment} key={i} >
      			 <MeetingPlan meetingModMONTH={moment(thisMeetingDate).format('MMMM')} meetingModDAY={moment(thisMeetingDate).format('DD')} meetingModHOUR={moment(thisMeetingDate).format('h:mm a')} uid={comment.uid} meetingTitle={comment.meetingInfo.name} meetingId={comment.id} meetingGlobalId={thisMeetingImg} location={comment.locationRef} cat={comment.meetingInfo.cat} blurb={comment.meetingInfo.meetingInfo["meeting short description"].str} />
      			 <MeetingAssets data={comment.assets} />
      			 <SortableList1 data={comment.meetingInfo.activities}/>
            </YearPlan>
          );

       }//end if meetingId

      }); //end of loop

        return ( <div className="commentList">{commentNodes}</div>    );
      } //end of render
    });

    var ActivityName = React.createClass({
      onClick: function() {
        loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid%>&isAgenda='+(this.props.item.activityNumber-1), true, 'Agenda')       
		  },
      render: function() {
        return (
            <a href="javascript:void(0)" onClick={this.onClick} className={this.props.selected ? "selected" : ""}>
               {this.props.item.name}
            </a>
        );
      }
    });

    var MeetingAssets = React.createClass({
     getInitialState: function() {
        return { show: false };
      },
      toggle: function() {
        this.setState({ show: !this.state.show });
      },
      render: function() {
        var commentNodes = this.props.data.map(function (comment ,i ) {
          return (
     			 <MeetingAsset item={comment} key={i} refId={comment.refId} title={comment.title} description={comment.description}/>
          );
        });
        return (
          <section className="column large-20 medium-20 large-centered medium-centered">
     		    <h6>meeting aids</h6>
     			  <ul className="large-block-grid-2 medium-block-grid-2 small-block-grid-1">
              {commentNodes}
      		  </ul>
            <a className="add-btn" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);" title="Add meeting aids"><i className="icon-button-circle-plus"></i> Add Meeting Aids</a>
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
    		<div className="section-wrapper">
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
    var CommentBox = React.createClass({
     loadCommentsFromServer: function( isFirst ) {
        console.log("loading..");
       $.ajax({

          url: this.props.url + 
    		(isActivNew==1 ? ("&isActivNew="+ isActivNew) : '')+
    		(isFirst ==1 ? ("&isFirst="+ isFirst) : ''),
          dataType: 'json',
          cache: false,
          success: function(data) {
          	this.setState({data:data.yearPlan});
      	  }.bind(this),
          error: function(xhr, status, err) {
            //-console.error(this.props.url, status, err.toString());
          }.bind(this)
        });
    	if( isActivNew ==1 ){
    		isActivNew=2;
    	}else if( isActivNew ==2 ){
    		isActivNew=0;
    	}
     },
      getInitialState: function() {
        return {data: []};
      },
      componentDidMount: function() {
        this.loadCommentsFromServer(1);
        setInterval( this.loadCommentsFromServer, this.props.pollInterval);
        setInterval( this.checkLocalUpdate, 1000);
      },
      checkLocalUpdate: function(){
      	if( (isActivNew == 1) || (isActivNew == 2) )
    			{ this.loadCommentsFromServer() ; }
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
    		isActivNew=1;
    	},
        render: function () {
    		return <section className="column large-20 medium-20 large-centered medium-centered">
      					 <h6>meeting agenda</h6>
      					 <p>Select and agenda item to view details, edit duration and delete. Drag and drop to reorder.</p>
     						 <SortableListItems1  key="{this.props.data}"  data={this.props.data} onClick={this.alex} onReorder={this.onReorder}/>
    				    </section>; 
        }
    });




    var SortableListItems1 = React.createClass({
      render: function() {
        if( this.props.data!=null ){
  				return (
					  <%@include file="include/meeting_agenda.jsp"%>
					);
        }else{
          return <div><img src="http://sgsitsindore.in/Images/wait.gif"/></div>
        }
      },
      componentDidMount: function() {
        var dom = $(this.getDOMNode());
        var onReorder = this.props.onReorder;
        dom.sortable({
          stop: function (event, ui) {
            var order = dom.sortable("toArray", {attribute: "id"});
            var yy  = order.toString().replace('"','');
            repositionActivity1(thisMeetingRefId , yy);
            onReorder(order);
          }
        });
      },
      componentWillUpdate: function() {
        var dom = $(this.getDOMNode());
        var onReorder = this.props.onReorder;
        dom.sortable({
            stop: function (event, ui) {
            	var order = dom.sortable("toArray", {attribute: "id"});
            	var yy  = order.toString().replace('"','');
            	repositionActivity1(thisMeetingRefId , yy);
    			onReorder(order);
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
    	},
    	error: function (data) { 
    	}
    	});
    } 

    React.render(
    <CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={10000} />,
      document.getElementById('theMeeting')
    );

    </script>
    
    <%@include file="include/modal_agenda.jsp"%> 
   
   
   
   
   



   
   
   
   
   
</div>
</div>




