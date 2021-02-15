<div class="content clearfix" id="panel3">
    <div id="rm-activities-pop-up" class="rm-activities-modal">

       <div class="rm-activities-pop-up-content">
           <span class="rm-activities-pop-up-close" onclick ="onCancelManageActivityPopUp()">&times;</span>
           <div class ='rm-activities-txt-container'><p class ="rm-activities-txt-container-paragraph">Are you sure you</p><p class ="rm-activities-txt-container-paragraph"> want to delete the activity?</p></div>
         <div class ='rm-activities-pop-up-btn-gp'><button class ='rm-activities-pop-up-btn-gp-btn cancel-btn' onclick ="onCancelManageActivityPopUp()">No</button> <button class 
             ='rm-activities-pop-up-btn-gp-btn' onclick ="onRemoveManageActivityPopUp()">Yes</button></div>
       </div>

     </div>
   <% if (selectedTroop.getYearPlan().getActivities() != null && selectedTroop.getYearPlan().getActivities().size() > 0) { %>
   <table>
       <%
           for (int t = 0; t < selectedTroop.getYearPlan().getActivities().size(); t++) {
       %>
        <tr>
           <td><strong><%=selectedTroop.getYearPlan().getActivities().get(t).getDate() %>
           </strong></td>
           <td><%=selectedTroop.getYearPlan().getActivities().get(t).getName() %>
           </td>
           <!-- <td><a href="javascript:void(0)"
                  onclick="rmCustActivity('<%=selectedTroop.getYearPlan().getActivities().get(t).getPath()%>')"
                  title="remove">Remove</a></td> -->

            <td><a href="javascript:void(0)"
                   onclick="rmActivities('<%=selectedTroop.getYearPlan().getActivities().get(t).getPath()%>')"
                   title="remove"><i class ="icon-button-circle-cross"></i></a></td>
           <div id ="remove-activities"></div>
       </tr>
       <% } %>
   </table>
   <% } else { %>
   <div>
       <p>There are no activities in your Year Plan.</p>
       <div style="float:right;">
           <button class="btn right" onclick="newActivity()">ADD ACTIVITY</button>
       </div>
   </div>
   <% } %>
</div>
<!--/content-3-->
<style>

.rm-activities-modal {
	display: none;
	/* Hidden by default */
	position: fixed;
	/* Stay in place */
	z-index: 1;
	/* Sit on top */
	left: 0;
	top: 0;
	width: 100%;
	/* Full width */
	height: 100%;
	/* Full height */
	overflow: auto;
	/* Enable scroll if needed */
	background-color: rgb(0, 0, 0);
	/* Fallback color */
	background-color: rgba(0, 0, 0, 0.4);
	/* Black w/ opacity */
}

.rm-activities-pop-up-content {
	background-color: #fefefe;
	margin: 15% auto;
	padding: 10px 20px;
	border: 1px solid #888;
	width: 23%;
}

.rm-activities-pop-up-close {
	color: #aaa;
	float: right;
	font-size: 28px;
	font-weight: bold;
}

.rm-activities-txt-container {
	padding: 32px 20px 30px 20px;
	text-align: center;
}

.rm-activities-txt-container-paragraph {
	margin: 0;
	font-size: 20px !important;
	font-weight: bold;
}

.rm-activities-pop-up-btn-gp {
	display: flex;
	justify-content: space-around;
	padding-bottom: 20px;
}

.rm-activities-pop-up-btn-gp-btn {
	padding: 10px 51px;
	color: white;
}

.rm-activities-pop-up-close {
	font-size: 28px !important;
	cursor: pointer;
	color: #000000;
	opacity: 0.7;
}

.rm-activities-pop-up-btn-gp-btn.cancel-btn {
	color: #00a850;
	background: #ffffff;
	border: 1.5px solid #00a850;
}

.rm-activities-pop-up-btn-gp-btn.cancel-btn:hover {
	background: #eeeeee;
}

@media only screen and (max-width: 768px) {
	.rm-activities-pop-up-content {
	      width: 90%;
	}
}

</style>