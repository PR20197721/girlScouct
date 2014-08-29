

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.jeditable.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/ckeditor/ckeditor.js"></script>

<script type="text/javascript" charset="utf-8">


(function($) {
	$.generateId = function() {
		return arguments.callee.prefix + arguments.callee.count++;
	};
	$.generateId.prefix = 'jq$';
	$.generateId.count = 0;

	$.fn.generateId = function() {
		return this.each(function() {
			this.id = $.generateId();
		});
	};
})(jQuery);


(function($) {
$.editable.addInputType('ckeditor', {
    /* Use default textarea instead of writing code here again. */
    //element : $.editable.types.textarea.element,
    element : function(settings, original) {
        /* Hide textarea to avoid flicker. */
        var textarea = $('<textarea>').css("opacity", "0").generateId();
        if (settings.rows) {
            textarea.attr('rows', settings.rows);
        } else {
            textarea.height(settings.height);
        }
        if (settings.cols) {
            textarea.attr('cols', settings.cols);
        } else {
            textarea.width(settings.width);
        }
        $(this).append(textarea);
        return(textarea);
    },
    content : function(string, settings, original) { 
        /* jWYSIWYG plugin uses .text() instead of .val()        */
        /* For some reason it did not work work with generated   */
        /* textareas so I am forcing the value here with .text() */
        $('textarea', this).text(string);
    },
    plugin : function(settings, original) {
        var self = this;
        if (settings.ckeditor) {
            setTimeout(function() { CKEDITOR.replace($('textarea', self).attr('id'), settings.ckeditor); }, 0);
        } else {
            setTimeout(function() { CKEDITOR.replace($('textarea', self).attr('id')); }, 0);
        }
    },
    submit : function(settings, original) {
        $('textarea', this).val(CKEDITOR.instances[$('textarea', this).attr('id')].getData());
	CKEDITOR.instances[$('textarea', this).attr('id')].destroy();
    }
});
})(jQuery);


$(function() {
        
  $(".editable_select").editable("<?php print $url ?>save.php", { 
    indicator : '<img src="img/indicator.gif">',
    data   : "{'Lorem ipsum':'Lorem ipsum','Ipsum dolor':'Ipsum dolor','Dolor sit':'Dolor sit'}",
    type   : "select",
    submit : "OK",
    style  : "inherit",
    submitdata : function() {
      return {id : 2};
    }
  });
  $(".editable_select_json").editable("<?php print $url ?>save.php", { 
    indicator : '<img src="img/indicator.gif">',
    loadurl : "<?php print $url ?>json.php",
    type   : "select",
    submit : "OK",
    style  : "inherit"
  });
  $(".editable_textarea").editable("/content/girlscouts-vtk/controllers/vtk.controller.html", { 
      indicator : "Saving....",
      type   : 'ckeditor',
      submitdata: { _method: "put" ,mid: "<%=meeting.getUid()%>"},
      select : true,
      submit : 'OK',
      cancel : 'cancel',
      cssclass : "editable",
      onblur: 'ignore',
      tooltip: "Click to edit...",
      id   : 'editMeetingName',
      name : 'newvalue',
      
      ckeditor : {
    	  toolbar:
    	  [
['Bold','Italic','Underline','Strike','-','Superscript','Format'],
['NumberedList','BulletedList','-','Outdent','Indent','Blockquote'],
['JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock'],
['Link','Unlink']
    	  ],
    	  height: 260,
    	  startupFocus: true
    	  } 
      
      
      
  });
  $(".editable_textile").editable("<?php print $url ?>save.php?renderer=textile", { 
      indicator : "<img src='img/indicator.gif'>",
      loadurl   : "<?php print $url ?>load.php",
      type      : "textarea",
      submit    : "OK",
      cancel    : "Cancel",
      tooltip   : "Click to edit..."
  });
  
  $(".click").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Click to edit...",
      style  : "inherit"
  });
  $(".dblclick").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Doubleclick to edit...",
      event     : "dblclick",
      style  : "inherit"
  });
  $(".mouseover").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      tooltip   : "Move mouseover to edit...",
      event     : "mouseover",
      style  : "inherit"
  });
  
  /* Should not cause error. */
  $("#nosuch").editable("<?php print $url ?>echo.php", { 
      indicator : "<img src='img/indicator.gif'>",
      type   : 'textarea',
      submit : 'OK'
  });

});
</script>

