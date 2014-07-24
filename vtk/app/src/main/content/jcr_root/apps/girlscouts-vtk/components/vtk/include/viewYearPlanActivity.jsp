<!-- apps/girlscouts-vtk/components/vtk/include/viewYearPlanActivity.jsp -->
<%
Activity activity = (Activity) _comp;
%>
<br/>
<div class="row meetingDetailHeader">
        <div class="small-24 medium-8 large-7 columns">
                <table class="planSquareWrapper">
                        <tr>
<%if( prevDate!=0 ){ %>
                		<td class="planSquareLeft"><a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=prevDate%>"><img width="20" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/previous.png"/></a></td>
<%} %>
                                <td class="planSquareMiddle">
					<div class="planSquare" style="background-color:#0096ff;">
						<div class="date">
        <%if( user.getYearPlan().getSchedule()!=null ) {%>
							<div class="cal"><span class="month"><%= FORMAT_MONTH.format(activity.getDate())%><br/></span><span class="day"><%= FORMAT_DAY_OF_MONTH.format(activity.getDate())%></span></div>
        <%} else {%>
							<div class="cal"><span class="month">Activity<br/></span><span class="day hide-for-small"><%=meetingCount%></span></div>
        <%}%>
						</div>
					</div>
				</td>
<%if( nextDate!=0 ){ %>
				<td class="planSquareRight"><a class="direction" href="/content/girlscouts-vtk/en/vtk.planView.html?elem=<%=nextDate%>"><img width="20" height="100" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/next.png"/></a>
				</td>
<%} %>
			</tr>
		</table>
        </div>
        
        
        
        <div class="small-24 medium-10 large-12 columns">
                <h1>Activity: <%= activity.getName() %></h1>

<br/><br/>Date: <%=FORMAT_MMddYYYY.format(activity.getDate()) %>
<br/><br/>Time: <%=FORMAT_hhmm_AMPM.format(activity.getDate()) %> - <%= FORMAT_hhmm_AMPM.format(activity.getEndDate()) %>


<%
String ageLevel=  user.getTroop().getGradeLevel();
ageLevel= ageLevel.substring( ageLevel.indexOf("-")+1);
ageLevel=ageLevel.toLowerCase().trim();
%>
<br/><br/>Age range: <%= ageLevel%>
<br/><br/>Location:
<%= activity.getLocationName() %>
<a href="/content/girlscouts-vtk/controllers/vtk.map.html?address=<%=activity.getLocationAddress()%>" target="_blank"><%=activity.getLocationAddress()%></a>


<br/><br/>Cost:<%=FORMAT_CURRENCY.format(activity.getCost()) %>

<div style="background-color:#efefef"><%=activity.getContent()%></div>

        </div>
        <div class="small-24 medium-6 large-5 columns linkButtonWrapper">
<%if( activity.getDate().after( new java.util.Date())){ %>
		<a href="#" class="button linkButton" onclick="rmCustActivity12('<%=activity.getPath()%>')">delete this activity</a>
<%} %>
		<a href="#" class="button linkButton" onclick="openClose('editCustActiv')">edit activity</a>
                <br/>
        </div>        
        
        
        
        
        
         <!--  
       <div style="background-color:lightblue; display:none;">
        	<h4>Upload File**</h4>
        		<%String aassetId = new java.util.Date().getTime() +"_"+ Math.random(); %>
    
   
  
              <form action="/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=aassetId %>" method="post"
                       onsubmit="return bindAssetToYPC( '/vtk/<%=user.getTroop().getCouncilCode()%>/<%=user.getTroop().getTroopName() %>/assets/<%=aassetId %>/custasset', '<%=activity.getUid() %>' )"   enctype="multipart/form-data">
                       
               <input type="hidden" name="id" value="<%=aassetId%>"/>      
               <input type="hidden" name="owner" value="<%=user.getId()%>"/>
               <input type="hidden" name="createTime" value="<%=new java.util.Date()%>"/>         
			   <input type="file" name="custasset" size="50" />
               <br />
                <input type="submit" value="Upload File" />
         </form>
        </div>
        -->
        
    
     
     
     
     
     
        
