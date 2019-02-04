<%@include file="/libs/foundation/global.jsp" %>
<%
	String bannertype = properties.get("bannertype", "None");

	if(!bannertype.equals("None")) {
		if (bannertype.equals("1")) bannertype = "";
		
		String message = properties.get("message"+bannertype,"");
		String url = properties.get("url"+bannertype,"");
		String alert = properties.get("alert"+bannertype,"");
		String newstype = properties.get("newstype"+bannertype,"");
		String bgcolor = properties.get("bgcolor"+bannertype,"");
		String textcolor = properties.get("textcolor"+bannertype,"");

		Resource thumbnail = resource.getChild("thumbnail"+bannertype);
		String filePath = "";
		if(thumbnail != null) {
			filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
		}		
		
		
		if(!bgcolor.equals("") && newstype.equals("Text")) {
%>
			<div class="inner-wrapper" style="background-color:#<%=bgcolor%>">
<%	
		} else if (newstype.equals("Image")) {
%>
			<div class="inner-wrapper" style="padding: 0;">
<%		} else {
%>
			<div class="inner-wrapper">
<%		}

		if(newstype.equals("Image")) {
			if(!filePath.equals("")) {
				if(!url.equals("")) {
					if(url.substring(0,4).equals("http")) {
   						%><a href="<%=url %>" target="_blank"><% 
					} else{
    					String gshtmlUrl = url + ".html";
    					%><a href="<%=gshtmlUrl %>"><% 
					}
				}
%>
				<img src="<%= filePath %>" alt="<%=alert%> <%=message%>" title="<%=alert%> <%=message%>" style="max-width:100%;" class="thumbnail"/>
<%
				if(!url.equals("")) {
%>
					</a>
<% 
				}
			}else{
				%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
			}
		}

		if(newstype.equals("Text")) {
			if(!url.equals("")) {
					if(url.substring(0,4).equals("http")) {
   						%><a href="<%=url %>" target="_blank"><% 
					} else{
    					String gshtmlUrl = url.replaceAll(".html","") + ".html";
    					%><a href="<%=gshtmlUrl %>"><% 
					}
			}
			if(!textcolor.equals("")) {
%>
				<strong style="color:#<%=textcolor%>"><%= alert %></strong> <span style="color:#<%=textcolor%>"><%= message %></span>
<%
			} else {
%>
				<strong><%= alert %></strong> <span><%= message %></span>
<%			
			}
			if(!url.equals("")) {
%>
				</a>
<%
			}
			if(alert.isEmpty() && message.isEmpty()){
				%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
			}
		}
%>
			</div>
<%			
	}else{
		%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
	}
%>