<style type="text/css">
#sidebar {
  width: 0px;
}

#content {
  width: 770px;
}

.editable input[type=submit] {
  color: #F00;
  font-weight: bold;
}
.editable input[type=button] {
  color: #0F0;
  font-weight: bold;
}

</style>






<!-- apps/girlscouts-vtk/components/vtk/include/viewYearPlanMeeting.jsp -->
<br/>
<div class="row meetingDetailHeader">
	<div class="small-24 medium-8 large-7 columns">
		<table class="planSquareWrapper">
			<tr>
<%if( prevDate!=0 ){ %>
				<td class="planSquareLeft"><a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"><img width="20" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/previous.png"/></a></td>
<%} %>
				<td class="planSquareMiddle">
		<div class="planSquare">
<%
		if( user.getYearPlan().getSchedule()!=null ) {
%>
			<div class="count"><%= meetingCount %></div>
<%
		}
                if (isCanceled) {
%>
			<div class="cancelled"><div class="cross">X</div></div>
<%
                }
%>
			<div class="date">
        <%if( user.getYearPlan().getSchedule()!=null ) {%>
				<div class="cal"><span class="month"><%= FORMAT_MONTH.format(searchDate)%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(searchDate)%><br/></span><span class="time hide-for-small"><%= FORMAT_hhmm_AMPM.format(searchDate)%></span></div>
        <%} else {%>
                                <div class="cal"><span class="month">Meeting<br/></span><span class="day"><%=meetingCount%></span></div>
        <%}%>
			</div>
		</div>
				</td>
<%if( nextDate!=0 ){ %>
				<td class="planSquareRight"><a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>"><img width="20" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/next.png"/></a></td>
<%} %>
			</tr>
		</table>
	</div>

        <div class="small-24 medium-10 large-12 columns">
		<h1>Meeting: <!-- %= meetingInfo.getName() % -->
		<span class="editable_textarea" id="editMeetingName"><%= meetingInfo.getName() %></span>
		 </h1>
		 
		 
				
				

		<!--  <%= meetingInfo.getAidTags() %> -->
<%
	Location loc = null;
	if( meeting.getLocationRef()!=null && user.getYearPlan().getLocations()!=null ) {
		for(int k=0;k<user.getYearPlan().getLocations().size();k++){
			if( user.getYearPlan().getLocations().get(k).getPath().equals( meeting.getLocationRef() ) ){
				loc = user.getYearPlan().getLocations().get(k);
			}
		}
	}
	if (loc != null) {
%>
			<p>Location: <%=loc.getName() %> - <a href="/content/girlscouts-vtk/controllers/vtk.map.html?address=<%= loc.getAddress()%>" target="_blank"><%=loc.getAddress() %></a></p>
<%
	} else {
%>
			<p><i>No location specified.</i></p>
<%
	}
	if( meeting.getCancelled()!=null && meeting.getCancelled().equals("true")){
%>
		<span class="alert">(Cancelled)</span>
<%
	}
%>
	</div>
        <div class="small-24 medium-6 large-5 columns linkButtonWrapper">
		<a href="#" class="mLocked button linkButton" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingLibrary.html?mpath=<%=meeting.getPath()%>&xx=<%=searchDate.getTime()%>', false, null, true)">replace this meeting</a>
		<br/>
