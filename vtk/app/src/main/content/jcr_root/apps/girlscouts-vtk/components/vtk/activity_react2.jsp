<%

if( false ){
	out.println("ACTIVITY ");
	return;	
}

String aid = planView.getYearPlanComponent().getUid();


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
<!--%@include file="include/myPop.jsp"%-->

<div id="panelWrapper" class="row content meeting-detail">
<%@include file="include/utility_nav.jsp"%>
<div id="theActivity">
  <script type="text/jsx">
   var isActivNew=0;
	var aPath;

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
    		return (
     			 <Activity data={x} /> 
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
               <div>
        				<%@include file="include/meeting_navigator.jsp"%>
        				Name:{this.props.data.name}
        				<br/>Date: {this.props.data.date} -{this.props.data.endDate}
        				<br/>Time:
        				<br/>Age range: 
        				<br/>Location:{this.props.data.locationName} --- {this.props.data.locationAddress} -- {this.props.data.locationRef}
        				<br/>Cost: {this.props.data.cost}
        				<br/>Desc: {this.props.data.activityDescription}
        				<br/>Path: {this.props.data.path}
        				<br/>Uid: {this.props.data.uid}
        				<br/>Register Url{this.props.data.registerUrl}
        				<br/>Canceled:{this.props.data.cancelled}
        				<br/>IsEditable: {this.props.data.isEditable}
        				<br/><a href="javascript:rmCustActivity12(aPath)">delete this activity</a>
			         </div>
        );
      }
    });

    </script>

</div>
</div>




