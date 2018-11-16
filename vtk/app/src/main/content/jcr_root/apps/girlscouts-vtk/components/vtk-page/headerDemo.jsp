<script>document.title="VTK Demo Site"</script>

<%
boolean showExtra= false;
boolean isDp= false;
if( org.girlscouts.vtk.utils.VtkUtil.getApiConfig(session) !=null ){
	showExtra= true;
	if( org.girlscouts.vtk.utils.VtkUtil.getApiConfig(session).getTroops().get(0).getPermissionTokens().contains(270) ){
		isDp=true;
    }
}

%>


<div class="header-wrapper row collapse hide-for-print">
    <div class="columns">
        <div id="header" style="width:188,height:65.8px; padding: 22px 0;" class="row">
            <div class="large-6 medium-9 columns">
                <div class="logo">
                    <!-- Artifact Browser -->
                    <!--[if lt IE 9]>
                      <nav class="logoLarge">
                        <img src="/content/dam/gssjc/sanj_green_logo.png" alt="Home"/>
                      </nav>
                    <![endif]-->
                    <!-- Modern Browser -->
                    <!--[if gt IE 8]><!-->
                    <nav class="column large-24 medium-24">
                        <link type="text/css" rel="stylesheet" href="/etc/designs/girlscouts-vtk/clientlibs/css/_demo.css">
                        <div class="vtk-demo-logo" style="width:188,height:73">
                            <!-- <img class="vtk-demo-logo-img" src="/etc/designs/girlscouts-vtk/images/log_demo.png" alt="Home" id="logoImg"> -->
                            <a href="/content/gssjc/en.html">
                                <img src="/content/dam/girlscouts-gsusa/images/logo/logo@2x.png" alt="Home" id="logoImg" width="188" height="73">
                            </a>
                        </div>
                    </nav>
                    <!--<![endif]-->
                </div>
                <div class="placeholder"></div>
            </div>
            <div class="large-18 medium-15 hide-for-small columns topMessage">

