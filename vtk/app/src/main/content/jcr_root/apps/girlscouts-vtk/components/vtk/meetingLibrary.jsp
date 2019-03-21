<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,com.day.cq.tagging.Tag,java.util.List,java.lang.Character" %>
<%@ page
  import="com.google.common.collect .*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>

<%
try{

	  java.util.List newItems = new java.util.ArrayList(); 
	  newItems.add("Badges for 2018-2019");
	  newItems.add("Badges_Petals|Badges_for_2018-2019");

	  newItems.add("Journey|Life_Skills");
      newItems.add("Life Skills");

	  newItems.add("Journey|STEM");
      newItems.add("STEM");

	  newItems.add("Journey|Outdoor");
      newItems.add("Outdoor");



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
	java.util.List<Meeting> meetings =yearPlanUtil.getAllMeetings(user,troop);
	Set<String> outdoorMeetingIds = meetingUtil.getOutdoorMeetings(user, troop);
	Set<String> globalMeetingIds = meetingUtil.getGlobalMeetings(user, troop);
	
java.util.List<Meeting> extraInfoMeetings= new java.util.ArrayList();
for( int i=0;i<meetings.size();i++){
	if( meetings.get(i).getMeetingPlanTypeAlt()!=null && !"".equals( meetings.get(i).getMeetingPlanTypeAlt() ) ){
		Meeting _alteredMeeting = (Meeting) VtkUtil.deepClone(meetings.get(i) );
		_alteredMeeting.setMeetingPlanType(meetings.get(i).getMeetingPlanTypeAlt());
		_alteredMeeting.setCatTags(meetings.get(i).getCatTagsAlt());
		extraInfoMeetings.add( _alteredMeeting );
	}
}
meetings.addAll(extraInfoMeetings);




	
	String find="";
%>
  <div class="header clearfix">

    <h3 class="columns small-20">
    <%if( request.getParameter("newCustYr")!=null){ %>
          Create Your Own Year Plan
      <%}else{ %>
              ADD A PETAL, BADGE OR JOURNEY
       <%}//end else %> 
    </h3>
    <span class="column small-1">
   
  

    </span>
    <a class="columns small-3" onclick="closeModalPage()"><i class="icon-button-circle-cross"></i></a>

  </div>

  <%

	boolean isWarning=false;
	String instruction = "Select a meeting library and pick the ones that best complete your multi-level Year Plan";


    if( request.getParameter("newCustYr")!=null){ 
    	instruction="Look through the meeting library and pick the ones that best complete your multi-level Year Plan";
    }else{ 
        instruction="Select a meeting to add to your Year Plan";

    }//end else 

instruction="";


    if (isWarning) {
  %>
  <div class="small-4 medium-2 large-2 columns meeting_library">
	<div class="warning"><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/></div>
	</div>
	<div class="small-20 medium-22 large-22 columns">
	  <% } %>
	  <%
		java.util.List<String> myMeetingIds= new java.util.ArrayList();
		java.util.List<MeetingE> myMeetings = new java.util.ArrayList();
		if( troop!=null && troop.getYearPlan()!=null && troop.getYearPlan().getMeetingEvents()!=null )
			myMeetings= troop.getYearPlan().getMeetingEvents();
		
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
		
			String meetingId = myMeetings.get(i).getRefId();
			meetingId= meetingId.substring(meetingId.lastIndexOf("/") +1).trim().toLowerCase();
		
			//if custom meeting 
			if( meetingId.contains("_") )
				meetingId= meetingId.substring(0, meetingId.indexOf("_")  );
		
			
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
			document.location="/content/girlscouts-vtk/en/vtk.html";
	  <%} else {%>
		if (window.opener) {
			window.opener.location.reload(false);
		} else {
			window.location.reload(false);
		}
	  <%}%>
	});
  }


  <%
  java.util.Map<String, String> mLevel= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mTypes= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mCats= new java.util.TreeMap<String, String>();

  java.util.Map<String, java.util.Set> mTylesPerLevel = new java.util.TreeMap();
  java.util.Map<String, java.util.Set> mCatsPerType  = new java.util.TreeMap();
  if( meetings!=null)
   for(int i=0;i<meetings.size();i++){
	  Meeting meeting = meetings.get(i);
meeting.setLevel( meeting.getLevel().replace("-","_"));



if( meeting!=null && meeting.getCatTags()!=null)
	meeting.setCatTags( meeting.getCatTags().replaceAll(" ","_") );
if( meeting!=null && meeting.getMeetingPlanType()!=null)
	meeting.setMeetingPlanType(meeting.getMeetingPlanType().replaceAll(" ","_"));

	  if( meeting.getLevel()!=null && !mLevel.containsKey( meeting.getLevel() ) ){
		  mLevel.put(meeting.getLevel(), "ML_"+new java.util.Date().getTime() +"_"+ Math.random());
		  mTylesPerLevel.put(meeting.getLevel(), new java.util.HashSet<String>() );
	  }
	  String cats = meeting.getCatTags();
	  if( cats!=null){
	 
	   StringTokenizer t = new StringTokenizer(cats, ",");

		  while( t.hasMoreElements() ){

			  String theCat= (String) t.nextToken();

			  mCats.put(theCat,  "MC_"+new java.util.Date().getTime() +"_"+ Math.random());

			  if( meeting.getMeetingPlanType()!=null && meeting.getCatTags()!=null){
				  java.util.Set _x = mCatsPerType.get(meeting.getMeetingPlanType());

				  if( _x==null ){

					  java.util.Set _y =new java.util.HashSet();
					  _y.add(theCat);
					  mCatsPerType.put( meeting.getMeetingPlanType(), _y );

				  }else if( _x!=null && !_x.contains(theCat)  ){


				   mCatsPerType.get( meeting.getMeetingPlanType()).add( theCat );
				  }
			  }//end if

		  }//edn whle
	  }//end if




	  if( meeting.getMeetingPlanType()!=null && !mTypes.containsKey( meeting.getMeetingPlanType() ) ){
		  mTypes.put(meeting.getMeetingPlanType(),  "MT_"+new java.util.Date().getTime() +"_"+ Math.random());
	  }//edn if

	  if( meeting.getMeetingPlanType()!=null && !mTylesPerLevel.get( meeting.getLevel() ).contains( meeting.getMeetingPlanType() ) ){
		   mTylesPerLevel.get( meeting.getLevel() ).add(meeting.getMeetingPlanType());
	   }

	}//end for

  
 %>

  </script>
  

  
  <form id="form-meeting-library" action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="get">

	  
	  <%if( request.getParameter("newCustYr")!=null){ %>
		  <input type="hidden" name="act" value="CreateCustomYearPlan" />
	  <%}else{ %>
		  <input type="hidden" name="addMeetings" value="true" />
	   <%}//end else %>  
  <div class="scroll" style="">
	<div class="content meeting-library row">
	<div class="columns small-24 small-centered">
		<p class="instruction " style="float:left;">
	 		 <span><%= instruction %></span>
			  
	  	</p>
	  	<!-- p class="" style="margin-bottom:0px; float:right">
		  <span><img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png" width="30px" vertical-align="baseline" />
		  </span>= <i>Get Girls Outside!</i> Activity Option</p -->	
	</div>
	 
	  <div id="cngMeet"></div>



	 <!--  start carlos 1 -->

	 <div id="vtk-meeting-filter" class="content">

		<div class="sectionHeader" style="">
			<div class="column small-22 small-centered" style="display:table;">
				<!-- span class="vtk-green-box" style="">
				<span class="icon-search-magnifying-glass" style=""></span>
				</span -->
				<table>

					<tr>
						<td colspan="2">
							<h3>Search to Add a Petal, Badge or Journey Meeting</h3>
						</td>
					</tr>

					<tr>	
					<td>
						<div class="__search row" style="border:1px solid gray; border-radius:6px; overflow:hidden">
							<div class="columns small-2">
								<span class="icon-search-magnifying-glass"></span>
							</div>

							<div class="columns small-20">
								<input type="text" name="search" maxlength="52" placeholder="Search for a badge or journey award by name" id="searchByMeetingTitle" value="" />
							</div>

							<div class="__X columns small-2" style="display:none">
								<span class="icon-cross"></span>
							</div>
							
							
						</div>
						<p id="showHideReveal" class="hide-for-print close">Or Use Filters</p>
						</td>
					<td>
							
						</td>


					</tr>
					<tr><td>



						</td></tr>
				</table>
			</div>
		</div>

        <div class="vtk-meeting-group" style="display:none;">
			<div class="main-filter column small-22 small-centered" style="display:table; padding-left:0;">
				<div class="row">
					<div class="column small-24">
						<div class="vtk-meeting-filter_title"><span>1.</span> Select your Girl Scout Level(s)</div>
						<div id="vtk-meeting-group-age" class="row">
						<!-- end carlos 1 -->
						<%-- Add Order to iterator --%>
						<%
							ArrayList<String> levelList = new ArrayList<String>(7);
							String[] levelArray = new String[]{"Daisy", "Brownie", "Junior","Cadette","Senior", "Ambassador","Multi_level"};
							for(int i = 0; i < 7; i++){
								String level = levelArray[i];
								String id= (String) mLevel.get(level);
								%>
								<span class="container" style="clear:both;">
								<span class="terminal" data-price="<%if(level.contains("Daisy"))out.println("1");else if(level.contains("Brownie"))out.println("2");else if(level.contains("Junior"))out.println("3");else out.println(100);%>">
								<div class="small-24 medium-6 column selection-box">
								<input type="radio" name="_tag_m" id="<%= id%>" value="<%=level %>"  <%=(troop.getTroop().getGradeLevel().contains(level) || troop.getTroop().getGradeLevel().contains(level.replace("_","-")) ) ? "" : "" %>"/>
								<label for="<%= id%>"><span></span><p><%=level.replace("_","-")  %> </p></label>
								</div>
								</span>
								</span>
						<%	} %>



	  				<!-- carlos 2 start -->
					</div>
				</div>
				<div class="column small-24" id="vtk-meeting-group-type" style="display:none">
					<div class="vtk-meeting-filter_title"><span>2.</span> Select the type of meeting plan you want </div>
					<div class="row">

					<!--  carlos 2 end  -->
					<%
						java.util.Iterator itrTypes= mTypes.keySet().iterator();
						while( itrTypes.hasNext()){
							String type =  (String)itrTypes.next();
							String id= (String) mTypes.get(type);
							%>
							<div class="small-24 medium-6 column selection-box <%= !itrTypes.hasNext() ? "end" : "" %>" style="display:none;min-height:60px;">
									<input type="radio" name="_tag_t" id="<%= id%>" value="<%=type %>"  />
									<label for="<%= id%>"><span></span><p> <%=type.equals("Badges_Petals") ? "Badges/Petals" : type.replaceAll("_", " ") %> </p></label>
						</div>
							<%
						}
					%>



	  <!--  carlos 3 start -->


					</div>
				</div>
			</div>
		</div>

		<div class="list-of-categories column small-22 small-centered" style="display:none;padding-left:0;">
			<div class="row">
				<div class="column small-24">
					<div class="vtk-meeting-filter_title"><span>3.</span> Select your  <span id="cat_selected" style="font-size:14px !important;"></span>  categories</div>
					<div id="vtk-meeting-group-categories"  class="row  wrap-vtk-meeting-group-categories">

	  <!--  end carlos 3  -->

		<%
		java.util.Iterator itrCats= mCats.keySet().iterator();
		int index = 1;

		while( itrCats.hasNext()){
			String cat =  (String)itrCats.next();
			String cat_fmted = cat.replaceAll("_", " ");
			String id= (String) mCats.get(cat);
			%>
				<div class="small-24 medium-12 large-6 column selection-box  <%= !itrCats.hasNext() ? "end" : "" %>"  style="min-height:70px">
					<input type="checkbox" name="_tag_c" id="<%= id%>" value="<%=cat %>" />

					
					<label for="<%= id%>"><span></span>
 					<p> 
 						<%= cat_fmted %> <% if( newItems.contains(cat_fmted)){ %>
						 <span style="font-size:10px;color:#F9A61A;font-weight:bold;background:none;display:inline-block; padding-top: 8px;" id="vtkCatItem_<%= id%>">
 							NEW
 						</span>
						 <% } %>
 					</p></label>
				</div>
			
		
			<%  } %>


	  <!--  carlos 4 start  -->


					</div>
				</div>
			</div>
		</div>



	</div>



