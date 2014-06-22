<!-- apps/girlscouts-vtk/components/vtk/include/location.jsp -->
<h1>Location</h1><div style="background-color:gray; color:#fff;">Manage Locations</div>
<p>Add, delete or edit locations to assign to your meetings</p>
<div>
	<div id="locMsg"></div>
	<br/>Location name:<input type="text" id="loc_name" value=""/>
	<br/>Location address <input type="text" id="loc_address" value=""/>
	<!--  
	<br/>City<input type="text" id="loc_city" value=""/>
	<br/>State<input type="text" id="loc_state" value=""/>
	<br/>Zip <input type="text" id="loc_zip" value=""/>
	-->
	<input type="hidden" id="loc_city" value=""/>
	<input type="hidden" id="loc_state" value=""/>
	<input type="hidden" id="loc_zip" value=""/>
	
	<br/><br/>
	<br/><a href="javascript:void(0)" onclick="addLocation()">add</a>
	|| <a href="javascript:void(0)" onclick="showLocationManager()">Manage Locations</a>
</div>


<div id="locList">

</div>
