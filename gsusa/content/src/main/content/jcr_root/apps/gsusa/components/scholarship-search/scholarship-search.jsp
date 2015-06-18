<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.TreeMap, java.util.ArrayList, java.util.SortedSet, java.util.TreeSet, 
java.lang.StringBuilder, java.net.URLDecoder, java.net.URLEncoder, java.net.URL, 
java.net.MalformedURLException, com.day.cq.wcm.api.WCMMode, java.util.Iterator" %>

<%
	String path = properties.get("path","");
	if(path.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>
**PLEASE ENTER A FILE PATH
		<%
	} else {
		//Categorize scholarship pages by type
		PageManager pm = resourceResolver.adaptTo(PageManager.class);
		Page parent = pm.getPage(path);
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
<form action="<%= currentPage.getPath()+".html"%>" method="GET">
	Type:
	<select name="type">
	<option value="All">All</option>
	<%
		for(String t : types){
			String tOpt = "<option name=\"" + URLEncoder.encode(t, "US-ASCII") + "\">" + t + "</option>";
	%>
	<%= tOpt %>
	<%
		}
	%>
	</select>
	State:
	<select name="state">
	<option value="All">All</option>
	<%
		for(String s : states){
			String sOpt = "<option name=\"" + URLEncoder.encode(s, "US-ASCII") + "\">" + s + "</option>";
	%>
	<%= sOpt %>
	<%
		}
	%>
	</select>
	<input type="submit" value="Search">
</form>
<%
		String typeParam = "All";
		if(slingRequest.getParameter("type") != null){
			typeParam = URLDecoder.decode(slingRequest.getParameter("type"), "UTF-8");
		}
		String stateParam = "All";
		if(slingRequest.getParameter("state") != null){
			stateParam = URLDecoder.decode(slingRequest.getParameter("state"), "UTF-8");
		}
		boolean searchMade = false;
		boolean anyResults = false;
		if(slingRequest.getParameter("type") != null && slingRequest.getParameter("state") != null){
			searchMade = true;
		}
		for(String key : map.keySet()){
			StringBuilder sb = new StringBuilder();
			boolean hasResults = false;
			sb.append(
					"<div id=\"TypeWrapper\">\n"
					 	+ "<div id=\"TypeTag\">" + key + "\n");
			//Generates result records
			//For future reference, if these parameter names seem random and/or wrong
			//let the record show that these are the names that were on the spreadsheet I was given
			for(Node n : map.get(key)){
				if((typeParam.equals("All") || (n.hasProperty("sType") && n.getProperty("sType").getString().equals(typeParam))) && (stateParam.equals("All") || (n.hasProperty("state") && n.getProperty("state").getString().equals(stateParam)))){
					hasResults = true;
					anyResults = true;
					StringBuilder record = new StringBuilder("<div id=\"Record\">\n");
					
					if(n.hasProperty("sponsor") && n.hasProperty("website")){
						String site = n.getProperty("website").getString();
						if(!site.substring(0,7).equals("http")){
							site = "http://" + site;
						}
						record.append("<p>\n<a href=\"" + site 
								+ "\" target=\"_blank\">\n<strong>" + n.getProperty("sponsor").getString()
								+ "</strong>\n</a>\n</p>\n");
					}
					
					else if(n.hasProperty("sponsor")){
						record.append("<p>\n<strong>" + n.getProperty("sponsor").getString() 
								+ "</strong>\n</p>\n");
					}
					
					else if(n.hasProperty("website")){
						record.append("<p>\n<a href=\"" + n.getProperty("website").getString()
								+ "\" target=\"_blank\">\n<strong>" + n.getProperty("website").getString()
								+ "</strong>\n</a>\n</p>\n");
					}
					
					if(n.hasProperty("city")){
						if(n.hasProperty("state")){
							record.append("<p>" + n.getProperty("city").getString() + ", " + n.getProperty("state").getString() + "\n</p>\n");
						}
						else{
							record.append("<p>" + n.getProperty("city").getString() + "\n</p>\n");
						}
					}
					
					else if(n.hasProperty("state")){
						record.append("<p>" + n.getProperty("state").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardName1")){
						record.append("<p>\n<b>\n<em>"
								+ n.getProperty("awardName1").getString() + "</em>\n</b>\n</p>\n");
					}
					
					if(n.hasProperty("awardContent1")){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent1").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardReq1")){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq1").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardName2")){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName2").getString() + "</em>\n</b>\n</p>\n");
					}
					
					if(n.hasProperty("awardContent2")){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent2").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardReq2")){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq2").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardName3")){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName3").getString() + "</em>\n</b>\n</p>\n");
					}
					
					if(n.hasProperty("awardContent3")){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent3").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardReq3")){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq3").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardName4")){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName4").getString() + "</em>\n</b>\n</p>\n");
					}
					
					if(n.hasProperty("awardContent4")){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent4").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("awardReq4")){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq4").getString() + "\n</p>\n");
					}
					
					record.append("<br>\n<p>\n<b>For more information:</b>\n</p>\n");
					
					if(n.hasProperty("contact")){
						//Sometimes people use a URL here, sometimes they use a name
						//Check for URLs
						try{
							URL u = new URL(n.getProperty("contact").getString());
							u.toURI();
							record.append("<p>\n<a href=\"" + n.getProperty("contact").getString() + "\">" + n.getProperty("contact").getString() + "</a>\n</p>\n");
						} catch(MalformedURLException e){
							record.append("<p>\n" + n.getProperty("contact").getString() + "\n</p>\n");
						}
					}
					
					if(n.hasProperty("contactTitle")){
						record.append("<p>\n" + n.getProperty("contactTitle").getString() + "</p>\n");
					}
					
					if(n.hasProperty("contactAddr1")){
						record.append("<p>\n" + n.getProperty("contactAddr1").getString() + "</p>\n");
					}
					
					if(n.hasProperty("contactAddr2")){
						record.append("<p>\n" + n.getProperty("contactAddr2").getString() + "</p>\n");
					}
					
					if(n.hasProperty("contactCity")){
						if(n.hasProperty("contactState") && n.hasProperty("contactZip")){
							record.append("<p>" + n.getProperty("contactCity").getString() + ", " + n.getProperty("contactState").getString() + " " + n.getProperty("contactZip").getString() + "\n</p>\n");
						}
						else if (n.hasProperty("contactState")){
							record.append("<p>" + n.getProperty("contactCity").getString() + ", " + n.getProperty("contactState").getString() + "\n</p>\n");
						}
						else{
							record.append("<p>" + n.getProperty("contactCity").getString() + "\n</p>\n");
						}
					}
					
					else if(n.hasProperty("contactState")){
						if(n.hasProperty("contactZip")){
							record.append("<p>" + n.getProperty("contactState").getString() + " " + n.getProperty("contactZip").getString() + "\n</p>\n");
						}
						record.append("<p>" + n.getProperty("contactState").getString() + "\n</p>\n");
					}
					
					else if(n.hasProperty("contactZip")){
						record.append("<p>" + n.getProperty("contactZip").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("contactPhone")){
						record.append("<p>" + n.getProperty("contactPhone").getString() + "\n</p>\n");
					}
					
					if(n.hasProperty("contactEmail")){
						record.append("<p>\n<a href=\"mailto:" + n.getProperty("contactEmail").getString() + "\">" + n.getProperty("contactEmail").getString() + "</a>\n</p>\n");
					}
					
					record.append("</div>");
					sb.append(record.toString());
				}
			}
			sb.append("</div>\n</div>");
			if(hasResults){
%>
<%= sb.toString() %>
<%
			}
		}
		if(searchMade && !anyResults){
			%> Sorry, no results found <%
		}
	}
%>