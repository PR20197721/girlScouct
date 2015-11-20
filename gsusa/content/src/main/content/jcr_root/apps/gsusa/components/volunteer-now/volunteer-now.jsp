<%@include file="/libs/foundation/global.jsp" %>

<%
    String callToActionName = properties.get("callToActionName", "Volunteer Now");
    String searchBtnName = properties.get("searchBtnName", "Go");
    String title = properties.get("title", "Find Your Local Council");
    String mainTitle = properties.get("mainTitle", "");
    String text = properties.get("text", "");
    String source = properties.get("source", "not_set");
    int maxWidth = properties.get("maxWidth", 210);

    String bg = "";
	try {
		bg = ((ValueMap)resource.getChild("bg").adaptTo(ValueMap.class)).get("fileReference", "");
	} catch (Exception e) {}
	if (!bg.equals("")) { %>
		<div class="standalone-volunteer join-volunteer-block bg-image" style="max-width:<%= maxWidth + "px"%>;">
		<!-- 	<div class="bg-image"> -->
				<% slingRequest.setAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE, true); %>
				<cq:include path="bg" resourceType="gsusa/components/image"/>
			<!-- </div> -->
			<% slingRequest.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE); %>
		    <div class="wrapper">
		        <a href="#" title="Volunteer" class="vol button arrow"><%= callToActionName %></a>
		        <form class="formVol hide">
		            <label><%= title %></label>
		            <input type="text" name="ZipVolunteer" maxlength="5" title="5 numbers zip code" pattern="[0-9]*" placeholder="Enter ZIP Code">
		            <input type="hidden" name="source" value="<%= source %>">
		        	<input class="button" type="submit" value="<%= searchBtnName %>">
		        </form>
		    </div>
		</div> <%
	} else if (!text.equals("")) { %>
		<div class="standalone-volunteer join-volunteer-block text-version">
		   <h5><%= mainTitle %></h5>
		   <p><%= text %></p>
		   <div class="wrapper">
		       <a href="#" title="Volunteer" class="vol button arrow"><%= callToActionName %></a>
		       <form class="formVol hide">
		           <label><%= title %></label>
		           <input type="text" name="ZipVolunteer" title="5 numbers zip code" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		           <input type="hidden" name="source" value="<%= source %>">
		       	<input class="button" type="submit" value="<%= searchBtnName %>">
		       </form>
		   </div>
		</div>
	<% } else { //bg is null, doing this mainly for css %>
		<div class="standalone-volunteer form-no-image  join-volunteer-block">
		    <a href="#" title="Volunteer Now" class="vol button arrow"><%= callToActionName %></a>
		    <form class="formVol hide">
		        <label><%= title %></label>
		        <input type="text" name="ZipVolunteer" title="5 numbers zip code" maxlength="5" pattern="[0-9]*" placeholder="Enter ZIP Code">
		        <input type="hidden" name="source" value="<%= source %>">
		        <input class="button" type="submit" value="<%= searchBtnName %>">
		    </form>
		</div><%
	}

%>