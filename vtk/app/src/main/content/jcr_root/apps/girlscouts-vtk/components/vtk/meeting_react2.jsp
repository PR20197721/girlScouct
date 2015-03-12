<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/meeting_react2.jsp -->
<% 

String mid = planView.getYearPlanComponent().getUid();
MeetingE meeting = planView.getMeeting();


Attendance attendance = meetingUtil.getAttendance( user,  troop,  meeting.getPath()+"/attendance");
int attendanceCurrent=0, attendanceTotal=0;

if( attendance !=null && attendance.getUsers()!=null ){
	attendanceCurrent = new StringTokenizer( attendance.getUsers(), ",").countTokens();
	attendanceTotal= attendance.getTotal();
}

Achievement achievement = meetingUtil.getAchievement( user,  troop,  meeting.getPath()+"/achievement");
int achievementCurrent=0;//, achievementTotal=0;

if( achievement !=null && achievement.getUsers()!=null ){
	achievementCurrent = new StringTokenizer( achievement.getUsers(), ",").countTokens();
	//achievementTotal= achievement.getTotal();
}


Location loc = null;
if( meeting.getLocationRef()!=null && troop.getYearPlan().getLocations()!=null ) {
	for(int k=0;k<troop.getYearPlan().getLocations().size();k++){
		if( troop.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
			loc = troop.getYearPlan().getLocations().get(k);
		}
	}
}

//pageContext.setAttribute("MEETING_achievement_TOTAL", achievementTotal);
pageContext.setAttribute("MEETING_achievement_CURRENT", achievementCurrent);
pageContext.setAttribute("MEETING_ATTENDANCE_TOTAL", attendanceTotal);
pageContext.setAttribute("MEETING_ATTENDANCE_CURRENT", attendanceCurrent);

pageContext.setAttribute("MEETING_PATH", meeting.getPath());
pageContext.setAttribute("PLANVIEW_TIME", Long.valueOf(planView.getSearchDate().getTime()));
pageContext.setAttribute("DETAIL_TYPE", "meeting");

%>
 <script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script> 

<%@include file="include/tab_navigation.jsp"%>

<script>
    var thisMeetingPath = "";
</script>

