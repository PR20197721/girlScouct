<!-- apps/girlscouts-vtk/components/vtk/include/location.jsp -->
<div id="locationEdit">
<div class="sectionBar">Manage Locations <%=new java.util.Date() %></div>
<div id="err" class="errorMsg error"></div>
<div class="setupCalendar">
	<p>Add, delete or edit locations to assign to your meetings</p>
        <form id="addLocationForm">
        <input type="hidden" id="loc_city" value=""/>
        <input type="hidden" id="loc_state" value=""/>
        <input type="hidden" id="loc_zip" value=""/>
        <div id="locMsg"></div>
        <div class="row">
                <div class="small-24 medium-4 large-4 columns"><a href="#" onclick="addLocation()" class="button linkButton">+&nbsp;Add</a></div>
                <div class="small-12 medium-10 large-10 columns"><input type="text" id="loc_name" value="" placeholder="Name of Location"/></div>
                <div class="small-12 medium-10 large-10 columns"><input type="text" id="loc_address" value="" placeholder="Address"/></div>
	</div>
</div>
<div id="locList"></div>
<script type="text/javascript">
	$(document).ready(function(){
		showLocationManager();
	});
</script>
</div>
