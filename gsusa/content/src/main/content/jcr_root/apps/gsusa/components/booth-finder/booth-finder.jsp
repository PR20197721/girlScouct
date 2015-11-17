<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.util.List,
                java.util.regex.Pattern,
                org.apache.sling.api.SlingHttpServletRequest,
                org.apache.sling.api.request.RequestPathInfo, 
                com.day.cq.wcm.api.WCMMode"%>
<%@include file="/libs/foundation/global.jsp"%>

<%@page session="false" %>

<%
String zip = getDefault("zip", "", slingRequest);
RequestPathInfo requestPathInfo = slingRequest.getRequestPathInfo();
String[] selectors = requestPathInfo.getSelectors();
// Check if any selector is a zip. If so, it takes over the zip parameter.
Pattern zipPattern = Pattern.compile("^\\d{5}$");
for (String selector : selectors) {
    if (zipPattern.matcher(selector).matches()) {
    	zip = selector;
    	break;
    }
}

if (zip == null || zip.isEmpty()) {
    %><cq:include script="zip-required.jsp" /><%
} else {
    // Initialize variables
    String radius = getDefault("radius", "25", slingRequest);
    String date = getDefault("date", "60", slingRequest);
    String sortBy = getDefault("sortBy", "distance", slingRequest);
    String pageParam = request.getParameter("page");
    int pageNum;
    try {
        pageNum = Integer.parseInt(pageParam);
    } catch (Exception e) {
        pageNum = 0;
    }
    int numPerPage = properties.get("numPerPage", 50);

    String queryString = request.getQueryString();
    if (queryString == null) {
		queryString = "";
    }
    
    BoothFinder boothFinder = sling.getService(BoothFinder.class);
    try {
        List<BoothBasic> booths = boothFinder.getBooths(zip, date, radius, sortBy, pageNum, numPerPage, queryString);
        Council council = boothFinder.getCouncil(zip);
        String preferredPath = council.preferredPath;

        request.setAttribute("gsusa_council_info", council);
        if (!booths.isEmpty()) {
            boolean headless = false;
            for (String selector : selectors) {
            	if (selector.equalsIgnoreCase("headless")) {
            		headless = true;
            		break;
            	}
            }

            request.setAttribute("gsusa_cookie_booths", booths);
            request.setAttribute("gsusa_booth_list_zip", zip);
            request.setAttribute("gsusa_booth_list_radius", radius);
            request.setAttribute("gsusa_booth_list_date", date);
            request.setAttribute("gsusa_booth_list_sortby", sortBy);
            request.setAttribute("gsusa_booth_list_pagenum", pageNum);
            request.setAttribute("gsusa_booth_list_numperpage", numPerPage);
            if (headless) {
                %><cq:include script="booths.jsp" /><%
            } else {
                %><cq:include script="booth-list.jsp" /><%
            }
            request.setAttribute("gsusa_cookie_booths", null);
        } else if ("Path1".equalsIgnoreCase(preferredPath)) {
            %><cq:include script="path1.jsp" /><%
        } else if ("Path2".equalsIgnoreCase(preferredPath)) {
            %><cq:include script="path2.jsp" /><%
        } else if ("Path3".equalsIgnoreCase(preferredPath)) {
            // Path3 is deprecated.
        } else if ("Path4".equalsIgnoreCase(preferredPath)) {
            %><cq:include script="path4.jsp" /><%
        } else if ("Path5".equals(preferredPath)) {
            %><cq:include script="path5.jsp" /><%
        }
        request.setAttribute("gsusa_council_info", null);
        request.setAttribute("gsusa_booth_list_zip", null);
        request.setAttribute("gsusa_booth_list_radius", null);
        request.setAttribute("gsusa_booth_list_date", null);
        request.setAttribute("gsusa_booth_list_sortby", null);
        request.setAttribute("gsusa_booth_list_pagenum", null);
        request.setAttribute("gsusa_booth_list_numperpage", null);
	} catch (Exception e) { // boothFinder.getBooths
//		System.out.println(e.getMessage());
       %><div>There is a problem communicating with the server. Please try again later.</div><%
    }
    
}
%>

<%!
public String getDefault(String param, String defaultValue, SlingHttpServletRequest req) {
    String value = req.getParameter(param);
    return value == null || value.trim().isEmpty() ? defaultValue : value;
}
%>
