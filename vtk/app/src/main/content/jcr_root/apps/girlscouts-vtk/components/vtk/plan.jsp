<%
    String activeTab = "plan";
    boolean showVtkNav = true;
    boolean isParent = "PA".equals(selectedTroop.getRole());
    boolean isAdmin = user.getApiConfig().getUser().isAdmin();
%>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/jquery.ui.touch-punch.min.js"></script>
<script src="/etc/designs/girlscouts-vtk/clientlibs/js/planView.js"></script>
<%
    String sectionClassDefinition = "meeting-detail";
%>
<%@include file="include/bodyTop.jsp" %>
<%@include file="include/modals/modal_help.jsp" %>
<%@include file="include/loader.jsp" %>
<%PlanView planView = meetingUtil.planView(user, selectedTroop, request);%>
<script>
    //Pollyfill for Browser  Previous to IE 9
    if (!Object.keys) {
        Object.keys = (function () {
            'use strict';
            var hasOwnProperty = Object.prototype.hasOwnProperty,
                hasDontEnumBug = !({toString: null}).propertyIsEnumerable('toString'),
                dontEnums = [
                    'toString',
                    'toLocaleString',
                    'valueOf',
                    'hasOwnProperty',
                    'isPrototypeOf',
                    'propertyIsEnumerable',
                    'constructor'
                ],
                dontEnumsLength = dontEnums.length;

            return function (obj) {
                if (typeof obj !== 'function' && (typeof obj !== 'object' || obj === null)) {
                    throw new TypeError('Object.keys called on non-object');
                }

                var result = [], prop, i;

                for (prop in obj) {
                    if (hasOwnProperty.call(obj, prop)) {
                        result.push(prop);
                    }
                }

                if (hasDontEnumBug) {
                    for (i = 0; i < dontEnumsLength; i++) {
                        if (hasOwnProperty.call(obj, dontEnums[i])) {
                            result.push(dontEnums[i]);
                        }
                    }
                }
                return result;
            };
        }());
    }
