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
    <script type="text/javascript">
    var thisMeetingRefId;
    var thisMeetingImg="default";
    var thisMeetingDate="a";
    var isActivNew=0;
    var agendaSched= null;
    var scrollTarget = "";
    var MeetingList = React.createClass({displayName: "MeetingList",
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
          if( isNaN(thisMeetingDate) ){
        	  thisMeetingDate = new Date(<%=planView.getSearchDate().getTime()%>);
        	  
          }
     return (
            React.createElement(YearPlan, {item: comment, key: i}, 
                   React.createElement(MeetingPlan, {meetingModMONTH: moment(thisMeetingDate).format('MMMM'), meetingModDAY: moment(thisMeetingDate).format('DD'), meetingModHOUR: moment(thisMeetingDate).format('h:mm a'), uid: comment.uid, meetingTitle: comment.meetingInfo.name, meetingId: comment.id, meetingGlobalId: thisMeetingImg, location: comment.locationRef, cat: comment.meetingInfo.cat, blurb: comment.meetingInfo.meetingInfo["meeting short description"].str}), 
                   React.createElement(MeetingAssets, {data: comment.assets}), 
                   React.createElement(SortableList1, {data: comment.meetingInfo.activities})
            )
          );

       }//end if meetingId

      }); //end of loop
        return ( React.createElement("div", {className: "commentList"}, commentNodes)    );
      } //end of render
    });

    var ActivityNameAlex = React.createClass({displayName: "ActivityNameAlex",
      onClick: function() {
       loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=mid%>&isAgenda='+(this.props.item.activityNumber-1), true, 'Agenda')       
        },
      render: function() {
        return (
            
            React.createElement("a", {href: "javascript:void(0)", onClick: this.onClick, className: this.props.selected ? "selected" : "", mid: "<%=mid%>", isAgenda: (this.props.item.activityNumber-1)}, 
               this.props.item.name
            )
        );
      }
    });


    var ActivityName = React.createClass({displayName: "ActivityName",     
      render: function() {
        return (
            React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isAgenda="+(this.props.item.activityNumber-1)}, this.props.item.name) 
        );
      }
    });



    var MeetingAssets = React.createClass({displayName: "MeetingAssets",
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
                  React.createElement(MeetingAsset, {item: comment, key: i, refId: comment.refId, title: comment.title, description: comment.description, extension: thisAssetExtension})
             );
           });
           return (
             React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 
                 React.createElement("h6", null, "meeting aids"), 
                   React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-1"}, 
                 commentNodes
                   ), 
              " <%if( hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ) {%>", 
               React.createElement("a", {className: "add-btn", "data-reveal-id": "modal_meeting_aids", href: "#", title: "Add meeting aids"}, React.createElement("i", {className: "icon-button-circle-plus"}), " Add Meeting Aids"), 
               "<%}%>"
               ) 
           );
         }
       });

    var YearPlan = React.createClass({displayName: "YearPlan",
      render: function() {
        return (
          React.createElement("div", {className: "comment"}, 
            React.createElement("h2", {className: "commentAuthor"}, 
              this.props.author
            ), 
            this.props.children
          )
        );
      }
    });

    var MeetingPlan = React.createClass({displayName: "MeetingPlan",
    	
      render: function() {
    	  
        return (
        		React.createElement("div",{className: "section-wrapper"},
        		/*nav include*/
        		React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered small-24"}, 

            React.createElement("div", {className: "meeting-navigation <%= (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ? " activity-navigation " : "" %> row collapse"}, 

            React.createElement("p", {className: "column"}, 

            React.createElement("span", null, 

           <%if(planView.getPrevDate()!=0){ %>

        	    React.createElement("a", {className: "direction prev", href: "/content/girlscouts-vtk/en/vtk.details.html?elem=<%=planView.getPrevDate()%>"})
           <%}else{%>""<%}%>   
      )

    ), 

            React.createElement("div", {className: "column"}, 

            React.createElement("h3", null, "<%=planView.getYearPlanComponent().getType()%>", " : ",this.props.id, this.props.meetingTitle), 

            React.createElement("p", {className: "date"}, 

            <%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )){ %> 

            React.createElement("span", {className: "month"}, this.props.meetingModMONTH), 

              React.createElement("span", {className: "day"}, this.props.meetingModDAY), 

              React.createElement("span", {className: "hour"}, this.props.meetingModHOUR)
            <%}else{%>""<%}%>

            
      

      )

    ), 

          React.createElement("p", {className: "column"}, 

          React.createElement("span", null, 

          <% if(planView.getNextDate()!=0 ){  %>

            React.createElement("a", {className: "direction next", href: "/content/girlscouts-vtk/en/vtk.details.html?elem=<%=planView.getNextDate()%>"})

          <%}else{%>""<%}%>

    

      )

    )

  )

),
        		/*end nav include*/
        		
        		
        		/**main info */
        		
        		React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered", id: "main-info"}, 
            React.createElement("div", {className: "row"}, 
            React.createElement("div", {className: "column large-17 medium-17 small-17"}, 
            React.createElement("p", null, this.props.blurb), 
            React.createElement("section", null, 
        
            React.createElement("p", null, 
            <%if(loc!=null){ %> 
                
            React.createElement("span", null, "Location: <%=loc.getName()%>  -"),
            React.createElement("a", {href: "/content/girlscouts-vtk/controllers/vtk.map.html?address=<%=loc.getAddress()%>", target: "_blank"}, "<%=loc.getAddress()%> ")
                
            <%}else{%>
                ""
            <%}%>
        )
      ), 
      React.createElement("section", null, 
        // React.createElement("p", null, "Category:"), 
        React.createElement("p", null, "Category: " + this.props.cat)
      )
    ), 
    React.createElement("div", {className: "column large-7 medium-7 small-7 text-right"}, 
      React.createElement("img", {src: this.props.meetingGlobalId, alt: "badge"})
    )
  )
),

        		
        		
        		
        		
        		/*end main info*/
        		
        		
        		
        		/*planning*/
        		React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 
  React.createElement("h6", null, "planning materials"), 
  React.createElement("ul", null, 
   React.createElement("li", null, 
    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isOverview=true"}, "Meeting Overview")
  ), 
<%if(hasPermission(troop, Permission.PERMISSION_VIEW_ACTIVITY_PLAN_ID)) {%> 
   React.createElement("li", null, 
    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isActivity=true"}, "Activity Plan")
   ), 
<%} %> 
   React.createElement("li", null, 
    React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_agenda_edit.html?mid=<%=mid%>&isMaterials=true"}, "Materials List")
  )
  )
)
        		/* end planning*/
        		
        		
        		
        		/*communication*/
        <% if (SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) {%>

,React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 

  React.createElement("h6", null, "manage communications"), 

  React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-2"},   
  
React.createElement("li", null, 
<%if(hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID )) {%> 
           
	<%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )) {%> 
	React.createElement("a", {href: "#", "data-reveal-id": "modal-meeting-reminder", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email") 
	<%} else{%>
            React.createElement("a", {href: "javascript:alert('You have not yet scheduled your meeting calendar.\\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email")
            <%} %>
			), 
			React.createElement("li", null, 
			<%if (planView.getMeeting().getSentEmails()!=null && !planView.getMeeting().getSentEmails().isEmpty()) {%> 
    		React.createElement("span",null, "(", "<%=planView.getMeeting().getSentEmails().size() %>", " sent -"),
    		React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, " view"), ")"
    		<%}else{ %>"" <%} %>
			) 
            
<%} else{ %>  
  React.createElement("a", {href: "#",title: "view sent emails","data-reveal-id": "modal_view_sent_emails"}, "Meeting Reminder email")
  ), 
  	React.createElement("li", null, 
  <%if (planView.getMeeting().getSentEmails()!=null && !planView.getMeeting().getSentEmails().isEmpty()) {%> 
      React.createElement("span",null, "(", "<%=planView.getMeeting().getSentEmails().size() %>", " sent -"),
      React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, " view"), ")"
  <%}else{ %>      
      React.createElement("span",null, "(", "0 sent",")")
  <%} %>
  ) 
