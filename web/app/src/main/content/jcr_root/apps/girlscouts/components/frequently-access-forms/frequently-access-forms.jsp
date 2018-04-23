<%@ page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%><%
%><%@include file="/libs/foundation/global.jsp"%>
<%@page import="java.util.ArrayList,
				java.util.List" %>

<%

List<String> linksList = new ArrayList<String>();
	if(currentNode.hasNode("links")){
        Node links = currentNode.getNode("links");
        NodeIterator iter = links.getNodes();
        while(iter.hasNext()){
            Node linkNode = iter.nextNode();
            String title = "";
            if(linkNode.hasProperty("title")){
                title = linkNode.getProperty("title").getString();
            }

            String externalLink = "";
            if(linkNode.hasProperty("externalLink")){
                externalLink = linkNode.getProperty("externalLink").getString();
            }

            String formPath = "";
			if(linkNode.hasProperty("formPath")){
                formPath = linkNode.getProperty("formPath").getString();
            }

            String newWindow = "";
			if(linkNode.hasProperty("newWindow")){
                newWindow = linkNode.getProperty("newWindow").getString();
            }


			String listItem = title + "|||" + externalLink + "|||" + formPath + "|||" + newWindow;
            linksList.add(listItem);

    
        }
	}


String[] links = new String[0];
if(!linksList.isEmpty()){
	links = linksList.toArray(new String[0]);
} else{
	links = properties.get("links", String[].class);
}

String freqTitle = properties.get("freq-title","");
String path = "";
if (freqTitle == null && links == null) {
	%><div data-emptytext="<%=component.getTitle()%>" class="cq-placeholder"></div><%
}else {%>
     <div class="row">
       <div class="small-24 large-24 medium-24 columns">
         <strong><%=freqTitle%></strong>
       </div>
     </div>
	 <div class="row">
	   <div class="small-24 large-24 medium-24 columns">
	    <div class="checkbox-grid">
	   
	  
	<%
	
	if(links!=null){
		for (int i = 0; i < links.length; i++) {
	 		String[] values = links[i].split("\\|\\|\\|");
			String label = values[0];
			String externalLink = values.length>=2? values[1] : "" ;
			String internalLink = values.length>=3 ? values[2] : "";
			String newWindow = values.length >= 4 && values[3].equalsIgnoreCase("true") ? " target=\"_blank\"" : "";
			if(!externalLink.isEmpty()){
				path = externalLink;
			}
			if(!internalLink.isEmpty()){
				path = genLink(resourceResolver, internalLink);
				
			}
		%>
			<span><a href="<%= path %>" <%=newWindow%>><%= label %></a></span><%
		}
	}%>
	    </div>
	   </div>
	 
	 </div> 
	 <div class="row">
	 	<div class="small-24 large-24 medium-24 columns">&nbsp;</div>
	 </div>
<%	
}

%>