<%
	String img= "";
	try{
		img= meeting.getRefId().substring( meeting.getRefId().lastIndexOf("/")+1).toUpperCase();
		if(img.contains("_") )img= img.substring(0, img.indexOf("_"));
%>
		<img  width="100" height="100" src="/content/dam/girlscouts-vtk/local/icon/meetings/<%=img%>.png" align="center"/>
<%
	}catch(Exception e){
		// no image
%>
		<i>No image available.</i>
<%
	}
%>
	</div>
</div>
<div class="row meetingDetailDescription">
	<div class="small-1 columns">&nbsp;</div>
        <div class="small-22 columns">
                <p>
             
               
               <%=meetingInfoItems.get("meeting short description").getStr() %>
                </p>
	</div>
        <div class="small-1 columns">&nbsp;</div>
</div>
<div class="row meetingDetailDescription linkButtonWrapper">
        <div class="small-8 columns"><a class="button linkButton tight" id="overviewButtonX" href="javascript:void(0)" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=meeting.getUid()%>&isOverview=true', true, 'Overview', false, true)">overview</a></div>
        <div class="small-8 columns"><a class="button linkButton tight" id="activityPlanButtonX" href="javascript:void(0)" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=meeting.getUid()%>&isActivity=true', true, 'Activity', false, true)">activity plan</a></div>
        <div class="small-8 columns"><a class="button linkButton tight" id="materialsListButton" href="javascript:void(0)" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=meeting.getUid()%>&isMaterials=true', true, 'Materials', false, true)">materials list</a></div>
</div>
<div class="row meetingDetailDescription">
        <div class="small-1 columns">&nbsp;</div>
        <div class="small-22 columns">
		<div id="m_overview" style="display:none;"></div>
		<div id="m_activities"  style="display:none;"></div>
        </div>
        <div class="small-1 columns">&nbsp;</div>
</div>
<script>
	$(function() {
                $( "#overviewButton" ).button().click(function( event ) {
			showIt('m_overview');
                });
                $( "#activityPlanButton" ).button().click(function( event ) {
			showIt('m_activities');
                });
	});
</script>
<div class="sectionHeader meetingAids">Meeting Aids</div>
<%
	String aidTags = meetingInfo.getAidTags();
	aidTags = (aidTags==null || "".equals(aidTags.trim())) ? "No tags." : aidTags.trim().toLowerCase();
%>
<p class="subSection" style="display:none;">Tags: <i><%=aidTags %></i></p>
<%
	if ( _aidTags  == null || _aidTags.size() == 0) {
%>
	<!--  p class="subSection" >No meetings aids found.</p -->
<%
	} else {
%>
<ul>
<%

if( _aidTags!=null )
 for(int i=0;i<_aidTags.size();i++){
        org.girlscouts.vtk.models.Asset asset = _aidTags.get(i);
        if( asset.getType(false)!=  org.girlscouts.vtk.dao.AssetComponentType.AID ) continue;
	String aidTitle = "Untitled";
	String aidDescription = "No description.";
	if (asset.getTitle() != null) {
                aidTitle = asset.getTitle();
	}
        if (asset.getDescription() != null) {
                aidDescription = asset.getDescription();
        }
%>
	<li><a href="<%=asset.getRefId()%>" target="_blank"><%=aidTitle %></a> - <%=aidDescription %></li>
<% 
 }
%>
</ul>
<%
	}
%>


<div class="sectionHeader">Meeting Agenda</div>

	<a href="javascript:void(0)" onclick="revertAgenda('<%=meeting.getPath()%>')"  class="mLocked">Revert to Original Agenda</a>


