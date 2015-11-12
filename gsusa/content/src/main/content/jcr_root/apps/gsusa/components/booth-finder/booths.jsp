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
int showContactBannerPer = properties.get("showContactBannerPer", 25);

// Reset count so it continues with previous booths.
int count = (numPerPage * pageNum) % showContactBannerPer;
boolean shouldDisplayContactBanner = "Path2".equals(council.preferredPath);
for (int i = 0; i < Math.min(booths.size(), numPerPage); i++) {
	request.setAttribute("gsusa-booth-list-item", booths.get(i));
	%><cq:include script="booth-item.jsp"/><%
	request.removeAttribute("gsusa-booth-list-item");

    if (shouldDisplayContactBanner && count == showContactBannerPer - 1) {
        %><cq:include script="contact-banner.jsp"/><%
    }
    count = (count + 1) % showContactBannerPer;
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