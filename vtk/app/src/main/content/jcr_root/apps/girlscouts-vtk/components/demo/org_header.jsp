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
                        <link type="text/css" rel="stylesheet"
                              href="/etc/designs/girlscouts-vtk/clientlibs/css/_demo.css">
                        <div class="vtk-demo-logo" style="width:188,height:73">
                            <img class="vtk-demo-logo-img" src="/etc/designs/girlscouts-vtk/images/log_demo.png"
                                 alt="Home" id="logoImg">
                            <a href="/content/gssjc/en.html">
                                <img src="/content/dam/gssjc/sanj_green_logo.png" alt="Home" id="logoImg" width="188"
                                     height="73">
                            </a>
                        </div>
                    </nav>
                    <!--<![endif]-->
                </div>
                <div class="placeholder"></div>
            </div>
            <div class="large-18 medium-15 hide-for-small columns topMessage">
                <div style="display:none;">
                    <div class="eyebrow-nav navigation-bar eyebrow-navigation">
                        <ul class="inline-list eyebrow-fontsize">
                            <li style="padding-top:3px; font-size: 14px">
                                Troop Leader
                            </li>
                            <li>
                                <a class="button tiny"
                                   style="background-color:white; color:#009447; margin-bottom: 10px; padding: 5px 10px;"
                                   href="/content/girlscouts-vtk/controllers/vtk.restartDemo.html">Start Over</a>
                            </li>
                        </ul>
                    </div>


                    <div id="vtk-dropdown-1" class="vtk-demo-dropdown" data-input-name="value1"
                         style="float: right; clear: both;">
                        <div class="vtk-demo-dropdown_main">
                            <div class="selected-option">GIRL SCOUT GRADE LEVELS</div>
                            <span class="icon-arrow-down3"
                                  style="color:#009447;position: absolute; top: 6px; right: 6px;">
                       </span>
                        </div>
                        <ul class="vtk-demo-dropdown_options">
                            <li data-value="">-----</li>
                            <li data-value="1">DAISY <span>grades k-1</span></li>
                            <li data-value="2">BROWNIE <span>grades 2-3</span></li>
                            <li data-value="3">JUNIOR <span>grades 4-5</span></li>
                            <li data-value="4">CADETTE <span>grades 6-8</span></li>
                            <li data-value="5">SENIOR <span>grades 9-10</span></li>
                            <li data-value="6">AMBASSADOR <span>grades 11-12</span></li>
                        </ul>
                    </div>
                </div>
                <div class="row collapse">
                    <div class="large-17 medium-17 small-24 columns">
                        <div class="vtk-vounteer-trainer">
                            Volunteer Toolkit Training
                        </div>
                    </div>
                </div>
            </div>


            <script type="text/javascript">
                // top-level namespace being assigned an object literal
                var gsusa = gsusa || {};

                // a convenience function for parsing string namespaces and
                // automatically generating nested namespaces
                function extendNS(ns, ns_string) {
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
                extendNS(gsusa, 'gsusa.component');

                gsusa.component = (function () {
                    // (Css Selector)
                    function dropDown(selector, callbackObject) {

                        //Create a dropDown usisng the estructure below pas the selector, callbackObject if you want to execute a function

                        // component sample
                        /*<div id="vtk-dropdown-1" class="vtk-demo-dropdown" data-input-name="value1">
                                <div class="vtk-demo-dropdown_main"><div class="selected-option">GIRL SCOUT GRADE LEVELS </div>
                                   <span class="icon-arrow-down3" style="color:#009447;position: absolute; top: 6px; right: 6px;"></span>
                               </div>
                                <ul class="vtk-demo-dropdown_options">
                                  <li data-value="">-----</li>
                                  <li data-value="1">DAISY <span>grades k-1</span></li>
                                  <li data-value="2">BROWNIE <span>grades 2-3</span></li>
                                  <li data-value="3">JUNIOR <span>grades 4-5</span></li>
                                  <li data-value="4">CADETTE <span>grades 6-8</span></li>
                                  <li data-value="5">SENIOR <span>grades 9-10</span></li>
                                  <li data-value="6">AMBASSADOR <span>grades 11-12</span></li>
                                </ul>
                          </div>*/

                        //get main element
                        var $element = $(selector);

                        //get target input form
                        var inputName = $element.data('input-name');

                        //get Default text
                        var default_text = $element.children('.vtk-demo-dropdown_main').children('.selected-option').html();

                        //Toggle the Options box
                        function toggle() {
                            $element.children('.vtk-demo-dropdown_options').toggle();
                        }

                        //add event listinert to the Icon
                        $element.children('.vtk-demo-dropdown_main').children('.icon-arrow-down3').click(toggle);

                        //add event listener to li inside options
                        $element.children('.vtk-demo-dropdown_options').children('li').click(function () {
                            var option;
                            var value = $(this).data('value');

                            if (callbackObject) {
                                if (callbackObject[value]) {
                                    console.log(callbackObject[value]);
                                    callbackObject[value]();
                                }
                            } else {
                                if ($('input[name=' + inputName + ']')) {
                                    $('input[name=' + inputName + ']').attr('value', value);
                                }

                                if (value !== "") {
                                    option = $(this).html();
                                } else {
                                    option = default_text;
                                }
                            }

                            $element.children('.vtk-demo-dropdown_main').children('.selected-option').html(option);
                            toggle();
                        });
                    }

                    return {
                        'dropDown': dropDown
                    };
                })();

                //sample
                gsusa.component.dropDown('#vtk-dropdown-1', {
                    '1': function () {
                        console.log(1)
                    }, '2': function () {
                        console.log(2)
                    }
                });
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
                            <a class="search-icon"><img src="/etc/designs/girlscouts/images/search_white.png" width="21"
                                                        height="21" alt="search icon"></a>
                            <a class="right-off-canvas-toggle menu-icon"><img
                                    src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28"
                                    alt="toggle hamburger side menu icon"></a>
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
                        <a class="right-off-canvas-toggle menu-icon"><img
                                src="/etc/designs/girlscouts/images/hamburger.png" width="22" height="28"
                                alt="right side menu hamburger icon"></a>
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
                <a class="show-for-medium right-off-canvas-toggle menu-icon"><img
                        src="/etc/designs/girlscouts/images/hamburger.png" width="19" height="28" alt="side menu icon"></a>
            </div>
        </div>
    </div>
</div>
