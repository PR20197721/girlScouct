<!doctype html>
<html class="no-js" lang="en">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
        <title>Foundation | Welcome</title>
<!-- Artifact Browser -->
<!--[if lt IE 9]>
        <link rel="stylesheet" href="css/foundation-ie8.css" />
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
        <link rel="stylesheet" href="css/foundation.css" />
<!--<![endif]-->
        <link rel="stylesheet" href="css/app.css" />
        <script src="js/vendor/modernizr.js"></script>
        
       
       
 
<link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
  <script src="//code.jquery.com/jquery-1.10.2.js"></script>
  <script src="//code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
  
 
  
 
       
       
        <script>
         
        	
        	
        	
        		 function x(planId){
        			
        		    $("#div1").load("/VTK/include/meeting.jsp?planId="+planId);
        		  }
        		 
        		 function testIt(){
        			 var s =  document.getElementById("sortable");
        			 alert(s);
        			 s.sortable=true
        			 alert(s.sortable ==true)
        			 /*  $("#sortable1234").sortable();  */
        			 
					}
        		
        </script>
      
       
        
        
        
        
        
        
        
    </head>
    <body>
    
    
     
    
        <div class="off-canvas-wrap">
            <div class="inner-wrap">
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
                <aside class="right-off-canvas-menu">
                    <ul class="off-canvas-list">
                        <li><label class="first">Foundation</label></li>
                        <li><a href="index.html">Home</a></li>
                    </ul>
                    <hr>
                    <ul class="off-canvas-list">
                        <li><label class="first">Learn</label></li>
                        <li><a href="learn/features.html">Features</a></li>
                        <li><a href="learn/faq.html">FAQ</a></li>
                    </ul>
                    <hr>
                    <ul class="off-canvas-list">
                        <li><label>Develop</label></li>
                        <li><a href="templates.html">Add-ons</a></li>
                        <li><a href="docs">Docs</a></li>
                    </ul>
                    <hr>
                    <div class="zurb-links">
                        <ul class="top">
                            <li><a href="http://zurb.com/about">About</a></li>
                            <li><a href="http://zurb.com/blog">Blog</a></li>
                            <li><a href="http://zurb.com/contact">Contact</a></li>
                        </ul>
                    </div>
                </aside>
<!--<![endif]-->

<!--PAGE STRUCTURE: HEADER-->
                <div id="header" class="row">
                    <div class="large-4 medium-5 small-24 columns">
<!-- Artifact Browser -->
<!--[if lt IE 9]>
                        <nav class="logoLarge">
                            <img src="images/gateway-logo.png" width="188" height="73"/>
                        </nav>
<![endif]-->
<!-- Modern Browser -->
<!--[if gt IE 8]><!-->
                        <nav class="hide-for-small logoLarge">
                            <img src="images/gateway-logo.png" width="188" height="73"/>
                        </nav>
                        <nav class="show-for-small logoSmall">
                            <center>
                            <img src="images/gateway-logo-small.png" width="38" height="38"/>
                            </center>
                        </nav>
<!--<![endif]-->
                    </div>
                    <div class="large-20 medium-19 hide-for-small columns topMessage">
                        <ul class="inline-list">
                            <li><a href="/about">About Our Council</a></li>
                            <li><a href="/shop">Shop</a></li>
                            <li><a href="/forms">Forms</a></li>
                            <li><a href="/calendar">Calendar</a></li>
                            <li><a href="/contact">Contact</a></li>
                            <li><a href="/espanol">Espanol</a></li>
                        </ul>
                        <div class="row">
                            <div class="large-17 medium-17 columns">
                                <span>Hello Sandy.</span> <a href="/signout" class="signout">SIGN OUT</a>
                            </div>
                            <div class="large-7 medium-7 columns searchBar">
                                <form action="/search" method="get">
                                    <input type="text" class="searchField"/>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="show-for-small small-24 columns topMessage alt">
                        <div class="row">
                            <div class="small-18 columns">
                                <span>Hello Sandy.</span> <a href="/signout" class="signout">SIGN OUT</a>
                            </div>
                            <div class="small-6 columns">
                                    <a class="right-off-canvas-toggle menu-icon"><img src="images/magnifyer-small.png" width="21" height="21"/></a>
                                    <a class="right-off-canvas-toggle menu-icon"><img src="images/hamburger.png" width="22" height="28"/></a>

                            </div>

                        </div>
                    </div>
                </div>