</div>


    
     <div style="background-color:#efefef;">
     <ul>
     <% 
     List<Asset> a_aidTags = activity.getAssets();

if( a_aidTags!=null )
 for(int i=0;i<a_aidTags.size();i++){
        String aidTagDescription = "No description.";
        if (a_aidTags.get(i).getDescription() != null) {
                aidTagDescription = java.net.URLEncoder.encode(a_aidTags.get(i).getDescription());
        }
	%><li>  
	<a href="#modal" id="<%=a_aidTags.get(i).getUid() %>" onclick="x12('<%=a_aidTags.get(i).getRefId()%>', '<%=aidTagDescription%>', '<%=a_aidTags.get(i).getUid() %>')"><%=aidTagDescription%></a>
	
	
	</li><% 
 }
     %>
     </ul>
     </div>
     
     
     







<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>

<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>

<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>
<!--
<link rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/alex/screen.css">
-->

<script>
$(function() {
	$( "#newCustActivity_date" ).datepicker({minDate: 0});
  });
  
  
jQuery(function($){
	
	
	 	
	    
	    
	$("#newCustActivity_date").inputmask("mm/dd/yyyy", {});
	
	$('#newCustActivity_date').datepicker({minDate: 0});
	
	$("#newCustActivity_startTime").inputmask("h:s", {});
	$("#newCustActivity_endTime").inputmask("h:s", {});
	$("#newCustActivity_cost").maskMoney({thousands:''});
	});
	




$.validator.addMethod('time', function(value, element, param) {
    return value == '' || value.match(/^([01][0-9]|2[0-3]):[0-5][0-9]$/);
}, 'Enter a valid time: hh:mm');

$.validator.addMethod('currency', function(value, element, regexp) {
    var re = /^\d{1,9}(\.\d{1,2})?$/;
    return this.optional(element) || re.test(value);
}, '');


$().ready(function() {
	
 		
	$("#signupForm").validate({	
		rules: {
			
			newCustActivity_name: {
				required: true,
				minlength: 2
			},
			newCustActivity_startTime:{
				required:true,
				minlength: 5,
				time: true
			},
			newCustActivity_endTime:{
				required:true,
				minlength: 5,
				time: true
			},
			newCustActivity_cost:{
				required:false,
				minlength: 4,
				currency:true
			},
			newCustActivity_date:{
				required:true,
				minlength:8,
				date:true
			}
			
		},
		messages: {
			newCustActivity_name: {
				required: "Please enter a Name",
				minlength: "Your Name must consist of at least 2 characters"
			},
			newCustActivity_startTime:{
				required: "Please enter a Start time",
				minlength: "Valid format HH:mm"
			},
			newCustActivity_endTime:{
				required: "Please enter a End time",
				minlength: "Valid format HH:mm"
			},
			newCustActivity_cost:{
				required: "Please enter a valid amount. Default 0.00",
				minlength: "Valid format 0.00"
			},
			newCustActivity_date:{
				required: "Please enter valid start date",
				minlength: "Valid format MM/dd/yyyy"
			}
		}
	});

 
});

	function saveActivity(){
	    if ($('#signupForm').valid()) {
	    	if(!timeDiff()){ return false;}
	    	editNewCustActivity('<%=activity.getUid()%>');
	    }
	    else {
                alert("The form has one or more errors.  Please update the form and try again.");
	    }
		
		
	}
	
