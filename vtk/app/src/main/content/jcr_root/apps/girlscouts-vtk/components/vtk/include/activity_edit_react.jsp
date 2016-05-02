<%@ page
  import="java.text.SimpleDateFormat,java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<cq:defineObjects />
<%@include file="session.jsp"%>

<%
org.girlscouts.vtk.models.PlanView planView = meetingUtil.planView(user, troop, request);
Activity activity = (Activity)planView.getYearPlanComponent();
%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.custom.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/inputmask.date.extensions.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskedinput.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.maskMoney.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.datepicker.validation.js"></script>
<script type="text/javascript" src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.validate.js"></script>

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
            if( isTimeCng ){
                 self.location="/content/girlscouts-vtk/en/vtk.html";
		    } else {
		        $('#modal_popup_activity').foundation('reveal', 'close');
		    }
	        }else {
	          alert("The form has one or more errors.  Please update the form and try again.");
	        }
        }
        
    $('#newCustActivity1').click(function() {
        if ($('#signupForm').valid()) {
            if(!timeDiff()){ return false;}
            editNewCustActivity('<%=activity.getUid()%>');
            } else {
                alert("There is a problem with the form entry. Please validate the data and try again.");
            }
        });

        function timeDiff() {
            var date = document.getElementById("newCustActivity_date").value;
            var startTime = document.getElementById("newCustActivity_startTime").value;
            var endTime = document.getElementById("newCustActivity_endTime").value;
            var newCustActivity_startTime_AP = document
                    .getElementById("newCustActivity_startTime_AP").value;
            var newCustActivity_endTime_AP = document
                    .getElementById("newCustActivity_endTime_AP").value;

            if (!Date.parse(new Date(date + " " + startTime + " "
                    + newCustActivity_startTime_AP))) {
                alert("Invalid Start Date,time. 12hr format: " + date + " "
                        + startTime + " " + newCustActivity_startTime_AP);
                return false;
            }
            if (!Date.parse(new Date(date + " " + endTime + " "
                    + newCustActivity_endTime_AP))) {
                alert("Invalid End Date,time. 12hr format: " + date + " " + endTime
                        + " " + newCustActivity_endTime_AP);
                return false;
            }

            if ((new Date(date + " " + startTime + " "
                    + newCustActivity_startTime_AP) - new Date(date + " " + endTime
                    + " " + newCustActivity_endTime_AP)) >= 0) {
                alert("StartTime after/equal EndTime");
                return false;
            } else
                return true;

        }
    
    function closeMe(){ $('.ui-dialog-content').dialog('close'); }

    var isTimeCng= false;

	function cngTimeFlag(){
	
	        isTimeCng= true;
	}
</script>

<div> <!--   id="editCustActiv"  class="reveal-modal" data-reveal -->
<div class="header clearfix">
    <h3 class="columns large-22">Edit Activity</h3>
    <a class="close-reveal-modal columns large-2" href="#"><i class="icon-button-circle-cross"></i></a>
  </div>
  <div class="scroll">
    <div class="content">
  <form class="cmxform" id="signupForm">        
    <div class="errorMsg error"></div>

    <div class="row">
      <div class="small-24 large-12 medium-12 columns">

        <input type="text" name="newCustActivity_name" id="newCustActivity_name" value="<%=activity.getName()%>" placeholder="Activity Name" />
      </div>
      <div class="small-24 large-3 medium-3 columns date">
         <input type="text" name="newCustActivity_date" id="newCustActivity_date" value="<%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, activity.getDate())%>" placeholder="mm/dd/yyyy" class="date calendarField" onchange="cngTimeFlag()"/>
      </div>
      <div class="large-1 columns medium-1 small-1 date">
        <label for="newCustActivity_date"><a class="icon-calendar"></a></label>
      </div>
      <div class="small-16 medium-2 large-2 columns">
        <input type="text" name="newCustActivity_startTime" id="newCustActivity_startTime" value="<%=VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, activity.getDate())%>" onchange="cngTimeFlag()"/>
      </div>
      <div class="small-8 medium-2 large-2 columns">
        
      <select id="newCustActivity_startTime_AP" class="ampm" onchange="cngTimeFlag()">
        <option value="AM" <%=VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, activity.getDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : ""%>>AM</option>
        <option value="PM" <%=VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, activity.getDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : ""%>>PM</option>
      </select>
      </div>
      <div class="small-16 medium-2 large-2 columns">
       <input type="text" id="newCustActivity_endTime" value="<%= VtkUtil.formatDate(VtkUtil.FORMAT_hhmm_AMPM, activity.getEndDate())%>" /> 
      </div>
      <div class="small-8 medium-2 large-2 columns">
        <select id="newCustActivity_endTime_AP"  class="ampm">
          <option value="AM" <%=VtkUtil.formatDate(VtkUtil.FORMAT_AMPM,activity.getEndDate()).toUpperCase().trim().equals("AM") ? "SELECTED" : "" %>>AM</option>
          <option value="PM" <%=VtkUtil.formatDate(VtkUtil.FORMAT_AMPM, activity.getEndDate()).toUpperCase().trim().equals("PM") ? "SELECTED" : "" %>>PM</option>
        </select>
      </div>
    </div><!--/row-->

    <div class="row">
      <div class="small-24 medium-12 large-12 columns">
      <input type="text" id="newCustActivity_locName" value="<%=activity.getLocationName()%>" placeholder="Location Name" />
      </div>
      <div class="small-24 medium-12 large-12 columns">
    
       <input type="text" id="newCustActivity_locAddr" value="<%=activity.getLocationAddress()%>" placeholder="Location Address" />
      </div>
    </div><!--/row-->

      <div class="row">
        <div class="small-24 medium-12 large-12 columns">
          <input type="text" name="newCustActivity_cost" id="newCustActivity_cost"  placeholder="Cost"  value="<%=FORMAT_COST_CENTS.format(activity.getCost())%>" />
        </div>
        <div class="small-24 medium-12 large-12 columns">
          <textarea id="newCustActivity_txt" rows="4" cols="5" placeholder="Activity Description"><%=activity.getContent()%></textarea>
        </div>
        </div><!--/row-->


        <div class="linkButtonWrapper">

        <input class="button linkButton" type="button" value="Save" id="newCustActivity1" onclick="saveActivity()" />
        <!--  <input type="button" class="button close" value="Cancel" onclick="closeMe()"/> -->
         <!--  <input type="button" class="close-reveal-modal" value="Cancel" /> -->
        </div>
      </form>
    </div>
  </div>

</div><!--/create activity-->
