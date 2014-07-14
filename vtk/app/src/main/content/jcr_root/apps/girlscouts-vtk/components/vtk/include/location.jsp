<!-- apps/girlscouts-vtk/components/vtk/include/location.jsp -->
<div class="sectionBar">Manage Locations</div>
<br/>
<div class="setupCalendar">
	<p>Add, delete or edit locations to assign to your meetings</p>
        <form>
        <input type="hidden" id="loc_city" value=""/>
        <input type="hidden" id="loc_state" value=""/>
        <input type="hidden" id="loc_zip" value=""/>
        <div id="locMsg"></div>
        <div class="row">
                <div class="small-4 columns"><a href="javascript:void(0)" onclick="addLocation()">+Add</a></div>
                <div class="small-10 columns"><input type="text" id="loc_name" value="" placeholder="Name of Location"/></div>
                <div class="small-10 columns"><input type="text" id="loc_address" value="" placeholder="Address"/></div>
	</div>
</div>
<div id="locList"></div>
<script type="text/javascript">
	$(document).ready(function(){
		showLocationManager();
	});
</script>
