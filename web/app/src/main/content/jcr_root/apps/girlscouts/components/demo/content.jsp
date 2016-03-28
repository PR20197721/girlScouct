<%
String vTroop = request.getParameter("vTroop") ==null ? "" : request.getParameter("vTroop");
String path="/usr/local/vtk"; //"/Users/akobovich/vtk";
java.io.File folder = new java.io.File(path);
java.io.File[] listOfFiles = folder.listFiles();


java.util.Map<String, String> permisDeff = new java.util.TreeMap<String, String>();
permisDeff.put("DP", "Troop Leader");
permisDeff.put("PA", "Parent");
org.girlscouts.vtk.auth.dao.SalesforceDAO sfDAO = new org.girlscouts.vtk.auth.dao.SalesforceDAO(null, null);
boolean  showTroopPermDetails  = request.getParameter("showTroopPermDetails") !=null ? true : false;
HttpSession hsession = request.getSession();


if( request.getParameter("rmUser")!=null){
   if( listOfFiles!=null ) 
     for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
              String name= listOfFiles[i].getName();
              if( name.contains("_"+  request.getParameter("rmUser") +".json") ){
                  listOfFiles[i].delete();
              }
          }//ednif
     }//end for
}//end if


%>
<script>

function xyz(slc){
	
	var e = document.getElementById("carlos");
	var strUser = e.options[e.selectedIndex].value;
	if( strUser==null || strUser =='' ){
	    self.location="?";
	}else{
		self.location="?vTroop="+ strUser;
	}
}
</script>
<div id="main" class="row collapse">

<div style="clear: both"></div>
<!-- vtk start -->

<div class="vtk-demo-wrap row">

	<div class="vtk-demo-select row">
		<select name="vTroop" id="carlos" onchange="xyz()">
			 <option value="">Select a Troop</option>
			 <option value="troop1" <%=vTroop.equals("troop1") ? "selected" : "" %>>red</option>
			 <option value="troop2" <%=vTroop.equals("troop2") ? "selected" : "" %>>green</option>
             <option value="troop3" <%=vTroop.equals("troop3") ? "selected" : "" %>>blue</option>
             <option value="troop4" <%=vTroop.equals("troop4") ? "selected" : "" %>>orange</option>
			 <option value="troop5" <%=vTroop.equals("troop5") ? "selected" : "" %>>violet</option>
		</select>
	</div>
	<!-- / Selected -->

	<div class="vtk-demo-wrap-top row">
		<div class="columns small-24 end">
			  	Plan quickly. Save Time. Stay Organized.<br>
			  	Everyone benefits when they know how to use the volunteer Toolkit.
		</div>
	</div>
  <!-- / info -->


	<div class="row vtk-demo-wrap-bottom">
		<div class="columns small-24">
			<div class="row">
			<% 
			
			if( request.getParameter("vTroop") ==null)
			     out.println("<h2 class='vtk-demo-select_'>Please Select A Troop First</h2>");
			else{
			  if( listOfFiles!=null ) {
			    for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isFile()) {
			          String name= listOfFiles[i].getName();

			          if( name!=null && name.startsWith("vtkUser")){
			              String rsp=  sfDAO.readFile(path+"/"+name).toString();
			              org.girlscouts.vtk.auth.models.User User = new org.girlscouts.vtk.auth.models.User();
			              String userName= name.substring( name.indexOf("_")+1 , name.indexOf(".") );
			              org.girlscouts.vtk.auth.models.ApiConfig apiConfig = new org.girlscouts.vtk.auth.models.ApiConfig();
			              apiConfig.setDemoUser(true);
			              apiConfig.setDemoUserName( userName );
			              try{
			                   User= sfDAO.getUser_parse(User, apiConfig, rsp);
			                  
			                   java.util.Set<String> roles = new java.util.HashSet<String>();
			                   java.util.List<org.girlscouts.vtk.salesforce.Troop> troops= apiConfig.getTroops();
			                   for(int ii=0;ii<troops.size();ii++){
			                       org.girlscouts.vtk.salesforce.Troop _troop = troops.get(ii);
			                       roles.add( _troop.getRole() );
			                   }

			                   if( User.isAdmin() ){  %>
                               
                               <div class="vtk-demo-card columns  small-24  medium-8 end">
                                   <div class="vtk-header-box">
                                     <a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=<%=vTroop %>&user=<%=userName%>">Council Admin <span class="float-right icon-button-arrow-right"></a>
                                   </div>
                             
                                   <p>Material and aids are organized to help everyone work together:</p>
                                   <ul>
                                     <li>Upload materials to support the troop Leaders.</li>
                                     <li>Report in troop finances.</li>
                                     <li>Stay organized across troops.</li>
                                   </ul>
                               </div>
                               
                               <!-- / Council Admin -->
                               <%
			                   }else if(roles.contains("DP")){
			                  %>
								 
								  <div class="vtk-demo-card columns small-24 medium-8 end">
										<div class="vtk-header-box">
										  <a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=<%=vTroop %>&user=<%=userName%>">Troop Leader <span class="float-right icon-button-arrow-right"></a>
										</div>
								
										<p>Everything ready, right at your fingertips to save time:</p>
										<ul>
										  <li>Pre-populated plans.</li>
										  <li>Add you own activities.</li>
										  <li>See your troop roster.</li>
										  <li>Track girls achievements and attendance.</li>
										</ul>
								  </div>
								  
								  <!-- / Troop Leader -->
								<%}else if(roles.contains("PA")){ %>
								 
								  <div class="vtk-demo-card columns  small-24  medium-8 end">
									  <div class="vtk-header-box">
										<a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=<%=vTroop %>&user=<%=userName%>">Parents <span class="float-right icon-button-arrow-right"></a>
									  </div>
									  <p>Check in on the troop and see what your girl needs for meetings:</p>
									  <ul>
										<li>View troop plans (easy-to use calendar)</li>
										<li>Stay In touch with the troop leader and volunteers.</li>
										<li>Find ways to help the troop.</li>
									  </ul>
									 
								  </div>
								  <!-- / Parents -->
								<% }           
			                   
			              }catch(Exception e){e.printStackTrace();}
			          } //if
			      } //if
			    } //for
			  }
			}
			  %>
			</div>
		</div>
	</div>

	<div class="row">
		<div class="vtk-bottom-frase columns small-24">
			> See for yourself! Phone, Tablet or Desktop, select a role and experience how easy troop planning can be.
		</div>
	</div>



