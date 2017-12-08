<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
String text = properties.get("text", "");
String resultPath = properties.get("resultPage", currentPage.getPath());
String formBgImage = properties.get("formbgimages", currentPage.getPath());
String images = properties.get("images", "");
if (WCMMode.fromRequest(request) == WCMMode.EDIT && (images == "")) {
    %>Camp Landing Hero. Double click here to edit.<%
} else {
%>
    <div class="camp-landing-hero hide-for-small ">
      <div class="bg-image">
        <img src="<%=images %>" alt="" />
      </div>
      <div class="camp-header" style="background: url('<%=formBgImage%>') no-repeat 0 0 transparent;">
        <div class="wrapper clearfix">
          <div class="wrapper-inner clearfix">
            <form class="find-camp clearfix" name="find-camp">

              <label for="zip-code"><%= text %></label>
              <div class="form-wrapper clearfix">
                <input type="text" maxlength="5" placeholder="ZIP Code" title="5 number zip code" class="zip-code" name="zip-code">
                <input type="submit" class="link-arrow" value="Go >"/>
                <!-- <span>Please enter a valid zip code</span> -->
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <script>
      //variable to be passed to app.js function
      var loc = "<%= resourceResolver.map(resultPath) %>.html";
    </script>
<%
    }
%>

