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
              "<%if( hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ) {%>", 
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
            React.createElement("div", {className: "section-wrapper"}, 
             "<%@include file=\"include/meeting_navigator.jsp\"%>", 
              "<%@include file=\"include/meeting_maininfo.jsp\"%>", 
              "<%@include file=\"include/meeting_planning.jsp\"%>", 
             " <%@include file=\"include/meeting_communication.jsp\"%>"
            )
          );
        }
      });

      var MeetingAsset = React.createClass({displayName: "MeetingAsset",
        render: function() {
          return (
            "<%@include file=\"include/meeting_aids.jsp\"%>"
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
              //-console.error(this.props.url, status, err.toString());
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
            if( this.state.data.meetingEvents!=null){
                x =  this.state.data.meetingEvents;
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
                      "<%@include file=\"include/meeting_agenda.jsp\"%>"
                    );
          }else{
            return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"})) 
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

   
