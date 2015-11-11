<%@ page import="com.day.text.Text, java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*, java.io.*, java.net.*"%>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>

 <div id="fullNav" class="hide-for-print"></div>
    <div id="panelWrapper" class="row content meeting-detail">         
		<a title="" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.rand.map1.html?address=1540+broadway+NYC">nyc</a>
		<br/><a title="" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.rand.map1.html?address=Paris fr">paris</a>
		<br/><a title="" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.rand.map1.html?address=boston ma">boston</a>
		<br/><a title="" data-reveal-id="modal_report_detail" data-reveal-ajax="true" href="/content/girlscouts-vtk/controllers/vtk.rand.map1.html?address=phila pa">phila</a>
		
		<div id="modal_report_detail"  class="reveal-modal"  data-reveal data-options="close_on_esc:true"></div>
 </div>
