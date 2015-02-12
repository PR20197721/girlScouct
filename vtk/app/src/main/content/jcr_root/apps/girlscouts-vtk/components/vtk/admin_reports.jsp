<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig,  org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp"%>
<% 
    String activeTab = "reports";
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content reports meeting-detail">
  <%@include file="include/utility_nav.jsp"%>


  <div class="column large-20 large-centered">         
      <div class="row">
        <dl class="accordion-inner clearfix" data-accordion="">
          <dt data-target="panel1b" class="clearfix">
            <span class="name column large-10">#1 Badge Year One</span>
            <span class="column large-4">123-456-7890</span>
          </dt>
          <dd class="accordion-navigation">
            <div id="panel1b" class="content">
              <ul class="column large-4">
                <li>DOB: 9/1/2004</li>
                <li>AGE: 10</li>
              </ul>
              <ul class="column large-18">
                <li><address>1 Main St. Apt 5B<br>Cleveland, OH<br>00000</address></li>
              </ul>
              
          
          <!-- attendance & ach -->
          
               <div style="background-color:yellow;">
                   /content/girlscouts-vtk/meetings/myyearplan/brownie/B14WA03
                   <br>Attendance: true
                   <br>Achievement: false
               </div>
                
              
            </div>
          </dd>
        </dl>
      </div>
      
      <div class="row">
        <dl class="accordion-inner clearfix" data-accordion="">
          <dt data-target="panel2b" class="clearfix">
            <span class="name column large-10">Cathy Timbob003</span>
           <!--  <span class="name column large-4 hide-for-small">&nbsp;</span> -->
            <a class="column large-8 email" href="mailto:smendez+7@northpointdigital.com">
              <i class="icon icon-mail"></i>smendez+7@northpointdigital.com
            </a>
            <span class="column large-4">610-555-1212</span>
          </dt>
          <dd class="accordion-navigation">
            <div id="panel2b" class="content">
              <ul class="column large-4">
                <li>DOB: 9/1/2004</li>
                <li>AGE: 10</li>
              </ul>
              <ul class="column large-18">
                <li><address>1 Main St. Apt 5B<br>Cleveland, OH<br>00000</address></li>
              </ul>
              
              
              <!-- attendance & ach -->
              
                   <div style="background-color:yellow;">
                       /content/girlscouts-vtk/meetings/myyearplan/brownie/B14WA03
                       <br>Attendance: true
                       <br>Achievement: false
                   </div>
                
              
            </div>
          </dd>
        </dl>
      </div>
      
    
  </div>

</div>