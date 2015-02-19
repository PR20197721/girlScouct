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


  <div class="column large-23 large-centered">         
    <div class="row">
      <dl class="accordion" data-accordion="">
        <dt data-target="panel1"><h3 class="on">Daisies</h3></dt>
        <dd class="accordion-navigation">
          <div class="content active" id="panel1">
            <div class="row">
              <div class="column large-23 large-centered">
                  <div class="row titles">
                    <span class="name column large-9 text-center">Year Plan</span>
                    <span class="name column large-4 text-center"># of Troops Adopted</span>
                    <span class="name column large-5 text-center"># of Plans Customized</span>
                    <span class="name column large-5 text-center end">Plans with Added Activities</span>
                  </div>
                  <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion="">
                      <dt data-target="panel1b" class="clearfix">
                        <span class="name column large-9">#1 Badge Year One</span>
                        <span class="column large-4 text-center">66</span>
                        <span class="column large-4 text-center">5</span>
                        <span class="column large-4 text-center">4</span>
                      </dt>
                      <dd class="accordion-navigation">
                        <div id="panel1b" class="content">
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a href="#" title="Troop 245" data-reveal-id="modal_report_detail">Troop245</a>
                            </span>
                            <p class="check column large-4 text-center large-push-9"></p>
                            <p class="check column large-4 text-center"></p>
<!--                             <span class="column large-4 text-center large-push-9">
                              <input type="checkbox" name="ch_1" id="ch_1" /><label for="ch_1"></label>
                            </span> -->
<!--                             <span class="column large-4 text-center">
                              <input type="checkbox" name="ch_2" id="ch_2" /><label for="ch_2"></label>
                            </span> -->
                          </div>
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a href="#" title="Troop 245" data-reveal-id="modal_report_detail">Troop245</a>
                            </span>
                            <p class="check column large-4 text-center large-push-9"></p>
                              <!-- <input type="checkbox" name="ch_3" id="ch_3" /><label for="ch_3"></label> -->
                            <p class="check column large-4 text-center"></p>
                              <!-- <input type="checkbox" name="ch_4" id="ch_4" /><label for="ch_4"></label> -->
                          </div>
                        </div>
                      </dd>
                    </dl>
                  </div>
                  
                  <div class="row">
                    <dl class="accordion-inner clearfix" data-accordion="">
                      <dt data-target="panel2b" class="clearfix">
                        <span class="name column large-10">#1 Badge Year One</span>
                      </dt>
                      <dd class="accordion-navigation">
                        <div id="panel2b" class="content">
                          <div class="clearfix">
                            <span class="column large-4 text-center large-push-9">
                              <a href="#" title="Troop 245" data-reveal-id="modal_report_detail">Troop245</a>
                            </span>
                            <p class="check column large-4 text-center large-push-9"></p>
                            <p class="check column large-4 text-center"></p>
 <!--                            <span class="column large-4 text-center large-push-9">
                              <input type="checkbox" name="ch_5" id="ch_5" /><label for="ch_5"></label>
                            </span>
                            <span class="column large-4 text-center">
                              <input type="checkbox" name="ch_6" id="ch_6" /><label for="ch_6"></label>
                            </span> -->
                          </div>
                        </div>
                      </dd>
                    </dl>
                  </div>
              </div>
            </div>
          </div>
        </dd>
      </dl>
    </div>    
  </div>
</div>
<%@include file="include/modals/modal_report_detail.jsp" %>