<% if(false){//  session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) ==null ){ %>
    <a class="button tiny" style="background-color:white !important; color:#009447; font-size: 12px; margin: 0 0 10px 0; padding: 5px 10px; <%=session.getAttribute("demoSiteUser")==null && session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) ==null ? " display:none; " : "" %>" href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html?isLogout=true">Log Out</a>
<%} %>

               <div <%=session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) ==null ? " style=\"display:none;\" " : "" %>>
                  <div class="eyebrow-nav navigation-bar eyebrow-navigation">
                    <ul class="inline-list eyebrow-fontsize">

                    </ul>
                  </div>






               </div>
               <div class="row collapse hide-for-medium">
                    <div class="large-17 medium-17 small-24 columns">
                        <div class="vtk-vounteer-trainer"<%=session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) ==null ? " style=\"margin-top:55px;\" " : " style=\"margin-top:40px;\" " %> >
                            Volunteer Toolkit Demo
                        </div>
                    </div>
                </div>
            </div>


                  <div class="vtk-control-panel hide-for-small" <%=session.getAttribute(org.girlscouts.vtk.models.User.class.getName()) ==null ? " style=\"display:none !important; \" " : "" %>>

                      <h6>VTK DEMO CONTROL PANEL</h6>

                      <p>Current view: <%= isDp ? "Troop Leader" : "Parent" %> </p>

                                      <div id="vtk-dropdown-1" class="vtk-demo-dropdown" data-input-name="value1" style="">
                      <div class="vtk-demo-dropdown_main">
                        <div class="selected-option">GIRL SCOUT GRADE LEVELS </div>
                        <span class="vtk-icon-arrow" style="">
                        </span>
                      </div>

                      <ul class="vtk-demo-dropdown_options" >
                        <li data-value="">Please select...</li>
                    <% if( isDp ){%>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Lisa&prefGradeLevel=1-Daisy">DAISY <span>grades K-1</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Martha&prefGradeLevel=2-Brownie">BROWNIE <span>grades 2-3</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Jenny&prefGradeLevel=3-Junior">JUNIOR <span>grades 4-5</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Victoria&prefGradeLevel=4-Cadette">CADETTE <span>grades 6-8</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Laura&prefGradeLevel=5-Senior">SENIOR <span>grades 9-10</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Penelope&prefGradeLevel=6-Ambassador">AMBASSADOR <span>grades 11-12</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Deb&prefGradeLevel=7-Milti-level">MULTI-LEVEL<span>MULTI-LEVEL</span></li>
                    <% }else{%>

                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Marcy&prefGradeLevel=1-Daisy">DAISY <span>grades K-1</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Gina&prefGradeLevel=2-Brownie">BROWNIE <span>grades 2-3</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Meredith&prefGradeLevel=3-Junior">JUNIOR <span>grades 4-5</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Luisa&prefGradeLevel=4-Cadette">CADETTE <span>grades 6-8</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Jennifer&prefGradeLevel=5-Senior">SENIOR <span>grades 9-10</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Felicia&prefGradeLevel=6-Ambassador">AMBASSADOR <span>grades 11-12</span></li>
                        <li data-value="/content/girlscouts-vtk/controllers/vtk.demo.index.html?vTroop=&user=Ramona&prefGradeLevel=7-Multi-level">MULTI-LEVEL <span>MULTI-LEVEL</span></li>
                        
                     <% }%>
                      </ul>




                    </div>
                    <a class="button tiny" style="" href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html?isLogout=true">Logout</a>
                    
                       <a class="button tiny" style="" href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html">Start Over</a>
                  </div>

            <script type="text/javascript">
                // top-level namespace being assigned an object literal
                var gsusa = gsusa || {};

                // a convenience function for parsing string namespaces and
                // automatically generating nested namespaces
                function extendNS( ns, ns_string ) {
                    var parts = ns_string.split('.'),
                        parent = ns,
                        pl, i;

                    if (parts[0] == "gsusa") {
                        parts = parts.slice(1);
                    }

                    pl = parts.length;
                    for (i = 0; i < pl; i++) {
                        //create a property if it doesnt exist
                        if (typeof parent[parts[i]] == 'undefined') {
                            parent[parts[i]] = {};
                        }

                        parent = parent[parts[i]];
                    }

                    return parent;
                }
                // Add the name space;
                extendNS(gsusa,'gsusa.component');

                gsusa.component = (function(){
                    // (Css Selector)
                    function dropDown(selector,callbackObject,state){



                        //Create a dropDown usisng the estructure below pas the selector, callbackObject if you want to execute a function

                        // component sample
                        /*<div id="vtk-dropdown-1" class="vtk-demo-dropdown" data-input-name="value1">
                                <div class="vtk-demo-dropdown_main"><div class="selected-option">GIRL SCOUT GRADE LEVELS </div>
                                   <span class="icon-arrow-down3" style="color:#009447;position: absolute; top: 6px; right: 6px;"></span>
                               </div>
                                <ul class="vtk-demo-dropdown_options">
                                  <li data-value="">-----</li>
                                  <li data-value="1">DAISY <span>grades K-1</span></li>
                                  <li data-value="2">BROWNIE <span>grades 2-3</span></li>
                                  <li data-value="3">JUNIOR <span>grades 4-5</span></li>
                                  <li data-value="4">CADETTE <span>grades 6-8</span></li>
                                  <li data-value="5">SENIOR <span>grades 9-10</span></li>
                                  <li data-value="6">AMBASSADOR <span>grades 11-12</span></li>
                                </ul>
                          </div>*/

                          var selected, option;

                        //get main element
                        var $element = $(selector);

                        //get target input form
                        var inputName = $element.data('input-name');


                        //state preprocess
                        if(typeof state == 'string'){
                            if(state !== undefined && state.split('-').length > 1){
                              selected = parseInt(state.split('-')[0]);

                              selected = isNaN(selected) ? undefined : selected;
                            }
                        }


                        //get Default text
                        var default_text = $element.children('.vtk-demo-dropdown_main').children('.selected-option').html();

                        //Toggle the Options box
                        function toggle(){
                            $element.children('.vtk-demo-dropdown_options').css('height',203);

                            if($element.offset().top + $element.children('.vtk-demo-dropdown_options').height() > $(window).height()){

                                var hei =  $(window).height()-$element.offset().top+$(window).scrollTop()-20;
                                    if(hei >203){
                                        hei = 203;
                                    }
                                    if(hei < 75){
                                      hei = 75;
                                    }
                                $element.children('.vtk-demo-dropdown_options').css('height',hei);
                            }

                            // $('.vtk-demo-dropdown').not('#'+$element.attr('id')).children('.vtk-demo-dropdown_options').hide();
                            $element.children('.vtk-demo-dropdown_options').toggle();
                        }

                        //add event listinert to the Icon
                        // $element.children('.vtk-demo-dropdown_main').children('.icon-arrow-down3').click(function(e){
                          $('#'+$element.attr('id')).click(function(e){
                            toggle();
                        });

                        //add event listener to li inside options
                        $element.children('.vtk-demo-dropdown_options').children('li').click(function(){

                            var value = $(this).data('value');

                            if(callbackObject){
                                if(callbackObject.local){
                                     window.location.assign(value);
                                }else{
                                    if(callbackObject[value]){
                                        callbackObject[value]();
                                    }
                                }
                            }else{
                                if($('input[name='+inputName+']')){
                                    $('input[name='+inputName+']').attr('value',value);
                                }

                                if(value !== ""){
                                    option = $(this).html();

                                }else{
                                    option = default_text;
                                }
                            }

                           $element.children('.vtk-demo-dropdown_main').children('.selected-option').html(option);
                           toggle();
                        });


                        $(document).keydown(function(e){

                            var key  = e.keyCode;

                            if(key == 27){
                                 $('.vtk-demo-dropdown').children('.vtk-demo-dropdown_options').hide();
                            }
                        });


                        $(document).click(function(e){
                          // console.log('click',$(e.target).parent('.vtk-demo-dropdown')[0] ,$element[0],$(e.target).parent('.vtk-demo-dropdown')[0] === $element[0]);
                          if($(e.target).parents('.vtk-demo-dropdown')[0] !== $element[0]){
                            $element.children('.vtk-demo-dropdown_options').hide();
                          }
                        });

                         if(selected){
                            option = $element.children('.vtk-demo-dropdown_options').children('li').eq(selected).html();
                            $element.children('.vtk-demo-dropdown_main').children('.selected-option').html(option);
                         }

                    }

                    return {
                        'dropDown': dropDown
                    };
                })();

            </script>

            <!-- //Mobile -->
            <div class="show-for-small small-24 columns topMessage alt">
                <div class="row vtk-login collapse">
                    <div class="columns small-19">
                        <div class="vtk-vounteer-trainer">
                            Volunteer Toolkit Training
                        </div>
                        <div style="clear:both"></div>
