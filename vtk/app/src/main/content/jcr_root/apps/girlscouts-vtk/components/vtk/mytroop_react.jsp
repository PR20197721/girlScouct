<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<%@ page import="com.google.common.collect .*"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
    java.util.Map<Contact, java.util.List<ContactExtras>> contactsExtras=null;
	java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
	if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
		contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
	}

	if( contacts==null ){
		contacts =	new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO, connectionFactory, sling.getService(org.girlscouts.vtk.ejb.SessionFactory.class)).getContacts( user.getApiConfig(), troop.getSfTroopId());
		if( contacts!=null ) {
			session.setAttribute("vtk_cachable_contacts" , contacts);
		}
		
		
		String emailTo=",";
		try{
			for(int i=0;i<contacts.size();i++)
			if( contacts.get(i).getEmail()!=null && !contacts.get(i).getEmail().trim().equals("") && !emailTo.contains( contacts.get(i).getEmail().trim()+"," )) {
				emailTo += (contacts.get(i).getFirstName()!=null ? contacts.get(i).getFirstName().replace(" ","%20") : "") + java.net.URLEncoder.encode("<" + contacts.get(i).getEmail() +">")+",";
			}
			emailTo = emailTo.trim();
			if( emailTo.endsWith(",") )  {
				emailTo= emailTo.substring(0, emailTo.length()-1);
			}
			if( emailTo.startsWith(",") ) {
				emailTo= emailTo.substring(1, emailTo.length());
			}
		}catch(Exception e){e.printStackTrace();}
		
		java.util.Map<java.util.Date, YearPlanComponent> sched = null;
		try{
			 //GOOD-sched = meetingUtil.getYearPlanSched(user, troop.getYearPlan(), true, true);
			sched = meetingUtil.getYearPlanSched(user, troop, troop.getYearPlan(), true, false);
		}catch(Exception e){e.printStackTrace();}

		BiMap sched_bm = HashBiMap.create(sched);//com.google.common.collect.HashBiMap().create();
		com.google.common.collect.BiMap sched_bm_inverse = sched_bm.inverse();

		 contactsExtras = contactUtil.getContactsExtras( user,  troop, contacts);
	
		 
    
	%> 
	<div class="email-content">
          <div class="modal-header">
       		 <div class="vtk-email-news-button">
                    <i class="icon-button-circle-cross"></i>
              </div>
             <div class="emailHeader">Email Content: </br></div>
      	  </div>
        <div class="modal-body">
            <p> Subject: </p>
            <textarea name="subject" id="subject" rows="1" cols="30"></textarea>
            <p> Body: </p>
            <textarea name="message" id="message" rows="10" cols="30"></textarea>
            <form id='file-catcher'>
              <input id='file-input' type='file' multiple/>
            </form>
    	</div>
        <div id='file-list-display'></div>
    	<div class="modal-footer">
        	<div id="sendEmail"class = "button tiny add-to-year-plan" emails="<%= emailTo%>">Send Emails</div>
      	</div>

    </div>
	<%@include file='myTroopImg.jsp' %>
	
	

			<% if( !VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_MEMBER_DETAIL_TROOP_ID) && VtkUtil.hasPermission(troop, Permission.PERMISSION_CAN_VIEW_OWN_CHILD_DETAIL_TROOP_ID)){
					  
			
			       for(int i=0; i<contacts.size(); i++) {
			            org.girlscouts.vtk.models.Contact contact = contacts.get(i);
			           // java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
			           java.util.List<ContactExtras> infos = contactsExtras.get( contact );
			           if(!user.getApiConfig().getUser().getContactId().equals(contact.getContactId() ) )
			        		continue;
			            %>
						  <div class="column large-24 large-centered mytroop">
						    <dl class="accordion" data-accordion>
			                <dt data-target="panel_myChild_<%=i%>"><h3 class="on">Achievements for <%=contact.getFirstName() %></h3></dt>
						      <dd class="accordion-navigation">
						        <div class="content <%=i==0 ? "active" : "" %>" id="panel_myChild_<%=i%>">
						             <%@include file='include/troop_child_achievmts.jsp' %>
							        </div>
							      </dd>
							    </dl>
							  </div>
			        <%}//edn for
			       
			      
			 }//edn if %>
		
		<%
		
		String role="Girl";
		if( role.equals("Girl") ){ %>
		  <div class="column large-24 large-centered mytroop">
		    <dl class="accordion" data-accordion>
		      <dt data-target="panel1">
		        <h3 class="on"><%=troop.getSfTroopName() %> INFO</h3>
		        <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)){ %>
		        <div id="mailBtn">
		           <a id = "#mailTroop" ><i class="icon-mail"></i>email to <%= contacts.size() %> contacts</a></div>
		           <label><input type="checkbox" name="delimiter">Please check this box if you use Outlook</label>
		         <%} %>
		         
		      </dt>
		      <dd class="accordion-navigation">
		        <div class="content active" id="panel1">
		           <%@include file='include/troop_member_detail.jsp' %>
		        </div>
		      </dd>
		    </dl>
		  </div>
		  
	   <%}
		role="Adult";
		if( role.equals("Adult") ){ %>
	      <div class="column large-24 large-centered mytroop">
            <dl class="accordion" data-accordion>
              <dt data-target="panel2">
                <h3 class="on"><%=troop.getSfTroopName() %> VOLUNTEERS</h3>
                <% if(VtkUtil.hasPermission(troop, Permission.PERMISSION_SEND_EMAIL_ALL_TROOP_PARENTS_ID)){ %>
                  <a style="float:right;margin-right: 20px" href="<%= sling.getService(org.girlscouts.vtk.helpers.ConfigManager.class).getConfig("communityUrl")%>/Membership_Troop_Renewal">Add a New Volunteer <img width="30px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/arrow2-right_yellow.png" valign="middle"> </a>
           
                 <%} %>
                 
              </dt>
              <dd class="accordion-navigation">
                <div class="content active" id="panel2">                
                   <%@include file='include/troop_volunteer_detail.jsp' %>
                </div>
              </dd>
            </dl>
          </div>
       <%}//edn else %>

