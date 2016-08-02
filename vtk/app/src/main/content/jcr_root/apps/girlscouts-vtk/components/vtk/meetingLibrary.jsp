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


  <%
  java.util.Map<String, String> mLevel= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mTypes= new java.util.TreeMap<String, String>();
  java.util.Map<String, String> mCats= new java.util.TreeMap<String, String>();

  java.util.Map<String, java.util.Set> mTylesPerLevel = new java.util.TreeMap();
  java.util.Map<String, java.util.Set> mCatsPerType  = new java.util.TreeMap();
  if( meetings!=null)
   for(int i=0;i<meetings.size();i++){
      Meeting meeting = meetings.get(i);

      if( meeting.getLevel()!=null && !mLevel.containsKey( meeting.getLevel() ) ){
          mLevel.put(meeting.getLevel(), "ML_"+new java.util.Date().getTime() +"_"+ Math.random());
          mTylesPerLevel.put(meeting.getLevel(), new java.util.HashSet<String>() );
      }


      String cats= meeting.getCatTags();
      if( cats!=null ){
    	  cats= cats+",";
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


	    		   mCatsPerType.get( meeting.getMeetingPlanType() ).add( theCat );
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
  <div class="scroll" style="">
    <div class="content meeting-library row">
      <p class="instruction columns small-24"><%= instruction %></p>
      <div id="cngMeet"></div>



     <!--  start carlos 1 -->

     <div id="vtk-meeting-filter" class="content">

    <div class="sectionHeader" style="">
        <div class="column small-22 small-centered" style="display:table; padding-left:0;">
            <span class="vtk-green-box" style="">
            <span class="icon-search-magnifying-glass" style=""></span>
            </span>
            <p id="showHideReveal" onclick="" class="hide-for-print close">FILTER MEETINGS BY TOPIC</p>
        </div>
    </div>

    <div class="vtk-meeting-group" style="display:none">
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
        	<div class="small-24 medium-12 large-8 column">
        	   <input type="checkbox" name="_tag_m" id="<%= id%>" value="<%=level %>"  onclick="doFilter(1)"/>
        	   <label for="<%= id%>"><span></span><p><%=level %> </p></label>
        	</div>
        	<%
        }
        %>



      <!-- carlos 2 start -->
                    </div>
                </div>
                <div class="column small-24 medium-12">
                    <div class="vtk-meeting-filter_title"><span>2.</span> Select the type of meeting plan you want</div>
                    <div id="vtk-meeting-group-type" class="row">

    <!--  carlos 2 end  -->
        <%
        java.util.Iterator itrTypes= mTypes.keySet().iterator();
        while( itrTypes.hasNext()){
            String type =  (String)itrTypes.next();
            String id= (String) mTypes.get(type);
            %>
            <div class="small-24 medium-12 large-8 column">
                    <input type="radio" name="_tag_t" id="<%= id%>" value="<%=type %>"  onclick="doFilter(2)"/>
                    <label for="<%= id%>"><span></span><p> <%=type %> </p></label>
           </div>
            <%
        }
        %>



      <!--  carlos 3 start -->


                    </div>
                </div>
            </div>
        </div>

        <div class="list-of-categories column small-22 small-centered" style="display:none; padding-left:0;">
            <div class="row">
                <div class="column small-24">
                    <div class="vtk-meeting-filter_title"><span>3.</span> Select your badge categories</div>
                    <div id="vtk-meeting-group-categories" class="row">

      <!--  end carlos 3  -->
        <%
        java.util.Iterator itrCats= mCats.keySet().iterator();
        while( itrCats.hasNext()){
            String cat =  (String)itrCats.next();
            String id= (String) mCats.get(cat);
            %>
            <div class="small-24 medium-12 large-4 column <%= !itrCats.hasNext() ? "end" : "" %>">
            <input type="checkbox" name="_tag_c" id="<%= id%>" value="<%=cat %>"  onclick="doFilter(3)"/>
            <label for="<%= id%>"><span></span><p> <%=cat %></p></label>
            </div>
            <%
        }
        %>


      <!--  carlos 4 start  -->


                    </div>
                </div>
            </div>
        </div>


        <div class="list-of-buttons column small-22 small-centered" style="display:none; padding-left:0;">
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
            $element.children('.vtk-dropdown_main').children('.icon-arrow-css').click(toggle);

            $element.children('.vtk-dropdown_options').find('input[type="checkbox"]').on('change', function(e){
                setTimeout(toggle,300);
                // console.log(e);
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

    function triggerOriginal(e){


            $(document.getElementById($(this).data('id'))).trigger('click');

            if($(this).find('input')[0].checked){
                document.getElementById($(this).data('id')).checked = true;
                }else{
                    document.getElementById($(this).data('id')).checked = false;
                document.getElementById($(this).data('id')).removeAttribute('checked');


            }

    }

    function createElement(el,filter){
        var $input = $(el).find('input');
        var $complement = $(el).find('label');

        var $li = $('<li data-id="'+$input.attr('id') +'" ></li>');

        var $newInput = $('<input id="__'+$input.attr('id')+'" data-id="'+$input.attr('id')+'" type="checkbox">');
        var $newComplement =  $('<label for="__'+$input.attr('id')+'"><span></span><p>'+ $complement.text() +'</p></label>')

        $li.click(triggerOriginal)
        $li.append($newInput,$newComplement);

        return $li;
    }

    function renderElement(origin,target,filter){
        var node = $(target).find('.vtk-dropdown_options');
         $.each($(origin).children(), function(indx,el){
            node.append(createElement(el,filter));
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

        function doThis(e){
        }

        function onChangeBack(e){
        }

        function eachList(which){
            $.each(which, function(i,e){
                // console.log($(this).attr('id'), $('input[data-id="'+$(this).attr('id')+'"]'))
                if($(this)[0].checked){

                    // $('input[data-id="'+$(this).attr('id')+'"]').trigger('change')

                    $('input[data-id="'+$(this).attr('id')+'"]')[0].checked = true;
                    $('input[data-id="'+$(this).attr('id')+'"]').parent().show();
                }else{
                    $('input[data-id="'+$(this).attr('id')+'"]')[0].checked =false;

                    $('input[data-id="'+$(this).attr('id')+'"]')[0].removeAttribute('checked');
                    $('input[data-id="'+$(this).attr('id')+'"]').parent().hide();
                }
            });
        }

        function onChangeDo(e){
            ageList = age.find('input[type="checkbox"]');
            typeList = type.find('input[type="radio"]');
            categoriesList = categories.find('input[type="checkbox"]');

            ageLength = age.find('input[type="checkbox"]:checked').length;
            typeLength = type.find('input[type="radio"]:checked').length;
            categoriesLength = categories.find('input[type="checkbox"]:checked').length;

            var visibleList;

            if( ageLength > 0 &&  typeLength > 0){
                visibleList = $('#vtk-meeting-group-categories').children('div').not(':hidden').length;
                $('.list-of-categories').slideDown();
                $('.list-of-buttons').slideDown();
                if(categoriesLength > 0 && visibleList > 0){
                    button.ok.removeClass('inactive-button');
                }else{
                    button.ok.addClass('inactive-button');
                }
            }else{
                visibleList = $('#vtk-meeting-group-categories').children('div').not(':hidden').length;
                if(visibleList == 0){
                    $('.list-of-categories').slideUp();
                    $('.list-of-buttons').slideUp();
                }
                button.ok.addClass('inactive-button');
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

                eachList(ageList);
                eachList(categoriesList);
                var noPlans = ($('#meetingSelect').children('.meeting-item').not(':hidden').length) ? $('#meetingSelect').children('.meeting-item').not(':hidden').length + ' Meeting Plan' : '0 Meeting Plan';
                 $('.no-plans').html(noPlans);
                $('#vtk-meeting-report').slideDown();
                $('.vtk-meeting-group').slideUp();
                $('#vtk-meeting-filter').find('#showHideReveal').toggleClass('open');
            }
        });

        button.cancel.on('click',function(e){
            resetVtkFilters();
            $('#vtk-meeting-filter').find('input')
            .not(':button, :submit, :reset, :hidden')
            .val('')
            .removeAttr('checked')
            .removeAttr('selected');
        });

        renderElement('#vtk-meeting-group-age','#vtk-dropdown-filter-1',1);
        renderElement('#vtk-meeting-group-categories','#vtk-dropdown-filter-2',3);
    });

</script>

      <!--  carlos 4 end  -->
      <div id="meetingSelect" class="meetingSelect column small-22 small-centered" >
          <!--<div class="row">-->


          <%

          //sort meetings by meeting name
          if( meetings !=null ){
              Collections.sort(meetings, new Comparator<Meeting>() {
                  public int compare(Meeting o1, Meeting o2) {
                      return o1.getLevel().compareTo(o2.getLevel());
                  }
              });
          }

          String currentLevel = "";

          for(int i=0;i<meetings.size();i++){
            Meeting meeting = meetings.get(i);
            if(!meeting.getLevel().equals(currentLevel)){
                currentLevel=meeting.getLevel();
%>
                <div class="meeting-age-separator small-24 column">
                    <%= meeting.getLevel() %>
                </div>
<%
            }
%>
            <div class="meeting-item column small-24" style="display:none;" id="TR_TAGS_;<%=mLevel.get(meeting.getLevel()) %>;<%=meeting.getMeetingPlanType()==null ? "" : mTypes.get(meeting.getMeetingPlanType()) %>;
            <%
            if(meeting.getMeetingPlanType()!=null  && meeting.getCatTags()!=null){
            	java.util.Set cats = mCatsPerType.get(meeting.getMeetingPlanType());
            	if( cats!=null){
	                java.util.Iterator itrCat = cats.iterator();
	                while( itrCat.hasNext() ){
	                    String x = (String) itrCat.next();
	                    if(!meeting.getCatTags().contains( x ) )continue;
	                    %><%= mCats.get(x)%><%=itrCat.hasNext() ? ";" : ""%><%
	                }
            	}
            }
            %>
            ">
                <div class="row">
                    <div class="column small-24 medium-16">
                          <p class="title"><%=meeting.getName()%></p>
                         <p class="tags" style="color:red;"> LEVEL:<%=meeting.getLevel() %> TYPE: <%=meeting.getMeetingPlanType() %> CATS: <%=meeting.getCatTags() %>
                          </p>
                        <p class="blurb"><%=meeting.getBlurb() %></p>
                    </div>
                     <div class="column small-24 medium-4">
                          <% if( !myMeetingIds.contains( meeting.getId().trim().toLowerCase()) ) { %>
                  <a onclick="cngMeeting('<%=meeting.getPath()%>')">Select Meeting</a>
                <% } else {%>
                  <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/check.png" width="10" height="15"> <i class="included">Included in Year Plan</i>

                    <%
                    if( !futureMeetings.contains(meeting.getId().toLowerCase() )  && reAddMeetings.contains( meeting.getId().toLowerCase() ) ){%>
                         <a onclick="cngMeeting('<%=meeting.getPath()%>')">Re-add meeting</a>
                    <%} %>
                <% }%>
                    </div>
                     <div class="column small-24 medium-4">
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
        <% } %>

          </div>

      </div>



            <!--<table id="meetingSelect" class="meetingSelect">
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
            <tr style="display:none;" id="TR_TAGS_;<%=mLevel.get(meeting.getLevel()) %>;<%=meeting.getMeetingPlanType()==null ? "" : mTypes.get(meeting.getMeetingPlanType()) %>;
            <%
            if(meeting.getMeetingPlanType()!=null  && meeting.getCatTags()!=null){
            	java.util.Set cats = mCatsPerType.get(meeting.getMeetingPlanType());
            	if( cats!=null){
	                java.util.Iterator itrCat = cats.iterator();
	                while( itrCat.hasNext() ){
	                    String x = (String) itrCat.next();
	                    if(!meeting.getCatTags().contains( x ) )continue;
	                    %><%= mCats.get(x)%><%=itrCat.hasNext() ? ";" : ""%><%
	                }
            	}
            }
            %>
            ">
                <td>
                        <p class="title"><%=meeting.getName()%></p>
                         <p class="tags" style="color:red;"> LEVEL:<%=meeting.getLevel() %> TYPE: <%=meeting.getMeetingPlanType() %> CATS: <%=meeting.getCatTags() %>
                          </p>
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
      </table>-->


    <!--</div>-->
  </div>

<script>
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
    // console.log("chkCats....");
         if( _cats==null || _cats.length<=0){
        	isShowCats= true;
         }else{

    //console.log("_____________chkCatssss: "+ x.id);
        	 isShowCats = isShowMeeting( _cats, x, true,'cats');
         }

         //console.log("test: "+ isShowLevel +":"+ isShowType +":"+ isShowCats );
         if( isShowLevel && isShowType && isShowCats ){
        	 x.style.display = "inline";
         }
       }
    }


    function isShowMeeting(els, x, isAllEmptyOk, catTest){
    	var countChecked= 0;

    //console.log("* size: "+ els.length);
    	for(var y = 0; y < els.length; y++){ //each filter
    //console.log("IsChecked: "+ els[y].checked);
            if( els[y].checked ){ //filter checked
            	countChecked++;
    //if( catTest=='cats'){console.log( "compared: "+x.id+" : " +els[y].id);}
               if( x.id.indexOf( els[y].id )!=-1 ){ //filter id found in meeting
    //if( catTest=='cats'){console.log( "found...");    }

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
    }


    function clearFilterTypes(){
   if(true)return; //D Kia
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

    initMeetings();





    // })
</script>