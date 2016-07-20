<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@ page
  import="com.google.common.collect .*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/meetingLibrary.jsp  -->
<%
  boolean showVtkNav = true;
  String activeTab = "resource";
  String meetingPath = request.getParameter("mpath");
  if( meetingPath==null || meetingPath.equals("null") || meetingPath.equals("")) {
    meetingPath=null;
    }

  if(meetingPath != null){
    showVtkNav =  false;
  }

  String ageLevel=  troop.getTroop().getGradeLevel();
    ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1).toLowerCase().trim();
    java.util.List<Meeting> meetings =yearPlanUtil.getAllMeetings(user,troop);//, ageLevel);
    String find="";
%>
  <div class="header clearfix">
    <h3 class="columns small-10">Meeting Library</h3>
    <span class="column small-11">HINT: meeting overviews are available under resources</span>
    <a class="close-reveal-modal columns small-3" onclick="closeModalPage()"><i class="icon-button-circle-cross"></i></a>
  </div>

  <%
    boolean isWarning=false;
    String instruction = "****Select a meeting to add to your Year Plan";
    
    if (isWarning) {
  %>
  <div class="small-4 medium-2 large-2 columns">
    <div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
    </div>
    <div class="small-20 medium-22 large-22 columns">
      <% } %>
      <% 
        java.util.List<String> myMeetingIds= new java.util.ArrayList();
        java.util.List<MeetingE> myMeetings = troop.getYearPlan().getMeetingEvents();
        java.util.List<String> futureMeetings = new java.util.ArrayList<String>();
        java.util.List<String> reAddMeetings = new java.util.ArrayList<String>(); 
        
        //add ability to add past meetings again
        java.util.Map<java.util.Date, YearPlanComponent> sched = null;
        try{
            sched = meetingUtil
                     .getYearPlanSched(user,
                             troop, troop.getYearPlan(), true, true);
        }catch(Exception e){e.printStackTrace();}
        BiMap sched_bm=   HashBiMap.create(sched);
        com.google.common.collect.BiMap<YearPlanComponent, java.util.Date> sched_bm_inverse = sched_bm.inverse();
        
       
        
        if(myMeetings!=null) {
          for(int i=0;i< myMeetings.size();i++){
            // ADD CANCELED MEETINGS if( myMeetings.get(i).getCancelled()!=null && myMeetings.get(i).getCancelled().equals("true")) continue;      
            //if( request.getParameter("isReenter")!=null && meetingPath.equals( myMeetings.get(i).getPath() ) ) continue;
            
            String meetingId = myMeetings.get(i).getRefId();
            meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
            myMeetingIds.add( meetingId );
            
            java.util.Date meetingDate =  sched_bm_inverse.get( myMeetings.get(i));
          
            if( meetingDate!=null && meetingDate.before( new java.util.Date() ) && meetingDate.after( new java.util.Date("1/1/2000") ) ) {
              reAddMeetings.add(meetingId);
                
            }else{
              futureMeetings.add(meetingId);
            }
          }
        }
      %>
    </div>
  <script>
  function cngMeeting(mPath){
    $( "#cngMeet" ).load( "/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "act=AddMeeting&addMeeting" : "act=SwapMeetings&cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath="+mPath,function( html ) {
        vtkTrackerPushAction('<%=meetingPath ==null ? "AddMeeting" : "ReplaceMeeting" %>');
        <%
            if( request.getParameter("xx") ==null ){
        %>
            document.location="/content/girlscouts-vtk/en/vtk.plan.html";
      <%} else {%>
        if (window.opener) {
            window.opener.location.reload(false);
        } else {
            window.location.reload(false);
        }
      <%}%>
    });
  }
  </script>
  <div class="scroll">
    <div class="content meeting-library row">
      <p class="instruction columns small-24"><%= instruction %></p>
      <div id="cngMeet"></div>
      
      
      <input type="button" value="reset" onclick="resetVtkFilters()"/>
      
      <table>
      <tr><th>Level-<%=troop.getSfTroopAge()%></th><th>Type</th><th>Category</th></tr>
      <tr>
      <%
        java.util.Map<String, String> mLevel= new java.util.TreeMap<String, String>();
        java.util.Map<String, String> mTypes= new java.util.TreeMap<String, String>();
        java.util.Map<String, String> mCats= new java.util.TreeMap<String, String>();
      
        
        if( meetings!=null)
         for(int i=0;i<meetings.size();i++){
            Meeting meeting = meetings.get(i);
            
            if( meeting.getLevel()!=null && !mLevel.containsKey( meeting.getLevel() ) ){
                mLevel.put(meeting.getLevel(), "ML_"+new java.util.Date().getTime() +"_"+ Math.random());
            }
            
            if(  meeting.getCatTags()!=null && !"".equals( meeting.getCatTags() ) && !mCats.containsKey( meeting.getCatTags() ) ){
                mCats.put(meeting.getCatTags(),  "MC_"+new java.util.Date().getTime() +"_"+ Math.random());
            }
            
            if( meeting.getMeetingPlanType()!=null && !mTypes.containsKey( meeting.getMeetingPlanType() ) ){
            	mTypes.put(meeting.getMeetingPlanType(),  "MT_"+new java.util.Date().getTime() +"_"+ Math.random());
	        }//edn if
            
          }//end for
       %>
     
      
      <td width="30%">
        <div style='overflow:auto; width:250px;height:100px;'>
        <% 
        java.util.Iterator itrLevel= mLevel.keySet().iterator();
        while( itrLevel.hasNext()){
        	String level =  (String)itrLevel.next();
        	String id= (String) mLevel.get(level);
        	%><li> <input type="checkbox" name="_tag" id="<%= id%>" value="<%=level %>"  onclick="doFilter()"/> <%=level %> </li><%
        }
        %>
         </div>
      </td>
     


      <td width="30%">
        <div style='overflow:auto; width:250px;height:100px;'>
        <% 
        java.util.Iterator itrTypes= mTypes.keySet().iterator();
        while( itrTypes.hasNext()){
            String type =  (String)itrTypes.next();
            String id= (String) mTypes.get(type);
            %><li> <input type="radio" name="_tag" id="<%= id%>" value="<%=type %>"  onclick="doFilter()"/> <%=type %> </li><%
        }
        %>
         </div>
      </td>
      
      
      <td width="30%">
       <div style='overflow:auto; width:250px;height:100px;'>
        <% 
        java.util.Iterator itrCats= mCats.keySet().iterator();
        while( itrCats.hasNext()){
            String cat =  (String)itrCats.next();
            String id= (String) mCats.get(cat);
            %><li> <input type="checkbox" name="_tag" id="<%= id%>" value="<%=cat %>"  onclick="doFilter()"/> <%=cat %> </li><%
        }
        %>
         </div>
      </td>
      </tr>
      
      
      
      
      <table id="meetingSelect" class="meetingSelect">
        <tbody>
          <%
          
          //sort meetings by meeting name
          if( meetings !=null ){
              Collections.sort(meetings, new Comparator<Meeting>() {
                  public int compare(Meeting o1, Meeting o2) {
                      return o1.getName().compareTo(o2.getName());
                  }
              });
          } 
         
          for(int i=0;i<meetings.size();i++){
            Meeting meeting = meetings.get(i);
          %>
            <tr style="display:none;" id="TR_TAGS_;<%=mLevel.get(meeting.getLevel()) %>;TYPE_HERE;CATS_HERE;">
                <td>
                        <p class="title"><%=meeting.getName()%></p>
                         <p class="tags" style="color:red;"> LEVEL:<%=meeting.getLevel() %> TYPE: <%=meeting.getMeetingPlanType() %> CATS: <%=meeting.getCatTags() %></p> 
                        <p class="blurb"><%=meeting.getBlurb() %></p>
                </td>
              <td>
                <% if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ) { %>
                  <a onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</a>
                <% } else {%>
                  <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="10" height="15"> <i class="included">Included in Year Plan</i>
                    
                    <%                   
                    if( !futureMeetings.contains(meeting.getId().toLowerCase() )  && reAddMeetings.contains( meeting.getId().toLowerCase() ) ){%>
                         <a onclick="cngMeeting('<%=meeting.getPath()%>')">Re-add meeting</a>
                    <%} %>
                <% }%>
              </td>
                <td>
              <%
                try {
                    String img= meeting.getId().substring( meeting.getId().lastIndexOf("/")+1).toUpperCase();
                    if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
                %>
                    <img width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
                  <% } catch(Exception e){
                        e.printStackTrace();
                    }
                %>
              </td>
            </tr>
          <% } %>
        </tbody>
      </table>
    </div>
  </div>