<!--PAGE STRUCTURE: HEADER BAR-->
                <div id="headerBar" class="row">
                    <div class="large-24 medium-24 hide-for-small columns" >
                        <ul class="inline-list">
                            <li><a class="menu" href="/aboutgs">ABOUT GIRL SCOUTS</a></li>
                            <li><a class="menu" href="/events">EVENTS&nbsp;&amp;&nbsp; ACTIVITIES</a></li>
                            <li><a class="menu" href="/camp">CAMP</a></li>
                            <li><a class="menu" href="/cookies">COOKIES</a></li>
                            <li><a class="menu" href="/volunteers">FOR&nbsp;VOLUNTEERS</a></li>
                            <li><a class="menu menuHighlight" href="/leader">My&nbsp;GS</a></li>
                        </ul>
                    </div>
                </div>
<!--PAGE STRUCTURE: MAIN-->
                <div id="main" class="row">
                
                <div style="background-color:fff; ">
                
                <%=new java.util.Date()%>
       
       
       
                
<dl class="tabs" data-tab >
  <dd ><a href="#panel2-1">My Troup</a></dd>
  <dd class="active"><a href="#panel2-2">Year Plan</a></dd>
  <dd><a href="#panel2-3">Meeting Plan</a></dd>
  <dd><a href="#panel2-4">Resources</a></dd>
  <dd><a href="#panel2-5">Community</a></dd>
</dl>
<div class="tabs-content">
  <div class="content" id="panel2-1">
    <p>First panel content goes here...
   
    <a href="javascript:void(0)" onclick="x()">test</a>
    
    
    
    </p>
  </div>
  <div class="content active" id="panel2-2">
  
  <div id="div1">
    <p><%@include file="include/yearPlan.jsp" %>
    
 <ul id="sortable123">
  <li value="1">Meeting 1</li>
  <li value="2">Meeting 2</li>
  <li value="3">Meeting 3</li>
  <li value="4">Meeting 4</li>
 </ul>
     <input type="button" onclick="doUpdMeeting()"/>
    </div>
     
     
    </p>
  </div>
  
   <script>
 
   $("#sortable123").sortable(
		   
		   {
		       
		        update:  function (event, ui) {
		        	doUpdMeeting()
		        }
		}
   );
   

   
   function getNewMeetingSetup() {
	   var toRet="";
       var liTags = document.getElementById ("sortable123").getElementsByTagName ("li");
       for (var i = 0; i < liTags.length; i++) {
    	   //toRet+= i+":"+ liTags[i].value +"," ; /*JSON*/
    	   toRet+=  liTags[i].value +"," ; 
       }
       return toRet.substring(0, toRet.length-1);
   }
   
   function doUpdMeeting(){
	
	   var newVals = getNewMeetingSetup();
	   alert( newVals );
	   //$("#div1").load("/VTK/include/controller.jsp?isMeetingCngAjax"+ newVals);
	   
	   
	   var x =$.ajax({ // ajax call starts
	          url: '/VTK/include/controller.jsp?isMeetingCngAjax='+ newVals, // JQuery loads serverside.php
	          data: '', // Send value of the clicked button
	          dataType: 'json', // Choosing a JSON datatype
	          success: function (data) { 
	              
	              console.log("OK");
	             
	          },
	   			error: function (data) { 
	   				console.log("BAD");
	   				
	   			}
	      });
	     
	 
	   reloadMeeting();
		
		
		
   }
   
   function reloadMeeting(){
	   
	   $("#div1").load("/VTK/include/meeting.jsp?isRefresh=true");
   }
   
   </script>
    
 
  <div class="content" id="panel2-3">
    <p>Third panel content goes here...</p>
  </div>
  <div class="content" id="panel2-4">
    <p>Fourth panel content goes here...</p>
  </div>
  <div class="content" id="panel2-5">
    <p>Fourth panel content goes here...</p>
  </div>
