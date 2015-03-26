<!-- PAGE START activity_react2.jsp -->
<%
String aid = planView.getYearPlanComponent().getUid();
pageContext.setAttribute("DETAIL_TYPE", "activity");
%>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<%@include file="include/tab_navigation.jsp"%>
<div id="modal_popup" class="reveal-modal" data-reveal></div>

<div id="panelWrapper" class="row content meeting-detail">
  <%@include file="include/utility_nav.jsp"%>
  <%@include file="include/activity_edit_react.jsp"%>
  <%@include file="include/modals/modal_meeting_reminder.jsp" %>
  <%@include file="include/modals/modal_view_sent_emails.jsp"%>			
  <div id="theActivity">


    <script type="text/javascript">
      var isActivNew=0;
      var aPath;
      var meetingStartDate="";
      var meetingEndDate="";
  
      var CommentBox = React.createClass({
      loadCommentsFromServer: function( isFirst ) {
        //console.log("loading..");
        $.ajax({
            url: this.props.url + 
        		(isActivNew==1 ? ("&isActivNew="+ isActivNew) : '')+
        		(isFirst ==1 ? ("&isFirst="+ isFirst) : ''),
              dataType: 'json',
              cache: false,
              success: function(data) {
              	this.setState({data:data});
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
          //resizeWindow();
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
        	
        	if( this.state.data.uid!=null){
    			x= this.state.data;
    			aPath= x.path;
    			meetingStartDate=new Date(Number(x.date));
    			meetingEndDate=new Date(Number(x.endDate));
      
         getEventImg( x.refUid );

              return (
            		  React.createElement(Activity, {data: x, meetingTitle: x.name, meetingModMONTH: moment(meetingStartDate).format('MMMM'), meetingModDAY: moment(meetingStartDate).format('DD'), meetingModHOUR: moment(meetingStartDate).format('h:mm a')})
                      
            		  );
        	}else{
        		 return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"})) 
        	}
        }
      });
      React.render(
    	      React.createElement(CommentBox, {url: "/content/girlscouts-vtk/controllers/vtk.controller.html?reactActivity=<%=aid%>", pollInterval: 10000}),
    	        document.getElementById('theActivity')
    	      );
        var Activity = React.createClass({

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
            
           React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered clearfix", id: "main-info"}, 

              React.createElement("div", {id: "activ_img"}), 

             React.createElement("ul", {className: "small-block-grid-2"}, 
                React.createElement("li", null, React.createElement("p", null, "Location Name:"), React.createElement("p", null, this.props.data.locationName ? this.props.data.locationName.replace('&nbsp;','').replace(/(<([^>]+)>)/ig,"") : '')), 
                React.createElement("li", null, React.createElement("p", null, "Location Address:"), React.createElement("p", null, this.props.data.locationAddress ? this.props.data.locationAddress.replace('&nbsp;','').replace(/(<([^>]+)>)/ig,"") : '', " ", this.props.data.locationRef ? this.props.data.locationRef.replace('&nbsp;','').replace(/(<([^>]+)>)/ig,"") : '')), 
                React.createElement("li", null, React.createElement("p", null, "Age:"), React.createElement("p", null, "<%=troop.getSfTroopAge()%>")), 
                React.createElement("li", null, React.createElement("p", null, "Cost:"), React.createElement("p", null,  fmtMaskedMoney(this.props.data.cost)))
              ), 
              React.createElement("p", {dangerouslySetInnerHTML: {__html: this.props.data.content}})
            )
            


/*communication*/
        <% if (SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) {%>

,React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 

  React.createElement("h6", null, "manage communications"), 

  React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-2"}, 

  <% if( (planView.getYearPlanComponent().getType() ==  YearPlanComponentType.ACTIVITY) ){%>

    React.createElement("li", null, 

<%    if(hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ACT_ID )) {%>

    React.createElement("a", {href: "#", "data-reveal-id": "modal-meeting-reminder", title: "Activity Reminder Email"}, "Edit/Send Invitation/Reminder"), 

    <%}else {%>

    React.createElement("a", null, "Invitation/Reminder"), 

<%    } %>

    ), 

    React.createElement("li", null, 

<%if (((Activity)planView.getYearPlanComponent()).getSentEmails()!=null && !((Activity)planView.getYearPlanComponent()).getSentEmails().isEmpty()) {%>

     React.createElement("span", null,"   (<%=((Activity)planView.getYearPlanComponent()).getSentEmails().size() %>", " sent -"), 
     React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, "view"), ")" 

 <%} %>

  )
  <%}else{ %>
  
  /*start else*/
    
  
  
  
  
React.createElement("li", null, 
<%if(hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_MT_ID )) {%> 
           
            <%if(planView.getSearchDate()!=null && planView.getSearchDate().after( new java.util.Date("1/1/1977") )) {%> 
                 React.createElement("a", {href: "#", "data-reveal-id": "modal-meeting-reminder", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email") 
            <%} else{%>
                React.createElement("a", {href: "javascript:alert('You have not yet scheduled your meeting calendar.\\nPlease select a year plan and schedule your meetings by clicking on the MEETING DATES AND LOCATION link.')", title: "Meeting Reminder Email"}, "Edit/Sent Meeting Reminder Email")
            <%} %>
            
             
             <%} else{ %>  
            React.createElement("a", null, "Meeting Reminder email"), 
            <%}%>
        ), 
        React.createElement("li", null, 
        <%if (planView.getMeeting().getSentEmails()!=null && !planView.getMeeting().getSentEmails().isEmpty()) {%> 
            React.createElement("span",null, "(", "<%=planView.getMeeting().getSentEmails().size() %>", " sent -"),
            React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, " view", ")")
            <%}else{ %>"" <%} %>
        ), 
        
        <%if(hasPermission(troop, Permission.PERMISSION_VIEW_ATTENDANCE_ID )) {%> 
        React.createElement("li", null, 
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
        
        <%} %>
  
  
  
  /*end else*/
  <%}%>
)

)
<%} %>
/*end communication*/


)
         // </div>
          );
        }
      });


Number.prototype.format = function(n, x) {
    var re = '(\\d)(?=(\\d{' + (x || 3) + '})+' + (n > 0 ? '\\.' : '$') + ')';
    return this.toFixed(Math.max(0, ~~n)).replace(new RegExp(re, 'g'), '$1,');
};
 function fmtMaskedMoney(amount){
        return "$"+amount.format(2);
    }
    </script>
  </div>

</div>
<!-- PAGE STOP activity_react2.jsp -->
