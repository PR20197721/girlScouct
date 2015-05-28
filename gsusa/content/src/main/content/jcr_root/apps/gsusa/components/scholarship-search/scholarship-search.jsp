<%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.TreeMap, java.util.ArrayList, java.util.SortedSet, java.util.TreeSet, java.lang.StringBuilder,
java.net.URLDecoder, java.net.URL, java.net.MalformedURLException" %>

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
			Node cNode = child.adaptTo(Node.class);
			String type = cNode.getProperty("sType");
			String state = cNode.getProperty("state");
			if(type == null){
				type = "Type Unspecified";
			}
			if(state == null){
				state = "State Unspecified";
			}
			
			//To be used in HTML form
			types.add(type);
			states.add(state);
			
			if (map.containsKey(cNode.getProperty(type))){
				map.get(type).add(cNode);
			} else{
				map.put(type,new ArrayList<Node>());
				map.get(type).add(cNode);
			}
		}
		%>	
<form action="<%= currentPage.getPath()+".html"%>" method="GET">
	Type:
	<select name="type">
	<option value="All">All</option>
	<%
		for(String t : types){
			String tOpt = "<option name=\"" + t + "\">" + t + "</option>";
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
			String sOpt = "<option name=\"" + s + "\">" + s + "</option>";
	%>
	<%= sOpt %>
	<%
		}
	%>
	</select>
	<input type="submit" value="Search">
