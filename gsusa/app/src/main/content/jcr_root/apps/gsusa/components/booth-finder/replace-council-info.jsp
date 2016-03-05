<%@page import="java.util.Map,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%!
public String replaceCouncilInfo(String input, Map<String, String> councilMap) {
	if (councilMap == null) {
		return input;
	} else {
		String output = input.replaceAll("\\{\\{code\\}\\}", councilMap.get("code"))
				             .replaceAll("\\{\\{name\\}\\}", councilMap.get("name"))
				             .replaceAll("\\{\\{abbrName\\}\\}", councilMap.get("abbrName"))
				             .replaceAll("\\{\\{cityStateZip\\}\\}", councilMap.get("cityStateZip"))
				             .replaceAll("\\{\\{url\\}\\}", councilMap.get("url"))
				             .replaceAll("\\{\\{cookieSaleStartDate\\}\\}", councilMap.get("cookieSaleStartDate"))
				             .replaceAll("\\{\\{cookieSaleEndDate\\}\\}", councilMap.get("cookieSaleEndDate"))
				             .replaceAll("\\{\\{cookiePageUrl\\}\\}", councilMap.get("cookiePageUrl"))
				             .replaceAll("\\{\\{cookieSaleContactEmail\\}\\}", councilMap.get("cookieSaleContactEmail"));
		String daysLeft = councilMap.get("daysLeft");
		if (daysLeft != null) {
			output = output.replaceAll("\\{\\{daysLeft\\}\\}", daysLeft);
		}
		return output;
	}
}
%>