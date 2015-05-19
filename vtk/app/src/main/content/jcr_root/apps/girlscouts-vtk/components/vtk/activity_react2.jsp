<!-- PAGE START activity_react2.jsp -->
<%
String aid = planView.getYearPlanComponent().getUid();
pageContext.setAttribute("DETAIL_TYPE", "activity");
%>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>



  <div id="vtkTabNav"></div>
  
<div id="panelWrapper" class="row content meeting-detail">
  <div id="vtkNav"></div>
  <div id="modal_popup" class="reveal-modal" data-reveal></div>
  <%@include file="include/modals/modal_meeting_reminder.jsp" %>     
  <%@include file="include/modals/modal_view_sent_emails.jsp"%>			
  <div id="theActivity">


    <script type="text/javascript">
    
  
function loadNav(){
	loadTabNav();
	loadUNav();
}


function loadUNav(){
    
    $.ajax({
        url: "/content/girlscouts-vtk/controllers/vtk.include.utility_nav.html?activeTab=planView",
        cache: false
    }).done(function( html ) {
        var vtkNav = document.getElementById("vtkNav");
        vtkNav.innerHTML =html;
    })
}



function loadTabNav(){
        
        $.ajax({
            url: "/content/girlscouts-vtk/controllers/vtk.include.tab_navigation.html?activeTab=planView",
            cache: false
        }).done(function( html ) {
            var vtkNav = document.getElementById("vtkTabNav");
            vtkNav.innerHTML =html;
        })
    }
    

    
    
    
      var isActivNew=0;
      var aPath;
      var meetingStartDate="";
      var meetingEndDate="";
      var activityHelper=null;
      var sentEmails=0;
      
      
      
      var ActivityCommunication = React.createClass({displayName: "Activity communication",
          render: function() {
              <% if (SHOW_BETA || sessionFeatures.contains(SHOW_BETA_FEATURE)) {%>
       return ( 		   
    		   React.createElement("section", {className: "column large-20 medium-20 large-centered medium-centered"}, 

		        React.createElement("h6", null, "manage communications"), 
		
		          React.createElement("ul", {className: "large-block-grid-2 medium-block-grid-2 small-block-grid-2"}, 


                     React.createElement("li", null, 
                    		 React.createElement( Emails)
                   )
                )
                 )
                 )
        <%}else{%>React.createElement("")<%} %>
      
        	  
        	  
        	  
        	  
        	  
          }
          });
         
       
          
          var Emails = React.createClass({displayName: "Emails Activity",
              render: function() {
            	  if( activityHelper.permissions!=null && activityHelper.permissions.indexOf('<%= Permission.PERMISSION_SEND_EMAIL_ACT_ID%>')!=-1){
                      return (
                              React.createElement("a", {href: "#", "data-reveal-id": "modal-meeting-reminder", title: "Activity Reminder Email"}, "Edit/Send Invitation/Reminder"
                              ,  React.createElement(ViewEmailWithPermis)
                              )
                              )
                  }else{
                     return( React.createElement("a", {href: "#", title: "view sent emails", "data-reveal-id": "modal_view_sent_emails"}, "Invitation/Reminder"
                      ,React.createElement(ViewEmailNoPermis)
                      ))
                  }
              }
            });  
          
          
          
          
          
          
          
          var ViewEmailWithPermis = React.createClass({displayName: "ViewEmailWithPermis",
              render: function() {
            	  if( sentEmails>0){
                      
                       return (
                    		   React.createElement("li", null, 
                    				   React.createElement("span", null, "(", sentEmails, " sent -"), 
                    						   React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, "view"), ")" 
                              )
                      )
                  }else{
                	  return React.createElement("li")
                  }
              }
            });  
          
          
          
          
          
          var ViewEmailNoPermis = React.createClass({displayName: "ViewEmailNoPermis",
              render: function() {
            	  if( sendEmails>0){
                      return (
                    		  React.createElement("li", null,
                              React.createElement("span", null,   "(", sendEmails, " sent -"), 
                                         React.createElement("a", {href: "#", title: "view sent emails", className: "view", "data-reveal-id": "modal_view_sent_emails"}, "view"), ")" 
                      ))
                  }else{
                      return (React.createElement("li", null,
                              React.createElement("span", null, "(", "0 sent ", ")") 
                       ))
                  }
              }
            });  
          
          
      var NavDirectionNext = React.createClass({displayName: "NavDirectionNext",
          render: function() {
              if(activityHelper.nextDate !=0 && activityHelper.nextDate!=null ){
                  return (  
                         React.createElement("a", { className: "direction next", href: "/content/girlscouts-vtk/en/vtk.details.html?elem="+activityHelper.nextDate})
                  
                        );
                }else{
                    return React.createElement("span", null, "");
                }
          }
        });
      
      var ActivityDate = React.createClass({displayName: "Prev Date",
          render: function() {
             

    	      if(activityHelper.currentDate !=null && (activityHelper.currentDate > new Date("1/1/1977")) ){ 
    	    	  return React.createElement("p", {className: "date"},
    	    			  React.createElement("span", {className: "month"}, this.props.meetingModMONTH), 

    	            React.createElement("span", {className: "day"}, this.props.meetingModDAY), 

    	            React.createElement("span", {className: "hour"}, this.props.meetingModHOUR)
    	            )
    	     }else{
    	    	 return React.createElement("p")
    	     }
            
    	      
          }
      });  
      
      function getParameterByName(name) {
          name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
          var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
              results = regex.exec(location.search);
          return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
      }

    function getElem(){
        var elem = getParameterByName('elem');
        if( elem!=null && elem!='')
            return "&elem="+elem;
        
        return "";
    }
    
    var NavDirectionPrev = React.createClass({displayName: "Prev Date",
        render: function() {
            if(activityHelper.prevDate!=0 && activityHelper.prevDate!=null ){
              return (
            		React.createElement("span", null,
                      React.createElement("a", {className: "direction prev", href: "/content/girlscouts-vtk/en/vtk.details.html?elem="+activityHelper.prevDate})
                    ) 
                 )
            }else{
                return React.createElement("span");
            }
         
        }
      });     
      
      
      var CommentBox = React.createClass({
      loadCommentsFromServer: function( isFirst ) {
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
          resizeWindow();
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
       	
        	if( this.state.data.activities!=null){
	
    			x= this.state.data.activities[0];
    			aPath= x.path;
    			meetingStartDate=new Date(Number(x.date));
    			meetingEndDate=new Date(Number(x.endDate));
    		    activityHelper= this.state.data.helper;
                getEventImg( x.refUid );
         
                sendEmails = x.sentEmails;
              return (
            		  React.createElement(Activity, {data: x, meetingTitle: x.name, meetingModMONTH: moment(meetingStartDate).format('MMMM'), meetingModDAY: moment(meetingStartDate).format('DD'), meetingModHOUR: moment(meetingStartDate).format('h:mm a')})
                      
            		  );
        	}else{
        		 return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"})) 
        	}
        }
      });
      React.render(
    	      React.createElement(CommentBox, {url: "/content/girlscouts-vtk/controllers/vtk.controller.html?reactActivity=x"+getElem(), pollInterval: 10000}),
    	        document.getElementById('theActivity')
    	      );
        var Activity = React.createClass({

        render: function() {
        return (
          React.createElement("div",{className: "section-wrapper"},
                /*nav include*/
                React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered small-24"}, 

  React.createElement("div", {className: "meeting-navigation activity-navigation row collapse"}, 

    React.createElement("p", {className: "column"}, 

   React.createElement(NavDirectionPrev)

    ), 

    React.createElement("div", {className: "column"}, 

      React.createElement("h3", null, "ACTIVITY : ",this.props.id, this.props.meetingTitle), 

     React.createElement(ActivityDate, {meetingModMONTH:this.props.meetingModMONTH, meetingModDAY:this.props.meetingModDAY, meetingModHOUR:this.props.meetingModHOUR})

    ), 

    React.createElement("p", {className: "column"}, 

      React.createElement("span", null, 

    		  React.createElement(NavDirectionNext)

    

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
                React.createElement("li", null, React.createElement("p", null, "Age:"), React.createElement("p", null, activityHelper.getSfTroopAge)), 
                React.createElement("li", null, React.createElement("p", null, "Cost:"), React.createElement("p", null,  fmtMaskedMoney(this.props.data.cost)))
              ), 
              React.createElement("p", {dangerouslySetInnerHTML: {__html: this.props.data.content}})
            )
            


,React.createElement(ActivityCommunication)


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
 
 loadNav();
    </script>
  </div>

</div>
<div id="modal_popup_activity" class="reveal-modal" data-reveal></div>
<!-- PAGE STOP activity_react2.jsp -->