</form>
<%
		String typeParam = URLDecoder.decode(slingRequest.getParameter("type"), "UTF-8");
		if(typeParam == null){
			typeParam = "All";
		}
		String stateParam = URLDecoder.decode(slingRequest.getParameter("state"), "UTF-8");
		if(stateParam == null){
			stateParam = "All";
		}
		for(String key : map){
			StringBuilder sb = new StringBuilder();
			boolean hasResults = false;
			sb.append(
					"<div id=\"TypeWrapper\">\n"
					 	+ "<div id=\"TypeTag\">" + key + "\n");
			//Generates result records
			//For future reference, if these parameter names seem random and/or wrong
			//let the record show that these are the names that were on the spreadsheet I was given
			for(Node n : map.get(key)){
				if(typeParam.equals("All") || (n.getProperty("sType") != null && n.getProperty("sType").equals(typeParam))){
					hasResults = true;
					StringBuilder record = new StringBuilder("<div id=\"Record\">\n");
					
					if(n.getProperty("sponsor") != null && n.getProperty("website") != null){
						record.append("<p>\n<a href=\"" + n.getProperty("website") 
								+ "\" target=\"_blank\">\n<strong>" + n.getProperty("sponsor")
								+ "</strong>\n</a>\n</p>\n");
					}
					
					else if(n.getProperty("sponsor") != null){
						record.append("<p>\n<strong>" + n.getProperty("sponsor") 
								+ "</strong>\n</p>\n");
					}
					
					else if(n.getProperty("website") != null){
						record.append("<p>\n<a href=\"" + n.getProperty("website") 
								+ "\" target=\"_blank\">\n<strong>" + n.getProperty("website")
								+ "</strong>\n</a>\n</p>\n");
					}
					
					if(n.getProperty("city") != null){
						if(n.getProperty("state") != null){
							record.append("<p>" + n.getProperty("city") + ", " + n.getProperty("state") + "\n</p>\n");
						}
						else{
							record.append("<p>" + n.getProperty("city") + "\n</p>\n");
						}
					}
					
					else if(n.getProperty("state") != null){
						record.append("<p>" + n.getProperty("state") + "\n</p>\n");
					}
					
					if(n.getProperty("awardName1") != null){
						record.append("<p>\n<b>\n<em>"
								+ n.getProperty("awardName1") + "</em>\n</b>\n</p>\n");
					}
					
					if(n.getProperty("awardContent1") != null){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent1") + "\n</p>\n");
					}
					
					if(n.getProperty("awardReq1") != null){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq1") + "\n</p>\n");
					}
					
					if(n.getProperty("awardName2") != null){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName2") + "</em>\n</b>\n</p>\n");
					}
					
					if(n.getProperty("awardContent2") != null){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent2") + "\n</p>\n");
					}
					
					if(n.getProperty("awardReq2") != null){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq2") + "\n</p>\n");
					}
					
					if(n.getProperty("awardName3") != null){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName3") + "</em>\n</b>\n</p>\n");
					}
					
					if(n.getProperty("awardContent3") != null){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent3") + "\n</p>\n");
					}
					
					if(n.getProperty("awardReq3") != null){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq3") + "\n</p>\n");
					}
					
					if(n.getProperty("awardName4") != null){
						record.append("<br>\n<p>\n<b>\n<em>"
								+ n.getProperty("awardName4") + "</em>\n</b>\n</p>\n");
					}
					
					if(n.getProperty("awardContent4") != null){
						record.append("<br>\n<p>\n<b>Award:</b></p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardContent4") + "\n</p>\n");
					}
					
					if(n.getProperty("awardReq4") != null){
						record.append("<br>\n<p>\n<b>Requirements:</b>\n</p>\n"
								+ "<p style=\"padding-left:20px\">\n" + n.getProperty("awardReq4") + "\n</p>\n");
					}
					
					record.append("<br>\n<p>\n<b>For more information:</b>\n</p>\n");
					
					if(n.getProperty("contact") != null){
						//Sometimes people use a URL here, sometimes they use a name
						//Check for URLs
						try{
							URL u = new URL(n.getProperty("contact"));
							u.toURI();
							record.append("<p>\n<a href=\"" + n.getProperty("contact") + "\">" + n.getProperty("contact") + "</a>\n</p>\n");
						} catch(MalformedURLException e){
							record.append("<p>\n" + n.getProperty("contact") + "\n</p>\n");
						}
					}
					
					if(n.getProperty("contactTitle") != null){
						record.append("<p>\n" + n.getProperty("contactTitle") + "</p>\n");
					}
					
					if(n.getProperty("contactAddr1") != null){
						record.append("<p>\n" + n.getProperty("contactAddr1") + "</p>\n");
					}
					
					if(n.getProperty("contactAddr2") != null){
						record.append("<p>\n" + n.getProperty("contactAddr2") + "</p>\n");
					}
					
					if(n.getProperty("contactCity") != null){
						if(n.getProperty("contactState") != null && n.getProperty("contactZip") != null){
							record.append("<p>" + n.getProperty("contactCity") + ", " + n.getProperty("contactState") + " " + n.getProperty("contactZip") + "\n</p>\n");
						}
						else if (n.getProperty("contactState") != null){
							record.append("<p>" + n.getProperty("contactCity") + ", " + n.getProperty("contactState") + "\n</p>\n");
						}
						else{
							record.append("<p>" + n.getProperty("contactCity") + "\n</p>\n");
						}
					}
					
					else if(n.getProperty("contactState") != null){
						if(n.getProperty("contactZip") != null){
							record.append("<p>" + n.getProperty("contactState") + " " + n.getProperty("contactZip") + "\n</p>\n");
						}
						record.append("<p>" + n.getProperty("contactState") + "\n</p>\n");
					}
					
					else if(n.getProperty("contactZip") != null){
						record.append("<p>" + n.getProperty("contactZip") + "\n</p>\n");
					}
					
					if(n.getProperty("contactPhone") != null){
						record.append("<p>" + n.getProperty("contactPhone") + "\n</p>\n");
					}
					
					if(n.getProperty("contactEmail") != null){
						record.append("<p>\n<a href=\"" + n.getProperty("contactEmail") + "\">" + n.getProperty("contactEmail") + "</a>\n</p>\n");
					}
					
					record.append("</div>");
					sb.append(record.toString());
				}
			}
			sb.append("</div>");
			if(hasResults){
%>
<%= sb.toString() %>
<%
			}
		}
	}
%>