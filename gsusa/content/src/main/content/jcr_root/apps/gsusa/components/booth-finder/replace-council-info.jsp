<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council" %>
<%!
public String replaceCouncilInfo(String input, Council council) {
	if (council == null) {
		return input;
	} else {
		String output = input.replaceAll("\\{\\{code\\}\\}", council.code)
				             .replaceAll("\\{\\{name\\}\\}", council.name)
				             .replaceAll("\\{\\{abbrName\\}\\}", council.abbrName)
				             .replaceAll("\\{\\{cityStateZip\\}\\}", council.cityStateZip)
				             .replaceAll("\\{\\{url\\}\\}", council.url)
				             .replaceAll("\\{\\{cookieSaleStartDate\\}\\}", council.cookieSaleStartDate)
				             .replaceAll("\\{\\{cookieSaleEndDate\\}\\}", council.cookieSaleEndDate)
				             .replaceAll("\\{\\{cookiePathUrl\\}\\}", council.cookiePageUrl)
				             .replaceAll("\\{\\{cookieSaleContactEmail\\}\\}", council.cookieSaleContactEmail);
		return output;
	}
}
%>