<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*,com.day.cq.tagging.Tag,java.util.List,java.lang.Character" %>
<%@ page
  import="com.google.common.collect .*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<!-- apps/girlscouts-vtk/components/vtk/meetingLibrary.jsp  -->

<%
try{
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

    <h3 class="columns small-10">
    <%if( request.getParameter("newCustYr")!=null){ %>
          Create Your Own Year Plan
      <%}else{ %>
              Meeting Library
       <%}//end else %> 
    </h3>
    <span class="column small-11">
    <%if( request.getParameter("newCustYr")!=null){ %>
          
      <%}else{ %>
              HINT: meeting overviews are available under resources
       <%}//end else %>
    </span>
    <a class="close-reveal-modal columns small-3" onclick="closeModalPage()"><i class="icon-button-circle-cross"></i></a>

  </div>


  <%

	boolean isWarning=false;
	String instruction = "Select a meeting library and pick the ones that best complete your multi-level Year Plan";


    if( request.getParameter("newCustYr")!=null){ 
    	instruction="Look through the meeting library and pick the ones that best complete your multi-level Year Plan";
    }else{ 
        instruction="Select a meeting to add to your Year Plan";
    }//end else 
    
    if (isWarning) {
  %>
  <div class="small-4 medium-2 large-2 columns">
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


  <%
  java.util.Map<String, String> mLevel= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mTypes= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mCats= new java.util.TreeMap<String, String>();

  java.util.Map<String, java.util.Set> mTylesPerLevel = new java.util.TreeMap();
  java.util.Map<String, java.util.Set> mCatsPerType  = new java.util.TreeMap();
  if( meetings!=null)
   for(int i=0;i<meetings.size();i++){
	  Meeting meeting = meetings.get(i);

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
	  /*
	  List<Tag> catList= meeting.getCatTags();
	  if( catList!=null ){
		  ListIterator<Tag> li = catList.listIterator();
		  while (li.hasNext()) {
			  Tag temp = li.next();
			  cats += temp.getName() + ",";
		  }
		  */
	   StringTokenizer t = new StringTokenizer(cats, ",");

		  while( t.hasMoreElements() ){

			  String theCat= (String) t.nextToken();

			  mCats.put(theCat,  "MC_"+new java.util.Date().getTime() +"_"+ Math.random());

			  if( meeting.getMeetingPlanType()!=null && meeting.getCatTags()!=null){// && !mCatsPerType.get(meeting.getMeetingPlanType()).contains(theCat) ){

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

   java.util.Iterator itr_mCatsPerType = mCatsPerType.keySet().iterator();
   while( itr_mCatsPerType.hasNext() ){
	   String tp = (String) itr_mCatsPerType.next();
	   %>var <%=tp%> = [ <%
	   java.util.Set cats = (java.util.Set) mCatsPerType.get(tp);
	   java.util.Iterator itrCat = cats.iterator();
	   while( itrCat.hasNext() ){
		   String x = (String) itrCat.next();
		   %> "<%= mCats.get(x)%>" <%=itrCat.hasNext() ? "," : ""%> <%
	   }
	   %>];<%
   }


  java.util.Iterator itr_mTylesPerLevel = mTylesPerLevel.keySet().iterator();
  while( itr_mTylesPerLevel.hasNext() ){
	  String level = (String) itr_mTylesPerLevel.next();
	  %> var <%=level%> = [<%
	  java.util.Set types = (java.util.Set ) mTylesPerLevel.get(level);
	  java.util.Iterator itrTypes = types.iterator();
	  while( itrTypes.hasNext()){
		  String x = (String) itrTypes.next();
		  %> "<%= mTypes.get(x)%>" <%=itrTypes.hasNext() ? "," : ""%><%
	  }
	  %>];<%

  }
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
	  <p class="instruction columns small-22 small-centered">
	  <%= instruction %>
	  </p>
	  <div id="cngMeet"></div>



	 <!--  start carlos 1 -->

	 <div id="vtk-meeting-filter" class="content">

	<div class="sectionHeader" style="">
		<div class="column small-22 small-centered" style="display:table; padding-left:0;">
			<span class="vtk-green-box" style="">
			<span class="icon-search-magnifying-glass" style=""></span>
			</span>
			<p id="showHideReveal" class="hide-for-print close">FILTER MEETINGS BY TOPIC</p>
		</div>
	</div>

	<div class="vtk-meeting-group" style="">
		<div class="main-filter column small-22 small-centered" style="display:table; padding-left:0;">
			<div class="row">
				<div class="column small-24 medium-12">
					<div class="vtk-meeting-filter_title"><span>1.</span> Select your Girl Scout Level(s)</div>
					<div id="vtk-meeting-group-age" class="row">


	 <!-- end carlos 1 -->


		<%
		java.util.Iterator itrLevel= mLevel.keySet().iterator();
		while( itrLevel.hasNext()){
			String level =  (String)itrLevel.next();
			String id= (String) mLevel.get(level);
			%>
			<span class="container" style="clear:both;">
			<span class="terminal" data-price="<%if(level.contains("Daisy"))out.println("1");else if(level.contains("Brownie"))out.println("2");else if(level.contains("Junior"))out.println("3");else out.println(100);%>">
			<div class="small-24 medium-12 column">
			   <input type="checkbox" name="_tag_m" id="<%= id%>" value="<%=level %>"  <%=troop.getTroop().getGradeLevel().contains(level) ? "CHECKED" : "" %> onclick="doFilter(1)"/>
			   <label for="<%= id%>"><span></span><p><%=level %> </p></label>
			</div>
			</span>
			</span>
			<%
		}
		%>



	  <!-- carlos 2 start -->
					</div>
				</div>
				<div class="column small-24 medium-12">
					<div class="vtk-meeting-filter_title"><span>2.</span> Select the type of meeting plan you want </div>
					<div id="vtk-meeting-group-type" class="row">

	<!--  carlos 2 end  -->
		<%
		java.util.Iterator itrTypes= mTypes.keySet().iterator();
		while( itrTypes.hasNext()){
			String type =  (String)itrTypes.next();
			String id= (String) mTypes.get(type);
			%>
			<div class="small-24 medium-12 column <%= !itrTypes.hasNext() ? "end" : "" %>" style="min-height:60px;">
					<input type="radio" name="_tag_t" id="<%= id%>" value="<%=type %>"  onclick="doFilter(2)"/>
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
					<div class="vtk-meeting-filter_title"><span>3.</span> Select your badge categories</div>
					<div id="vtk-meeting-group-categories"  class="row  wrap-vtk-meeting-group-categories">

	  <!--  end carlos 3  -->

		<%
		java.util.Iterator itrCats= mCats.keySet().iterator();
		int index = 1;

		while( itrCats.hasNext()){
			String cat =  (String)itrCats.next();
			String id= (String) mCats.get(cat);
			%>
				


				<div class="small-24 medium-12 large-6 column <%= !itrCats.hasNext() ? "end" : "" %>"  style="min-height:70px">
					<input type="checkbox" name="_tag_c" id="<%= id%>" value="<%=cat %>"  onclick="doFilter(3)"/>
					<label for="<%= id%>"><span></span><p> <%=cat.replaceAll("_", " ")  %></p></label>
				</div>


			
		
			<%  } %>


	  <!--  carlos 4 start  -->


					</div>
				</div>
			</div>
		</div>


		<div class="list-of-buttons column small-22 small-centered" style="padding-left:0;">
			<div class="row">
				<div id="vtk-meeting-group-button" class="column small-24" style="padding:25px 0 25px 0;">
					<div id="vtk-meeting-group-button_cancel" class="button tiny ">CANCEL</div>
					<div id="vtk-meeting-group-button_ok" class="button tiny inactive-button">VIEW MEETING PLANS</div>
				</div>
			</div>
		</div>
	</div>



</div>


<div id="vtk-meeting-report" class="content" style="display:none;">
	<div class="main-report column small-22 small-centered" style="padding-left:0;">
		<div class="row">
			<div class="column small-24">
				<h6>Badge Meetings</h6>
			</div>
		</div>
	</div>

	<div class="main-report-search column small-22 small-centered" style="padding-left:0;">
		<div class="row">
			<div class="column small-24">
				<div class="no-plans column small-24 medium-8">

				</div>
				<div class="column small-12 medium-8">

					<div id="vtk-dropdown-filter-1" class="vtk-dropdown-check-box" data-input-name="value1">
						<div class="vtk-dropdown_main">
							<div class="selected-option">View GS Level</div>
							<span class="icon-arrow-css" style="">

							</span>
						</div>
						<ul class="vtk-dropdown_options">

						</ul>
					</div>

				</div>
				<div class="column small-12 medium-8">

					<div id="vtk-dropdown-filter-2" class="vtk-dropdown-check-box" data-input-name="value1">
						<div class="vtk-dropdown_main">
							<div class="selected-option">View category</div>
							<span class="icon-arrow-css" style="">

							</span>
						</div>
						<ul class="vtk-dropdown_options">

						</ul>
					</div>


				</div>
			</div>
		</div>
	</div>

</div>


<script type="text/javascript">

	// top-level namespace being assigned an object literal
	var gsusa = gsusa || {};

	// a convenience function for parsing string namespaces and
	// automatically generating nested namespaces
	function extendNS( ns, ns_string ) {
		var parts = ns_string.split('.'),
			parent = ns,
			pl, i;

		if (parts[0] == "gsusa") {
			parts = parts.slice(1);
		}

		pl = parts.length;
		for (i = 0; i < pl; i++) {
			//create a property if it doesnt exist
			if (typeof parent[parts[i]] == 'undefined') {
				parent[parts[i]] = {};
			}

			parent = parent[parts[i]];
		}

		return parent;
	}
	
	// Add the name space;
	extendNS(gsusa,'gsusa.component');

	gsusa.component = (function(){
		// (Css Selector)
		function dropDownCheckBox(selector,callbackObject){

			//get main element
			var $element = $(selector);

			//get target input form
			var inputName = $element.data('input-name');

			//get Default text
			var default_text = $element.children('.vtk-dropdown_main').children('.selected-option').html();

			//Toggle the Options box
			function toggle(){
				$element.children('.vtk-dropdown_options').toggle();
			}

			//add event listinert to the Icon
			$element.children('.vtk-dropdown_main').click(toggle);

			$element.children('.vtk-dropdown_options').find('input[type="checkbox"]').on('change', function(e){
				setTimeout(toggle,300);

			})

			$(document).click(function(e){
				if(!$element.find($(e.target)).length){
					 $element.children('.vtk-dropdown_options').hide();
				}
			})

		   }


		return {
			'dropDownCheckBox': dropDownCheckBox
		};
	})();

	var categoryCollectionsObj = {};

	var queryDown ={
		age: function(list){



			var x = list.map(function(e,i){
				var element = $(i);

				for (var ia = categoryCollectionsObj['age'].length - 1; ia >= 0; ia--) {
					var xy = element.attr('id').indexOf(categoryCollectionsObj['age'][ia]);
					if(xy>-1){
						return element;
					}
				};

			});

			 return x;
		},
		cat: function(list){
			var y = list.map(function(e,i){
				var element = $(i);

				for (var ib = categoryCollectionsObj['cat'].length - 1; ib >= 0; ib--) {
					var xyx = element.attr('id').indexOf(categoryCollectionsObj['cat'][ib]);
					if(xyx>-1){
						return element;
					}
				};
			});

			 return y;
		}		
	}

	function triggerOriginal(e){
		var type;

		var $type = $(this).parents('.vtk-dropdown-check-box');

		if($type.attr('id') === 'vtk-dropdown-filter-1'){
			type='age';
		}else{
			type ='cat';
		}
		
		var ide = $(this).data('id');
		var indxC = categoryCollectionsObj[type].indexOf(ide);

		if(indxC === -1){
			document.getElementById(ide).checked = true;
			categoryCollectionsObj[type].push(ide);
		}else{
			document.getElementById(ide).checked = false;
			document.getElementById(ide).removeAttribute('checked');
			categoryCollectionsObj[type].splice(indxC, 1);
			
		}

		$('.meeting-item').hide();
		$('.no-content').hide();
		$('#meetingSelect').show();



		if(type == 'age'){



			queryDown['age'](queryDown['cat']($('.meeting-item'))).each(function(){
				$(this).show();
			})



		}else{
			queryDown['cat'](queryDown['age']($('.meeting-item'))).each(function(){
				$(this).show();
			})
		}


		if($('.meeting-item:visible').length){
			$('.no-plans').html($('.meeting-item:visible').length + " Meeting Plan");
		}else{
			$('.no-plans').html("0  Meeting Plan");
			$('.no-content').show();
			$('#meetingSelect').hide();

		}
	}

	function createElement(el){
		var $input = $(el).find('input');
		var $complement = $(el).find('label');


		var $li = $('<li data-id="'+$input.attr('id') +'" ></li>');

		var $newInput = $('<input id="__'+$input.attr('id')+'" data-id="'+$input.attr('id')+'" type="checkbox">');
		var $newComplement =  $('<label for="__'+$input.attr('id')+'"><span></span><p>'+ $complement.text() +'</p></label>')

		$newInput.click(triggerOriginal)
		$li.append($newInput,$newComplement);

		// categoryCollections.push($input.attr('id'));

		return $li;
	}

	function renderElement(origin,target,sort){

		var node = $(target).find('.vtk-dropdown_options');
		var listNodes;
	
		if(sort){
			var orden =  ['Daisy', 'Brownie', 'Junior']
			listNodes =[];

			$.each($(origin).children(), function(indx,el){
				listNodes[orden.indexOf($(el).find('input').val())] = el;
			})
		}else{
			listNodes = $(origin).children();
		}

		$.each(listNodes, function(indx,el){
				node.append(createElement(el));
		})
		
	}

	gsusa.component.dropDownCheckBox('#vtk-dropdown-filter-1');
	gsusa.component.dropDownCheckBox('#vtk-dropdown-filter-2');

	$(function(){
		var age = $('#vtk-meeting-group-age');
		var type = $('#vtk-meeting-group-type');
		var categories = $('#vtk-meeting-group-categories');

		var ageList, typeList, categoriesList , ageLength , typeLength , categoriesLength;

		var button ={
			ok: $('#vtk-meeting-group-button_ok'),
			cancel: $('#vtk-meeting-group-button_cancel')
		}





		function eachList(which,name){
				categoryCollectionsObj[name] =[];

			$.each(which, function(i,e){
				
				if($(this)[0].checked){
					$('input[data-id="'+$(this).attr('id')+'"]')[0].checked = true;
					$('input[data-id="'+$(this).attr('id')+'"]').parent().show();
					categoryCollectionsObj[name].push($(this).attr('id'));
				}else{
					$('input[data-id="'+$(this).attr('id')+'"]')[0].checked =false;
					$('input[data-id="'+$(this).attr('id')+'"]')[0].removeAttribute('checked');
					$('input[data-id="'+$(this).attr('id')+'"]').parent().hide();
				}
			});
		}

		function onChangeDo(e){
		  // 
		  $('#vtk-dropdown-filter-2').show();
 
			ageList = age.find('input[type="checkbox"]');
			typeList = type.find('input[type="radio"]');
			categoriesList = categories.find('input[type="checkbox"]');

			ageLength = age.find('input[type="checkbox"]:checked').length;
			typeLength = type.find('input[type="radio"]:checked').length;
			categoriesLength = categories.find('input[type="checkbox"]:checked').length;

			var visibleList =$('.wrap-vtk-meeting-group-categories').children('div').filter(function() { return $(this).css("display") == "block" }).length;
	 
			if( ageLength > 0 &&  typeLength > 0){		  
				if(visibleList > 0){
					$('.list-of-categories').slideDown();
					button.ok.addClass('inactive-button');
				}else{
					$('.list-of-categories').slideUp();
					button.ok.removeClass('inactive-button');  
				}
			}else{
				if(visibleList === 0){
				    $('.list-of-categories').slideUp();
				     button.ok.addClass('inactive-button');
				 	$("#meetingSelect").hide();
				}
				// button.ok.removeClass('inactive-button');
			}

			if( ageLength > 0 &&  typeLength > 0 && categoriesLength > 0){
				button.ok.removeClass('inactive-button');
			}
		}

		$('#vtk-meeting-filter').find('#showHideReveal').stop().click(function(e){
			$(this).toggleClass('open')
			$('.vtk-meeting-group').slideToggle();
			$('.vtk-dropdown_options').hide();
			$('#vtk-meeting-report').hide();
		})

		age.find('input').on('change', onChangeDo);
		type.find('input').on('change', onChangeDo);
		categories.find('input').on('change', onChangeDo);

		button.ok.on('click',function(e){
			if(!$(this).hasClass('inactive-button')){
				eachList(ageList,'age');
				eachList(categoriesList,'cat');
				var noPlans = ($('#meetingSelect').children('.meeting-item').filter(function() { return $(this).css("display") == "block" }).length) ? $('#meetingSelect').children('.meeting-item').filter(function() { return $(this).css("display") == "block" }).length + ' Meeting Plan' : '0 Meeting Plan';


				 $('.no-plans').html(noPlans);
				 		$('.no-content').hide();
				$('#vtk-meeting-report').slideDown();

				$('.vtk-meeting-group').slideUp();
				$('#vtk-meeting-filter').find('#showHideReveal').toggleClass('open');


				$("#meetingSelect").slideDown();

				var optionsL = $('#vtk-dropdown-filter-2').find('.vtk-dropdown_options').children('li').filter(function() { return $(this).css("display") == "block" || $(this).css("display") == 'list-item' }).length

				if(optionsL > 0){
					$('#vtk-dropdown-filter-2').show();
				}else{
					$('#vtk-dropdown-filter-2').hide();
				}
			}
		});

		button.cancel.on('click',function(e){
			resetVtkFilters();
			button.ok.addClass('inactive-button');

			$('#vtk-meeting-filter').find('input')
			.not(':button, :submit, :reset, :hidden')
			.val('')
			.removeAttr('checked')
			.removeAttr('selected');

			$("#meetingSelect").slideUp();
		});

		renderElement('#vtk-meeting-group-age','#vtk-dropdown-filter-1',true);
		renderElement('#vtk-meeting-group-categories','#vtk-dropdown-filter-2',false);
	});


var meetingLibraryModal = new ModalVtk('meeting-library-modal');


	meetingLibraryModal.init();
</script>


				
		<div class="no-content column small-24" style="display:none;text-align:center; padding:80px 0" >
		  	<h4>No Meetings match your filter criteria</h4>
		</div>
	  <!--  carlos 4 end  -->
	  <div id="meetingSelect" class="meetingSelect column small-22 small-centered" style="display:none;">
		  <!--<div class="row">-->





	<div style="position: absolute;width: 100%;height: inherit;top: 0;bottom: 0; overflow: hidden;">
		<div class="vtk-float-submit">
		<input class="button tiny" type="button" value="CANCEL" onclick="closeModal()"/>

		<input class="button tiny" type="button" value="CLEAR" onclick="clearList()" />

	  <%if( request.getParameter("newCustYr")!=null){ %>
		   <input class="button tiny inactive-button add-to-year-plan" type="button" value="ADD TO YEAR PLAN"  onclick="createCustPlan(null)"/>
	  <%}else{ %>
		   <input class="button tiny inactive-button add-to-year-plan" type="submit"  value="ADD TO YEAR PLAN" />
	  <%}//end else %> 
	</div>  
	</div>

		  <%

		  //sort meetings by this specific order: dAisy > bRownie > jUnior
		  if (meetings != null) {
			  
		  /*
			  Collections.sort(meetings, new Comparator<Meeting>() {
				  public int compare(Meeting o1, Meeting o2) {
					  //return o1.getLevel().compareTo(o2.getLevel());
					  return Character.compare(o1.getLevel().charAt(1), o2.getLevel().charAt(1)); 
				  }
			  });
		  */
		  
		  /*
			  Collections.sort(meetings, new Comparator() {

			        public int compare(Object o1, Object o2) {

			            String x1 = ((Meeting) o1).getLevel().charAt(1) +"";
			            String x2 = ((Meeting) o2).getLevel().charAt(1) +"";
			            int sComp = x1.compareTo(x2);

			            if (sComp != 0) {
			               return sComp;
			            } else {
			               String x3 = ((Meeting) o1).getName();
			               String x4 = ((Meeting) o2).getName();
			               return x3.compareTo(x4);
			            }
			    }});
		  
		  */
		  
		  /*
			  Collections.sort(meetings,
					  java.util.Comparator.comparing(p1 -> ((Meeting)p1).getLevel().charAt(1))
		                     .thenComparing(p1 -> ((Meeting)p1).getName()) );
		                     //.thenComparing(p1 -> p1.getArtist()));
		  */
		  
		  
		    /*
              Collections.sort(meetings,
                      java.util.Comparator.comparing(Meeting::getLevel)
                             .thenComparing(Meeting::getName) );
                          */
             
             meetings = VtkUtil.sortMeetings( meetings );
          
		  
		  
		  }

		  String currentLevel = "";

		  for(int i=0;i<meetings.size();i++){
			Meeting meeting = meetings.get(i);
			if(!meeting.getLevel().equals(currentLevel)){
				currentLevel=meeting.getLevel();
				%>
				<div style="display:none;" class="meeting-age-separator column small-24 levelNav_<%= currentLevel %>" id="levelNav_<%= currentLevel %>">
                    <%= currentLevel %>
                </div>

				<% 
		   }
%>


			<div class="meeting-item column small-24" style="display:none;" id="TR_TAGS_;<%=mLevel.get(meeting.getLevel()) %>;<%=meeting.getMeetingPlanType()==null ? "" : mTypes.get(meeting.getMeetingPlanType()) %>;<%= meeting.getLevel()%>;
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
						  <p class="title"><%=meeting.getName()%></p>
						 
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
							<input type="checkbox" name="addMeetingMulti" id="<%=meeting.getPath()%>" 
							value="<%=meeting.getPath()%>"/>
							<label for="<%=meeting.getPath()%>"><span></span>

							<%if( request.getParameter("newCustYr")!=null){ %>
								   <p onclick="createCustPlan('<%=meeting.getPath()%>')">Select Meeting</p>
							  <%}else{ %>
								   <p onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</p>
							  <%}//end else %>


							</label>
							</div>
						   

							
							 
				
							
				<% } else {%>
				  <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="10" height="15"> <i class="included">Included in Year Plan</i>

					<%
					if( !futureMeetings.contains(meeting.getId().toLowerCase() )  && reAddMeetings.contains( meeting.getId().toLowerCase() ) ){%>
						 <a onclick="cngMeeting('<%=meeting.getPath()%>')">Re-add meeting</a>
					<%} %>
				<% }%>

						  </div>
						 </div>
					</div>
					 <div class="column small-24 medium-4">
									<div style="display:table;;min-height:110px">
					   <div style="display:table-cell;height:inherit;vertical-align:middle; text-align:center;">

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
				</div>
									</div>

					</div>
				</div>
			</div>
		<% } %>

		  </div>

	  </div>

  </div>
  	
	  
</form>
<script>
	function closeModal(){
		$('#gsModal').find('a').children('i').trigger('click');
	}


	function clearList(){
		var arraylist =  Array.prototype.slice.call(document.getElementsByName('addMeetingMulti'))

		arraylist.forEach(function(element){
			element.removeAttribute('checked');
			element.checked=false;
		})

		checkAddMeetingMulti();
	}

	function checkAddMeetingMulti(){
		var _dd = [].slice.call(document.getElementsByName('addMeetingMulti'));

		var _hasOneCheck = _dd.some(function(el){
			return el.checked;
		});
	
		if(_hasOneCheck){
			$('.add-to-year-plan').removeClass('inactive-button');
		}else{
			$('.add-to-year-plan').addClass('inactive-button');
		}
	}

	$('[name=addMeetingMulti]').on('change',checkAddMeetingMulti)



	function doFilter(clickSrc){

		$(this).attr('checked', true);

		
		if( clickSrc==1 ){clearFilterTypes(); clearFilterCats();}
		if( clickSrc==2 ){ clearFilterCats();}
		clearResults();
		runFilterType();

		var _levels = document.getElementsByName("_tag_m");
		var _types = document.getElementsByName("_tag_t");
		var _cats = document.getElementsByName("_tag_c");

	   var tt = document.getElementById("meetingSelect");
	//    var t= tt.getElementsByTagName("tr");
		var t = $(tt).find('.meeting-item');


	   for(var i=0;i<t.length;i++){ //each meeting
		// var x= t[i];

		var x = t.eq(i)[0];

		 var isShowLevel = isShowMeeting( _levels, x, false, 'level');

		 var isShowType = false;
		 if( _types ==null || _types.length<=0) {
			 isShowType= true;
		 }else{
			 isShowType = isShowMeeting( _types, x, true,'type');
		 }

		 var isShowCats = false;

		 if( _cats==null || _cats.length<=0){
			isShowCats= true;
		 }else{


	
			 isShowCats = isShowMeeting( _cats, x, true,'cats');
		 }

		 
		 if( isShowLevel && isShowType && isShowCats ){
			 x.style.display = "inline";
			 
			 
			

			document.getElementById("levelNav_"+ x.id.split(';')[3]).style.display = "inline";
		 }
	   }
	}


	function isShowMeeting(els, x, isAllEmptyOk, catTest){
		var countChecked= 0;


		for(var y = 0; y < els.length; y++){ //each filter
	
			if( els[y].checked ){ //filter checked
				countChecked++;
	
			   if( x.id.indexOf( els[y].id )!=-1 ){ //filter id found in meeting

				 return true;
			   }
			}
		}
		if( countChecked==0 && isAllEmptyOk ) return true;
		return false;
	}

	function resetVtkFilters(){
		 var els= document.getElementsByName("_tag_m");
		 for(var y = 0; y < els.length; y++){
			 els[y].checked = false;
		 }
		 clearFilterTypes();
		 clearFilterCats();
		 clearResults();
	}




	function clearResults(){

		var tt = document.getElementById("meetingSelect");
		// var t= tt.getElementsByTagName("tr");
		var t = $(tt).find('.meeting-item');
		for(var i=0;i<t.length;i++){ //each meeting
		//    var x= t[i];
		var x = t.eq(i);
		//    x.style.display = "none"
		x.hide();
		//    x.parentElement.style.display = 'none';

		}
		
		clearLevelHeader();
	}
	
	function clearLevelHeader(){
		
		<%
		  java.util.Iterator itr44= mLevel.keySet().iterator();
		  while( itr44.hasNext() ){
			 %> document.getElementById("levelNav_<%=itr44.next()%>" ).style.display ='none'; <%
		  }
		%>
	}


	function clearFilterTypes(){

		<%
		itrTypes= mTypes.keySet().iterator();
		while( itrTypes.hasNext()){
			String type =  (String)itrTypes.next();
			String id= (String) mTypes.get(type);
			%>

			document.getElementById("<%= id%>").checked = false;
			document.getElementById("<%= id%>").removeAttribute('checked');
			document.getElementById("<%= id%>").style.display='none';
			document.getElementById("<%= id%>").parentElement.style.display='none';
			<%
		}
		%>
	}

	function clearFilterCats(){
		<%
		itrCats= mCats.keySet().iterator();
		while( itrCats.hasNext()){
			String cat =  (String)itrCats.next();
			String id= (String) mCats.get(cat);
			%>

			document.getElementById("<%= id%>").checked=false;
			document.getElementById("<%= id%>").removeAttribute('checked');
			document.getElementById("<%= id%>").style.display='none';
			document.getElementById("<%= id%>").parentElement.style.display = 'none';
			//document.getElementById("<%= id%>").removeAttribute('data-v');


			<%
		}
		%>
	}

	function runFilterType(){
		<%
		itrLevel= mLevel.keySet().iterator();
		while( itrLevel.hasNext()){
			String level =  (String)itrLevel.next();
			String id= (String) mLevel.get(level);
			%>
			if( document.getElementById("<%= id%>").checked ){

				for(var y = 0; y < <%=level%>.length; y++){
					document.getElementById(<%=level%>[y]).style.display ='inline';
				//document.getElementById(<%=level%>[y]).setAttribute('data-v',true);

					document.getElementById(<%=level%>[y]).parentElement.style.display = 'inline';
				}
			}
			<%
		}//edn while
		%>


		<%
		itrTypes= mTypes.keySet().iterator();
		while( itrTypes.hasNext()){
			String tp =  (String)itrTypes.next();
			String id= (String) mTypes.get(tp);
			%>
			if( document.getElementById("<%= id%>").checked ){
			  if(typeof <%=tp%> != 'undefined'){
				for(var y = 0; y < <%=tp%>.length; y++){
					document.getElementById(<%=tp%>[y]).style.display ='inline';
					document.getElementById(<%=tp%>[y]).parentElement.style.display = 'inline';

				}//end for
			   }//end if
			}//edn if
			<%
		}//edn while
		%>
	}

	//init
	function initMeetings(){
		clearFilterTypes();
		clearFilterCats();
		doFilter();


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
		  				location.reload();
			});

		
		
	}
	
	
	function checkIfOnWasClickedX(configObject){

		var  _arrayList = [], v, _hasOne;
		var nodelist = document.getElementsByName("addMeetingMulti");;

		_arrayList = Array.prototype.slice.call(nodelist);

		_hasOne = _arrayList.some(function(el){
			return el.checked;
		})

		if(_hasOne){
			configObject.yes();
		}else{
			configObject.no();
		}
	}

	
	
	initMeetings();

	$('.container').sort(function (a, b) {
		  return $(a).find('.terminal').data('price') - $(b).find('.terminal').data('price');
		}).each(function (_, container) {
		  $(container).parent().append(container);
	});



	// })
</script>


<%}catch(Exception e){e.printStackTrace();}%>