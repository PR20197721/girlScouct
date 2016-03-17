<div id="main" class="row collapse">

<div style="clear: both"></div>
<!-- vtk start -->

<div class="vtk-demo-wrap row">

	<div class="vtk-demo-wrap-top row">
		<div class="columns small-push-3 small-18 end">
			<h2>
			  	Plan quickly. Save Time. Stay Organized.
			</h2>
			<h1>
			  	Everyone benefits when they know how to use the volunteer Toolkit.
			</h1>
		</div>
	</div>
  <!-- / info -->

	
<div class="row vtk-demo-wrap-bottom">
	
  <div class="vtk-demo-card columns small-24 medium-push-3 medium-6">
		<div class="vtk-header-box">
		  <a href="">Troop Leader <span class="float-right icon-button-arrow-right"></a>
		</div>
		
		<p>Everything ready, right at your fingertips to save time:</p>
		<ul>
		  <li>Pre-populated plans.</li>
		  <li>Add you own activities.</li>
		  <li>See your troop roster.</li>
		  <li>Track girls' achievements and attendance.</li>
		</ul>
  </div>
  <!-- / Troop Leader -->

  <div class="vtk-demo-card columns small-24  medium-push-3 medium-6">
	  <div class="vtk-header-box">
		<a href="">Parents <span class="float-right icon-button-arrow-right"></a>
	  </div>
	
	  <p>Check in on the troop and see what your girl needs for meetings:</p>
	  <ul>
		<li>View troop plans (easy-to use calendar)</li>
		<li>Stay In touch with the troop leader and volunteers.</li>
		<li>Find ways to help the troop.</li>
	  </ul>
  </div>
  <!-- / Parents -->

  <div class="vtk-demo-card columns small-24 medium-push-3 medium-6 end">
	  <div class="vtk-header-box">
		<a href="">Council Admin <span class="float-right icon-button-arrow-right"></a>
	  </div>
	
	  <p>Material and aids are organized to help everyone work together:</p>
	  <ul>
		<li>Upload materials to support the troop Leaders.</li>
		<li>Report in troop finances.</li>
		<li>Stay organized across troops.</li>
	  </ul>
  </div>
  <!-- / Council Admin -->
</div>



</div>



<!-- Erase -->
<div class="vtk-table-container">

<div class="table-action-button">
  <a href="?showTroopPermDetails=ad">show permission details</a> <a href="/content/girlscouts-vtk/en/vtk.html?useAsDemo=true">Add/Update User</a>
</div>  


<table align="center">
 <tr>
	<th>**User name</th>
	<th>Permissions</th>
	<th>Kiosk mode</th>
	<th>Shared mode</th>
	<th>Remove User</th>
 </tr>
<%
String path="/Users/akobovich/vtk";
java.io.File folder = new java.io.File(path);
java.io.File[] listOfFiles = folder.listFiles();


if( request.getParameter("rmUser")!=null){
	
	 for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			  String name= listOfFiles[i].getName();
			  if( name.contains("_"+  request.getParameter("rmUser") +".json") ){
				  listOfFiles[i].delete();
			  }
		  }//ednif
	 }//end for
}//end if   





java.util.Map<String, String> permisDeff = new java.util.TreeMap<String, String>();
permisDeff.put("DP", "Troop Leader");
permisDeff.put("PA", "Parent");


org.girlscouts.vtk.auth.dao.SalesforceDAO sfDAO = new org.girlscouts.vtk.auth.dao.SalesforceDAO(null, null);
boolean  showTroopPermDetails  = request.getParameter("showTroopPermDetails") !=null ? true : false;
HttpSession hsession = request.getSession();
//org.girlscouts.vtk.models.User user= org.girlscouts.vtk.utils.VtkUtil.getUser( hsession );

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
			  /*try {
				  if (hsession.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()) != null) {
					  apiConfig = ((org.girlscouts.vtk.auth.models.ApiConfig) session.getAttribute(org.girlscouts.vtk.auth.models.ApiConfig.class.getName()));
				  } else {
					 //return null;
				  }
			  } catch (ClassCastException cce) {
				  //return null;
			  } 
			  */
			  
			  try{
				   User= sfDAO.getUser_parse(User, apiConfig, rsp);
				   //System.err.println("tata ss : "+apiConfig.getTroops().size());
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
										   String rl_deff= permisDeff.get( rl );
										   
										   %><%=(rl_deff==null) ? rl : rl_deff %> <%= itr.hasNext() ? "," : ""%><%
									   }
								   }
										
								   
								   %>
							 </td>
							 <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=<%=userName%>">VTK</a></td>
							 <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=<%=userName %>&isGroupDemo=true">Group VTK</a></td>
							 <td><a href="?rmUser=<%=userName %>">Remove</a></td> 
							
					  </tr>
				   <% 
			  }catch(Exception e){e.printStackTrace();
		  }
		  
		  
		  /*
		  if( name!=null && name.startsWith("vtkTroop")){
				  //out.println("<li>File " + listOfFiles[i].getName());
				  
				  String rsp= sfDAO.readFile(path+"/"+name).toString();
				 // out.println(rsp);
				  try{
				   java.util.List<org.girlscouts.vtk.salesforce.Troop> troops= sfDAO.troopInfo_parse(user.getApiConfig().getUser(), rsp);
				   //out.println("Troops: "+  troops.size() );
				   
				   %>
					<tr>
						  <td><%=name %></td> 
						  <td>    <%= user.getApiConfig().getUser().isAdmin() ? "<li> User is Admin</li>" : "" %>        
				   <% 
				   java.util.Set<String> roles = new java.util.HashSet<String>();
				   for(int ii=0;ii<troops.size();ii++){
					   org.girlscouts.vtk.salesforce.Troop _troop = troops.get(ii);
					   roles.add( _troop.getRole() );
					   if (showTroopPermDetails){
						   out.println("<li>Troop :"+_troop.getTroopName()+" Role: "+_troop.getRole());
					   }
				   }
				   
				   if (!showTroopPermDetails){%><%=roles%><%}
				   %>
				   </td>
				   <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=Alice">VTK</a></td>
				   <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=Alice&isGroupDemo=true">Group VTK</a></td>
				  
				 </tr>
				 <%
				  }catch(Exception e){e.printStackTrace();}
		  }
		  */
	  } 
	}
	}


%>

</table>
</div>

<!-- vtk end -->

</div>