</script>
<div id="yearPlanMeetings"
     class="<%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ) ? "vtk-currentYear-plan year_plan" : "vtk-pastYear-plan year_plan" %>">
    <div id="thePlan">
        <script type="text/javascript">
            var isActivNew;
            var isFirst = 1;
            var meetingPassed = true;
            var scrollTarget = "";

            var CommentBox = React.createClass({
                displayName: "CommentBox",

                loadCommentsFromServer: function (isFirst) {
                    if (isFirst) {
                        $.ajax({
                            url: this.props.url + '&isFirst=1',
                            dataType: 'json',
                            cache: false,
                            success: function (data) {
                                console.info('data coming back');
                                this.setState({data: data});
                                this.isReordering = false;
                            }.bind(this),
                        });
                    } else if (<%=user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"" ) %>) {


                        getDataIfModified("year-plan.json", this, function (data, textStatus, req) {
                            // Skip if is 304.
                            // Skip if is reordering.
                            if (req.status == 200 && !this.isReordering) {
                                this.setState({data: data});
                            }

                        });

                    }
                },
                getInitialState: function () {
                    return {data: []};
                },

                pollIntervalID: null,
                isReordering: false,
                componentDidMount: function () {
                    loadNav('plan', true);


                    // Need to skip dispatcher cache for the first time load.
                    this.loadCommentsFromServer(true);
                    this.pollIntervalID = setInterval(this.loadCommentsFromServer, this.props.pollInterval);
                    setInterval(this.checkLocalUpdate, 100);
                },
                delayReload: function () {
                    // Set a flag to indicating reordering in progress.
                    this.isReordering = true;

                    // Extend 10 seconds for the next poll.
                    if (this.pollIntervalID != null) {
                        clearTimeout(this.pollIntervalID);
                    }

                    this.pollIntervalID = setInterval(this.loadCommentsFromServer, this.props.pollInterval);
                },
                checkLocalUpdate: function () {
                    if ((isActivNew == 1) || (isActivNew == 2)) {
                        this.loadCommentsFromServer();
                    }
                },
                render: function () {
                    var x, yearPlanName;
                    if (this.state.data.schedule != null) {
                        x = this.state.data.schedule;
                        yearPlanName = this.state.data.yearPlan;
                        return (
                            React.createElement(YearPlanComponents, {
                                yearPlanName: yearPlanName,
                                data: x,
                                parentComponent: this
                            })
                        );
                    } else if (this.state.data != null && this.state.data.yearPlan != null && this.state.data.yearPlan == 'NYP' &&  <%= !VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" :  "false" %>) {
                        return React.createElement("h3", {className: "notice column large-22 large-centered medium-20 medium-centered small-21 small-centered"}, "Hello! Your girl's Troop Leader has not yet set up the troop's Year Plan. Please contact the Troop Leader for more info on their use of the Volunteer Toolkit.");

                    } else if (this.state.data != null && this.state.data.yearPlan != null && this.state.data.yearPlan == 'NYP' &&  <%= VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" : "false" %>) {
                        yesPlan.auto();
                        return React.createElement("h3", null);
                    } else {

                        return React.createElement("h3", null);
                    }

                }
            });
            var YearPlanComponents = React.createClass({
                displayName: "YearPlanComponents",
                onReorder: function (order) {
                    // Reordering
                    var parent = this.props.parentComponent;

                    // Delay reload
                    parent.delayReload();

                    // Go ahead to reorder
                    parent.setState({data: {}});
                    parent.loadCommentsFromServer(true);
                },
                render: function () {
                    return (
                        React.createElement("div", {id: "yearPlanMeetings", className: "columns"},
                            React.createElement("div", {className: "row"},
                                React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                    React.createElement("h1", {className: "yearPlanTitle"}, this.props.yearPlanName),
                                    React.createElement("p", {className: "hide-for-print <%= VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : "hide" %> "}, "Drag and drop to reorder meetings")
                                )
                            ),
                            React.createElement(MeetingComponent, {
                                key: this.props.data.yearPlanName + (new Date()).getTime(),
                                data: this.props.data,
                                onReorder: this.onReorder
                            })
                        )
                    );
                } //end of render
            });

            var outdoorIcon = React.createClass({
                displayName: "outdoorIcon",

                render: function () {


                    var isOutdoorAvailable = this.props.isOutdoorAvailable, imgName;

                    if (this.props.isOutdoor) {
                        imgName = "outdoor.png";
                    } else {
                        imgName = "indoor.png";
                    }

                    var options = {
                        className: 'outdoor-icon has-tip tip-top radius',
                        src: '/etc/designs/girlscouts-vtk/clientlibs/css/images/' + imgName,
                        style: {
                            width: '45px',
                            border: 'none',
                            cursor: 'pointer',
                            paddingTop: '3px'
                        },
                        "data-tooltip": "outdoorIcon",
                        "aria-haspopup": true,
                        title: "<b>Get Girls Outside!</b>"
                    };

                    if (isOutdoorAvailable) {
                        return (React.createElement("img", options));
                    }
                    return null;
                }
            });

            var globalIcon = React.createClass({
                displayName: "globalIcon",

                render: function () {
                    var isGlobalAvailable = this.props.isGlobalAvailable, imgName;
                    if (this.props.isGlobal) {
                        imgName = "globe_selected.png";
                    } else {
                        imgName = "globe_unselected.png";
                    }
                    var options = {
                        className: 'global-icon has-tip tip-top radius',
                        src: '/etc/designs/girlscouts-vtk/clientlibs/css/images/' + imgName,
                        style: {
                            width: '39px',
                            border: 'none',
                            cursor: 'pointer',
                            paddingTop: '6px'
                        },
                        "data-tooltip": "globalIcon",
                        "aria-haspopup": true
                        ,
                        title: "<b>Go Global!</b>"
                    };

                    if (isGlobalAvailable) {
                        return (React.createElement("img", options));
                    }
                    return null;
                }
            });


            var DirectCalendar = React.createClass({
                displayName: 'DirectCalendar',
                close: function () {
                    this.props.close();
                    document.body.style.overflowY = ''
                },
                getInitialState: function () {
                    return {
                        change: false,
                        time: moment.tz(this.props.time,"America/New_York").format('h:mm'),
                        message: {
                            type: '',
                            display: '',
                        },
                        design: this.calculateDesign()

                    }
                },
                componentDidMount: function () {
                    var _this = this;
                    var currentYear = CurrentYear(new Date());
                    $('.vtk-calendar-ui-jquery').datepicker({
                        minDate: currentYear.start(),
                        maxDate: currentYear.end(),
                        defaultDate: new Date(_this.props.time)
                    }).on('change', function (e) {
                        _this.change();
                    });


                    $(window).resize(function () {
                        _this.setState({design: _this.calculateDesign()})
                    })

                },
                componentWillUnmount: function () {
                    $('.vtk-calendar-ui-jquery').datepicker('destroy');
                    $(window).off('resize');
                },
                saveChange: function () {
                    var _this = this;
                    if (moment(this.refs.date.value + " " + this.refs.time.value + " " + this.refs.ap.value).isValid()) {

                        var newTime = moment(this.refs.date.value + ' ' + this.refs.time.value + ' ' + this.refs.ap.value).utc().format('YYYY-MM-DDTHH:mm:ss.SSS');
                        var arrayOfKeys = Object.keys(this.props.obj);

                        if (!arrayOfKeys.some(function (key) {
                            return !!~key.indexOf(newTime)
                        })) {

                            var date = this.refs.date.value;
                            var ap = this.refs.ap.value;
                            var time = this.refs.time.value;

                            updSchedDirectCalendar(this.props.time, {
                                date: date,
                                ap: ap,
                                time: time
                            }).success(function () {
                                location.reload();
                            }).fail(function () {
                                _this.setState({
                                    message: {
                                        type: 'Error:',
                                        display: 'We have problem in the serve please try again.'
                                    }
                                })
                            })

                        } else {
                            this.setState({
                                message: {
                                    type: 'Note:',
                                    display: 'This date and time have already been selected for another meeting.'
                                }
                            })
                        }
                    } else {
                        this.setState({message: {type: 'Error:', display: 'Date is Invalid'}})
                    }
                },
                removeMeeting: function () {

                    var meetingId = this.props.meeting.uid,
                        time = this.props.time,
                        level = this.props.meeting.meetingInfo.level;

                    this.close();

                    var systemTime = <%= System.currentTimeMillis() %>;

                    // Always allow meeting in the future to be removed.
                    if (systemTime < time) {
                        rmMeetingWithConf(meetingId, time, level, this.props.meeting.meetingInfo.name);
                        return;
                    }

                    // If there are users on either the attendance record or the achievement record block meeting deletion until these records are deleted.
                    if (this.props.meeting.attendance && this.props.meeting.attendance.users.trim() || this.props.meeting.achievement && this.props.meeting.achievement.users.trim()) {
                        rmMeetingWithConfBlocked(meetingId, time, level, this.props.meeting.meetingInfo.name);
                    } else {
                        rmMeetingWithConf(meetingId, time, level, this.props.meeting.meetingInfo.name);
                    }

                },
                seeMoreCalendar: function () {
                    this.props.close();
                    newLocCal();
                },
                changeInput: function (e) {

                    this.setState({change: true, time: e.target.value, message: {type: '', display: ''}})
                },
                change: function () {
                    this.setState({change: true, message: {type: '', display: ''}})
                },
                calculateDesign: function () {
                    var h = window.innerHeight,
                        w = window.innerWidth,
                        l = 0,
                        t = 0,
                        hbox = 0,
                        wbox = 0;

                    if (w < 751) {
                        hbox = h;
                        wbox = w;
                        l = false;
                        t = false;
                        document.body.style.overflowY = 'hidden';
                    } else {
                        l = true;
                        t = true;
                        hbox = 320;
                        wbox = 650;
                        document.body.style.overflowY = '';
                    }

                    function calculateLeft(left) {
                        if (!l) {
                            return '0px';
                        } else {
                            return left + 133 + 'px';
                        }
                    }

                    function calculateTop(top) {
                        if (!t) {
                            return '0px';
                        } else {
                            return top - 100 + 'px';
                        }
                    }

                    function calculateWidth() {
                        return wbox;
                    }

                    function calculateHeight() {
                        return hbox;
                    }

                    return {
                        left: calculateLeft,
                        top: calculateTop,
                        width: calculateWidth,
                        isMobile: w < 751,
                        height: calculateHeight
                    };
                },
                render: function () {
                    var _this = this;
                    var meeting = this.props.meeting;
                    var time = this.props.time;

                    return (React.createElement('div', {
                                className: this.state.design.isMobile ? 'vtk-direct-calendar __fixed' : 'vtk-direct-calendar',
                                style: {
                                    top: this.state.design.top(this.props.element.clientY),
                                    left: this.state.design.left(this.props.element.clientX),
                                    maxWidth: this.state.design.width(),
                                    height: this.state.design.height()

                                }
                            },
                            React.createElement('div', {className: 'columns small-24'},
                                React.createElement('div', {className: 'row'},
                                    React.createElement('div', {className: 'columns small-24'},
                                        React.createElement('p', {
                                                className: '__title',
                                                style: {}
                                            }, moment(time).format('MMM DD, YYYY'), React.createElement('span', {style: {marginLeft: '15px'}}, ''), meeting.meetingInfo.name
                                        ), (this.state.message.display) ? React.createElement('p',
                                            {
                                                className: '__error_message small-only-text-center', style: {}
                                            }, this.state.message.type + ' ' + this.state.message.display) : null
                                    ), React.createElement('p', {
                                        className: '',
                                        style: {marginBottom: '6px', textAlign: 'center'}
                                    }, 'Change meeting date and time')
                                ),
                                React.createElement('div', {className: 'row'},
                                    React.createElement('div', {className: "__calendar columns small-24 medium-12"},
                                        React.createElement('div', {ref: 'date', className: 'vtk-calendar-ui-jquery'},
                                            null
                                        )
                                    ),
                                    React.createElement('div', {className: "columns small-24 medium-12"},
                                        React.createElement('div', {className: " __other_site"},
                                            React.createElement('div', {className: "columns small-24"},
                                                React.createElement('div', {className: '__inputs'},

                                                    React.createElement('div', {className: 'columns small-8'},
                                                        React.createElement('p', {}, 'Start Time')
                                                    ),

                                                    React.createElement('div', {className: 'columns small-8'},
                                                        React.createElement('input', {
                                                            ref: 'time',
                                                            value: this.state.time,
                                                            type: 'text',
                                                            onChange: function (e) {
                                                                _this.changeInput(e)
                                                            }
                                                        })
                                                    ),
                                                    React.createElement('div', {
                                                            className: 'columns small-8',
                                                            onChange: function (e) {
                                                                _this.change(e)
                                                            }
                                                        },
                                                        React.createElement('select', {ref: 'ap'},
                                                            React.createElement('option', {
                                                                value: "pm",
                                                                selected: !!~moment(time).format('LT').indexOf('PM')
                                                            }, "PM"),
                                                            React.createElement('option', {
                                                                value: "am",
                                                                selected: !!~moment(time).format('LT').indexOf('AM')
                                                            }, "AM")
                                                        )
                                                    )
                                                ),
                                                React.createElement('div', {
                                                        className: '__buttons',
                                                        style: {textAlign: 'center'}
                                                    },
                                                    React.createElement('div', {className: "columns small-12"},
                                                        React.createElement('button', {
                                                            className: 'button',
                                                            style: {width: '100%'},
                                                            onClick: function () {
                                                                _this.close()
                                                            }
                                                        }, 'CANCEL')
                                                    ),
                                                    React.createElement('div', {className: "columns small-12"},
                                                        React.createElement('button', {
                                                            className: 'button',
                                                            style: {width: '100%'},
                                                            disabled: !_this.state.change,
                                                            onClick: function () {
                                                                _this.saveChange()
                                                            }
                                                        }, "SAVE")
                                                    )
                                                )
                                            ),
                                            React.createElement('div', {className: "columns small-24"},
                                                null
                                            )
                                        ),
                                        React.createElement('div', {style: {}, className: "__second_part"},
                                            React.createElement('p', {
                                                className: this.state.design.isMobile ? '__button_as_mobile' : '',
                                                onClick: function () {
                                                    _this.removeMeeting()
                                                },
                                                style: {}
                                            }, 'Delete Meeting'),
                                            React.createElement('p', {
                                                className: this.state.design.isMobile ? '__button_as_mobile' : '',
                                                onClick: function () {
                                                    _this.seeMoreCalendar()
                                                },
                                                style: {}
                                            }, 'See more calendar options')
                                        )
                                    )
                                )
                            )
                        )
                    );
                }
            });


            var MeetingComponent = React.createClass({
                displayName: "MeetingComponent",
                getInitialState: function () {
                    return {
                        element: {},
                        time: undefined,
                        meeting: {}
                    }
                },

                render: function () {
                    var _this = this;

                    function closeModal() {
                        _this.setState({
                            element: {},
                            time: undefined,
                            meeting: {}
                        })
                    }

                    function openModal(options, time, comment, meeting) {
                        if (moment.tz(comment, "America/New_York").get('year') > 1978) {
                            _this.setState({ //Make sure it clean the previous state
                                    element: {},
                                    time: undefined,
                                    meeting: {}
                                }, function () {
                                    _this.setState({
                                        element: options,
                                        time: time,
                                        meeting: meeting
                                    })
                                }
                            )
                        } else {
                            if (<%= (user.getCurrentYear().equals( VtkUtil.getCurrentGSYear()+"") ) ? "true" : "false" %> &&
                            <%= VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "true" : "false" %>)
                            {
                                newLocCal();
                            }
                        }
                    }

                    if (this.props.data != null) {
                        var keys = Object.keys(this.props.data);
                        var obj = this.props.data;
                        meetingPassed = true;
                        return (React.createElement('div', {},
                                (this.state.time) ? React.createElement(DirectCalendar, {
                                    element: this.state.element,
                                    time: this.state.time,
                                    meeting: this.state.meeting,
                                    close: closeModal,
                                    obj: obj
                                }) : null,
                                React.createElement("ul", {id: "sortable123"},
                                    keys.map(function (comment, i) {
                                        if (obj[comment].type == 'MEETINGCANCELED') {

                                            return (

                                                React.createElement("li", {className: 'row meeting ui-state-default ui-state-disabled'},
                                                    React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                                        React.createElement("div", {}, React.createElement(DateBox, {
                                                            comment: comment,
                                                            obj: obj,
                                                            openModal: openModal
                                                        })),
                                                        React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                                            /* React.createElement(outdoorIcon, {isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable, isOutdoor: obj[comment].anyOutdoorActivityInMeeting}), */
                                                            React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {
                                                                isOutdoor: obj[comment].anyOutdoorActivityInMeeting,
                                                                dateRaw: comment,
                                                                date: moment(comment).toDate(),
                                                                name: obj[comment].meetingInfo.name
                                                            })),
                                                            React.createElement("p", {className: "category"}, obj[comment].meetingInfo.cat),
                                                            React.createElement("p", {className: "blurb"}, obj[comment].meetingInfo.blurb)
                                                        ),
                                                        React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"},
                                                            React.createElement(MeetingImg, {
                                                                mid: obj[comment].meetingInfo.id,
                                                                meetingInfo: obj[comment].meetingInfo,
                                                                isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable,
                                                                isOutdoor: obj[comment].anyOutdoorActivityInMeeting,
                                                                isGlobalAvailable: obj[comment].anyGlobalActivityInMeetingAvailable,
                                                                isGlobal: obj[comment].anyGlobalActivityInMeeting
                                                            })
                                                        )
                                                    )
                                                )


                                            );

                                        } else if (obj[comment].type == 'MEETING') {

                                            return (
                                                React.createElement("li", {
                                                        className: <%if( !VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ){%> true || <%} %> (moment(comment) < moment(new Date()) && (moment(comment).get('year') > 2000)) ? 'row meeting ui-state-default ui-state-disabled' : 'row meeting ui-state-default',
                                                        key: obj[comment].sortOrder,
                                                        id: obj[comment].sortOrder + 1
                                                    },
                                                    React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                                        React.createElement("img", {
                                                            className: (moment(comment) < moment(new Date()) && (moment(comment).get('year') > 2000)) ? "touchscroll hide" : "touchscroll <%= VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID) ? "" : " hide" %>",
                                                            src: "/etc/designs/girlscouts-vtk/clientlibs/css/images/throbber.png"
                                                        }),
                                                        React.createElement("div", {}, React.createElement(DateBox, {
                                                            comment: comment,
                                                            obj: obj,
                                                            openModal: openModal
                                                        })),
                                                        React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                                            React.createElement("div", {className: "meeting-icons"},
                                                                React.createElement(outdoorIcon, {
                                                                    isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable,
                                                                    isOutdoor: obj[comment].anyOutdoorActivityInMeeting
                                                                }),
                                                                React.createElement(globalIcon, {
                                                                    isGlobalAvailable: obj[comment].anyGlobalActivityInMeetingAvailable,
                                                                    isGlobal: obj[comment].anyGlobalActivityInMeeting
                                                                })),
                                                            React.createElement("p", {className: "subtitle"}, React.createElement(ViewMeeting, {
                                                                isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable,
                                                                isOutdoor: obj[comment].anyOutdoorActivityInMeeting,
                                                                isGlobalAvailable: obj[comment].anyGlobalActivityInMeetingAvailable,
                                                                isGlobal: obj[comment].anyGlobalActivityInMeeting,
                                                                dateRaw: comment,
                                                                date: moment(comment).toDate(),
                                                                name: obj[comment].meetingInfo.name
                                                            })),
                                                            React.createElement("p", {className: "category"}, obj[comment].meetingInfo.cat),
                                                            React.createElement("p", {className: "blurb"}, obj[comment].meetingInfo.blurb)
                                                        ),
                                                        React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"},
                                                            React.createElement(MeetingImg, {
                                                                mid: obj[comment].meetingInfo.id,
                                                                meetingInfo: obj[comment].meetingInfo,
                                                                isOutdoorAvailable: obj[comment].anyOutdoorActivityInMeetingAvailable,
                                                                isOutdoor: obj[comment].anyOutdoorActivityInMeeting,
                                                                isGlobalAvailable: obj[comment].anyGlobalActivityInMeetingAvailable,
                                                                isGlobal: obj[comment].anyGlobalActivityInMeeting
                                                            })
                                                        )
                                                    )
                                                )


                                            );
                                        } else if (obj[comment].type == 'ACTIVITY') {


                                            var activityLocation = obj[comment].locationName != null ? obj[comment].locationName.replace('&nbsp;', '').replace(/(<([^>]+)>)/ig, "") : "";
                                            var activityContent = obj[comment].content != null ? obj[comment].content.replace('&nbsp;', '').replace(/(<([^>]+)>)/ig, "") : "";

                                            return (
                                                React.createElement("li", {
                                                        draggable: false,
                                                        className: "row meeting activity ui-state-default ui-state-disabled",
                                                        key: obj[comment].uid
                                                    },
                                                    React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                                        React.createElement("div", {},
                                                            React.createElement("div", {className: bgcolor(obj, comment, 0)},
                                                                React.createElement("div", {className: "date"},
                                                                    React.createElement("p", {className: "month"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('MMM')),
                                                                    React.createElement("p", {className: "day"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('DD')),
                                                                    React.createElement("p", {className: "hour"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('hh:mm a'))
                                                                )
                                                            )
                                                        ),
                                                        React.createElement("div", {className: "large-22 medium-22 small-24 columns"},
                                                            React.createElement("p", {className: "subtitle"},
                                                                React.createElement(ViewMeeting, {
                                                                    dateRaw: comment,
                                                                    date: moment(comment),
                                                                    name: obj[comment].name
                                                                })
                                                            ),
                                                            React.createElement("p", {className: "category"}, activityContent),
                                                            React.createElement("p", {className: "blurb"}, activityLocation)
                                                        ),
                                                        React.createElement("div", {className: "large-2 medium-2 columns hide-for-small"})
                                                    )
                                                )

                                            );
                                        } else if (obj[comment].type == 'MILESTONE' && obj[comment].show) {
                                              function parseLink(blurb){
                                                  var index = blurb.search("\\[.*\\]\\(.*\\)");
                                                  if (index != -1){
                                                      var endBracketIndex = blurb.indexOf(']',index);
                                                      var word = blurb.substring(index+1, endBracketIndex);
                                                      var openParenIndex = blurb.indexOf('(', endBracketIndex);
                                                      var closeParenIndex = blurb.indexOf(')', openParenIndex);
                                                      var hrefString = blurb.substring(openParenIndex + 1, closeParenIndex);
                                                      var link = "<a href='" + hrefString + "'>" + word + "</a>";
                                                      var finalBlurb = blurb.substring(0,index) + link + blurb.substring(closeParenIndex+1);
                                                      return parseLink(finalBlurb);
                                                  }
                                                  return blurb;
                                              }
                                              return (
                                                  React.createElement("li", {className: "row milestone"},
                                                      React.createElement("div", {className: "column large-20 medium-20 large-centered medium-centered"},
                                                          React.createElement("span", null, moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('MM/DD/YY'), " ", ""),
                                                                      React.createElement("span", {dangerouslySetInnerHTML:{__html: parseLink(obj[comment].blurb)}} )
                                                      )
                                                  )

                                              );
                                          }
                                    })
                                )
                            )
                        );
                    } else {
                        return React.createElement("div", null, React.createElement("img", {src: "/etc/designs/girlscouts-vtk/images/loading.gif"}))
                    }
                },
                onReorder: function (order) {
                    isActivNew = 1;

                },
                componentDidMount: function () {
                    resizeWindow();
                    //--link_bg_square();
                    //loadNav('plan');

                    if (Modernizr.touch) {
                        scrollTarget = ".touchscroll";
                    } else {


                    }

                    $(document).foundation({
                        tooltip: {
                            selector: '.has-tip',
                            additional_inheritable_classes: [],
                            tooltip_class: '.tooltip',
                            touch_close_text: 'tap to close',
                            disable_for_touch: true,
                            tip_template: function (selector, content) {
                                return '<span data-selector="' + selector + '" class="'
                                    + Foundation.libs.tooltip.settings.tooltip_class.substring(1)
                                    + '">' + content + '<span class="nub"></span></span>';
                            }
                        }
                    });


                    var dom = $(ReactDOM.findDOMNode(this));
                    var onReorder = this.props.onReorder;

                    dom.sortable({
                        items: "li:not(.ui-state-disabled)",
                        delay: 150,
                        distance: 5,
                        opacity: 0.5,
                        scroll: true,
                        scrollSensitivity: 10,
                        tolerance: "intersect",
                        handle: scrollTarget,
                        helper: 'clone',
                        stop: function (event, ui) {
                            var order = dom.sortable("toArray", {attribute: "id"});
                            var yy = order.toString().replace('"', '');
                            doUpdMeeting1(yy, onReorder, order);
                        },
                        start: function (event, ui) {

                        }
                    }).disableSelection();

                },
                componentWillUpdate: function () {


                    if (Modernizr.touch) {
                        scrollTarget = ".touchscroll";
                    }


                    var dom = $(ReactDOM.findDOMNode(this));
                    var onReorder = this.props.onReorder;
                    dom.sortable({
                        items: "li:not(.ui-state-disabled)",
                        delay: 150,
                        distance: 5,
                        opacity: 0.5,
                        scroll: true,
                        scrollSensitivity: 10,
                        tolerance: "intersect",
                        handle: scrollTarget,
                        helper: 'clone',
                        stop: function (event, ui) {
                            var order = dom.sortable("toArray", {attribute: "id"});
                            var yy = order.toString().replace('"', '');
                            doUpdMeeting1(yy, onReorder, order);
                        },
                        start: function (event, ui) {
                        }
                    }).disableSelection();
                }

            });

            var requirementsModal = function (binder, isOutdoorAvailable, isOutdoor, isGlobalAvailable, isGlobal) {
                var imgName;
                if (isOutdoor) {
                    imgName = "outdoor.png";
                } else {
                    imgName = "indoor.png";
                }
                var globalImgName;
                if (isGlobal) {
                    globalImgName = "globe_selected.png";
                } else {
                    globalImgName = "globe_unselected.png";
                }
                var modal = $('#requirementsModal');

                var image = (isOutdoorAvailable) ? '<img style="width:44px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/' + imgName + '" />' : ' ';
                if (isGlobalAvailable) {
                    if (image.trim().length > 0) {
                        image += " ";
                    }
                    image += '<img style="width:44px" src="/etc/designs/girlscouts-vtk/clientlibs/css/images/' + globalImgName + '" />';
                }


                var _template = '<div class="modal_resource" style="height: 608px;overflow: hidden;">' +
                    '<div class="header clearfix">' +
                    '<h3 class="columns large-22">REQUIREMENTS</h3>' +
                    '<a class="close-reveal-modal columns large-2" href="#">' +
                    '<span style="font-size: 24px; color: black; font-weight: normal;">X</span>' +
                    '</a>' +
                    '</div>' +
                    '<div class="scroll content">' +
                    '<section class="content">' +
                    '<div style="text-align:center">' +
                    '<img src="/content/dam/girlscouts-vtk/local/icon/meetings/' + binder.id + '.png" />' +
                    '<h3>' + binder.reqTitle + '</h3><br />' +
                    '</div>' +
                    '<div>' + binder.req +
                    '</div><p style="text-align:center">' +
                    image +
                    '</p></section>' +
                    '</div>' +
                    '</div>';
                $(document).foundation();
                modal.html(_template)
                    .foundation('reveal', 'open');
            };


            var MeetingImg = React.createClass({
                displayName: "MeetingImg",
                render: function () {
                    var _this = this;
                    var src = "/content/dam/girlscouts-vtk/local/icon/meetings/" + this.props.mid + ".png";
                    var imgReturn = "";
                    var onClick = function () {
                    };

                    if (this.props.meetingInfo.req) {
                        imgReturn += ' _requirement_modal';
                        onClick = function (e) {
                            requirementsModal(_this.props.meetingInfo, _this.props.isOutdoorAvailable, _this.props.isOutdoor, _this.props.isGlobalAvailable, _this.props.isGlobal);
                        }
                    }
                    return (
                        React.createElement("img", {
                            src: src,
                            className: imgReturn,
                            onClick: onClick,
                            onError: function () {
                                $(this).addClass('hidden');
                            }
                        })
                    );
                }
            });

            var ViewMeeting = React.createClass({
                displayName: "ViewMeeting",
                render: function () {
                    var date = new Date(this.props.dateRaw).getTime();
                    if (date.toString() == 'NaN') {
                        date = new Date(this.props.date).getTime();
                    }
                    var src = "<%=relayUrl %>/content/girlscouts-vtk/en/vtk.details.html?elem=" + date;
                    return (

                        //React.createElement("a", {href: src}, this.props.name +":Outdoor available? "+this.props.isOutdoorAvailable+" Outdoor selected? : "+ this.props.isOutdoor)
                        React.createElement("a", {href: src}, this.props.name)

                    );
                }
            });

            function doUpdMeeting1(newVals, callback, callbackArgs) {
                var x = $.ajax({
                    url: '/content/girlscouts-vtk/controllers/vtk.controller.html?act=ChangeMeetingPositions&isMeetingCngAjax=' + newVals, // JQuery loads serverside.php
                    data: '',
                    dataType: 'html',
                }).done(function (html) {
                    vtkTrackerPushAction('MoveMeetings');
                    console.info('Before calling callback');
                    if (callback) {
                        console.info('Calling callback');
                        callback(callbackArgs);
                    }
                });
            }

            ReactDOM.render(
                React.createElement(CommentBox, {
                    url: "/content/girlscouts-vtk/controllers/vtk.controller.html?yearPlanSched=X",
                    pollInterval: 10000
                }),
                document.getElementById('thePlan')
            );

            function bgcolor(obj, comment, objType) {

                if (obj[comment].cancelled == 'true') {

                    return "bg-square canceled";

                } else if (meetingPassed &&
                    ((moment(comment) > moment(new Date())) || (moment(comment).get('year') < 1978))
                ) {
                    if (objType == '1') {
                        meetingPassed = false;
                    }

                    return "bg-square current";


                } else if (moment(comment).get('year') < 1978) {
                    return "bg-square";


                } else if (moment(comment) < moment(new Date())) {
                    return "bg-square passed";

                } else {
                    return "bg-square";
                }
            }

            function imageExists(image_url) {
                var http = new XMLHttpRequest();
                http.open('HEAD', image_url, false);
                http.send();
                return http.status != 404;
            }

            var DateBox = React.createClass({
                displayName: "DateBox",
                render: function () {
                    var obj = this.props.obj;
                    var comment = this.props.comment;
                    var xx = moment.tz(comment, "America/New_York");
                    var meeting = obj[comment];
                    var _this = this;
                    return (
                        React.createElement("a", {
                                onClick: function (event) {
                                    var e = event.currentTarget.offsetParent;
                                    event.preventDefault();
                                    if (<%= VtkUtil.hasPermission(selectedTroop, Permission.PERMISSION_EDIT_YEARPLAN_ID)%>) {
                                        _this.props.openModal({
                                            clientX: e.offsetLeft,
                                            clientY: e.offsetTop
                                        }, moment(comment).valueOf(), comment, meeting);
                                    }

                                }
                            },
                            React.createElement("div", {className: bgcolor(obj, comment, 1)},
                                React.createElement("div", {className: (moment(comment).get('year') < 1978 || obj[comment].type == 'MEETINGCANCELED') ? "hide" : "count"}, (obj[comment].sortOrder) + 1),
                                React.createElement("div", {className: "date"},
                                    React.createElement("p", {className: "month"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? "meeting" : moment.tz(comment, "America/New_York").format('MMM')),
                                    React.createElement("p", {className: "day"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? (obj[comment].sortOrder)+1 : moment.tz(comment, "America/New_York").format('DD')),
                                    React.createElement("p", {className: "hour"}, moment.tz(comment, "America/New_York").get('year') < 1978 ? "" : moment.tz(comment, "America/New_York").format('hh:mm a'))
                                )
                            )
                        )
                    );
                }
            });

            /*
                    //when dialog resizes we need to place is in the middle of the page.
                   $(window).resize(function() {
                     dWidth = $('.vtk-body .ui-dialog.modalWrap').width();
                     if($(window).width() > 920) {
                       $('.vtk-body .ui-dialog.modalWrap').css({'max-width':$(window).width()/2, 'width':'100%', 'left': ($(window).width()-dWidth)/2});
                     } else {
                       $('.vtk-body .ui-dialog.modalWrap').css({'max-width':$(window).width()/2, 'width':'100%', 'left': '0 !important'});
                     }
                   });

            */
        </script>
    </div>
    <%
    if(!isParent || isAdmin){
    %>
    <div style="text-align: center;">
        <button class="btn button btn-line" onclick="doMeetingLib(true)"><i class="icon-search-magnifying-glass"></i>
            SEARCH TO ADD MEETINGS
        </button>
    </div>
    <%
    }
    %>
</div>
<div id="requirementsModal" class="reveal-modal" data-reveal=""></div>
<br/> <br/>
<!-- Hack for NUB in Tooltip -->
<style>
    .tooltip span.nub {
        left: 18px;
    }
</style>
<%@include file="include/bodyBottom.jsp" %>
