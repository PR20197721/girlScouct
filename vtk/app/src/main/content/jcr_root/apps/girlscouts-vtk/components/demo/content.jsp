<script>
function checkAgeGroup( _url, ageGroupElem){
	
	var isSelected = isAgeGroupSelected(ageGroupElem);
	if( !isSelected ){
		alert("Please select grade first");
	}else{
		var e = document.getElementById(ageGroupElem);
	    var ageGroup = e.options[e.selectedIndex].value; 
	    self.location= _url + ageGroup;
	}
	return isSelected;
}

function isAgeGroupSelected(ageGroupElem){
	var e = document.getElementById(ageGroupElem);
	var ageGroup = e.options[e.selectedIndex].value;
	if( ageGroup ==null || ageGroup =='' ){
		return false;
	}
	return true;
}

function goToUrl(elem){
	var e = document.getElementById(elem);
    var _url = e.options[e.selectedIndex].value;
   // alert( _url );
    self.location= _url;
}
</script>

<%@page import="java.util.HashMap"%>
<%




session.putValue("VTK_troop",null);
session.putValue(org.girlscouts.vtk.auth.models.User.class.getName(),null);
session.putValue(org.girlscouts.vtk.auth.models.ApiConfig.class.getName(), null);
session.putValue(org.girlscouts.vtk.models.User.class.getName(), null);
      

Cookie killMyCookie = new Cookie("girl-scout-name", null);
killMyCookie.setMaxAge(0);
killMyCookie.setPath("/");
response.addCookie(killMyCookie);

if( session== null || session.getAttribute("demoSiteUser")==null){ %>
       
<div id="main" class="row content">
    
    <div class=" vtk-demo-form-box columns small-20 small-centered medium-10 medium-centered">
        
        <div class="vtk-demo-form-top">
            <!-- <img src="" alt=""> THE GIRLSCOUT LOGO-->
        </div>


        <div class="div-demo-form-message" style="display:<%=request.getParameter("err") == null ? "none;" : "" %>">
          <div data-alert class="alert-box alert ">
                 <%=request.getParameter("err") == null ? "" : request.getParameter("err") %>
          </div>
        </div>
  
        <div class="vtk-demo-login-form">
        <h4 style="font-size: 20px;line-height: 22px;margin-bottom: 20px;">Welcome.</h4>

          <form action="/content/girlscouts-demo/en/jcr:content" method="POST">
                     <h4 style="font-size: 20px;line-height: 26px;">Please enter your council's password to access the Volunteer Toolkit Demo.</h4>
<!--             <div class="vtk-demo-form-input">
              <div class="vtk-demo-form-label">Username</div>
              <input type="text" name="u" value=""/>
            </div> -->
            
            <div class="vtk-demo-form-input">
              <!-- <div class="vtk-demo-form-label">Password</div> -->
              <input type="password" name="p" value=""/>
            </div>
            
            <div class="vtk-demo-form-input">
              <input class="button tiny" type="submit" value="LOG IN" name="login"/>
              <!-- <a class="vtk-forgot-link" href="#">Forgot password</a> -->
            </div>
       
        </form>
        </div>
    </div>
</div>


<% 
return;
}//end if




String vTroop = request.getParameter("vTroop") ==null ? "" : request.getParameter("vTroop");
String path="/usr/local/vtk"; 
java.io.File folder = new java.io.File(path);
java.io.File[] listOfFiles = folder.listFiles();


java.util.Map<String, String> permisDeff = new java.util.TreeMap<String, String>();
permisDeff.put("DP", "Troop Leader");
permisDeff.put("PA", "Parent");

