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

  Image component

  Draws an image. 

--%>

<%@ page session="false" %>
<%@ page import="com.day.cq.wcm.api.components.DropTarget" %>
<%@ include file="/libs/foundation/global.jsp" %>
<%@ taglib prefix="gsusa" uri="https://girlscouts.org/gsusa/taglib" %>
<%@ taglib prefix="cq" uri="http://www.day.com/taglibs/cq/1.0" %>

<%
	
	String additionalCssStyle = properties.get("./additionalCssStyle", "");
	String linkUrl = properties.get("./linkURL", "");
	String title = properties.get("./jcr:title", "");
	String alt = properties.get("./alt", "");
	String style = "";
	if(additionalCssStyle.length() > 0) {
		style = "style=\""+additionalCssStyle+"\"";
	}
	
	String styleClass = DropTarget.CSS_CLASS_PREFIX + "image " + properties.get("./additionalCss", "");
	
    // add design information if not default (i.e. for reference paras)
    String suffix = null;
    if (!currentDesign.equals(resourceDesign)) {
    		suffix = currentDesign.getId();
    }
%>

<div id="<%= "cq-image-jsp-" + resource.getPath() %>" <%=style%> >
	<gsusa:image href="<%= linkUrl%>" alt="<%=alt %>" title="<%=title %>" relativePath='' styleClass='<%=styleClass %>' suffix='<%= suffix %>' newWindow='<%= properties.get("./newWindow", false) %>' />
</div>
<cq:text property="jcr:description" placeholder="" tagName="small" escapeXml="true"/>