<!--                         <div class="login"><span>Hello Vanessa.</span><a href="javascript:void(0)" onclick="girlscouts.components.login.signOut();" class="signout link-login">SIGN OUT</a></div> -->
                        <div class="demo">
                            <a href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html">Restart Demo</a>
                        </div>
                    </div>
                    <div class="small-5 columns">
                        <div class="small-search-hamburger">
                            <a class="search-icon"><img src="/etc/designs/girlscouts/images/search_white.png" width="21" height="21" alt="search icon"></a>
                            <a class="right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28" alt="toggle hamburger side menu icon"></a>
                        </div>
                    </div>
                </div>
                <div class="row hide srch-box collapse">
                    <div class="hide srch-box small-22 columns search-box">
                        <form action="/content/gssjc/en/site-search.html" method="get">
                            <input type="text" name="q" placeholder="" class="searchField">
                        </form>
                    </div>
                    <div class="small-2 columns">
                        <a class="right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28" alt="right side menu hamburger icon"></a>
                    </div>
                </div>
            </div>
        </div>
        <!--PAGE STRUCTURE: HEADER BAR-->
        <div id="headerBar" style="height:30px;" class="row collapse hide-for-small">
            <div class="global-navigation large-19 medium-23 global-nav columns small-24 large-push-5">
                <!-- apps/girlscouts/components/global-navigation/global-navigation.jsp -->
                <ul class="inline-list">
                   <li class="">
                        <br></li>
                        <!--

                    <li class="">
                        <a class="show-for-large-up menu  " href="/content/gssjc/en/events/event-list.html">EVENTS</a>
                        <a class="show-for-medium-only menu  " href="/content/gssjc/en/events/event-list.html"> EVENTS</a>
                    </li>
                    <li class="">
                        <a class="show-for-large-up menu  " href="/content/gssjc/en/cookies/about-girl-scout-cookies.html">COOKIES</a>
                        <a class="show-for-medium-only menu  " href="/content/gssjc/en/cookies/about-girl-scout-cookies.html"> COOKIES</a>
                    </li>
                    <li class="">
                        <a class="show-for-large-up menu  " href="/content/gssjc/en/for-volunteers/why-volunteer.html">VOLUNTEER</a>
                        <a class="show-for-medium-only menu  " href="/content/gssjc/en/for-volunteers/why-volunteer.html"> VOLUNTEER</a>
                    </li>
                    <li class="">
                        <a class="show-for-large-up menu  " href="/content/gssjc/en/camps/camping-experiences.html">CAMPS</a>
                        <a class="show-for-medium-only menu  " href="/content/gssjc/en/camps/camping-experiences.html"> CAMP</a>
                    </li>
                    <li class="">
                        <a class="show-for-large-up menu  " href="/content/gssjc/en/our-council/about-our-council.html">OUR COUNCIL</a>
                        <a class="show-for-medium-only menu  " href="/content/gssjc/en/our-council/about-our-council.html"> OUR COUNCIL</a>
                    </li> -->
                </ul>
                <script>
                // $(document).foundation({
                //     dropdown: {
                //         // specify the class used for active dropdowns
                //         active_class: 'open',
                //         opened: function() {
                //             $('#drop1').parent('li').addClass('on');
                //         },
                //         closed: function() {
                //             $('#drop1').parent('li').removeClass('on');
                //         }
                //     }
                // });
                // $(document).ready(function() {
                //     $(window).resize(function() {
                //         if ($('#drop1').hasClass('open')) {
                //             $('#drop1').css('left', 'auto !important');
                //         }
                //     });
                // });
                </script>
            </div>
            <div class="small-search-hamburger show-for-medium medium-1 columns">
                <a class="show-for-medium right-off-canvas-toggle menu-icon"><img src="/etc/designs/girlscouts/images/hamburger.png" width="19" height="28" alt="side menu icon"></a>
            </div>
        </div>
    </div>
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



<div id="myModal-demo" class="reveal-modal medium" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
  <h2 id="modalTitle" style="font-size:18px; color:white;padding:10px 30px;background-color:green">VTK DEMO DETAILS</h2>

  <div class="container-model">
    <p>The Volunteer Toolkit Demo Site mirrors all of the current features and functionality live in the VTK. However, this site does not necessarily mirror the exact interface a user will experience once logged in off of their council site; the demo emphasis is on the features and functionality of the digital tool.</p>

  <p>Please note this demo website does not contain real girl data, all troop tab data is prototypical. Additionally, certain features of the VTK have been disabled such as email functionality to “parents/caregivers” and the ability to download Year Plans and Meeting Aids, in order to protect proprietary information.</p>

  <p>Information presented on this website is for demo purposes only, your customizations will not be saved. Content presented is proprietary of Girl Scouts of the United States of America.</p>


  <br>
 <br>
 <br>


  </div>



  <a class="close-reveal-modal" aria-label="Close"><i style="color:white" class="icon-button-circle-cross"></i></a>

 </div>




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