org.girlscouts.vtk.auth.dao.SalesforceDAO sfDAO = new org.girlscouts.vtk.auth.dao.SalesforceDAO(null, null, sling.getService(org.girlscouts.vtk.ejb.SessionFactory.class).getSession());
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

    <div class="vtk-demo-select row" style="display:none;">
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
        <div class="columns small-push-2 small-20 end">
                Plan quickly. Save time. Stay organized.<br>
                <p>The Volunteer Toolkit (VTK) gives Troop Leaders the tools and features they need for troop management and program delivery. Parents have their own view of VTK as well. Go ahead and play around in our demo environment using any device - desktop, laptop, smart phone or tablet.</p>
        </div>
    </div>
  <!-- / info -->

    <div class="row vtk-demo-wrap-bottom">
        <div class="columns small-24">
            <div class="row">
        
              
               <div class="vtk-demo-card columns  small-24  medium-push-2 medium-10 end">
                                      <div class="vtk-header-box">
                                        <a href="javascript:void(0)" onclick="return goToUrl('age_group_parent')">Parents </a>
                                      </div>
                                      <p>Key Features:</p>
                                      <ul>
                                        <li>View troop plans and content (read only)</li>
                                        <li>See your girl's achievements.</li>
                                        <li>Review your contact information.</li>
                                        <li>Download meeting aids and resources.</li>
                                      </ul>
                                      <br />
                                       <p style="margin-top:4px;font-weight:bold; font-size:14px;">Start the demo by selecting a grade level</p>

                                      <div id="vtk-dropdown-2" class="vtk-demo-dropdown" data-input-name="value1" style="width:100%">
                                          <div class="vtk-demo-dropdown_main"><div class="selected-option">GIRL SCOUT GRADE LEVELS </div>
                                             <span class="icon-arrow-down3" style="color:#009447;position: absolute; top: 6px; right: 6px;font-size:14px"></span>
                                         </div>
                                          <ul class="vtk-demo-dropdown_options">
                                            <li data-value="">Please select...</li>

                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Marcy&prefGradeLevel=1-Daisy">DAISY <span>grades k-1</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Gina&prefGradeLevel=2-Brownie">BROWNIE <span>grades 2-3</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Meredith&prefGradeLevel=3-Junior">JUNIOR <span>grades 4-5</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Luisa&prefGradeLevel=4-Cadette">CADETTE <span>grades 6-8</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Jennifer&prefGradeLevel=5-Senior">SENIOR <span>grades 9-10</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Felicia&prefGradeLevel=6-Ambassador">AMBASSADOR <span>grades 11-12</span></li>

                                          </ul>
                                      </div>


                                   </div>
                                  <!-- / Parents -->
              
              
              
              
              
              <div class="vtk-demo-card columns small-24 medium-push-2 medium-10">
                                        <div class="vtk-header-box">
                                          <a href="javascript:void(0)" onclick="return goToUrl('age_group_dp')">Troop Leader</a>
                                        </div>
                                
                                        <p>Key Features:</p>
                                        <ul>
                                          <li>Pre-populated Year Plans with meeting content.</li>
                                          <li>Add you own troop activities or council event.</li>
                                          <li>View your troop roster.</li>
                                          <li>Email meeting reminders to parents.</li>
                                          <li>Track girls' attendance and achievements.</li>
                                        </ul>


                                         <p style="font-weight:bold; font-size:14px;">Start the demo by selecting a grade level</p>



                                        <div id="vtk-dropdown-3" class="vtk-demo-dropdown" data-input-name="value1" style="width:100%">
                                          <div class="vtk-demo-dropdown_main"><div class="selected-option">GIRL SCOUT GRADE LEVELS </div>
                                             <span class="icon-arrow-down3" style="color:#009447;position: absolute; top: 6px; right: 6px;font-size:14px"></span>
                                         </div>
                                          <ul class="vtk-demo-dropdown_options">
                                          <li data-value="">Please select...</li>

                                            
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Lisa&prefGradeLevel=1-Daisy">DAISY <span>grades k-1</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Martha&prefGradeLevel=2-Brownie">BROWNIE <span>grades 2-3</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Jenny&prefGradeLevel=3-Junior">JUNIOR <span>grades 4-5</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Victoria&prefGradeLevel=4-Cadette">CADETTE <span>grades 6-8</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Laura&prefGradeLevel=5-Senior">SENIOR <span>grades 9-10</span></li>
                                            <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Penelope&prefGradeLevel=6-Ambassador">AMBASSADOR <span>grades 11-12</span></li>

                                          </ul>
                                      </div>


                                  </div>
                                  <!-- / Troop Leader --> 
              
              
              
            </div>
        </div>
    </div>

    <div class="row">
        <div class="vtk-bottom-frase columns small-24">

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

    <p>The Volunteer Toolkit (VTK) mirrors the features and functionality available in the VTK. Please note this demo does not contain real girl data. Additionaly.</p>
  
    <a class="close-reveal-modal" aria-label="Close">&#215;</a>

    <a class="button radius success right tiny" href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html">Restart</a>
    <a class="button secondary right tiny" onclick="$('a.close-reveal-modal').trigger('click');">Cancel</a>
</div>





<div id="myModal-demo" class="reveal-modal medium" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
  <h2 id="modalTitle" style="font-size:18px; color:white;padding:10px 30px;background-color:green">VTK DEMO DETAILS</h2>

  <div class="container-model">
    <p>The Volunteer Toolkit (VTK) mirrors the features and functionality available in the VTK. Please note this demo does not contain real girl data. Additionally, certain features of the VTK have been disabled such as sending an email to parents/care</p>

  <p>Information presented on this website is for demo purposes only. Any customizations you make will not be saved. The content presented is the property of GSUSA.</p>

  <p>We hope you enjoy the demo.</p>


  <br>
 <br>
 <br>
  

  </div>



  <a class="close-reveal-modal" aria-label="Close"><i style="color:white" class="icon-button-circle-cross"></i></a>

 </div>


<script>
    
    $(function(){
      $(document).foundation();
    //  showWelcomePop();
      gsusa.component.dropDown('#vtk-dropdown-2',{local:true});
      gsusa.component.dropDown('#vtk-dropdown-3',{local:true});
      
    });

    function showWelcomePop(){
	    if(typeof(Storage) !== "undefined") {
	        var isShowWecomePop = localStorage.getItem("isShowDemoWelcomePop");
	        if( isShowWecomePop!=null && isShowWecomePop!='' && isShowWecomePop== 'true'){
	            
	            localStorage.setItem("isShowDemoWelcomePop", "false");
	            displayWelcomePop();
	        }
	    } 
    }
    
    function displayWelcomePop(){
    	 $('#myModal-demo').foundation('reveal', 'open');
    }
    
</script>

<style>
#myModal-demo{
 padding:0px; 
 top:0px;
}

.container-model{
  padding: 10px 30px;
}

.close-reveal-modal{
  top:-5px !important;
}



</style>
