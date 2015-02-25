<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<%
  java.util.List<org.girlscouts.vtk.models.Contact> contacts = null;
  if( isCachableContacts && session.getAttribute("vtk_cachable_contacts")!=null ) {
	  contacts = (java.util.List<org.girlscouts.vtk.models.Contact>) session.getAttribute("vtk_cachable_contacts");
  }

  if( contacts==null ){
	   contacts =	new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
	   if( contacts!=null )
		   session.setAttribute("vtk_cachable_contacts" , contacts);
  }
  
  
  String emailTo=",";
	try{
			for(int i=0;i<contacts.size();i++)
				if( contacts.get(i).getEmail()!=null && !contacts.get(i).getEmail().trim().equals("") && 
						!emailTo.contains( contacts.get(i).getEmail().trim()+"," ) ) 
					//emailTo+= contacts.get(i).getEmail() +",";
					emailTo += "\""+Text.escape(contacts.get(i).getFirstName()) +"\"" +"<" + contacts.get(i).getEmail() +">,";
			
			emailTo = emailTo.trim(); 
			if( emailTo.endsWith(",") ) 
				emailTo= emailTo.substring(0, emailTo.length()-1);
			if( emailTo.startsWith(",") ) 
				emailTo= emailTo.substring(1, emailTo.length());
			System.err.println("testh: "+ emailTo);
			emailTo = java.net.URLEncoder.encode( emailTo );
			System.err.println("testh: after "+ emailTo);
			
	}catch(Exception e){e.printStackTrace();}
	

%>
<%@include file="include/utility_nav.jsp"%>
<%@include file='include/modals/modal_upload_img.jsp' %>

  <div class="hero-image">
    <%
            if (!resourceResolver.resolve(troopPhotoUrl).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
    %>
        <img id="troopPhoto" src="<%=troopPhotoUrl %>" alt="GirlScouts <%=troop.getTroop().getTroopName()%> Photo" />
        <a data-reveal-id="modal_upload_image" title="update photo" href="#nogo" title="upload image"><i class="icon-photo-camera"></i></a>
    <%
    	}
    %>
  </div>
  <div class="column large-24 large-centered mytroop">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3><a href='mailto:<%=emailTo%>'><i class="icon icon-mail"></i>email to <%= contacts.size() %> contacts</a></dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-20 large-centered">
                <% for(int i=0; i<contacts.size(); i++) { 
                    org.girlscouts.vtk.models.Contact contact = contacts.get(i);
                    java.util.List<ContactExtras> infos = contactUtil.girlAttendAchievement(user, troop, contact);
                    String _email= java.net.URLEncoder.encode(contact.getFirstName() +"<"+contact.getEmail() +">");
                   %>
                <div class="row">
                  <dl class="accordion-inner clearfix" data-accordion>
                    <dt data-target="panel<%=i+1%>b" class="clearfix">
                      <span class="name column large-10"><%=contact.getFirstName() %></span>
                     <!--  <span class="name column large-4 hide-for-small">&nbsp;</span> -->
   <a class="column large-8 email" href="mailto:<%=_email%>">
                        <i class="icon icon-mail"></i><%=contact.getEmail() %>
                      </a>
                      <span class="column large-4"><%=contact.getPhone() %></span>
                    </dt>
                    <dd class="accordion-navigation">
                      <div id="panel<%=i+1%>b" class="content">
                        <ul class="column large-4">
                          <li>DOB: <%=contact.getDob() %></li>
                          <li>AGE: <%=contact.getAge() %></li>
                        </ul>
                        <ul class="column large-18">
                          <li><address><%=contact.getAddress() %><br/><%=contact.getCity() %>, <%=contact.getState() %><br/><%=contact.getZip() %></address></li>
                        </ul>
                        
                        
                        <!-- attendance & ach -->
                        <%
                        for(int y=0;y<infos.size();y++){
                        	       if(infos.get(y).isAchievement()){
                            	      %> <%= infos.get(y).getYearPlanComponent().getType()== YearPlanComponentType.MEETING ? ((MeetingE) infos.get(y).getYearPlanComponent()).getMeetingInfo().getName() : "" %><% %>
                        	       }
                        }
                        %>
                        
                      </div>
                    </dd>
                  </dl>
                </div>
                <%}//end for %>
              
            </div>
          </div>
        </div>
      </dd>

      
      
              </dl>
            </div>
          </div>
        </div>
      </dd>

    </dl>

  </div><!--/column-->

<%
	if (request.getHeader("newTroopPhoto") != null) {

%>
<script>
	$( document ).ready(function() {
		var d = new Date();
		$("troopPhoto").attr("src", $(".hero-image img").attr("src") + "?" + d.getTime());
	});
</script>
<%
	}
%>