</div>


		<div class="list-of-buttons column small-22 small-centered" style="padding-left:0;">
			<div class="row">
				<div id="vtk-meeting-group-button" class="column small-24" style="padding:25px 0 25px 0;">
					<div id="vtk-meeting-group-button_cancel" class="button tiny ">CANCEL</div>
					<div id="vtk-meeting-group-button_ok" class="button tiny disabled " >VIEW LIST</div>
				</div>
			</div>
		</div>

		<div class="loading-meeting" style="display:none" ></div>	

		<div id="meeting-library-no-content" class="no-content column small-24" style="display:none; padding:40px 0 0 25px" >
		  	<h5></h5>
			  <p>"Apply filters" can step you through how meetings are organized</p>
		</div>
	  <!--  carlos 4 end  -->
	  <div id="meetingSelect" class="meetingSelect column small-24 small-centered" style="display:none;">
		  <!--<div class="row">-->

		
				<%-- // --%>
		<div style="display: flex;
			justify-content: center;
			flex-grow: 1;
			flex-basis: 100%;
			width: 100%;
			min-height: 80px;
			position:fixed;
			bottom:50px;
			left:0px;
			z-index:1001;
			overflow: hidden;">
			<div class="vtk-float-submit">
				<input class="button tiny" type="button" value="CANCEL" onclick="closeModal()"/>
				<%if(request.getParameter("isReplaceMeeting")==null){%>
				<input class="button tiny inactive-button clear-meeting-filter-result" type="button" value="CLEAR SELECTED MEETINGS" />
				<%if( request.getParameter("newCustYr")!=null){ %>
				<input class="button tiny inactive-button add-to-year-plan" type="button" value="ADD TO YEAR PLAN"  onclick="createCustPlan(null)"/>
				<%}else{ %>
				<input class="button tiny inactive-button add-to-year-plan" type="submit"  value="ADD TO YEAR PLAN" />
				<%}//end else %> 
				<%}%>
			</div>  
		</div>
		<%--  --%>
	<div id="no-of-meeting" class="no-of-meeting" style="display:none;padding-left:25px;" >
		<p></p>
	</div>

	
		  <%

		  //sort meetings by this specific order: dAisy > bRownie > jUnior
		  if (meetings != null) {
			  meetings = VtkUtil.sortMeetings( meetings );
          }

		  String currentLevel = "";
		  
		  //uniq meeting by path: issue with altTags, altMeetintType
		  meetings = VtkUtil.filterUniqMeetingByPath(meetings);
		  
		  for(int i=0;i<meetings.size();i++){
			Meeting meeting = meetings.get(i);
			boolean isReq= ( meeting.getReq()==null || "".equals(meeting.getReq() ) ) ? false : true;
			
			if(!meeting.getLevel().equals(currentLevel)){
				currentLevel=meeting.getLevel();
				
				%>
				<div style="display:none;" class="meeting-age-separator column small-24 levelNav_<%= currentLevel %>" id="levelNav_<%= currentLevel %>">
                    <%= currentLevel.replace("_","-") %>
                </div>

				<% 
		   }
%>


			<div class="meeting-item column small-24" style="display:none;" data-url="<%=meeting.getPath()%>" id="TR_TAGS_;<%=mLevel.get(meeting.getLevel()) %>;<%=meeting.getMeetingPlanType()==null ? "" : mTypes.get(meeting.getMeetingPlanType()) %>;<%= meeting.getLevel()%>;
			<%
			if(meeting.getMeetingPlanType()!=null  && meeting.getCatTags()!=null){
				java.util.Set cats = mCatsPerType.get(meeting.getMeetingPlanType());
				if( cats!=null){
					java.util.Iterator itrCat = cats.iterator();
					while( itrCat.hasNext() ){
						String x = (String) itrCat.next();
						if(!meeting.getCatTags().contains( x ) )continue;
						%><%= mCats.get(x) %><%=itrCat.hasNext() ? ";" : ""%><%
					}
				}
			}
			%>
			">
			
    
				<div class="row">
					<div class="column small-24 medium-14">
						<div style="display:table;min-height:110px">
					   		<div style="display:table-cell;height:inherit;vertical-align:middle;">
								<p class="title"><%=meeting.getName()%>  <%=(globalMeetingIds.contains(meeting.getId()) ? "<img data-tooltip aria-haspopup='true' class='has-tip tip-top radius meeting_library' title='<b>Go Global!</b>' style='width:30px;vertical-align:top;padding-top:2px;cursor:auto;border:none' src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/globe_selected.png\">" : "")%> 
								<%=(outdoorMeetingIds.contains(meeting.getId()) ? "<img data-tooltip aria-haspopup='true' class='has-tip tip-top radius meeting_library' title='<b>Get Girls Outside!</b>' style='width:30px;vertical-align:bottom;cursor:auto;border:none' src=\"/etc/designs/girlscouts-vtk/clientlibs/css/images/outdoor.png\">" : "")%>
								</p>
						 
								<p class="blurb"><%=meeting.getBlurb() %></p>
								<p class="tags"> 
									<span>
										<%
										if(meeting.getCatTags()!=null){
											
											java.util.StringTokenizer t= new StringTokenizer(meeting.getCatTags(), ",");
											while( t.hasMoreElements()){
												%><%=t.nextToken().replace("_", " ")%><%=t.hasMoreElements() ? "," : "" %> <% 
											}
										}
										%>
									</span>
								</p>
							</div>
						</div>
					</div>
					<div class="column small-24 medium-6">
						<div style="display:table;min-height:110px; width: inherit;">
					   		<div style="display:table-cell;height:inherit;vertical-align:middle; text-align:center;">
								<% if( request.getParameter("newCustYr")!=null || !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ) { %>

									<div class="middle-checkbox" style="text-align:center;">

						 	  										<table>
						 	                                          <tr><td>
						 	                                            <%if(request.getParameter("isReplaceMeeting")==null ){%>
						 													<input type="checkbox" name="addMeetingMulti" id="<%=meeting.getPath()%>_<%=i%>" value="<%=meeting.getPath()%>"/>
						 	                                              <%} %>
						 	                                            <label for="<%=meeting.getPath()%>_<%=i%>"><span></span>
						 	
						 	
						 	
						 	                                            </label>
						 	                                              </td><td>
						 	                                              	<%if(request.getParameter("isReplaceMeeting")!=null ){%>
						 	                                              		<p class="select-meeting-withaction" style="display:none" onclick="cngMeeting('<%=meeting.getPath()%>')">SELECT MEETING</p>
						 	                                                <%}else{%>
						 	                                              <p style="color:#000;">SELECT MEETING</p>
						 	 												<%}%>
						 	                                              </td></tr></table>
 					
									</div>
								<% } else {%>
				  				<img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="10" height="15"> <i class="included">Included in Year Plan</i>
								<% }%>
						 	</div>
						 </div>
					</div>
					<div class="column small-24 medium-4">
						<div style="min-height:110px; width:100%">
							<div style="height:inherit;vertical-align:middle; text-align:center;width:100%">
							 <% try { %>
								<%	
									String img= meeting.getId().substring( meeting.getId().lastIndexOf("/")+1).toUpperCase();
									if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
									String isReqClass = isReq ? " _requirement_modal" : "" ;
									String function = isReq ? "onclick=openRequirementDetail(this)":""; 
								%>
									<img width="100" <%= function %>   class="image <%= isReqClass %>" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png"/>
								
								<% } 
										catch(Exception e){
										e.printStackTrace();
									}
								%>
							</div>
						</div>
					</div>
				</div>

				<% if(isReq){ %>
					<div class="__requiments_details row" style="display:none">
						<div class="column small-24" style="padding:10px;">
						
							
							<div class="_requiments_description">
								<p style="margin-bottom: 5px"><b><%= meeting.getReqTitle() %></b></p>
								<%= meeting.getReq() %>
							</div>

							<p style="text-align:center; margin-top:20px">
								<span class="vtk-button" style="cursor:ponter;" onclick="_closeME(this)">
									&nbsp;&nbsp;&nbsp;CLOSE&nbsp;&nbsp;&nbsp;
								</span>
							</p>
						</div>
					</div>
				<% } %>

			</div>
		<% } %>
		



		  </div>




	  </div>

  </div>
  	
	  