<div id="panelWrapper" class="row content meeting-detail">
<%@include file="include/utility_nav.jsp"%>
<%@include file="include/modals/modal_meeting_aids.jsp"%>
<%@include file="include/modals/modal_agenda.jsp"%>
<%@include file="include/modals/modal_meeting_reminder.jsp" %>
<%@include file="include/modals/modal_view_sent_emails.jsp"%>

  <div id="theMeeting">
    <script type="text/jsx">
      var thisMeetingRefId;
      var thisMeetingImg="default";
      var thisMeetingDate="a";
      var isActivNew=0;
      var agendaSched= null;

      var MeetingList = React.createClass({
        getInitialState: function() {
          return { show: false };
        },
        toggle: function() {
          this.setState({ show: !this.state.show });
        },
        render: function() {
         var scheduleDates =null;
      		if( this.props.schedule!=null){
      			scheduleDates= this.props.schedule.dates;
      		}
         
         var commentNodes = this.props.data.map(function (comment ,i ) {
         
         if(comment.uid=='<%=mid%>') {
          
          if( scheduleDates !=null ) {
        		
  			var scheduleDatesArray = scheduleDates.split(',');
        		thisMeetingDate =  scheduleDatesArray[comment.id];	
		
      	  } else{ 
  			   thisMeetingDate=new Date('<%=planView.getSearchDate()%>');

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

      var ActivityNameAlex = React.createClass({
        onClick: function() {
         loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid%>&isAgenda='+(this.props.item.activityNumber-1), true, 'Agenda')       
  		  },
        render: function() {
          return (
              
              <a href="javascript:void(0)" onClick={this.onClick} className={this.props.selected ? "selected" : ""} mid='<%=mid%>' isAgenda={(this.props.item.activityNumber-1)}>
                 {this.props.item.name}
              </a>
          );
        }
      });


      var ActivityName = React.createClass({     
        render: function() {
          return (
              <a data-reveal-id="modal_popup" data-reveal-ajax="true" href={"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isAgenda="+(this.props.item.activityNumber-1)}>{this.props.item.name}</a> 
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
            var thisAssetExtension = "pdf";
            var thisAssetExtensionPattern=/.*\.(.+)$/;
            if (comment.refId.indexOf(".") != -1) {
              thisAssetExtension = comment.refId.replace(thisAssetExtensionPattern, "$1");
            }
            return (
       			 <MeetingAsset item={comment} key={i} refId={comment.refId} title={comment.title} description={comment.description} extension={thisAssetExtension}/>
            );
          });
          return (
            <section className="column large-20 medium-20 large-centered medium-centered">
       		    <h6>meeting aids</h6>
       			  <ul className="large-block-grid-2 medium-block-grid-2 small-block-grid-1">
                {commentNodes}
        		  </ul>
              <%if( hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ) {%>
              <a className="add-btn" data-reveal-id="modal_meeting_aids" href="#" title="Add meeting aids"><i className="icon-button-circle-plus"></i> Add Meeting Aids</a>
              <%}%>
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
      		if( isActivNew ==1 ){
          		isActivNew=2;
          	}else if( isActivNew ==2 ){
          		isActivNew=0;
          	}
        	  }.bind(this),
            error: function(xhr, status, err) {
              //-console.error(this.props.url, status, err.toString());
            }.bind(this)
          });

  		/*
      	if( isActivNew ==1 ){
      		isActivNew=2;
      	}else if( isActivNew ==2 ){
      		isActivNew=0;
      	}
  		*/

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
                  <p>Select and agenda item to view details, edit duration or delete. Drag and drop to reorder.</p>
       						 <SortableListItems1  key="{this.props.data}"  data={this.props.data} onClick={this.alex} onReorder={this.onReorder}/>
                  <AgendaTotal data={this.props.data}/>   				
                  <strong><a data-reveal-id="modal_agenda" className="add-btn"><i className="icon-button-circle-plus"></i> Add Agenda Item</a></strong>
						     </section>; 
          }
      });

      var SortableListItems1 = React.createClass({
        render: function() {
          if( this.props.data!=null ){
            agendaSched=null;
    				return (
  					  <%@include file="include/meeting_agenda.jsp"%>
  					);
          }else{
            return <div><img src="/etc/designs/girlscouts-vtk/images/loading.gif"/></div> 
          }
        },
        componentDidMount: function() {
          resizeWindow();
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

      function repositionActivity1(meetingPath,newVals){
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
      <%
      	String elem = request.getParameter("elem");
      	if (elem != null) {
      		elem = "&elem=" + elem;
      	} else {
      		elem = "";
      	}
      %>
      <CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf<%= elem%>" pollInterval={10000} />,
        document.getElementById('theMeeting')
      );


     var AgendaTotal = React.createClass({
           
            render: function() {
              return (
                  <p className="row">
                    <strong className="column small-2 small-push-21">{getAgendaTotalTime(this.props.data)}</strong>
                  </p>
                  
              );
            }
          });



    function getAgendaTotalTime(x){

    if( x==null ) return "";
      var total =0;
      x.map((function(item, i) {
            total += item.duration;
      }))


        var hours = Math.floor( total / 60);          
        var minutes = total % 60;

        if( hours<=0 )
          return minutes;
       else
        return hours+":"+ minutes;

    }


    function getAgendaTime( duration ){
     if( agendaSched==null ){
        agendaSched= thisMeetingDate.getTime();
     }

    var curr= agendaSched;
      agendaSched  = addMinutes( agendaSched, duration);

      return curr;
    }

    function addMinutes(date, minutes) {
        return new Date(date + minutes*60000).getTime();
    }

   
      </script>
  </div>
</div>
<div id="modal_popup" class="reveal-modal" data-reveal></div>
