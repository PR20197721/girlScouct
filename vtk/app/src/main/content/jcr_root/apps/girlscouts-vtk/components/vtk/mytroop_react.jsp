<%@ page
  import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<%
        String activeTab = "myTroop";
        boolean showVtkNav = true;
%>
<%@include file="include/tab_navigation.jsp"%>

<div id="panelWrapper" class="row content">
  <div class="column large-24 large-centered">

    <dl class="accordion" data-accordion>
      <dt data-target="panel1"><h3>TROOP 123 Info</h3><a href="mailto:adulfan@gmail.com">email</a></dt>
      <dd class="accordion-navigation">
        <div class="content active" id="panel1">
          <div class="row">
            <div class="column large-23 large-centered">
              <dl class="accordion" data-accordion>
                <dd class="accordion-navigation">
                  <a href="#panel1b">Accordion 1</a>
                  <div id="panel1b" class="content">
                    Panel 1. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
                  </div>
                </dd>
                <dd class="accordion-navigation">
                  <a href="#panel2b">Accordion 2</a>
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

      <dt data-target="panel2"><h3>TROOP 123 Info</h3><a href="mailto:adulfan@gmail.com">email</a></dt>
      <dd class="accordion-navigation" id="panel2">
        <div class="content">
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


<!--     <dl class="accordion" data-accordion>
      <dd class="accordion-navigation">
        <a href="#panel1b">Accordion 1</a>
        <div id="panel1b" class="content active">
          Panel 1. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.
        </div>
      </dd>
      <dd class="accordion-navigation">
        <a href="#panel2b">Accordion 2</a>
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
    </dl> -->

  </div>
</div>