$('#newCustActivity1').click(function() {
	
    if ($('#signupForm').valid()) {
    	if(!timeDiff()){ return false;}
    	editNewCustActivity('<%=activity.getUid()%>');
    }
    else {
        alert("Invalid.Fix it");
    }
});
	
	
	function timeDiff(){
		var date= document.getElementById("newCustActivity_date").value;
		var startTime = document.getElementById("newCustActivity_startTime").value;
		var endTime = document.getElementById("newCustActivity_endTime").value;
		var newCustActivity_startTime_AP = document.getElementById("newCustActivity_startTime_AP").value;
		var newCustActivity_endTime_AP = document.getElementById("newCustActivity_endTime_AP").value;
		
		
		if(!Date.parse( new Date( date +" " + startTime +" "+newCustActivity_startTime_AP) )) {alert("Invalid Start Date,time. 12hr format: "+date +" " + startTime +" "+newCustActivity_startTime_AP);return false;}
		if(!Date.parse( new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) )) {alert("Invalid End Date,time. 12hr format: "+date +" " + endTime +" "+newCustActivity_endTime_AP);return false;}
		
		
			
		if( (new Date(date +" "+ startTime+ " "+newCustActivity_startTime_AP) - new Date( date +" " + endTime +" "+newCustActivity_endTime_AP) ) >=0 )
			{alert("StartTime after/equal EndTime"); return false;}
		else 
			return true;
		
	}
</script>  
<div id="editCustActiv" style=" display:none;">
<form class="cmxform" id="signupForm">
	
	<h2>Edit Activity</h2>
	<div class="sectionBar">Edit Custom Activity</div>
	<div id="newCustActivity_err" style="color:red;"></div>
        <div class="row">
                <div class="small-6 columns">
			<font color="red">*</font> <input type="text" name="newCustActivity_name" id="newCustActivity_name" value="<%=activity.getName() %>" style="width:200px;" placeholder="Name of Activity"/>
		</div>
                <div class="small-6 columns">
			Date: ex:05/07/2014<input type="text" name="newCustActivity_date" id="newCustActivity_date" value="<%=FORMAT_MMddYYYY.format(activity.getDate()) %>" style="width:160px;"/>
                </div>  
                <div class="small-6 columns">
			Start Time: <input type="text" name="newCustActivity_startTime" id="newCustActivity_startTime" value="<%= FORMAT_hhmm_AMPM.format(activity.getDate())%>" style="width:100px;" />
			<select id="newCustActivity_startTime_AP">
			 
			 <option value="am" <%=  FORMAT_AMPM.format(activity.getDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : "" %>>am</option>
			<option value="pm" <%=  FORMAT_AMPM.format(activity.getDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : "" %>>pm</option>
			 </select> </div>  
                <div class="small-6 columns">
			End Time: <input type="text" id="newCustActivity_endTime" value="<%=FORMAT_hhmm_AMPM.format(activity.getEndDate()) %>"  style="width:100px;"/>
			<select id="newCustActivity_endTime_AP">
			<option value="am" <%=  FORMAT_AMPM.format(activity.getEndDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : "" %>>am</option>
			<option value="pm" <%=  FORMAT_AMPM.format(activity.getEndDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : "" %>>pm</option></select>
                </div> 
	</div>
        <div class="row">
                <div class="small-12 columns">
			Location Name <input type="text" id="newCustActivity_locName" value="<%=activity.getLocationName() %>" style="width:100px;"/>
		</div>
                <div class="small-12 columns">
			Location Address <input type="text" id="newCustActivity_locAddr" value="<%=activity.getLocationAddress() %>" style="width:100px;"/>
                </div>
	</div>
        <div class="row">
                <div class="small-16 columns">
			<textarea id="newCustActivity_txt" rows="4" cols="5" " style="width:300px;"><%=activity.getContent() %></textarea>
                </div>
                <div class="small-8 columns">
                
                
              <div style="background-color:red;">Cost: <input type="text" name="newCustActivity_cost"  id="newCustActivity_cost" value="<%=FORMAT_COST_CENTS.format(activity.getCost())%>"/></div>
			<input type="button" value="Save" id="newCustActivity1" onclick="saveActivity()"/>
			<input type="button" value="Cancel" onclick="openClose('editCustActiv')"/>
			  
             
                </div>
        </div>
	 </form>

</div>

<%--@include file="../include/manageCommunications.jsp" --%>


    