<% }//edn if contact %>

<script>
    var fileInput = document.getElementById('file-input');
    var fileListDisplay = document.getElementById('file-list-display');
    var fileList = [];
    var fileData = [];
    fileInput.addEventListener('change', function (evnt) {
        fileList = [];
        for (var i = 0; i < fileInput.files.length; i++) {
            fileList.push(fileInput.files[i]);
            let reader = new FileReader();
            reader.onload = function(){
                fileData.push(reader.result);
            };
            reader.readAsDataURL(fileInput.files[i]);
        }
        renderFileList();
     });
    renderFileList = function () {
        fileListDisplay.innerHTML = '';
        fileList.forEach(function (file, index) {
            var fileDisplayEl = document.createElement('p');
          fileDisplayEl.innerHTML = (index + 1) + ': ' + file.name;
          fileListDisplay.appendChild(fileDisplayEl);
        });
    };
    $("#sendEmail").click(function(){
        if($("#sendEmail").attr("toClose") === "true"){
            $("#sendEmail").text("Send Email");
    		$("#sendEmail").attr("toClose", "false");
            $(".email-content").css('display', 'none');
            $(".modal-body").html("<p> Subject: </p><textarea name=\"subject\" id=\"subjectArea\" rows=\"1\" cols=\"30\"></textarea><p> Body: </p><textarea name=\"message\" id=\"messageArea\" rows=\"10\" cols=\"30\"></textarea><form id='file-catcher'><input id='file-input' type='file' multiple/></form>");
            $(".email-content").hide();

        //SEND EMAIL
        }else{
    		$("#sendEmail").text("Close");
        	$("#sendEmail").attr("toClose", "true");
            var formData = new FormData();
            formData.append('act', 'SendEmail');
            formData.append('message', $("textarea")[0].value);
            formData.append('subject', $("textarea")[1].value);
            formData.append('addresses', decodeURIComponent($("#sendEmail").attr("emails")));
            for(var i = 0; i<fileList.length; i++){
                name = fileList[i].name;
                name = name.replace(/\.[^/.]+$/, "");
                formData.append('file'+(i+1), fileList[i]);
                formData.append('file'+(i+1)+"Name", name);
                formData.append('file'+(i+1)+"Type", fileList[i].type);
            }
            $.ajax({
                    url: '/content/girlscouts-vtk/controllers/vtk.controller.html',
                    type: 'POST',
                    data: formData,
                    processData:false,
                    contentType: false,
                    success: function(result) {
                        alert("Email successfully sent");

                    }
                });
            $(".modal-body").html("<strong>Email Sent</strong>");
        }


        });
	    $("#mailBtn").click(function(){
            if( $(".email-content").css("display") === "none" ){
                $(".email-content").show();
            }else{
                 $(".email-content").hide();
            }

        });

        $(".vtk-email-news-button").click(function(){
            $(".email-content").css('display', 'none');
            $(".modal-body").html("<p> Subject: </p><textarea name=\"subject\" id=\"subjectArea\" rows=\"1\" cols=\"30\"></textarea><p> Body: </p><textarea name=\"message\" id=\"messageArea\" rows=\"10\" cols=\"30\"></textarea><form id='file-catcher'><input id='file-input' type='file' multiple/></form>");
            $("#mailBtn").attr("show","false");

        });


</script>

