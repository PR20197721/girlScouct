<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.util.List" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
List<BoothBasic> booths = (List<BoothBasic>)request.getAttribute("gsusa_cookie_booths");
Council council = (Council)request.getAttribute("gsusa_council_info");
int numPerPage = (Integer)request.getAttribute("gsusa_booth_list_numperpage");
int pageNum = (Integer)request.getAttribute("gsusa_booth_list_pagenum");

for (int i = 0; i < Math.min(booths.size(), numPerPage); i++) {
	request.setAttribute("gsusa-booth-list-item", booths.get(i));
	%><cq:include script="booth-item.jsp"/><%
	request.removeAttribute("gsusa-booth-list-item");
}

// If there are no more booths, hide the "more" link.
if (booths.size() <= numPerPage) {
%>
    <script>
        $('.booth-finder .show-more').hide();
    </script>
<%
}
%>