<%--
  Copyright 1997-2008 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'element' component

  Draws organizer fields for an event
  ==============================================================================
  DEPRECATED since CQ 5.6.  Use /libs/social/calendar/components/form/organizer/organizer.jsp directly.
  ==============================================================================

--%><%@include file="/libs/foundation/global.jsp"%><%
%><%@ page session="false" import="com.day.cq.wcm.foundation.forms.FormsHelper,
        com.day.cq.wcm.foundation.forms.LayoutHelper,
        com.day.cq.wcm.foundation.forms.ValidationInfo,
        javax.jcr.*" %><%

    // What       : ics     : Example                : JCR
    // ----------------------------------------------------------
    // userid     :         : john.smith             : userID
    
    // name       : CN      : John Smith             : jcr:title
    // email      : <value> : john.smith@company.com : email
    // sentby     : SENT-BY : peter.x@company.com    : sentBy
    // ldapdir    : DIR     : ldap://host.com:666..  : directoryEntry
    // department :         : PR                     : department
    // phone      :         : 12345678               : phone
    
    final String userIDValue = FormsHelper.getValue(slingRequest, "organizer/userID", currentNode.getSession().getUserID());
    // TODO: get name & co. from profile !?
    final String nameValue = FormsHelper.getValue(slingRequest, "organizer/jcr:title", "");
    final String emailValue = FormsHelper.getValue(slingRequest, "organizer/email", "");
    final String phoneValue = FormsHelper.getValue(slingRequest, "organizer/phone", "");
    final String departmentValue = FormsHelper.getValue(slingRequest, "organizer/department", "");
    
    //final String sentByValue = FormsHelper.getValue(slingRequest, "sentBy", "");
    //final String directoryEntryValue = FormsHelper.getValue(slingRequest, "directoryEntry", "");
%>
    <div class="form_row">
        <% LayoutHelper.printTitle("", "Organizer", false, out); %>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./organizer/userID", "User ID", false, out); %>
        <div class="form_rightcol">
            <input type="text" name="./organizer/userID" value="<%= userIDValue %>" size="60" class="x-form-text x-form-field" />
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./organizer/jcr:title", "Full Name", false, out); %>
        <div class="form_rightcol">
            <input type="text" name="./organizer/jcr:title" value="<%= nameValue %>" size="60" class="x-form-text x-form-field" />
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./organizer/email", "Email", false, out); %>
        <div class="form_rightcol">
            <input type="text" name="./organizer/email" value="<%= emailValue %>" size="60" class="x-form-text x-form-field" />
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./organizer/phone", "Phone", false, out); %>
        <div class="form_rightcol">
            <input type="text" name="./organizer/phone" value="<%= phoneValue %>" size="60" class="x-form-text x-form-field" />
        </div>
    </div>
    <div class="form_row">
        <% LayoutHelper.printTitle("./organizer/department", "Department", false, out); %>
        <div class="form_rightcol">
            <input type="text" name="./organizer/department" value="<%= departmentValue %>" size="60" class="x-form-text x-form-field" />
        </div>
    </div>

    