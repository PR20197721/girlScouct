<!-- PAGE START activity_react2.jsp -->
<%

if( false ){
	out.println("ACTIVITY ");
	return;	
}

String aid = planView.getYearPlanComponent().getUid();
pageContext.setAttribute("DETAIL_TYPE", "activity");


%>

<!-- <script src="http://code.jquery.com/jquery-1.9.1.js"></script> -->
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<script src="http://fb.me/react-with-addons-0.12.1.js"></script>

<%@include file="include/tab_navigation.jsp"%>
<!--%@include file="include/myPop.jsp"%-->

<div id="panelWrapper" class="row content meeting-detail">
<%@include file="include/utility_nav.jsp"%>
<div id="theActivity">
<%@include file="include/activity_edit_react.jsp"%>

  <script type="text/jsx">
   var isActivNew=0;
	var aPath;
    var meetingStartDate="";
    var meetingEndDate="";

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
          return (
                         <Activity data={x} meetingTitle={x.name} meetingModMONTH={moment(meetingStartDate).format('MMMM')} meetingModDAY={moment(meetingStartDate).format('DD')} meetingModHOUR={moment(meetingStartDate).format('h:mm a')}/>
    	    );
    	}else{
    		return <div>loading...</div>;
    	}
      }
    });


    React.render(
    <CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactActivity=<%=aid%>" pollInterval={10000} />,
      document.getElementById('theActivity')
    );



	var Activity = React.createClass({
      render: function() {
        return (
                <div className="section-wrapper">
<%@include file="include/meeting_navigator.jsp"%>
<section className="column large-20 medium-20 large-centered medium-centered" id="main-info">
	<div className="row">
		<div className="column large-17 medium-17 small-17">

 <p>{this.props.data.content}</p>

      <section>
        <p>Location:</p>
        <p>{this.props.data.locationName} <br/>{this.props.data.locationAddress} {this.props.data.locationRef}</p>
      </section>

     

	  <section>
        <p>Age:</p>
        <p><%=troop.getSfTroopAge()%></p>
      </section>

	
	  <section>
        <p>Cost:</p>
        <p>{this.props.data.cost}</p>
      </section>


			
		</div>
		<div className="column large-7 medium-7 small-7"></div>
	</div>
</section>
</div>
        );
      }
    });

    </script>
</div>
<br/><a href="javascript:rmCustActivity12(aPath)">delete this activity</a>
</div>


			        

<!-- PAGE STOP activity_react2.jsp -->
