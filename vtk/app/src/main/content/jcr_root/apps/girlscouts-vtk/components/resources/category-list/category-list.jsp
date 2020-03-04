<%--

  Category List component.

  A list of VTK resource categories

--%><%
%>
<%@page import="com.day.cq.wcm.api.WCMMode,
                com.day.cq.wcm.api.components.IncludeOptions" %>
<%
%>
<%@include file="/libs/foundation/global.jsp" %>
<%
%>
<%@page session="false" %>
<%
%><%
    final int COL_NUM = 3;
    
    final int NONCUSTOMTOTAL = 9;
    final int NONCUST_PER_COL = NONCUSTOMTOTAL / COL_NUM;
    final int FIRST_TWO_COL_NONCUSTOM = NONCUSTOMTOTAL - NONCUST_PER_COL;
    
    final int CUSTOMTOTAL = 3;
    final int CUST_PER_COL = CUSTOMTOTAL / COL_NUM;
    final int FIRST_TWO_COL_CUSTOM = CUSTOMTOTAL - CUST_PER_COL;
%>
<div class="column small-24 medium-12 large-8"><%
    for (int i = 0; i < FIRST_TWO_COL_NONCUSTOM; i++) {
        if (i != 0 && i % NONCUST_PER_COL == 0) {
            for (int j = 0; j < CUST_PER_COL; j++){
                String path = "custom-category-" + j;
                IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
                %><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/custom-category"/><%
            }
            %></div>
            <div class="column small-24 medium-12 large-8"><%
        }
    String path = "category-" + i;
    IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category"/><%
    }
    for (int i = CUST_PER_COL; i<FIRST_TWO_COL_CUSTOM; i++){
		String path = "custom-category-" + i;
        IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
        %><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/custom-category"/><%
    }
%></div>
<%

    // For the last column. It has to be printed twice
%>
<div class="column small-24 medium-12 large-8 hide-for-medium-only"><%
    for (int i = FIRST_TWO_COL_NONCUSTOM; i < NONCUSTOMTOTAL; i++) {
        String path = "category-" + i;
        IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category"/><%
    }
    for (int i = FIRST_TWO_COL_CUSTOM; i<CUSTOMTOTAL; i++){
        String path = "custom-category-" + i;
        IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
        %><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/custom-category"/><%
    }
%></div>
<%

    WCMMode prevMode = WCMMode.DISABLED.toRequest(request);
%>
<div class="column small-24 medium-24 large-8 show-for-medium-only"><%
    for (int i = FIRST_TWO_COL_NONCUSTOM; i < NONCUSTOMTOTAL; i++) {
        String path = "category-" + i;
        IncludeOptions.getOptions(request, true).getCssClassNames().add("__box");
        IncludeOptions.getOptions(request, true).getCssClassNames().add("column");
        IncludeOptions.getOptions(request, true).getCssClassNames().add("small-12");
        if (i == NONCUSTOMTOTAL - 1) {
            IncludeOptions.getOptions(request, true).getCssClassNames().add("end");
        }
%><cq:include path="<%= path %>" resourceType="girlscouts-vtk/components/resources/category"/><%
    }
%></div>
<%
    prevMode.toRequest(request);

%>
<div style="clear:both"></div>
<%
%>