<script>
    function doFilter(){
    
    	
    	clearResults();
    	
        var els = document.getElementsByName("_tag");
        //console.log( "tags size: "+ els.length);
    
       var tt = document.getElementById("meetingSelect");
       var t= tt.getElementsByTagName("tr");
       for(var i=0;i<t.length;i++){ //each meeting
        var x= t[i];
       
           var isHide= false;
           for(var y = 0; y < els.length; y++){ //each filter
          
               if( els[y].checked ){ //filter checked
               
                  if( x.id.indexOf( els[y].id )!=-1 ){ //filter id found in meeting
                  
                    x.style.display = "inline";
                    isHide= false;
                    continue;
                  }
               }
             
           }
           
          // if( !isHide ){ x.style.display = "none"; }
       }
     
    }
    
    function resetVtkFilters(){
    	 var els= document.getElementsByName("_tag");
    	 for(var y = 0; y < els.length; y++){
    		 els[y].checked = false;
    	 }
    	 clearFilterTypes();
    	 clearFilterCats();
    	 clearResults();
    }
    
    
   
    
    function clearResults(){
    	
    	var tt = document.getElementById("meetingSelect");
        var t= tt.getElementsByTagName("tr");
        for(var i=0;i<t.length;i++){ //each meeting
           var x= t[i];
           x.style.display = "none";
        }
    }
    
    
    function clearFilterTypes(){
    	<% 
        itrTypes= mTypes.keySet().iterator();
        while( itrTypes.hasNext()){
            String type =  (String)itrTypes.next();
            String id= (String) mTypes.get(type);
            %>document.getElementById("<%= id%>").style.display='none';<%
        }
        %>
    }
    
    function clearFilterCats(){
    	<% 
        itrCats= mCats.keySet().iterator();
        while( itrCats.hasNext()){
            String cat =  (String)itrCats.next();
            String id= (String) mCats.get(cat);
            %>document.getElementById("<%= id%>").style.display='none';<%
        }
        %>
    }
    
    doFilter();
</script>