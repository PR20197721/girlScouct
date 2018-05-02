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

  Default parsys component

  Includes all child resources but respects the columns control resources and
  layouts the HTML accordingly.

--%><%@page import="java.util.HashSet,
                    java.util.Set,
                    com.day.cq.commons.jcr.JcrConstants,
                    com.day.cq.wcm.api.WCMMode,
                    com.day.cq.wcm.api.components.IncludeOptions,
                    com.day.cq.wcm.foundation.Paragraph,
                    com.day.cq.wcm.foundation.ParagraphSystem" %><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%
	final int COLUMNS_PER_ROW = 24;	

	String cssClasses = properties.get("cssClasses", "");

	String largeColumns = properties.get("largeColumns", "4");
	String mediumColumns = properties.get("mediumColumns", "2");
	String smallColumns = properties.get("smallColumns", "1");
	//largeColumns = "0".equals(largeColumns) ? "4" : largeColumns;
	//mediumColumns = "0".equals(mediumColumns) ? "2" : mediumColumns;
	//smallColumns = "0".equals(smallColumns) ? "1" : smallColumns;
	// Do not need to catch NumberFormatException because it is guaranteed by the "numberfield" widget
	int largeColumnsNum = Integer.parseInt(largeColumns);
	int mediumColumnsNum = Integer.parseInt(mediumColumns);
	int smallColumnsNum = Integer.parseInt(smallColumns);
	
	largeColumnsNum = largeColumnsNum < 1 ? 4 : largeColumnsNum;
	mediumColumnsNum = mediumColumnsNum < 1 ? 2 : mediumColumnsNum;
	smallColumnsNum = smallColumnsNum < 1 ? 1 : smallColumnsNum;
	
	String largeCss = Integer.toString(COLUMNS_PER_ROW / largeColumnsNum);
	String mediumCss = Integer.toString(COLUMNS_PER_ROW / mediumColumnsNum);
	String smallCss = Integer.toString(COLUMNS_PER_ROW / smallColumnsNum);
	
	int maxColumnsNum = Math.max(largeColumnsNum, Math.max(mediumColumnsNum, smallColumnsNum));
	
	for (int i = 0; i < maxColumnsNum; i++) {
	    String parPath = "./par-" + Integer.toString(i);
	    setCssClasses("columns large-" + largeCss + " medium-" + mediumCss + " small-" + smallCss, request);
	    %><cq:include path="<%= parPath %>" resourceType="foundation/components/parsys" /><%
	}
%>