<%}%>
        
        
        <%if(hasPermission(troop, Permission.PERMISSION_VIEW_ATTENDANCE_ID )) {%> 
        ,React.createElement("li", null, 
            React.createElement("a", {"data-reveal-id": "modal_popup", "data-reveal-ajax": "true", href: "/content/girlscouts-vtk/controllers/vtk.include.modals.modal_attendance.html?mid=<%=planView.getYearPlanComponent().getUid() %>&isAch=<%=(planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING) ? ((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getIsAchievement() : "false" %>&mName=<%= (planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING) ? ((MeetingE)planView.getYearPlanComponent()).getMeetingInfo().getName() : ((Activity)planView.getYearPlanComponent()).getName()%>"}, "Record Attendance & Achievements")
        ), 
        React.createElement("li", null, "(" 
            <%if( pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") ==null || pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL").equals("")){ %> 
                   , "none present, no achievements" 
            <%}else{ %> 
		            <% if(pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT") ==null || ((Integer)pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT")) ==0 ){ %>
		              ,"none present,"  
		            <%}else{%>
		                ,"<%= pageContext.getAttribute("MEETING_ATTENDANCE_CURRENT") %> of <%= pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") %> present,"
		             <%}%> 
		            
		             
		            <% if( pageContext.getAttribute("MEETING_achievement_CURRENT") ==null ||  ((Integer)pageContext.getAttribute("MEETING_achievement_CURRENT")) ==0){ %>
		              ,"no achievements" 
		            <% }else{%> 
		             ,"<%= pageContext.getAttribute("MEETING_achievement_CURRENT") %> of <%= pageContext.getAttribute("MEETING_ATTENDANCE_TOTAL") %> achievement(s)"
		            <%} %>
             ,")"
        )
        <%} %>
        
        <%}%>
  
  
	)

)
<%} %>
/*end communication*/
      )
         )
      }
    });

    var MeetingAsset = React.createClass({displayName: "MeetingAsset",
      render: function() {
        return (
        		React.createElement("li", null, 
        			    React.createElement("a", {href: this.props.refId, target: "_blank", title: "View Meeting Aids", className:  "icon "+ this.props.extension}, this.props.title), 
        			    React.createElement("p", {className: "info"}, this.props.description)
        			  )
        );
      }
    });
    var CommentBox = React.createClass({displayName: "CommentBox",
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
            
          }.bind(this)
        });
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
          if( <%=planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETING%> && this.state.data.meetingEvents!=null){
              x =  this.state.data.meetingEvents;
              sched = this.state.data.schedule;
              return (
                   React.createElement(MeetingList, {data: x, schedule: sched}) 
              );
          }else if( <%=planView.getYearPlanComponent().getType()== YearPlanComponentType.MEETINGCANCELED%> &&  this.state.data.meetingCanceled!=null){
                  x =  this.state.data.meetingCanceled;
                  sched = this.state.data.schedule;
                  return (
                       React.createElement(MeetingList, {data: x, schedule: sched}) 
                  );
          }else{
              return React.createElement("div", null, "loading...");
          }
      }
    });
    
    
     var SortableList1 = React.createClass({displayName: "SortableList1",
      getInitialState: function() {
          return {data: this.props.data};
        },
      onReorder: function (order) {
          isActivNew=1;

      },
        render: function () {
          return React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 
                           React.createElement("h6", null, "meeting agenda"), 
                React.createElement("p", null, "Select and agenda item to view details, edit duration or delete. Drag and drop to reorder."), 
                           React.createElement(SortableListItems1, {key: "{this.props.data}", data: this.props.data, onClick: this.alex, onReorder: this.onReorder}), 
                React.createElement(AgendaTotal, {data: this.props.data}),                
                React.createElement("strong", null, React.createElement("a", {"data-reveal-id": "modal_agenda", className: "add-btn"}, React.createElement("i", {className: "icon-button-circle-plus"}), " Add Agenda Item"))
                           ); 
        }
    });

    var SortableListItems1 = React.createClass({displayName: "SortableListItems1",
      render: function() {
        if( this.props.data!=null ){
          agendaSched=null;
          return (
                		  

    		   React.createElement("ul", null, 
    		      this.props.data.map((function(item, i) {
    		      return React.createElement("li", {className: "row ui-state-default", key: item.activityNumber, id: item.activityNumber}, 
    		        React.createElement("div", {className: "wrapper clearfix"},



                React.createElement("img", {className: (moment('<%=planView.getSearchDate()%>') < moment( new Date()) && (moment('<%=planView.getSearchDate()%>').get('year') >2000)) ? "touchscroll hide" : "touchscroll <%=hasPermission(troop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>", src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"}),


    		          React.createElement("div", {className: "large-3 medium-3 small-3 columns small-push-1 large-push-2"}, 
    		            React.createElement("span", null,   moment('<%=planView.getSearchDate()%>').format('YYYY') <1978 ? item.activityNumber : moment( getAgendaTime( item.duration )).format("h:mm"), " ")
    		          ), 
    		            React.createElement("div", {className: "large-17 columns medium-17 small-17 small-push-1 large-push-1"}, 
    		              React.createElement(ActivityName, {item: item, key: item.uid, selected: item.uid, itemSelected: this.setSelectedItem, activityNumber: item.activityNumber - 1})
    		            ), 
    		            React.createElement("div", {className: "large-3 medium-3 small-3 columns"}, 
    		              React.createElement("span", null, ":", item.duration<10 ? ("0"+item.duration) : item.duration)
    		            )
    		          )
    		        );
    		                })) 
    		            
    		            
    		  )
    		  

      );
        }else{
          return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"})) 
        }
      },
      componentDidMount: function() {
       try{ resizeWindow(); }catch(err){}
        if (Modernizr.touch) {

            scrollTarget = ".touchscroll";
          } 
        
        var dom = $(this.getDOMNode());
        var onReorder = this.props.onReorder;
        dom.sortable({
        	items: "li:not(.ui-state-disabled)",
            delay:150,
            distance: 5,
            opacity: 0.5 ,
            scroll: true,
            scrollSensitivity: 10 ,
            tolerance: "intersect" ,
            handle: scrollTarget,
            helper:'clone',
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
        if (Modernizr.touch) {
            scrollTarget = ".touchscroll";
          }

        var onReorder = this.props.onReorder;
        dom.sortable({
        	items: "li:not(.ui-state-disabled)",
            delay:150,
            distance: 5,
            opacity: 0.5 ,
            scroll: true,
            scrollSensitivity: 10 ,
            tolerance: "intersect" ,
            handle: scrollTarget,
            helper:'clone',
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
    	//location.reload();
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
    React.createElement(CommentBox, {url: "/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf<%= elem%>", pollInterval: 10000}),
      document.getElementById('theMeeting')
    );


   var AgendaTotal = React.createClass({displayName: "AgendaTotal",
         
          render: function() {
            return (
                React.createElement("p", {className: "row"}, 
                  React.createElement("strong", {className: "column small-2 small-push-21"}, getAgendaTotalTime(this.props.data))
                )
                
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
