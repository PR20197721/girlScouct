<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ page import="com.day.cq.tagging.Tag, com.google.common.collect.*, org.girlscouts.vtk.models.*,  org.girlscouts.vtk.ejb.*, org.girlscouts.vtk.dao.*, org.girlscouts.vtk.models.Meeting,org.girlscouts.vtk.models.MeetingE,org.girlscouts.vtk.models.YearPlanComponent,java.util.*" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<%
    try {
        java.util.List newItems = new java.util.ArrayList();
        newItems.add("Badges for 2019-2020");
        newItems.add("Badges_Petals|Badges_for_2019-2020");
        newItems.add("Journey|STEM");
        newItems.add("STEM");
        newItems.add("Journey|Outdoor");
        newItems.add("Outdoor");
        boolean showVtkNav = true;
        String activeTab = "resource";
        String meetingPath = request.getParameter("mpath");
        if (meetingPath == null || meetingPath.equals("null") || meetingPath.equals("")) {
            meetingPath = null;
        }
        if (meetingPath != null) {
            showVtkNav = false;
        }
        String ageLevel = troop.getTroop().getGradeLevel();
        ageLevel = ageLevel.substring(ageLevel.indexOf("-") + 1).toLowerCase().trim();
        java.util.List<Meeting> meetings = new java.util.ArrayList<Meeting>();
        java.util.List<Meeting> extraInfoMeetings = new java.util.ArrayList<Meeting>();
        Set<String> outdoorMeetingIds = new HashSet<String>();
        Set<String> globalMeetingIds = new HashSet<String>();

        String find = "";
%>
<div class="header clearfix">
    <h3 class="columns small-20">
        <%if (request.getParameter("newCustYr") != null) { %>
        Create Your Own Year Plan
        <%} else { %>ADD A PETAL, BADGE OR JOURNEY<%}//end else %>
    </h3>
    <span class="column small-1"></span>
    <a class="columns small-3" onclick="closeModalPage()"><span id="gsModalClose">X</span></a>
</div>
<%
    boolean isWarning = false;
    String instruction = "Select a meeting library and pick the ones that best complete your multi-level Year Plan";
    if (request.getParameter("newCustYr") != null) {
        instruction = "Look through the meeting library and pick the ones that best complete your multi-level Year Plan";
    } else {
        instruction = "Select a meeting to add to your Year Plan";
    }//end else
    instruction = "";
    if (isWarning) {
        %>
        <div class="small-4 medium-2 large-2 columns meeting_library">
            <div class="warning">
                <img src="/etc/designs/girlscouts-vtk/clientlibs/css/images/warning-small.png" width="20" height="20" align="left"/>
            </div>
        </div>
        <div class="small-20 medium-22 large-22 columns">
    <% } %>

</div>
<script>
    function cngMeeting(mPath) {
        $("#cngMeet").load("/content/girlscouts-vtk/controllers/vtk.controller.html?<%=meetingPath ==null ? "act=AddMeeting&addMeeting" : "act=SwapMeetings&cngMeeting"%>=true&fromPath=<%=meetingPath%>&toPath=" + mPath, function (html) {
            vtkTrackerPushAction('<%=meetingPath ==null ? "AddMeeting" : "ReplaceMeeting" %>');
            <%
                if( request.getParameter("xx") ==null ){
            %>
            document.location = "/content/girlscouts-vtk/en/vtk.html";
            <%} else {%>
            if (window.opener) {
                window.opener.location.reload(false);
            } else {
                window.location.reload(false);
            }
            <%}%>
        });
    }
</script>
<form onsubmit="callToServer()" id="form-meeting-library" action="/content/girlscouts-vtk/controllers/vtk.controller.html" method="get">
    <%if (request.getParameter("newCustYr") != null) { %>
    <input type="hidden" name="act" value="CreateCustomYearPlan"/>
    <%} else { %>
    <input type="hidden" name="addMeetings" value="true"/>
    <%}//end else %>
    <div class="scroll" style="">
        <div class="content meeting-library row">
            <div class="columns small-24 small-centered">
                <p class="instruction " style="float:left;">
                    <span><%= instruction %></span>
                </p>
            </div>
            <div id="cngMeet"></div>
            <!--  start carlos 1 -->
            <div id="vtk-meeting-filter" class="content">
                <div class="sectionHeader" style="">
                    <div class="column small-22 small-centered" style="display:table;">
                        <table>
                            <tr>
                                <td colspan="2">
                                    <h3>Search to Add a Petal, Badge or Journey Meeting</h3>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <div class="__search row" style="border:1px solid gray; border-radius:6px; overflow:hidden">
                                        <div class="columns small-2">
                                            <span class="icon-search-magnifying-glass"></span>
                                        </div>
                                        <div class="columns small-20">
                                            <input type="text" name="search" maxlength="52" placeholder="Search for a badge or journey award by name" id="searchByMeetingTitle" value=""/>
                                        </div>
                                        <div class="__X columns small-2" style="display:none">
                                            <span class="icon-cross"></span>
                                        </div>
                                    </div>
                                    <p id="showHideReveal" class="hide-for-print close">Or Use Filters</p>
                                </td>
                                <td>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="vtk-meeting-group" style="display:none;">
                    <div class="main-filter column small-22 small-centered" style="display:table; padding-left:0;">
                        <div class="row">
                            <div class="column small-24">
                                <div class="vtk-meeting-filter_title"><span>1.</span> Select your Girl Scout Level(s)
                                </div>
                                <div id="vtk-meeting-group-age" class="row">
                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="1">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="daisySelect" value="Daisy">
                                                <label for="daisySelect"><span></span><p>Daisy </p></label>
                                            </div>
                                        </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="2">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="brownieSelect" value="Brownie" >
                                                <label for="brownieSelect"><span></span><p>Brownie </p></label>
                                            </div>
                                         </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="3">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="juniorSelect" value="Junior" >
                                                <label for="juniorSelect"><span></span><p>Junior </p></label>
                                            </div>
                                        </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="100">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="cadetteSelect" value="Cadette" >
                                                <label for="cadetteSelect"><span></span><p>Cadette </p></label>
                                            </div>
                                        </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="100">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="seniorSelect" value="Senior" >
                                                <label for="seniorSelect"><span></span><p>Senior </p></label>
                                            </div>
                                        </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="100">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="ambassadorSelect" value="Ambassador">
                                                <label for="ambassadorSelect"><span></span><p>Ambassador </p></label>
                                            </div>
                                        </span>
                                    </span>

                                    <span class="container" style="clear:both;">
                                        <span class="terminal" data-price="100">
                                            <div class="small-24 medium-6 column selection-box">
                                                <input class="gradeLevelSelect" type="radio" name="_tag_m" id="multiSelect" value="Multi_level">
                                                <label for="multiSelect"><span></span><p>Multi-level </p></label>
                                            </div>
                                        </span>
                                    </span>
                                </div>
                            </div>
                            <div class="column small-24" id="vtk-meeting-group-type" style="display:none">
                                <div class="vtk-meeting-filter_title">
                                    <span>2.</span> Select the type of meeting plan you want
                                </div>
                                <div id="vtk-group-section"class="row">

                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="list-of-categories column small-22 small-centered" id="vtk-meeting-category-parent"style="display:none;padding-left:0;">
                        <div class="row">
                            <div class="column small-24">
                                <div class="vtk-meeting-filter_title"><span>3.</span> Select your
                                    <span id="cat_selected" style="font-size:14px !important;"></span> categories
                                </div>
                                <div id="vtk-meeting-group-categories" class="row  wrap-vtk-meeting-group-categories">

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="list-of-buttons column small-22 small-centered" style="padding-left:0;">
                <div class="row">
                    <div id="vtk-meeting-group-button" class="column small-24" style="padding:25px 0 25px 0;">
                        <div id="vtk-meeting-group-button_cancel" onclick="closeModalPage()" class="button tiny ">CANCEL</div>
                        <div id="vtk-meeting-group-button_ok" class="button tiny disabled ">VIEW LIST</div>
                    </div>
                </div>
            </div>
            <div class="loading-meeting" style="display:none"></div>
            <div id="meeting-library-no-content" class="no-content column small-24" style="display:none; padding:40px 0 0 25px">
                <h5></h5>
                <p>"Apply filters" can step you through how meetings are organized</p>
            </div>
            <!--  carlos 4 end  -->
            <div id="meetingSelect" class="meetingSelect column small-24 small-centered" style="display:none;">
                <!--<div class="row">-->
                <%-- // --%>
                <div style="display: flex;justify-content: center;flex-grow: 1;flex-basis: 100%;width: 100%;min-height: 80px;position:fixed;bottom:50px;left:0px;z-index:1001;overflow: hidden;">
                    <div class="vtk-float-submit">
                        <input style="color: #18AA5E; background: white !important; border: solid 1px #18AA5E;" class="button tiny" type="button" value="CANCEL" onclick="closeModalPage()"/>
                        <%if (request.getParameter("isReplaceMeeting") == null) {%>
                        <input class="button tiny inactive-button clear-meeting-filter-result" type="button" value="CLEAR SELECTED MEETINGS"/>
                        <%if (request.getParameter("newCustYr") != null) { %>
                        <input class="button tiny inactive-button add-to-year-plan" type="button" value="ADD TO YEAR PLAN" onclick="createCustPlan(null)"/>
                        <%} else { %>
                        <input class="button tiny inactive-button add-to-year-plan" type="submit" value="ADD TO YEAR PLAN"/>
                        <%}//end else %>
                        <%}%>
                    </div>
                </div>
                <div id="no-of-meeting" class="no-of-meeting" style="display:none;padding-left:25px;">
                    <p></p>
                </div>
            </div>
        </div>
    </div>
</form>
<script>
    function openRequirementDetail(element) {
        $(element).parents('.meeting-item').find('.__requiments_details').toggle();
    }

    function _closeME(element) {
        $(element).parents('.__requiments_details').toggle();
    }

    //Polyfill
    //===================================
    if (typeof Object.assign != 'function') {
        // Must be writable: true, enumerable: false, configurable: true
        Object.defineProperty(Object, "assign", {
            value: function assign(target, varArgs) { // .length of function is 2
                'use strict';
                if (target == null) { // TypeError if undefined or null
                    throw new TypeError('Cannot convert undefined or null to object');
                }
                var to = Object(target);
                for (var index = 1; index < arguments.length; index++) {
                    var nextSource = arguments[index];

                    if (nextSource != null) { // Skip over if undefined or null
                        for (var nextKey in nextSource) {
                            // Avoid bugs when hasOwnProperty is shadowed
                            if (Object.prototype.hasOwnProperty.call(nextSource, nextKey)) {
                                to[nextKey] = nextSource[nextKey];
                            }
                        }
                    }
                }
                return to;
            },
            writable: true,
            configurable: true
        });
    }
    //===================================

    var currentYear = CurrentYear(new Date());
    //Params Creator
    var params = (function () {
    var _params = {
        keywords: '',
        level: [],
        categoryTags: [],
        meetingPlanType: '',
        year: (currentYear.start()).getFullYear()
    };
    function _addParams(newParam) {
        _params = Object.assign(_params, newParam);
    }
    function _getParams() {
        return _params;

    }
    function _clear() {
        _params = {
            keywords: '',
            level: [],
            categoryTags: [],
            meetingPlanType: '',
            year: (currentYear.start()).getFullYear()
        }
    }
    return {
        add: _addParams,
        get: _getParams,
        clear: _clear
    }
    })();
    function cleanMeetingCheckbox() {
        $('input[name="addMeetingMulti"]').each(function () {
            this.checked = false;
        })
        $('.clear-meeting-filter-result').addClass('inactive-button');
        $('.add-to-year-plan').addClass('inactive-button');
    }
    function buttonLogic(){

            var object = params.get();
            var keyInObject = Object.keys(object);
            var somethingFill = keyInObject.some(function (e) {
                return object[e] !== ''
            })
            var isKeyboard = (object['keywords'].length >= 3);
            var isLevel = !!object['level'].length;
            if (isKeyboard || isLevel) {
                $('#vtk-meeting-group-button_ok').removeClass('disabled');
            } else {
                $('#vtk-meeting-group-button_ok').addClass('disabled');
            }

     }
    //x in input
    $('#vtk-meeting-filter .__search .__X').on('click', function () {
        $('#searchByMeetingTitle').val('');
        params.add({keywords: ''})
        buttonLogic();
        $(this).hide();
    })
    //keywords
    $('#searchByMeetingTitle').on('keyup', function (event) {
        var key = event.target.value, element = $('#vtk-meeting-filter .__search .__X');
        params.add({keywords: key.split(' ').join(' ')})
        if (key.length >= 3) {
            element.show();
        } else {
            element.hide();
        }
        buttonLogic();
    })


    function closeModal() {
        $('#gsModal').find('a').children('i').trigger('click');
    }

    $('.clear-meeting-filter-result').on('click', function () {
        $('input[name="addMeetingMulti"]').each(function () {
            this.checked = false;
        })
        $('.clear-meeting-filter-result').addClass('inactive-button');
        $('.add-to-year-plan').addClass('inactive-button');
    });

    //Cancel enter submit
    var time = 0;
    $('#form-meeting-library').on('keypress', function (e) {
        var keyCode = e.keyCode || e.which; //Hack for mack and IE
        if (keyCode === 13) { //Check if key enter is pressed
            e.preventDefault(); //Prevent send the form
            if (e.timeStamp > time + 1500) {  //avoid multiple click in the enter buttom (one second and an half)
                if(!$('#vtk-meeting-group-button_ok').hasClass('disabled')){
                    callToServer();
                }else{
                    console.log("Error submitting form, please enter more data");
                }
                time = e.timeStamp //save the previos time stamp
            }
            return false;
        }
    });
    $(".gradeLevelSelect").on("click", function(){
         $('#vtk-meeting-group-button_ok').removeClass('disabled');
    });

    $("#vtk-meeting-group-button_ok").on('click', function(){
        if(!$('#vtk-meeting-group-button_ok').hasClass('disabled')){
            //Age
            var Age = []
            $('#vtk-meeting-group-age input').each(function (a, e) {
                if (e.checked) {
                    Age.push(e.value.replace(/_/g, '-')); //level JSP is parse all '-' to '_' legacy logic.
                }
            });
            params.add({level: Age})

            //types
            var type;
            $('#vtk-meeting-group-type input').each(function (a, e) {
                if (e.checked) {
                    type = $(this);
                }
            });
            if(type !== undefined){
                params.add({meetingPlanType: $(type).attr("value").replace(new RegExp(' ', 'g'), "_")});
            }else{
                params.add({meetingPlanType: ''});
            }

            //categories
            var Cat = []
            $('[name="_tag_c"]').each(function (a, e) {
                if (e.checked) {
                    Cat.push(e.value);
                }
            });
            params.add({categoryTags: Cat});
            callToServer();
        }

    });

    $('#vtk-meeting-filter').find('#showHideReveal').stop().click(function (e) {
        if($("#vtk-meeting-category-parent").css("display") !== "none"){
            $("#vtk-meeting-category-parent").slideUp(400, function(){
                $("#vtk-meeting-group-categories").html("");
            });
        }
        if($("#vtk-meeting-group-type").css("display") !== "none"){

            $("#vtk-meeting-group-type").slideUp(400, function(){
                $("#vtk-group-section").html("");
            });
        }
        getMeetingResponse()
    })


    function createCustPlan(singleMeetingAdd) {
        var sortedIDs = "";
        if (singleMeetingAdd == null) {
            var els = document.getElementsByName("addMeetingMulti");
            for (var y = 0; y < els.length; y++) {
                if (els[y].checked == true) {
                    sortedIDs = sortedIDs + els[y].value + ",";
                }
            }
        } else {
            sortedIDs = sortedIDs + singleMeetingAdd + ",";
        }
        $.ajax({
            url: "/content/girlscouts-vtk/controllers/vtk.controller.html?act=CreateCustomYearPlan&mids=" + sortedIDs,
            cache: false
        })
        .done(function (html) {
            vtkTrackerPushAction('CreateCustomYearPlan');
            location.href = "/content/girlscouts-vtk/en/vtk.html";
        });
    }


</script>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<style>
    .tooltip span.nub {
        left: 10px;
    / / hack for look fine in page
    }
</style>