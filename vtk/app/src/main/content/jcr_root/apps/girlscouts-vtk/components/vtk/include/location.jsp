<!-- apps/girlscouts-vtk/components/vtk/include/location.jsp -->
<!-- <div id="locationEdit">
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
			<div class="small-24 medium-6 large-5 columns"><a href="#" onclick="addLocation()" class="button linkButton">+&nbsp;Add</a></div>
			<div class="small-24 medium-8 large-9 columns"><input type="text" id="loc_name" value="" placeholder="Name of Location"/></div>
			<div class="small-24 medium-10 large-10 columns"><input type="text" id="loc_address" value="" placeholder="Address"/></div>
		</div>
		</form>
	</div>
	<div id="locList"></div>
</div>
 <p>Add, delete or edit locations to assign to your meetings.</p>
 -->
 <div class="content clearfix row" id="panel2">
  <div id="locationEdit" class="columns small-24"><!-- add 4 js err ms -->
    <p>Add, delete or edit locations to assign to your meetings.</p>
    <div id="err" class="errorMsg error"></div>
    <form id="addLocationForm" onsubmit="return false">
  	   <input type="hidden" id="loc_city" value=""/>
  	   <input type="hidden" id="loc_state" value=""/>
  	   <input type="hidden" id="loc_zip" value=""/>
       <section class="row">
         <div class="column small-10">
            <input type="text" placeholder="Location Name" id="loc_name" value="" />
         </div>
         <div class="column small-10">
           <input type="text" placeholder="Location Address" id="loc_address" value="" />
         </div>
         <button class="btn right" onclick="addLocation()">Add</button>
       </section>
       <section class="row">
        <div id="locList"><script>loadLocMng();</script></div>
       </section>
    </form>
   </div>  
 </div><!--/content-2-->