<div id="main" class="row collapse">

<!-- vtk start -->

<a href="?showTroopPermDetails=ad">show permission details</a>
<br/><a href="/content/girlscouts-vtk/en/vtk.html?useAsDemo=true">Add/Update User</a>



<table border="1">
 <tr>
    <th>User name</th>
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
                             <td><a href="/content/girlscouts-vtk/controllers/vtk.demo.index.html?user=<%=userName %>&isGroupDemo=true">VTK</a></td>
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

<!-- vtk end -->

</div>