<p>Select an item to view details, edit duration, or delete. Drag and drop items to reorder them.</p>
<ul id="sortable" >
<%
	java.util.Calendar activSched= java.util.Calendar.getInstance();
	activSched.setTime( searchDate);
	int duration =0;
	for(int ii=0;ii< _activities.size();ii++){
		Activity _activity = _activities.get(ii);
%>
	<li value="<%=(ii+1)%>">
		<table class="plain agendaItem" width="100%">
			<tr>
<%
	if( !isLocked) {
%>

				<td class="agendaScroll"><img class="touchscroll" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/touchscroll-small.png" width="21" height="34"></td>
<%
		if( user.getYearPlan().getSchedule()!=null ){ 
%>
				<td class="agendaTime"><%=FORMAT_hhmm_AMPM.format(activSched.getTime()) %></td>   
<%
		}
	}
%>
				<td>
				
					<%if( !isLocked) {%>
						<a href="javascript:void(0)"  class="mLocked" onclick="loadModalPage('/content/girlscouts-vtk/controllers/vtk.meetingMisc.html?mid=<%=meeting.getUid()%>&isAgenda=<%=ii %>', true, 'Agenda')"><%=_activity.getName() %></a>
					<%}else{ %>
						<%=_activity.getName() %>
					<%} %>
					
				</td>
				<td class="agendaDuration"><%=_activity.getDuration() %></td>
			</tr>
		</table>
	</li>
<% 
		if( user.getYearPlan().getSchedule()!=null )
			activSched.add(java.util.Calendar.MINUTE, _activity.getDuration() );
		duration+= _activity.getDuration();
	}
%>
</ul>

<table class="plain">
	<tr>
		<td width="1000" align="left">
			<b>End <%int min= duration%60;%> <%=duration /60 >0 ? duration /60 +"hr" : ""%> <%= min<10 ? "0"+min : min%>min</b>
		</td>
	</tr>
</table>

<%
	if(false)
	 for(int ii=0;ii< _activities.size();ii++){ 
		Activity _activity = _activities.get(ii);
%>
		<%@include file="editActivity.jsp" %> 
<%
	}
	
%>

  <!--  
	<input type="button" name="" value="Add Agenda Items" onclick="addCustAgenda()"  class="mLocked button linkButton"/>
-->
    
<a href="javascript:void(0)" onclick="loadModal('#newMeetingAgenda', true, 'Agenda', false);">Add Agenda Items</a>


<div id="newMeetingAgenda" style="display:none;">
<% if(true){// user.getYearPlan().getSchedule() !=null){ %>
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
	
	<%if( activSched.getTime() !=null && activSched.getTime().after(new java.util.Date("1/1/2000") ) ){ %>
	 + (<%= activSched.getTime()%>)
	 <%} %>
	
	<br/>Description:<textarea id="newCustAgendaTxt"></textarea>
	<br/><br/>
	<div class="linkButtonWrapper">
		<input type="button" value="save" onclick="createCustAgendaItem1('<%=searchDate.getTime()%>', '<%=activSched.getTime().getTime()%>', '<%=meeting.getPath()%>')" class="button linkButton"/>
	</div>
<%}else{ out.println("VIEW MODE"); } %>
</div>

<br/><br/>

<%if( !isLocked ){ %>

<style>
.mLocked{ }
.mLocked a{  }
</style>

<div id="meetingLibraryView">
<% if( false) {//user.getYearPlan().getSchedule()!=null ) { %>
	<div class="tmp" id="popup123" style="background-color:#EEEEEE;">
		<%@include file="email/meetingReminder.jsp" %>
	</div>
<%} %>
</div>
	<script>
		var scrollTarget = "";
		if (Modernizr.touch) {
			// touch device
			scrollTarget = ".touchscroll";
		}
		$("#sortable").sortable({
			delay:150,
			cursor: "move" ,
			distance: 5,
			opacity: 0.5 ,
			scroll: true,
			scrollSensitivity: 10 ,
			tolerance: "intersect" ,
			handle: scrollTarget,
		update:  function (event, ui) {
			repositionActivity('<%=meeting.getRefId()%>');
		}
		});


$(function() {
	$( ".button" ).button().click(function( event ) {
		event.preventDefault();
	});
});
	</script>
	<%@include file="../include/manageCommunications.jsp" %>
<%}else{ %>	
	
	
	<style>
.mLocked{ display:none;}
.mLocked a{ display:none; }
</style>


<% } %>
