
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.troop.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />

<%@include file="../include/session.jsp"%>
<%

	org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView1(  user,  troop,  request);
	String mid=planView.getYearPlanComponent().getUid();
	
%>

<html>
 



  <!-- script src="http://fb.me/react-0.12.1.js"></script -->
   <!--script src="http://fb.me/JSXTransformer-0.12.1.js"></script -->
   <!--script src="http://cdnjs.cloudflare.com/ajax/libs/react/0.12.1/react-with-addons.js"></script -->
   <!--script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script -->
  
       <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
    <script src="http://fb.me/react-0.12.1.js"></script>
   <script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
     <script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
  
    
  <body>
  
  <style>
  	  .meetingHeader{border:5px solid green; background-color:yellow;}
  	
	  li.placeholder { background: rgb(255,240,120);}
	  li.placeholder:before {content: "Drop here"; color: rgb(225,210,90);}
	  ul { list-style: none; margin:0; padding:0}
	  li {padding: 5px; background:#eee}
	  
  
  </style>
 <div id="testDiv"></div>
 <hr/>
 
 
    <div id="content"></div>
    <script type="text/jsx">

var thisMeetingRefId;
var thisMeetingPath;

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
    var commentNodes = this.props.data.map(function (comment ,i ) {


if(comment.uid=='<%=mid%>'){
	thisMeetingRefId	= comment.refId;
	thisMeetingPath = comment.path;
      return (

        <YearPlan  item={comment} key={i} >
			
			<MeetingPlan uid={comment.uid} meetingTitle={comment.meetingInfo.name}
				meetingId={comment.id}
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





var MeetingActivities = React.createClass({
total: 0,

 getInitialState: function() {
    return { show: false };
  },
  componentWillMount: function() {
    
  },
	componentDidMount: function(){ 	
		
	},
  toggle: function() {
    this.setState({ show: !this.state.show });
  },

onReorder: function (order) {

this.setState(this.props.data);
		//this.setState({data: order});
console.log(12);		
console.log(order);
	},

  render: function() {
	//this.total = xx1(this.props.data);
    //var commentNodes = this.props.data.map((function (comment ,i ) {
	  
console.log(11);
console.log(this.props.data);

	return <SortableListItems items={this.props.data} onReorder={this.onReorder}/>;
    // return (<!-- %@include file="meetingActivity.jsp"% -->);
	

  }
});




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
   // setInterval(this.toggle, 1500);
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
      <ul>
        {commentNodes}
      </ul>
    );
  }
});








var YearPlanComponent = React.createClass({
 getInitialState: function() {
    return { show: false };
  },
  render: function() {
    var commentNodes = this.props.data;
    return (
      <div className="commentList">
        {commentNodes}
      </div>
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
       this.setState({data: data.yearPlan.meetingEvents});
 	   this.setState({yp: data});

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

	var x = this.state.data ? this.state.data : 'loading...';
	var y = this.state.yp ? this.state.yp.uid : 'loading year plan...';

    return (
      <div className="commentBox">
        
       
 			<MeetingList data={x} />
       		<YearPlanComponent data={y} />
      </div>
    );
  }
});


	  function cloneWithProps(c) {
        var newInstance = new c.constructor();
        newInstance.construct(c.props);
        return newInstance;
      }






React.render(
<CommentBox url="/content/girlscouts-vtk/controllers/vtk.controller.html?reactjs=asdf" pollInterval={2000} />,
  document.getElementById('content')
);


function xx(activities){
//console.log(1);
//console.log( YearPlan);
//console.log(this.props);
//alert( MeetingPlan.refId );
	var yy='';
	for( var x in activities ){
		//console.log( "*** "+ activities[x].activityNumber);
		yy+= activities[x].activityNumber +",";
	} 
//console.log(yy);
repositionActivity(thisMeetingRefId , yy);
	
}



function xx1(activities){
	var total=0 ;
	for( var x in activities ){	
		var i = activities[x].duration ;
		total += i;		
	} 
	return total;	
}





var SortableList = React.createClass({
	getInitialState: function() {
		return {
			items: this.props.items 
		};
	},

	onReorder: function (order) {
		this.setState({items: order});
console.log(order);
	},

	render: function () {
		return <SortableListItems items={this.state.items} onReorder={this.onReorder}/>;
	}
});

var SortableListItems = React.createClass({
 	render: function() {
        return <ul>
        	{this.props.items.map(function(item) {
        		return <li id={item}>
                   {item}
                </li>;
        	})}
        </ul>;
    },
    componentDidMount: function() {
    	var dom = $(this.getDOMNode());
    	var onReorder = this.props.onReorder;
        dom.sortable({
        	stop: function (event, ui) {
        		var order = dom.sortable("toArray", {attribute: "id"});
        		dom.sortable("cancel");
console.log( order );
        		onReorder(order);
        	}
        });
    }
});




var SortableList1 = React.createClass({



	getInitialState: function() {
		/*
		return {
			items: this.props.items 

		};
		*/
 return { show: false };
	},

	onReorder: function (order) {
		//this.setState({items: order});
console.log(order);


// Update data1
    var data = this.props.data;

console.log(data);

	var newData = new Array();
	//for( var x in order ){
	for(var i=0; i <order.length;i++){//
		console.log("__ "+ order[i]);
		newData[i] = data[order[i]];
	}



/*
    for( var x in data ){	
		var i = data[x].activityNumber ;
		console.log("** "+i);	
	} 
*/
   this.setState({data: newData});


	},


 render: function () {


		return <SortableListItems1  data={this.props.data} onReorder={this.onReorder}/>;
	}
});


var SortableListItems1 = React.createClass({



render: function() {


 		return <ul>
        	{this.props.data.map(function(item, i) {
        		return <li id={item.activityNumber} >
                   {item.name}
                </li>;
        	})}
        </ul>;


    
  },



 	render123: function() {

        return <ul>
        	{this.props.items.map((function(item, i) {
        		return <li key={i} >
                   {item.name}**
                </li>;
        	}).bind(this))
	}
        </ul>;
    },
    componentDidMount: function() {
    	var dom = $(this.getDOMNode());
    	var onReorder = this.props.onReorder;
        dom.sortable({
        	stop: function (event, ui) {
        		var order = dom.sortable("toArray", {attribute: "id"});
        		dom.sortable("cancel");
console.log( order );
        		onReorder(order);
var yy  = order.toString().replace('"','');
console.log( yy );
//repositionActivity1(thisMeetingRefId , yy);



    

        	}
        });	
    }
});



function repositionActivity1(meetingPath,newVals ){
	//-var newVals = getNewActivitySetup();
console.log(1);	
	var x =$.ajax({ // ajax call starts
		url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=RearrangeActivity&mid='+meetingPath+'&isActivityCngAjax='+ newVals, // JQuery loads serverside.php
		data: '', // Send value of the clicked button
		dataType: 'html', // Choosing a JSON datatype
		success: function (data) { 
			//location.reload();
		},
		error: function (data) { 
		}
	});
} 
React.renderComponent(<SortableList  items={['item 1', 'item 222', 'item 4']}/>, document.getElementById('testDiv'));






    </script>
    
    <hr/>
    <a href="javascript:void(0)" onclick="revertAgenda( thisMeetingPath )"  class="mLocked">Revert to Original Agenda</a>
    <br/><br/>
    
    	<a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add Agenda Items</a>


<div id="newMeetingAgenda" style="display:none;">

       <h1>Add New Agenda Item</h1> 
	
	Enter Agenda Item Name:<br/>
	<input type="text" id="newCustAgendaName" value=""/>
	
	<br/>Time Allotment:
	<select id="newCustAgendaDuration">
		<option value="5">5</option>
		<option value="10">10</option>
                <option value="15">15</option>
		<option value="20">20</option>
                <option value="25">25</option>
		<option value="30">30</option>
	</select>
	
	
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	<div class="linkButtonWrapper">
		<input type="button" value="save" onclick="createCustAgendaItem1('<%=planView.getSearchDate().getTime()%>', '1', thisMeetingPath)" class="button linkButton"/>
	</div>

    
  </body>
</html>