</div>

<br/></br>

    

                </div>





                
                    <div class="large-24 medium-24 small-24 columns" style="display:none;">
                        <div class="row">
                            <div id="heroBanner" class="large-24 medium-24 small-24 columns">
                                <img src="images/welcome.png" class="hide-for-small hide-for-medium"/>
                                <img src="images/welcome-medium.png" class="show-for-medium"/>
                                <img src="images/welcome-small.png" class="show-for-small"/>
                            </div>
                        </div>
                        <br/><br/>
                        <div class="row home-section feature-shortstory">
                            <div class="small-24 medium-24 large-24 columns">
                                <div class="row">
                                    <div class="hide-for-small hide-for-medium large-24 columns">
                                        <div class="feature-icon">
                                            <img src="images/trefoil-icon.png" width="50" height="50"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">BE A GIRL SCOUT</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-8 show-for-medium columns">&nbsp;</div>
                                    <div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
                                        <div class="feature-icon">
                                            <img src="images/arrow-down.png" width="30" height="30"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">BE A GIRL SCOUT</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-4 show-for-medium columns">&nbsp;</div>
                                </div>
                                <div class="row content">
                                    <div class="small-24 medium-12 large-12 columns">
                                        <p>Girls Scouts of Arkansas, Oklahoma and Texas is where girls discover the fun, friendship and power of working together.Thought extraordinary field trips, sports, skill-building clinics community service projects, cultural exchanges and environlemt stewardships, our girls courageous and strong.</p>
                                        <a href="/">Continue &gt;</a>
                                        <br/><br/>
                                    </div>
                                    <div class="small-24 medium-12 large-12 columns">
                                       <img src="images/medium-lifevests.png" width="800"/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br/><br/>
                        <div class="row home-section feature-shortstory">
                            <div class="small-24 medium-24 large-24 columns">
                                <div class="row">
                                    <div class="hide-for-small hide-for-medium large-24 columns">
                                        <div class="feature-icon">
                                            <img src="images/heart-icon.png" width="50" height="50"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">VOLUNTEER</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-8 show-for-medium columns">&nbsp;</div>
                                    <div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
                                        <div class="feature-icon">
                                            <img src="images/arrow-down.png" width="30" height="30"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">VOLUNTEER</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-4 show-for-medium columns">&nbsp;</div>
                                </div>
                                <div class="row content">
                                    <div class="small-24 medium-12 large-12 columns">
                                       <img src="images/medium-planting.png" width="800"/>
                                    </div>
                                    <div class="small-24 medium-12 large-12 columns">
                                        <p>Girls Scouts of Arkansas, Oklahoma and Texass is where girls discover the fun, friendship and power of working together.
                    Thought extraordinary field trips, sports, skill-building clinics community service projects, cultural exchanges and environlemt stewardships, our girls courageous and strong.</p>
                                        <a href="/">Continue &gt;</a>
                                        <br/><br/>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <br/><br/>
                        <div class="row home-section events-list">
                            <div class="small-24 medium-24 large-24 columns">
                                <div class="row">
                                    <div class="hide-for-small hide-for-medium large-24 columns">
                                        <div class="feature-icon">
                                            <img src="images/heart-icon.png" width="50" height="50"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">UPCOMING EVENTS</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-8 show-for-medium columns">&nbsp;</div>
                                    <div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
                                        <div class="feature-icon">
                                            <img src="images/arrow-down.png" width="30" height="30"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">UPCOMING EVENTS</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-4 show-for-medium columns">&nbsp;</div>
                                </div>
                                <ul class="small-block-grid-1 medium-block-grid-1  large-block-grid-2 content">
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-12 large-8 columns">
                                                <img src="images/small-snowkids.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-12 large-16 columns">
                                                <h3><a href="http://www.google.com">Winter Jam</a></h3>
                                                <p>Time: 6:00 pm to 2:00 pm</p>
                                                <p>Date: Fri 10 Jan to Sat 11 Jan 2014</p>
                                                <p>Location: Camp Lakamanga</p>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-12 large-8 columns">
                                                <img src="images/small-trio.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-12 large-16 columns">
                                                <h3><a href="http://www.google.com">Winter Jam</a></h3>
                                                <p>Time: 6:00 pm to 2:00 pm</p>
                                                <p>Date: Fri 10 Jan to Sat 11 Jan 2014</p>
                                                <p>Location: Camp Lakamanga</p>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-12 large-8 columns">
                                                <img src="images/small-campfire.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-12 large-16 columns">
                                                <h3><a href="http://www.google.com">Winter Jam</a></h3>
                                                <p>Time: 6:00 pm to 2:00 pm</p>
                                                <p>Date: Fri 10 Jan to Sat 11 Jan 2014</p>
                                                <p>Location: Camp Lakamanga</p>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-12 large-8 columns">
                                                <img src="images/small-hiking.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-12 large-16 columns">
                                                <h3><a href="http://www.google.com">Winter Jam</a></h3>
                                                <p>Time: 6:00 pm to 2:00 pm</p>
                                                <p>Date: Fri 10 Jan to Sat 11 Jan 2014</p>
                                                <p>Location: Camp Lakamanga</p>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <br/><br/>
                        <div class="row home-section news-list">
                            <div class="small-24 medium-24 large-24 columns">
                                <div class="row">
                                    <div class="hide-for-small hide-for-medium large-24 columns">
                                        <div class="feature-icon">
                                            <img src="images/heart-icon.png" width="50" height="50"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">COUNCIL NEWS</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-8 show-for-medium columns">&nbsp;</div>
                                    <div class="small-24 medium-12 hide-for-large  hide-for-xlarge hide-for-xxlarge columns">
                                        <div class="feature-icon">
                                            <img src="images/arrow-down.png" width="30" height="30"/>
                                        </div>
                                        <div class="feature-title">
                                            <h2><a href="http://yahoo.com">COUNCIL NEWS</a></h2>
                                        </div>
                                    </div>
                                    <div class="medium-4 show-for-medium columns">&nbsp;</div>
                                </div>
                                <ul class="small-block-grid-1 content">
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-8 large-6 columns">
                                                <img src="images/small-scout.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-16 large-18 columns">
                                                <h3><a href="http://www.google.com">Cookie Skills Translate to Life Skills</a></h3>
                                                <p>17 Dec 2013</p>
                                                <p>Almost everyone knows Girl Scouts Cookies, but some don't realize that the Girl Scout Cookie Program is the largest girl-run business in the world.  Through learning by earning, Girl Scouting aims...</p>
                                            </div>
                                        </div>
                                    </li>
                                    <li>
                                        <div class="row">
                                            <div class="small-24 medium-8 large-6 columns">
                                                <img src="images/small-leader.png" width="483" height="305">
                                            </div>
                                            <div class="small-24 medium-16 large-18 columns">
                                                <h3><a href="http://www.google.com">Cookie Skills Translate to Life Skills</a></h3>
                                                <p>17 Dec 2013</p>
                                                <p>Almost everyone knows Girl Scouts Cookies, but some don't realize that the Girl Scout Cookie Program is the largest girl-run business in the world.  Through learning by earning, Girl Scouting aims...</p>
                                            </div>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
<!--PAGE STRUCTURE: FOOTER-->
                <div id="footer" class="row">
                    <div class="large-24 medium-24 small-24 columns">
                        <a class="menu" href="/privacy">Privacy Policy</a>
                        <a class="menu" href="/terms">Terms and Conditions</a>
                    </div>
                </div>
            </div>
        </div>
    <script src="js/vendor/jquery.js"></script>
    <script src="js/foundation.min.js"></script>
    <script>
      $(document).foundation();
     
     
    </script>
   
    </body>
</html>
