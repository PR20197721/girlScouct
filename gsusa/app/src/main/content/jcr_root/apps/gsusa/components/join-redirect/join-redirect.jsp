<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String type = properties.get("type", "join");
String formName = type.equals("join") ? "formJoin" : "formVol";
String formZipName = type.equals("join") ? "ZipJoin" : "ZipVolunteer";
String source = properties.get("source", "");
String text = properties.get("text", "JOIN!");
String textColor = properties.get("textcolor", "#FFFFFF");
String[] images = properties.get("images", String[].class);


Resource logo = resource.getChild("logo");
String logoPath = "";
if (logo != null) {
	logoPath = ((ValueMap)logo.adaptTo(ValueMap.class)).get("fileReference", "");
}

// Mobile
String textMobile = properties.get("textmobile", "JOIN!");
String backgroundColor = properties.get("backgroundcolor", "FFFFFF");

Resource logoMobile = resource.getChild("logomobile");
String logoMobilePath = "";
if (logoMobile != null) {
	logoMobilePath = ((ValueMap)logoMobile.adaptTo(ValueMap.class)).get("fileReference", "");
}

Resource bannerMobile = resource.getChild("bannermobile");
String bannerMobilePath = "";
if (bannerMobile != null) {
	bannerMobilePath = ((ValueMap)bannerMobile.adaptTo(ValueMap.class)).get("fileReference", "");
}


String autoplayspeed = properties.get("autoplayspeed", "");		//slider speed
String speed = properties.get("speed", "");						//slider transition speed

if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == null || images.length == 0)) {
    %>GIRL Join Redirect Component. Double click here to edit.<%
} else {
%>
    <div class="join-redirect-hero hide-for-small">
      <div class="join-redirect-slider">
<%	    for (String image : images) {
			int lastDotPos = image.lastIndexOf(".");
			String img2x = image.substring(0, lastDotPos) + "@2x" + image.substring(lastDotPos);
%>
      		<div><img src="<%= image %>" data-at2x="<%= img2x %>" alt="" /></div>
<%    } %>
      </div>
      <div class="join-redirect-header">
        <div class="wrapper">
          <div class="wrapper-inner clearfix">
            <form class="<%= formName %>" name="<%= formName %>">
              <img src="<%= logoPath %>" />
              <label for="zip-code" style="color:<%= textColor %>"><%= text %></label>
              <div class="form-wrapper">       
                <input type="text" placeholder="ZIP Code" maxlength="5" pattern="[0-9]{5}" title="5 number zip code" class="zip-code" name="<%= formZipName %>">
                <input type="hidden" name="source" value="<%= source %>">
                <input type="submit" class="link-arrow" value="Go >"/>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    
    
    <div class="join-redirect-hero show-for-small">
      <div class="join-redirect-header">
        <div class="wrapper" style="background-color:<%= backgroundColor %>">
          <div class="wrapper-inner clearfix">
          <form class="<%= formName %>" name="<%= formName %>">
              <img src="<%= logoMobilePath %>" />
              <label for="zip-code"><%= textMobile %></label>
              <div class="form-wrapper">       
                <input type="text" placeholder="ZIP Code" maxlength="5" pattern="[0-9]{5}" title="5 number zip code" class="zip-code" name="<%= formZipName %>">
                <input type="hidden" name="source" value="<%= source %>">
                <input type="submit" class="link-arrow" value="Go >"/>
              </div>
            </form>
          </div>
        </div>
      </div>
      <div class="join-redirect-banner">
      	<img src="<%= bannerMobilePath %>" />
      </div>
    </div>
<script>

joinRedirectSpeed = <%= speed %>;
joinRedirectAutoplaySpeed = <%= autoplayspeed %>;

</script>
    
    
    
<%
    }
%>