</form>
<script>



	function openRequirementDetail(element){
		$(element).parents('.meeting-item').find('.__requiments_details').toggle();
	}


	function _closeME(element){
		$(element).parents('.__requiments_details').toggle();
	}

	//Polyfill
	//===================================
	if (typeof Object.assign != 'function') {
		// Must be writable: true, enumerable: false, configurable: true
		Object.defineProperty(Object, "assign", {
			value: function assign(target, varArgs) { // .length of function is 2
			'use strict';
			if (target == null) { // TypeError if undefined or null
				throw new TypeError('Cannot convert undefined or null to object');
			}

			var to = Object(target);

			for (var index = 1; index < arguments.length; index++) {
				var nextSource = arguments[index];

				if (nextSource != null) { // Skip over if undefined or null
				for (var nextKey in nextSource) {
					// Avoid bugs when hasOwnProperty is shadowed
					if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
					to[nextKey] = nextSource[nextKey];
					}
				}
				}
			}
			return to;
			},
			writable: true,
			configurable: true
		});
	}
	//===================================

	function closeModal(){
		$('#gsModal').find('a').children('i').trigger('click');
	}

	function createCustPlan(singleMeetingAdd) {

		var sortedIDs="";
		
		if( singleMeetingAdd==null) {
			var els = document.getElementsByName("addMeetingMulti");

			for(var y = 0; y < els.length; y++){
				if( els[y].checked == true ){
					sortedIDs= sortedIDs +els[y].value+ ",";
				}
			}


		}else{

			sortedIDs = sortedIDs + singleMeetingAdd +",";

		}
		
		$.ajax({
			url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomYearPlan&mids="+ sortedIDs,
			cache: false
				})
			.done(function( html ) {
					vtkTrackerPushAction('CreateCustomYearPlan');
					location.href= "/content/girlscouts-vtk/en/vtk.html";
		});
	}


	$(function(){
		var previewsType = undefined;


		search = {};
		var currentYear = CurrentYear(new Date());

		//Params Creator
		var params = (function (){
			var _params = {
				keywords:'',
				level:[],
				categoryTags:[],
				meetingPlanType:'',
				year:(currentYear.start()).getFullYear()
			};


			function _addParams(newParam){
				_params = Object.assign(_params,newParam);
			}

			function _getParams(){
				return _params;
				
			}


			function _clear(){
				_params = {
					keywords:'',
					level:[],
					categoryTags:[],
					meetingPlanType:'',
					year:(currentYear.start()).getFullYear()
				}
			}

			return {
				add: _addParams,
				get: _getParams,
				clear:_clear
			}
		})();


		function renderAmountOfMeeting(numberOfMeeting){

			var print = (numberOfMeeting==1)?" Meeting plan":" Meeting plans"

			$('#no-of-meeting>p').html(numberOfMeeting + print);

			$('#no-of-meeting').show();
		}


		//Render Show / hide meetigns
		function executeShowAndHide(ArrayToShow){
			var showLevelNav = (function(){
				var _levels = [];
				
				function _add(level){
					if(!(!!~_levels.indexOf(level))){
						_levels.push(level)
					}
				}

				return {
					levels:_levels,
					add: _add,
				}
			}());




			$('#meetingSelect').slideUp();
			$('.meeting-age-separator').hide();

			$('#no-of-meeting').hide()

			//Colapse filter 
			$('#vtk-meeting-filter').find('#showHideReveal').removeClass('open');
			$('.vtk-meeting-group').hide();


			var meetingToShow = ArrayToShow.map(function(meeting){
				return meeting.id;
			})

			 $('.meeting-item').hide(); 

			if(meetingToShow.length>0){
				for(var ___x = 0;  meetingToShow.length > ___x; ___x++ ){
					var element =  $('[data-meetingid="'+meetingToShow[___x]+'"]');
					element.show();

					console.log(element.attr('id').split(';'));
					showLevelNav.add(element.attr('id').split(';')[3])

				}

				
				for (var __h=0; showLevelNav.levels.length > __h; __h++){
					$('.levelNav_'+showLevelNav.levels[__h].replace(/-/g,'_')).show();
				}


			}else{
				noMeetingFound(params.get()['keywords'])
			} 


			renderAmountOfMeeting(meetingToShow.length);
			$('#meetingSelect').slideDown();
		}

		function noMeetingFound(keywords){
			//TODO: Add No content 
			$('.meeting-library .loading-meeting').hide();
			$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow','auto');
			$('#meeting-library-no-content>h6>#term-search').text('');
			$('#meeting-library-no-content>h5').text('Sorry no meeting titles have the term, "'+ keywords+ '"');
			$('#meeting-library-no-content').show();
			$('.meeting-library #vtk-meeting-filter').fadeIn();
					$('.meeting-library .list-of-buttons').fadeIn();
			
		}

		//Call to the server
		function callToserver(){
			$('#meeting-library-no-content').hide();
			$('.meeting-library #vtk-meeting-filter').fadeOut();
			$('.meeting-library .list-of-buttons').fadeOut();
			$('.meeting-library .loading-meeting').show();
			$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow','none');
			$('#meetingSelect').slideUp();
	
			bottoms_library.ok.disable();

			var call = $.ajax({
				url: "/bin/vtk/v1/meetingSearch",
				dataType: "json",
				type: 'POST',
				contentType: "application/json",
				cache: false,
				data: JSON.stringify(params.get())

			})

	

			call.done(function(data){
				executeShowAndHide(data);
					$('.meeting-library .loading-meeting').hide()
					$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow','auto');
					$('.meeting-library #vtk-meeting-filter').fadeIn();
					$('.meeting-library .list-of-buttons').fadeIn();

					bottoms_library.ok.enable();
			}); 

			call.fail(function(err){
		
				console.error('error:',err);

					$('.meeting-library .loading-meeting').hide()
					$('.vtk-body .ui-dialog.modalWrap .scroll').css('overflow','auto');
					$('.meeting-library #vtk-meeting-filter').fadeIn();
					$('.meeting-library .list-of-buttons').fadeIn();

					bottoms_library.ok.enable();
			
			})	

		}

		//Button Actions Logic
		function _buttonLogic(){
			var object = params.get();
			var keyInObject = Object.keys(object);

			var somethingFill = keyInObject.some(function(e){
				return object[e] !== '' 
			})
			var isKeyboard = (object['keywords'].length >= 3);
			var isLevel = !!object['level'].length;
			
			if( isKeyboard || isLevel ){
				bottoms_library.ok.enable();
			}else{
				clearMeetingId();
				bottoms_library.ok.disable();
			}
		} 

		//Clear the check b
		function clearCategories(){
				$('[name="_tag_c"]').each(function(idx,ele){
					this.checked = false;
				});

				$('#vtk-meeting-filter .vtk-meeting-group .list-of-categories').hide();
		}

		function cleanMeetingCheckbox(){
			$('input[name="addMeetingMulti"]').each(function(){
				this.checked = false;
			})

				$('.clear-meeting-filter-result').addClass('inactive-button');
				$('.add-to-year-plan').addClass('inactive-button');
		}

		function clearMeetingId(){
			$('#meetingSelect').slideUp(function(){
				$('.meeting-item').hide();
				cleanMeetingCheckbox();
			});
		}

		function clearType(){
			$('#vtk-meeting-group-type input').each(function(idx,ele){
					this.checked = false;
					this.parentElement.style.display = 'none';
			})
		}


		//x in input
		$('#vtk-meeting-filter .__search .__X').on('click',function(){
				$('#searchByMeetingTitle').val('');
				params.add({keywords:''})
				$(this).hide();
				_buttonLogic()
		})
		//keywords
		$('#searchByMeetingTitle').on('keyup', function(event){
			var key = event.target.value, element = $('#vtk-meeting-filter .__search .__X');
			params.add({keywords:key.split(' ').join(' ')})
			
			if(key.length >= 3) {
				element.show();
			}else{
				element.hide();
			}
			

				_buttonLogic();
		})
		//Age
		$('#vtk-meeting-group-age input').on('click', function(){
			var Age = []
			$('#vtk-meeting-group-age input').each(function(a,e){
				if(e.checked){
					Age.push(e.value.replace(/_/g,'-')); //level JSP is parse all '-' to '_' legacy logic.
				}
			});
	
			params.add({level:Age})

			if(Age.length){
				$('#vtk-meeting-group-type').hide();
				clearType();

				var mapEleType = [];

				for(var _y=0; Age.length > _y; _y++){
					for(var _X in search[Age[_y]]){
						var element = document.getElementById(_X);
							mapEleType.push(element.parentElement);

					}
				}

				for(var d = 0; mapEleType.length>d; d ++){
					mapEleType[d].style.display= '';
				}
				
				$('#vtk-meeting-group-type').show();
			}else{
				$('#vtk-meeting-group-type').hide();
				
			}

			
			clearCategories();
			params.add({meetingPlanType:''});
			_buttonLogic();
		});
		//Type
		$('#vtk-meeting-group-type input').on('click', function(){
			//Handle unclick radio
			if(this == previewsType){
				this.checked = false;
				previewsType = undefined;

				clearCategories();
				clearMeetingId();

				params.add({categoryTags:[]});
				params.add({meetingPlanType:''});
			}else{
				previewsType = this;
			}

			var type;
			$('#vtk-meeting-group-type input').each(function(a,e){
				if(e.checked){
					type = e;
				}
			});

			$('#vtk-meeting-filter .vtk-meeting-group .list-of-categories').hide()
			
			$('[name="_tag_c"]').each(function(idx,ele){
				$(ele)[0].checked = false;
				$(ele).parent().hide();
			});
		
			if(type){
					var levels = params.get()['level'];

					var c = [];
					for(var _e=0; levels.length>_e; _e++){
							if(search[levels[_e]][type.id]){
								for (var _g=0; search[levels[_e]][type.id].length>_g; _g++){

									var element = document.getElementById(search[levels[_e]][type.id][_g]);
									c.push(element.parentElement);
								}	
							}						
					}
				
					if(c.length>0){
						for(var x = 0; c.length > x; x++ ){
							c[x].style.display = '';
						}

						$('#vtk-meeting-filter .vtk-meeting-group .list-of-categories').show() 
					}
				
				params.add({"meetingPlanType":type.value})
			}else{
				params.add({"meetingPlanType":''})
			}
				
			_buttonLogic();
		
		});
		//Category
		$('[name="_tag_c"]').on('click', function(){
			var Cat = []
			$('[name="_tag_c"]').each(function(a,e){
				if(e.checked){
					Cat.push(e.value); 
				}
			});
			params.add({categoryTags:Cat})
			_buttonLogic();
		})


		//Buttoms elements
		var bottoms_library = {
			ok:{
				element:$('#vtk-meeting-group-button_ok'),
				disable: function(){
						this.element.addClass('disabled');
				},
				enable: function(){
						this.element.removeClass('disabled');
				}

			},
			cancel:{
				element:$('#vtk-meeting-group-button_cancel')
			}
		} 

		//Call to the server
		bottoms_library.ok.element.on('click',function(){
			if(!$(this).hasClass('disabled')){
				callToserver();
			}		
		})

		//Close Modal
		bottoms_library.cancel.element.on('click',closeModal);
		
		if(is_add_meeting){
			$('.select-meeting-withoutaction').show();
			$('.select-meeting-withaction').hide();
		}else{
			$('.select-meeting-withaction').show();
			$('.select-meeting-withoutaction').hide();
		}	


		//Create Search Object
		/*
			STRUCTURE
			search = {
				level:{
					types : [categories]
				}
			}
		
		*/

		//Run After page load checking all the meetings
		$('.meeting-item').each(function(idx,ele){
			
			//Add the meeid attribute
			$(this).attr('data-meetingid',$(this).data('url').split('/').pop());
			var elementAttr = $(this).attr('id').split(';');

			elementAttr[3] = elementAttr[3].replace('_','-');

			if(elementAttr[3]!=='') {
				if(!search.hasOwnProperty(elementAttr[3])){
					search[elementAttr[3]]={};
				}

	
				if(elementAttr[2]!==''){
					if(!search[elementAttr[3]].hasOwnProperty(elementAttr[2].trim()) && elementAttr[2].trim().length > 0){
						search[elementAttr[3]][elementAttr[2].trim()]=[];
					}
					if(elementAttr.length>4){
						for (var c=4;c < elementAttr.length; c++){
							if(search[elementAttr[3]][elementAttr[2]].indexOf(elementAttr[c].trim()) == -1 && elementAttr[c].trim().length > 0){
								search[elementAttr[3]][elementAttr[2]].push(elementAttr[c].trim());	
							}					
						}
					}
				}

			}



		});

		//Cancel enter submit
		var time = 0;
		$('#form-meeting-library').on('keypress', function(e) {
			var keyCode = e.keyCode || e.which; //Hack for mack and IE
			
			if (keyCode === 13){ //Check if key enter is pressed
				e.preventDefault(); //Prevent send the form

				if (e.timeStamp>time+1500) {  //avoid multiple click in the enter buttom (one second and an half)
					valueSearch = $(this).serializeArray().find(function(e){ //check in the form for the input[name="search"]
						return e.name == 'search';
					})
					if(valueSearch.value.length>2){ //Check the is more that two character
						callToserver();
					}
					
							
					time = e.timeStamp //save the previos time stamp
				}

				return false; 
			}
		});

        //Listining for a change in the meeting and activate buttons.
		$('.meeting-item input[name="addMeetingMulti"]').on('change', function(){
		
			var enablebuttom = $('.meeting-item:visible input[name="addMeetingMulti"]')
			.toArray()
			.some(function(i,e,a){
				return i.checked;
			});

			if(enablebuttom){
				$('.clear-meeting-filter-result').removeClass('inactive-button');
				$('.add-to-year-plan').removeClass('inactive-button');
			}else{
				$('.clear-meeting-filter-result').addClass('inactive-button');
				$('.add-to-year-plan').addClass('inactive-button');
			}

		})

		//Lisining for  clear meeting selectect
		$('.clear-meeting-filter-result').on('click', function(){
			cleanMeetingCheckbox();
		});


		$('#vtk-meeting-filter').find('#showHideReveal').stop().click(function(e){
			$(this).toggleClass('open')
			$('.vtk-meeting-group').slideToggle();
			$('.vtk-dropdown_options').hide();
			$('#vtk-meeting-report').hide();
		})


	});

	$('#form-meeting-library').on('submit',  function(e){ $('.add-to-year-plan').addClass('inactive-button');  })
</script>


<%}catch(Exception e){e.printStackTrace();}%>


<style>
	.tooltip span.nub{
		left:10px; //hack for look fine in page
	}
</style>
