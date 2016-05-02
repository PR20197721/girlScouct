<!-- apps/girlscouts-vtk/components/vtk/include/location.jsp -->
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