<% if( request.getParameter("showDetails") !=null ){ %>
<div class="vtk-table-container">

<div class="table-action-button">
  <a href="?showTroopPermDetails=ad&showDetails=true">show permission details</a> <br/><a href="/content/girlscouts-vtk/en/vtk.html?useAsDemo=true&showDetails=true">Add/Update User</a>
</div>


<table align="center">
 <tr>
	<th>User name</th>
	<th>Permissions</th>
	<th>Kiosk mode</th>
	<th>Shared mode</th>
	<th>Virtual Troop mode</th>
	<th>Remove User</th>
 </tr>
<%



if( listOfFiles!=null ) 
	for (int i = 0; i < listOfFiles.length; i++) {
	
	  if (listOfFiles[i].isFile()) {
		  String name= listOfFiles[i].getName();

		  if( name!=null && name.startsWith("vtkUser")){
			  String rsp=  sfDAO.readFile(path+"/"+name).toString();
			  org.girlscouts.vtk.auth.models.User User = new org.girlscouts.vtk.auth.models.User();
			  String userName= name.substring( name.indexOf("_")+1 , name.indexOf(".") );
			  org.girlscouts.vtk.auth.models.ApiConfig apiConfig = new org.girlscouts.vtk.auth.models.ApiConfig();
			  apiConfig.setDemoUser(true);
			  apiConfig.setDemoUserName( userName );
			  try{
				   User= sfDAO.getUser_parse(User, apiConfig, rsp);
				   %>
					  <tr>
							 <td><%=User.getName() %></td>
							 <td>
								  <%= User.isAdmin() ? "Admin," : "" %>
								   <%
								   java.util.Set<String> roles = new java.util.HashSet<String>();
								   java.util.List<org.girlscouts.vtk.salesforce.Troop> troops= apiConfig.getTroops();
								   for(int ii=0;ii<troops.size();ii++){
									   org.girlscouts.vtk.salesforce.Troop _troop = troops.get(ii);
									   roles.add( _troop.getRole() );
									   if (showTroopPermDetails){
										   out.println("<li>Troop :"+_troop.getTroopName()+" Role: "+_troop.getRole());
									   }
								   }

								   if (!showTroopPermDetails){
									   java.util.Iterator itr= roles.iterator();
									   while( itr.hasNext() ){
										   String rl= (String) itr.next();
										   if( rl==null) continue;
										   String rl_deff= permisDeff.get( rl );

										   %><%=(rl_deff==null) ? rl : rl_deff %> <%= itr.hasNext() ? "," : ""%><%
									   }
								   }
								   %>
							 </td>
							 <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=<%=userName%>">VTK</a></td>
							 <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=<%=userName %>&isGroupDemo=true">VTK</a></td>
							 <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=<%=vTroop %>&user=<%=userName %>&isGroupDemo=true">VTK</a></td>
                             
							 <td><a href="?showDetails=true&rmUser=<%=userName %>">Remove</a></td>

					  </tr>
				   <%
			  }catch(Exception e){e.printStackTrace();
		  }
	  }
	 }
	}

%>

</table>
</div>

<%}//edn if showDetails %>

</div>


<div id="myModal" class="reveal-modal tiny" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
  <h2 id="modalTitle">Are You Sure?</h2>
  <p>If you restart the demo, any changes you have made will be lost.</p>
  <!-- <p>I'm a cool paragraph that lives inside of an even cooler modal. Wins!</p> -->
  <a class="close-reveal-modal" aria-label="Close">&#215;</a>

  <a class="button radius success right tiny" href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html">Restart</a>
<a class="button secondary right tiny" onclick="$('a.close-reveal-modal').trigger('click');
">Cancel</a>
</div>

<script>
	$(document).foundation();
</script>


