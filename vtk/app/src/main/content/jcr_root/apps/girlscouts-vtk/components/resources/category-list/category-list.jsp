<%--

  Category List component.

  A list of VTK resource categories

--%><%
%><%@page import="java.util.*,
	com.day.cq.wcm.api.components.IncludeOptions,
	com.day.cq.wcm.api.WCMMode"%><%
%><%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %><%
%><%
	final int TOTAL = 9;
	final int COL_NUM = 3;
	final int NUM_PER_COL = TOTAL / COL_NUM;
	final int FIRST_TWO_COL_COUNT = TOTAL - NUM_PER_COL;
	%><div class="column small-24 medium-12 large-8"><%
	for (int i = 0; i < FIRST_TWO_COL_COUNT; i++) {
		if (i != 0 && i % NUM_PER_COL == 0) {
			%></div><div class="column small-24 medium-12 large-8"><%
		}
		String path = "category-" + Integer.toString(i);
		IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
		%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category" /><%
	}
	%></div><%

	// For the last column. It has to be printed twice
	%><div class="column small-24 medium-12 large-8 hide-for-medium-only"><%
	for (int i = FIRST_TWO_COL_COUNT; i < TOTAL; i++) {
		String path = "category-" + Integer.toString(i);
		IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
		%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category" /><%
	}
	%></div><%
	
	WCMMode prevMode = WCMMode.DISABLED.toRequest(request);
	%><div class="column small-24 medium-24 large-8 show-for-medium-only"><%
	for (int i = FIRST_TWO_COL_COUNT; i < TOTAL; i++) {
		String path = "category-" + Integer.toString(i);
		IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
		IncludeOptions.getOptions(request, true).getCssClassNames().add("column");
		IncludeOptions.getOptions(request, true).getCssClassNames().add("small-12");
		if (i == TOTAL - 1) {
			IncludeOptions.getOptions(request, true).getCssClassNames().add("end");
		}
		%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category" /><%
	}
	%></div><%
	prevMode.toRequest(request);

	%><div style="clear:both"></div><%
%>