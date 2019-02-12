<%@include file="/libs/foundation/global.jsp" %>
<%@ page import="org.apache.sling.settings.SlingSettingsService,
				 java.util.Set,
				 com.day.cq.wcm.api.WCMMode"%><%
%>
<%
	String message = properties.get("message","");
	String url = properties.get("url","");
	String alert = properties.get("alert","");
	String newstype = properties.get("newstype","");
	String bgcolor = properties.get("bgcolor","");
	String textcolor = properties.get("textcolor","");

	Resource thumbnail = resource.getChild("thumbnail");
	String filePath = "";
	if(thumbnail != null) {
		filePath = ((ValueMap)thumbnail.adaptTo(ValueMap.class)).get("fileReference", "");
	}

	Set<String> runModes = sling.getService(SlingSettingsService.class).getRunModes();

	if(!newstype.equals("None")) {
		if(!bgcolor.equals("") && newstype.equals("Text")) {

			%><div class="inner-wrapper" style="background-color:#<%=bgcolor%>"><%
	
		} else if (newstype.equals("Image")) {

			%><div class="inner-wrapper" style="padding: 0;"><%
		} else {

			%><div class="inner-wrapper"><%
		}

		if(newstype.equals("Image")) {
			if(!filePath.equals("")) {
				if(!url.equals("")) {

					if(url.substring(0,4).equals("http")) {
   						%><a href="<%=url %>" target="_blank"><% 
					} else{
    					String gshtmlUrl = url.replaceAll(".html","") + ".html";
    					%><a href="<%=gshtmlUrl %>"><% 
					}
 
				}

				%><img src="<%= filePath %>" alt="<%=alert%> <%=message%>" title="<%=alert%> <%=message%>" style="max-width:100%;" class="thumbnail"/><%

				if(!url.equals("")) {

					%></a><%
 
				}
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

				%><strong style="color:#<%=textcolor%>"><%= alert %></strong> <span style="color:#<%=textcolor%>"><%= message %></span><%

			} else {

				%><strong><%= alert %></strong> <span><%= message %></span><%
			
			}
			if(!url.equals("")) {

				%></a><%

			}
		}

			%>
			<div class="vtk-breaking-news-button">
                <i class="icon-button-circle-cross"></i>
            </div>
			</div><%
			
    } 
    %>
		<div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder">
		</div>
    <%





%>

