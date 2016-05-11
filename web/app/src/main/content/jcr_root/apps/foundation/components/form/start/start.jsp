<%@page session="false"%><%--
  Copyright 1997-2009 Day Management AG
  Barfuesserplatz 6, 4001 Basel, Switzerland
  All Rights Reserved.

  This software is the confidential and proprietary information of
  Day Management AG, ("Confidential Information"). You shall not
  disclose such Confidential Information and shall use it only in
  accordance with the terms of the license agreement you entered into
  with Day.

  ==============================================================================

  Form 'start' component

  Draws the start of a form

--%><%@include file="/libs/foundation/global.jsp"%>
<%
%><%@ page import="com.day.cq.wcm.foundation.forms.ValidationInfo,
                 com.day.cq.wcm.api.WCMMode,
                 com.day.cq.wcm.foundation.forms.FormsConstants,
                 com.day.cq.wcm.foundation.forms.FormsHelper,
                 org.apache.sling.api.resource.Resource,
                 org.apache.sling.api.resource.ResourceUtil,
                 org.apache.sling.api.resource.ValueMap,
                 com.day.cq.wcm.foundation.forms.FieldHelper,
                 com.day.cq.wcm.foundation.forms.FormsHelper,
                 com.day.cq.wcm.foundation.forms.FieldDescription,
                 java.util.Iterator,
                 org.apache.sling.scripting.jsp.util.JspSlingHttpServletResponseWrapper, com.day.cq.wcm.foundation.Placeholder"%><%
%><cq:setContentBundle/>
<cq:include script="abacus.jsp"/><%
    FormsHelper.startForm(slingRequest, new JspSlingHttpServletResponseWrapper(pageContext));
	
    // we create the form div our selfs, and turn decoration on again.
    %><div class="form"><%
    %><%= Placeholder.getDefaultPlaceholder(slingRequest, "Form Start", "") %><%
    componentContext.setDecorate(true);
    // check if we have validation erros

    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	 	Resource formStartResource = resource;
	    ValueMap vm = properties;
	    StringBuilder sb = new StringBuilder();
	    //sb.append(req.getContextPath());
	    sb.append("/etc/importers/bulkeditor.html?rootPath=");
	    String actionPath = (String)vm.get("action", "");
	    String actionType = (String)vm.get("actionType", "");
	    if ((actionPath.trim().length() != 0) && (actionType.trim().endsWith("store"))) {
	    
	    %>
	        <style>
	        	button#view-data:hover, button#view-data:focus {
		            background: linear;
		            background: #fff9f9;
		            background: -moz-linear-gradient(top, #fff9f9 0%, #dee3e8 100%);
		            background: -webkit-gradient(linear, left top, left bottom, color-stop(0%,#fff9f9), color-stop(100%,#dee3e8));
		            background: -webkit-linear-gradient(top, #fff9f9 0%,#dee3e8 100%);
		            background: -o-linear-gradient(top, #fff9f9 0%,#dee3e8 100%);
		            background: -ms-linear-gradient(top, #fff9f9 0%,#dee3e8 100%);
		            background: linear-gradient(to bottom, #fff9f9 0%,#dee3e8 100%);
		            filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#fff9f9', endColorstr='#dee3e8',GradientType=0 );
		            border: solid 1px #666;
		            border-radius: 3px;
	            }

	            button#view-data {
		            border:solid 1px #666;
		            border-radius: 3px;
		            background-color: #ededed;
		            padding-left: 3px;
		            padding-right: 3px;
		            cursor: pointer;
		            margin: 0;
		            overflow: visible;
		            width: auto;
		            transition: none;
		            -moz-outline: 0 none;
		            outline: 0 none;
		            font: normal 11px Tahoma, Arial, Helvetica, sans-serif;
		            color: #333;
		            padding: 2px 4px;
		            margin:15px 0 20px 0;
	            }
	        </style>
	        <% 
	        
		    if (actionPath.endsWith("*")) {
		      actionPath = actionPath.substring(0, actionPath.length() - 1);
		    }
		    if (actionPath.endsWith("/")) {
		      actionPath = actionPath.substring(0, actionPath.length() - 1);
		    }
		    sb.append(FormsHelper.encodeValue(actionPath));
		    sb.append("&initialSearch=true&contentMode=false&spc=true&hdelb=false");
		    Iterator elements = FormsHelper.getFormElements(formStartResource);
		    while (elements.hasNext()) {
		      Resource element = (Resource)elements.next();
		      FieldHelper.initializeField(slingRequest, slingResponse, element);
		      FieldDescription[] descs = FieldHelper.getFieldDescriptions(slingRequest, element);
		      for (FieldDescription desc : descs) {
		        if (!desc.isPrivate()) {
		          String name = FormsHelper.encodeValue(desc.getName());
		          if (name.equals("submit")) {
		              continue;
		          }
		          sb.append("&cs=");
		          sb.append(name);
		          sb.append("&cv=");
		          sb.append(name);
		        }
		      }
		    }
		    sb.append("&cs=timestamp&cv=jcr:created");
			%><div><button id="view-data" type="button" class="x-btn-text" onclick='CQ.shared.Util.open("<%=sb.toString() %>", null, "FormReport"); return false;'>View Data ...</button></div><%
	    }       
    }

    final ValidationInfo info = ValidationInfo.getValidationInfo(request);
    if ( info != null ) {
        %><p class="form_error"><fmt:message key="Please correct the errors and send your information again."/></p><%
        final String[] msgs = info.getErrorMessages(null);
        if ( msgs != null ) {
            for(int i=0;i<msgs.length;i++) {
                %><p class="form_error"><%=msgs[i]%></p><%
            }
        }
    }
%>
