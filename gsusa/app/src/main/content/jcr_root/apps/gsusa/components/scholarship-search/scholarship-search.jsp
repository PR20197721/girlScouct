<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.TreeMap, 
				java.util.Arrays,
				java.util.ArrayList, 
				java.util.SortedSet, 
				java.util.TreeSet,
				java.lang.StringBuilder, 
				java.net.URLDecoder, 
				java.net.URLEncoder, 
				java.net.URL,
				java.net.MalformedURLException, 
				com.day.cq.wcm.api.WCMMode, 
				java.util.Iterator,
				java.util.regex.Pattern,
				java.util.regex.Matcher" %>


<%
	boolean searchMade = false;
	String path = properties.get("path","");
	if(path.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>
**PLEASE ENTER A FILE PATH
		<%
	} else if (!path.equals("")) {
		String typeParam = "All";
		String stateParam = "All";

		String mySelector = slingRequest.getRequestPathInfo().getSelectorString();
		if (mySelector != null) {
			String[] mySelectorArray = mySelector.split("\\."); 

			if(mySelectorArray.length == 2) {		
				if(mySelectorArray[0] != null){
					// cannot send "/" so replacing it with "~"
					mySelectorArray[0] = mySelectorArray[0].replace("~","/");
					typeParam = URLDecoder.decode(mySelectorArray[0], "UTF-8");
				}
				if(mySelectorArray[1] != null){
					// cannot send "/" so replacing it with "~"
					mySelectorArray[1] = mySelectorArray[1].replace("~","/");
					stateParam = URLDecoder.decode(mySelectorArray[1], "UTF-8");
				}
				searchMade = true;
			}
		}
		
		//Categorize scholarship pages by type
		PageManager pm = resourceResolver.adaptTo(PageManager.class);
		Page parent = pm.getPage(path);
		if (parent != null){
			Iterator<Page> children = parent.listChildren();
			TreeMap<String,ArrayList<Node>> map = new TreeMap<String,ArrayList<Node>>();
			SortedSet<String> types = new TreeSet<String>();
			SortedSet<String> states = new TreeSet<String>();
			while (children.hasNext()){
				Page child = children.next();
				Node childNode = child.adaptTo(Node.class);
				Node contentNode = childNode.getNode("jcr:content");
				String type = "Type Unspecified";
				if(contentNode.hasProperty("sType")){
					type = contentNode.getProperty("sType").getString();
				}
				String state = "State Unspecified";
				if(contentNode.hasProperty("state")){
					state = contentNode.getProperty("state").getString();
				}
	
				//To be used in HTML form
				types.add(type);
				states.add(state);
	
				if (map.containsKey(type)){
					map.get(type).add(contentNode);
				} else{
					map.put(type,new ArrayList<Node>());
					map.get(type).add(contentNode);
				}
			}
		%>
		
<script>
	function displayResults(obj) {
		// cannot send "/" so replacing it with "^"
		var myType = encodeURIComponent(obj["type"].value).replace(/'/g,"%27").replace(/"/g,"%22").replace("%2F","%7E");
		var myState = encodeURIComponent(obj["state"].value).replace(/'/g,"%27").replace(/"/g,"%22").replace("%2F","%7E");
		var myStr = "<%=currentPage.getPath()%>"+"."+myType+"."+myState+".html" 
		window.location=myStr;
		return false;
	}
</script>		
		
<form id="scholarship-form" onsubmit="return displayResults(this);">
	<section class="scholarship-type">
		<label>Type:</label>
		<select name="type">
			<option value="All">All</option>
			<%
				for(String t : types){
					String tOpt = "<option name=\"" + URLEncoder.encode(t, "US-ASCII") + "\">" + t + "</option>";
					if(t.equals(typeParam)){
						tOpt = "<option selected=\"selected\" name=\"" + URLEncoder.encode(t, "US-ASCII") + "\">" + t + "</option>";
					}
					
			%>
			<%= tOpt %>
			<%
				}
			%>
		</select>
	</section>
	<section class="scholarship-state">
		<label>State:</label>
		<select name="state">
			<option value="All">All</option>
			<%
				for(String s : states){
					String sOpt = "<option name=\"" + URLEncoder.encode(s, "US-ASCII") + "\">" + s + "</option>";
					if(s.equals(stateParam)){
						sOpt = "<option selected=\"selected\" name=\"" + URLEncoder.encode(s, "US-ASCII") + "\">" + s + "</option>";
					}
			%>
			<%= sOpt %>
			<%
				}
			%>
		</select>
	</section>
	<input type="submit" value="Search" class="button">
</form>
<%
	boolean anyResults = false;
	if(!searchMade){
		%> <p>Please note: if you are searching by state and your council office is in another state than the one you live in, be sure to search both states.</p><%
	}
	else{
		for(String key : map.keySet()){
			StringBuilder sb = new StringBuilder();
			boolean hasResults = false;
			sb.append(
				"<div class=\"type-wrapper\">\n<h4>"+ key + "</h4>" + "<ul class=\"records\">");
				//Generates result records
				//For future reference, if these parameter names seem random and/or wrong
				//let the record show that these are the names that were on the spreadsheet I was given
				for(Node n : map.get(key)){
					if((typeParam.equals("All") || (n.hasProperty("sType") && n.getProperty("sType").getString().equals(typeParam))) && (stateParam.equals("All") || (n.hasProperty("state") && n.getProperty("state").getString().equals(stateParam)))){
						hasResults = true;
						anyResults = true;
						StringBuilder record = new StringBuilder("<li>\n");
	
						if(n.hasProperty("sponsor") && n.hasProperty("website")){
							String site = n.getProperty("website").getString();
							if(site.substring(0,3).equals("www")) {
								site = "http://" + site;
							}
							if(!site.substring(0,3).equals("www") && !site.substring(0,4).equals("http")) {
								site = "http://" + site;
							}
							record.append("<h5>\n<a href=\"" + site
									+ "\" target=\"_blank\">" + n.getProperty("sponsor").getString()
									+ "</a>\n</h5>");
						}
	
						else if(n.hasProperty("sponsor")){
							record.append("<p><strong>" + n.getProperty("sponsor").getString()
									+ "</strong></p>");
						}
	
						else if(n.hasProperty("website")){
							record.append("<p><a href=\"" + n.getProperty("website").getString()
									+ "\" target=\"_blank\"><strong>" + n.getProperty("website").getString()
									+ "</strong></a>\n</p>");
						}
	
						if(n.hasProperty("city")){
							if(n.hasProperty("state")){
								record.append("<p>" + n.getProperty("city").getString() + ", " + n.getProperty("state").getString() + "</p>");
							}
							else{
								record.append("<p>" + n.getProperty("city").getString() + "</p>");
							}
						}
	
						else if(n.hasProperty("state")){
							record.append("<p>" + n.getProperty("state").getString() + "</p>");
						}
	
						if(n.hasProperty("awardName1")){
							record.append("<p><strong>"
									+ n.getProperty("awardName1").getString() + "</strong></p>");
						}
	
						if(n.hasProperty("awardContent1")){
							record.append("<p class=\"title\"><strong>\nAward:</strong></p>"
									+ "<p>" + n.getProperty("awardContent1").getString() + "</p>");
						}
	
						if(n.hasProperty("awardReq1")){
							record.append("<p class=\"title\"><strong>\nRequirements:</strong></p>"
									+ "<p>" + n.getProperty("awardReq1").getString() + "</p>");
						}
	
						if(n.hasProperty("awardName2")){
							record.append("<p>\n<strong>"
									+ n.getProperty("awardName2").getString() + "</strong></p>\n");
						}
	
						if(n.hasProperty("awardContent2")){
							record.append("<p class=\"title\"><strong>\nAward:</strong></p>"
									+ "<p>" + n.getProperty("awardContent2").getString() + "</p>");
						}
	
						if(n.hasProperty("awardReq2")){
							record.append("<p class=\"title\"><strong>Requirements:</strong></p>"
									+ "<p>" + n.getProperty("awardReq2").getString() + "</p>");
						}
	
						if(n.hasProperty("awardName3")){
							record.append("<p>\n<strong>"
									+ n.getProperty("awardName3").getString() + "</strong></p>\n");
						}
	
						if(n.hasProperty("awardContent3")){
							record.append("<p class=\"title\"><strong>\nAward:</strong></p>"
									+ "<p>" + n.getProperty("awardContent3").getString() + "</p>");
						}
	
						if(n.hasProperty("awardReq3")){
							record.append("<p class=\"title\"><strong>\nRequirements:</strong></p>"
									+ "<p>" + n.getProperty("awardReq3").getString() + "</p>");
						}
	
						if(n.hasProperty("awardName4")){
							record.append("<p>\n<strong>"
									+ n.getProperty("awardName4").getString() + "</strong></p>");
						}
	
						if(n.hasProperty("awardContent4")){
							record.append("<p class=\"title\"><strong>\nAward:</strong></p>"
									+ "<p>" + n.getProperty("awardContent4").getString() + "</p>");
						}
	
						if(n.hasProperty("awardReq4")){
							record.append("<p class=\"title\"><strong>\nRequirements:</strong></p>"
									+ "<p>" + n.getProperty("awardReq4").getString() + "</p>");
						}
	
						record.append("<p class=\"title\"><strong>For more information:</strong></p>");
	
						record.append("<p>");
						if(n.hasProperty("contact")){
							//Sometimes people use a URL here, sometimes they use a name
							//Check for URLs
							Pattern p = Pattern.compile("(http(s)?://)([\\w-]+\\.)+[\\w-]+(/[\\w- ;,./?%&=]*)?");
							Matcher m = p.matcher(n.getProperty("contact").getString());
							if(m.matches()){
								record.append("<a href=\"" + n.getProperty("contact").getString() + "\">" + n.getProperty("contact").getString() + "</a><br/>");
							}else{
								m = p.matcher("http://" + n.getProperty("contact").getString());
								if(m.matches()){
									record.append("<a href=\"http://" + n.getProperty("contact").getString() + "\">http://" + n.getProperty("contact").getString() + "</a><br/>");
								} else{
									p = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
									m = p.matcher(n.getProperty("contact").getString());
									if(m.matches()){
										record.append("<a href=\"mailto:" + n.getProperty("contact").getString() + "\">" + n.getProperty("contact").getString() + "</a><br>");
									}
									else{
										record.append(n.getProperty("contact").getString() + "<br/>");
									}
								}
							}
						}
	
						if(n.hasProperty("contactTitle")){
							record.append(n.getProperty("contactTitle").getString() + "<br/>");
						}
	
						if(n.hasProperty("contactAddr1")){
							record.append(n.getProperty("contactAddr1").getString() + "<br/>");
						}
	
						if(n.hasProperty("contactAddr2")){
							record.append(n.getProperty("contactAddr2").getString() + "<br/>");
						}
	
						if(n.hasProperty("contactCity")){
							if(n.hasProperty("contactState") && n.hasProperty("contactZip")){
								record.append(n.getProperty("contactCity").getString() + ", " + n.getProperty("contactState").getString() + " " + n.getProperty("contactZip").getString() + "<br/>");
							}
							else if (n.hasProperty("contactState")){
								record.append(n.getProperty("contactCity").getString() + ", " + n.getProperty("contactState").getString() + "<br/>");
							}
							else{
								record.append(n.getProperty("contactCity").getString() + "<br/>");
							}
						}
	
						else if(n.hasProperty("contactState")){
							if(n.hasProperty("contactZip")){
								record.append(n.getProperty("contactState").getString() + " " + n.getProperty("contactZip").getString() + "<br/>");
							}
							record.append(n.getProperty("contactState").getString() + "<br/>");
						}
	
						else if(n.hasProperty("contactZip")){
							record.append(n.getProperty("contactZip").getString() + "<br/>");
						}
	
						if(n.hasProperty("contactPhone")){
							record.append(n.getProperty("contactPhone").getString() + "<br/>");
						}
	
						if(n.hasProperty("contactEmail")){
							record.append("<a href=\"mailto:" + n.getProperty("contactEmail").getString() + "\">" + n.getProperty("contactEmail").getString() + "</a>");
						}
	
						record.append("</p></li>");
						sb.append(record.toString());
					}
				}
				sb.append("</ul>\n</div><!--/type-wrapper-->");
				if(hasResults){
			%>
			<%= sb.toString() %>
			<%
					}
				}
			}
			if(searchMade && !anyResults){
				%> Sorry, no results found <%
			}
		}
	}
%>