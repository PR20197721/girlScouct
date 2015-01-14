<%@ page
  import="com.day.text.Text, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, java.io.*, java.net.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<!-- PAGEID :: ./app/src/main/content/jcr_root/apps/girlscouts-vtk/components/vtk/mytroop_react.jsp -->
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
    String activeTab = "myTroop";
    boolean showVtkNav = true;

    java.util.List<org.girlscouts.vtk.models.Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
	String emailTo=",";
	try{
			for(int i=0;i<contacts.size();i++)
				if( contacts.get(i).getEmail()!=null && !contacts.get(i).getEmail().trim().equals("") && 
						!emailTo.contains( contacts.get(i).getEmail().trim()+"," ) ) 
					//emailTo+= contacts.get(i).getEmail() +",";
					emailTo += Text.escape(contacts.get(i).getFirstName()) +"<" + contacts.get(i).getEmail() +">,";
			
			emailTo = emailTo.trim(); 
			if( emailTo.endsWith(",") ) 
				emailTo= emailTo.substring(0, emailTo.length()-1);
			if( emailTo.startsWith(",") ) 
				emailTo= emailTo.substring(1, emailTo.length());
			
			System.err.println( emailTo );
			
	}catch(Exception e){e.printStackTrace();}
%>

<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" />
<script src="http://fb.me/react-0.12.1.js"></script>
<script src="http://fb.me/JSXTransformer-0.12.1.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<script src="http://fb.me/react-with-addons-0.12.1.js"></script>

<%@include file="include/tab_navigation.jsp"%>

<div id="panelWrapper" class="row content">

<%@include file="include/utility_nav.jsp"%>

<%@include file='include/modals/modal_upload_img.jsp' %>

  <div class="hero-image">
    <%
            if (!resourceResolver.resolve(troopPhotoUrl).getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
    %>
        <img src="<%=troopPhotoUrl %>" alt="GirlScouts Troop <%=troop.getTroop().getTroopName()%> Photo" />
    <%
    	}
    %>
    <a data-reveal-id="modal_upload_image" title="update photo" href="#nogo" title="upload image"><i class="icon-photo-camera"></i></a>
  </div>
  <div class="column large-24 large-centered">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3 class="on"><%=troop.getSfTroopName() %> INFO</h3><a href='mailto:<%=emailTo%>'><i class="icon icon-mail"></i>email to <%= contacts.size() %> contacts</a></dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-20 large-centered">
                <% for(int i=0; i<contacts.size(); i++) { 
                    org.girlscouts.vtk.models.Contact contact = contacts.get(i);
                %>
                <div class="row">
                  <dl class="accordion-inner clearfix" data-accordion>
                    <dt data-target="panel<%=i+1%>b" class="clearfix">
                      <span class="name column large-6"><%=contact.getFirstName() %></span>
                      <span class="name column large-4">Jack Berger</span>
                      <a class="column large-8" href="mailto:<%= Text.escape(contact.getFirstName()) %><<%=contact.getEmail() %>>">
                        <i class="icon icon-mail"></i><%=contact.getEmail() %>
                      </a>
                      <span class="column large-4"><%=contact.getPhone() %></span>
                    </dt>
                    <dd class="accordion-navigation">
                      <div id="panel<%=i+1%>b" class="content">
                        <ul class="column large-4">
                          <li>DOB: 9/1/2004</li>
                          <li>AGE: 10</li>
                        </ul>
                        <ul class="column large-18">
                          <li><address>1 Main St. Apt 5B<br/>Cleveland, OH<br/>00000</address></li>
                        </ul>
                      </div>
                    </dd>
                  </dl>
                </div>
                <%}//end for %>
              
            </div>
          </div>
        </div>
      </dd>

      
      <!-- 
      <dt data-target="panel2"><h3><%=troop.getSfTroopName() %> VOLUNTEERS</h3><a href="mailto:adulfan@gmail.com">email to 10 contacts</a></dt>
      <dd class="accordion-navigation">
        <div class="content" id="panel2">
          <div class="row">
            <div class="column large-23 large-centered">
              <dl class="accordion" data-accordion>
                <dd class="accordion-navigation">
                  <a href="#panel1b">Accordion 1</a>
                  <div id="panel1 b" class="content active">
                    Panel 1. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                  </div>
                </dd>
                <dd class="accordion-navigation">
                  <a href="#panel3b">Accordion 2</a>
                  <div id="panel2b" class="content">
                    Panel 2. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                  </div>
                </dd>
                <dd class="accordion-navigation">
                  <a href="#panel3b">Accordion 3</a>
                  <div id="panel3b" class="content">
                    Panel 3. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                  </div>
                </dd> -->
              </dl>
            </div>
          </div>
        </div>
      </dd>

    </dl>

  </div><!--/column-->
</div><!--panel-wrapper-->
