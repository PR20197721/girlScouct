<%@ page import="org.apache.commons.lang.StringEscapeUtils,
                 org.girlscouts.vtk.models.Location,
                 org.girlscouts.vtk.models.MeetingE,
                 org.girlscouts.vtk.models.YearPlanComponent,
                 org.girlscouts.vtk.osgi.component.dao.YearPlanComponentType,
                 org.girlscouts.vtk.osgi.component.util.MeetingUtil" %>
<%@include file="/libs/foundation/global.jsp" %>
<cq:defineObjects/>
<%@include file="include/session.jsp" %>
<!-- apps/girlscouts-vtk/components/vtk/locationManage.jsp -->
<%!
    String activeTab = "community";
    boolean showVtkNav = true;
%>
<%
    java.util.List<Location> locations = selectedTroop.getYearPlan().getLocations();
    if (locations == null || locations.size() <= 0) {
        out.println("Applies to " + selectedTroop.getYearPlan().getMeetingEvents().size() + " of " + selectedTroop.getYearPlan().getMeetingEvents().size() + " meetings");
        return;
    }
%>
<div id="locMsg1"></div>
<div class="locationListing columns"><%
    for (int i = 0; i < locations.size(); i++) {
        Location location = locations.get(i);
        %><div class="locationResultElement">
            <div class="row">
                <!--<div class="small-8 columns"><a href="javascript:void(0)" onclick="rmLocation('<%=location.getUid()%>'); " class="button linkButton">-&nbsp;Remove</a></div>-->
                <div class="small-10 columns"><h3><%=location.getName()%></h3></div>
                <div class="small-14 columns"><h5><%=location.getAddress()%></h5></div>
            </div>
            <div class="row">
                <div class="small-24 columns"><%
                    if (selectedTroop.getYearPlan().getSchedule() != null) {
                        %><div class="locationList">
                            <ul class="clearfix small-block-grid-3"><%
                                java.util.Map<java.util.Date, YearPlanComponent> sched = new MeetingUtil().getYearPlanSched(selectedTroop.getYearPlan());
                                java.util.Iterator itr = sched.keySet().iterator();
                                int num = 0;
                                while (itr.hasNext()) {
                                    java.util.Date date = (java.util.Date) itr.next();
                                    YearPlanComponent _comp = sched.get(date);
                                    if (_comp.getType() != YearPlanComponentType.MEETING) {
                                        continue;
                                    }
                                    String mLoc = ((MeetingE) _comp).getLocationRef();
                                    mLoc = mLoc == null ? "" : mLoc;
                                    if (date.after(new java.util.Date())) {
                                        num = num + 1;
                                        %><li>
                                            <input type="checkbox" id="chbx_<%=i%>_<%=num%>" name="<%=location.getName() %>" value="<%=date%>" <%=mLoc.equals(location.getPath()) ? "CHECKED" : ""%> />
                                            <label for="chbx_<%=i%>_<%=num%>"><p><span
                                                    class="date"><%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, date)%></span></p>
                                            </label><%
                                            if (((MeetingE) _comp).getCancelled() != null && ((MeetingE) _comp).getCancelled().equals("true")) {
                                                %><span class="alert">(Cancelled)</span><% 
                                            } 
                                        %></li><%
                                    } else {
                                        %><li><%=mLoc.equals(location.getPath()) ? "Activity day past" : ""%>
                                            <del><%=VtkUtil.formatDate(VtkUtil.FORMAT_MMddYYYY, date)%></del>
                                        </li><%
                                    }
                                }
                            %></ul>
                            <div class="linkButton Wrapper">
                                <button class="btn"
                                        onclick="updLocations('<%=location.getPath()%>', '<%=StringEscapeUtils.escapeJavaScript( location.getName() )%>'); "
                                        class="button linkButton">Assign to checked locations
                                </button>
                                <button class="btn" onclick="applyLocToAllMeetings('<%=location.getPath()%>')"
                                        class="button linkButton">Apply to all meetings
                                </button>
                                <button class="btn right" onclick="rmLocation('<%=location.getUid()%>');">Remove</button>
                            </div>
                        </div><!--/location-list--><%
                    } else {
                        %><p>Please create a schedule in the CALENDAR section in order to assign locations to meetings by date</p><%
                    }
                %></div><!--small-20 columns-->
            </div><!--/row-->
        </div><!--/locationResultElement--><%           
    }
%></div>
