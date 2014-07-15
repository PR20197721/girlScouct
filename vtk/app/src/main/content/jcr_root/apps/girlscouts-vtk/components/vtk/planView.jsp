<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.user.*, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/planView.jsp -->
<%!
        String activeTab = "planView";
        boolean showVtkNav = true;
%>
<%@include file="include/vtk-nav.jsp"%>
<%



	java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(user.getYearPlan());
	if( sched==null || (sched.size()==0)){out.println( "You must first select a year plan."); return;}
	java.util.List<java.util.Date> dates =new java.util.ArrayList<java.util.Date>(sched.keySet());
	long nextDate=0, prevDate=0;
	java.util.Date searchDate=null;
	if( request.getParameter("elem") !=null ) {
		searchDate = new java.util.Date( Long.parseLong(  request.getParameter("elem")  ) );	
	} else {
		
		if( user.getYearPlan().getSchedule()==null)
			searchDate = (java.util.Date) sched.keySet().iterator().next();
		else{
			
		  java.util.Iterator itr = sched.keySet().iterator();
		  while( itr.hasNext() ){
			searchDate= (java.util.Date)itr.next();
			if( searchDate.after( new java.util.Date() ) )
				break;
			
		  }
	    }
			
	}

	int currInd =dates.indexOf(searchDate);
	if( dates.size()-1 > currInd )
		nextDate = ((java.util.Date)dates.get(currInd+1)).getTime();
	if( currInd>0 )
		prevDate = ((java.util.Date)dates.get(currInd-1)).getTime();

	YearPlanComponent _comp= sched.get(searchDate);
	
%> 
       <div id="planMsg"></div>
      <% 
       				switch( _comp.getType() ){
       					case ACTIVITY :
       					%>  <%@include file="include/viewYearPlanActivity.jsp" %>    <% 
       					break;
       					
       					case MEETING :
       						
       						%><%@include file="include/viewYearPlanMeeting.jsp" %><% 
       								
           					break;
       				}       			
       %>
       <div id="editAgenda"></div>

       
       
       
       
       
       
       
       
<style>
.modal-example-content {
    width: 600px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
    background-color: #FFF;
    border: 1px solid rgba(0, 0, 0, 0.2);
    border-radius: 6px;
    outline: 0 none;
}
.modal-example-header {
    border-bottom: 1px solid #E5E5E5;
    padding: 15px;
}
.modal-example-body p,
.modal-example-header h4 {
    margin: 0;
}
.modal-example-body {
    padding: 20px;
}
</style>




 
<script>
function x12(xx, ttl, id){
	
	var y = document.getElementById('ifr');
	y.src= xx;
	
	 $.fn.custombox( document.getElementById(id) );
	
	 document.getElementById('xyz').innerHTML= ttl;
}

</script>
              
             
             
             
             <div id="modal" style="display: none;" class="modal-example-content">
        <div class="modal-example-header" >
            <span id="xyz"></span><button type="button" class="close" onclick="$.fn.custombox('close');">&times;</button>
            
        </div>
        <div class="modal-example-body" >
            <p>
            
            	<iframe id="ifr" height="500" width="550" src=""></iframe>
            </p>
        </div>
    </div>
       
