<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<!-- PAGE START asset.jsp -->
<%@include file="include/session.jsp"%>
<!--  WHAT IS THIS JSP?? Refactor -->
<script> 
function assignAid(aidId, meetingId, assetName, assetDesc){
      
      $.ajax({
            cache: false,
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
            type: 'POST',
            data: { 
                act:'AddAid',
                addAids:aidId,
                meetingId: meetingId,
                assetName:assetName,
                assetDesc:assetDesc,
                assetType:'<%=request.getParameter("aType")%>',
                a:Date.now()
            },
            success: function(result) {
            	applyAids(aidId, assetName, '<%=request.getParameter("aType")%>');
            }
        });
}




function rmAid(aidId, meetingId, assetName, assetDesc){
      
	
      $.ajax({
            cache: false,
            url: '/content/girlscouts-vtk/controllers/vtk.controller.html?rand=' + Date.now(),
            type: 'POST',
            data: { 
                act:'RemoveAsset',
                rmAsset:aidId,
                meetingId: meetingId,
                assetName:assetName,
                assetDesc:assetDesc,
                a:Date.now()
            },
            success: function(result) {
            	applyAids(aidId, assetName, '<%=request.getParameter("aType")%>');
            }
        });
    applyAids(aidId, assetName);
}


</script>

<div class="header clearfix">
    <h3 class="columns large-22">Add Meeting</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">


<div class="row modalHeader">
<%
    String aidId= request.getParameter("aidId");
    java.util.Map <java.util.Date,  YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(troop.getYearPlan());
    boolean isWarning=false;
    String instruction = null;
    if( sched==null || (sched.size()==0)) {
        isWarning = true;
        instruction = "You must first select a year plan before adding resources.";
    } else {
        instruction = "Add &quot;<b>" + java.net.URLDecoder.decode(request.getParameter("aidName")) + "</b>&quot; to Meeting(s)";
    }
    if (isWarning) {
%>
        <div class="small-4 medium-2 large-2 columns">
        <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
    </div>
        <div class="small-16 medium-20 large-20 columns">
<%
    } else {
%>
        <div class="small-20 medium-22 large-22 columns">
<%
        }
%>
                <span class="instruction"><%= instruction %></span>
        <hr/>
    </div>
    <div class="small-4 medium-2 large-2 columns">
    </div>
</div>
<%
    if (sched != null && sched.size() > 0) {
%>
<div class="row modalBody">
    <div class="small-24 medium-24 large-24 columns">
        <ul>
<%
        java.util.Iterator itr= sched.keySet().iterator();
        list:while( itr.hasNext() ){
            java.util.Date dt= (java.util.Date) itr.next();
            YearPlanComponent _comp= sched.get(dt);
            String displayName ="";
            java.util.List<Asset> assets = null;
            boolean isActivity = false;     
            switch( _comp.getType()){
                case ACTIVITY :
                    displayName=((Activity)_comp).getName();
                    assets =  ((Activity)_comp).getAssets();
                    isActivity = true;
                    break;
                case MEETING :
                    Meeting meetingInfo =yearPlanUtil.getMeeting(user, troop, ((MeetingE) _comp).getRefId() );
                    displayName=meetingInfo.getName();
                    assets =  ((MeetingE) _comp).getAssets(); 
                    break;
                case MEETINGCANCELED :
                     continue list;
                    
            }
            boolean meetingIsSelected = false;
            if( assets!=null ) {
                for(int i=0;i<assets.size();i++){
                    if( assets.get(i).getRefId().equals(aidId ) ) {
                        meetingIsSelected = true;
                    }
                }
            }
            if (meetingIsSelected && !isActivity) {
%>
            <li class="checked">
                <%=displayName%> (<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/checked-small.png" width="20" height="20"/> added)
                <a href="javascript:void(0)" onclick="rmAid('<%=aidId %>', '<%=_comp.getUid()%>', '<%=request.getParameter("aidName")%>')">remove</a>
            </li>
<%
            } else if(!isActivity){
%>
            <li><a href="javascript:void(0)" onclick="assignAid('<%=aidId %>', '<%=_comp.getUid()%>', '<%=request.getParameter("aidName")%>')"><%=displayName%></a></li>
<%
                        }
        }
%>
        </ul>
    </div>
</div>
<%
    }
%>


 </div>
  </div>
</div>
<!-- PAGE STOP asset.jsp -->
