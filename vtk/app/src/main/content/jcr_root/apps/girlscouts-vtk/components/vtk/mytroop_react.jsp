<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
    String activeTab = "myTroop";
    boolean showVtkNav = true;

    java.util.List<org.girlscouts.vtk.models.Contact>contacts = new org.girlscouts.vtk.auth.dao.SalesforceDAO(troopDAO).getContacts( user.getApiConfig(), troop.getSfTroopId() );
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
  <div class="column large-24 large-centered">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3>TROOP 123 Info</h3><a href="mailto:adulfan@gmail.com">email to (n)</a></dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-23 large-centered">
              <dl class="accordion" data-accordion>
                <% for(int i=0; i<contacts.size(); i++) { 
                    org.girlscouts.vtk.models.Contact contact = contacts.get(i);
                    out.println(contacts);
                %>
                <dt data-target="panel<%=i+1%>b">
                  <p class="name"><%=contact.getFirstName() %></p>
                  <a href="mailto:<%=contact.getEmail() %>"><%=contact.getEmail() %></a>
                  <a href="tel:+<%=contact.getPhone() %>"><%=contact.getPhone() %></a>
                </dt>
                <dd class="accordion-navigation">
                  <div id="panel<%=i+1%>b" class="content">
                      
                  </div>
                </dd>
                <%}//end for %>
              </dl>
            </div>
          </div>
        </div>
      </dd>

      <dt data-target="panel2"><h3>TROOP 123 Info</h3><a href="mailto:adulfan@gmail.com">email</a></dt>
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
                </dd>
              </dl>
            </div>
          </div>
        </div>
      </dd>

    </dl>

  </div><!--/column-->
</div><!--panel-wrapper-->