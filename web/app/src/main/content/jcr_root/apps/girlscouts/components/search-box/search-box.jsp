<%@include file="/libs/foundation/global.jsp" %>
<%@include file="/apps/girlscouts/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%
String placeholderText = properties.get("placeholder-text","");
String searchAction = properties.get("searchAction", null);
String action = (String)request.getAttribute("altSearchPath");
if ((null==searchAction) && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
	Please edit Search Box Component
<%
} else if(searchAction==null){
	%> Search Not Configured <%
} else {

	if( !(action!=null && !action.trim().equals("")) )
	    	action = currentSite.get(searchAction,String.class);	
%>
    <form onsubmit="submitForm()" action="<%=action%>.html" method="get" path="<%=currentNode.getPath()%>">
		<input type="text" name="q" placeholder="<%=placeholderText %>" class="searchField"/>
	</form>
<%}%>
<script>
    function submitForm(){
        var search = $($(".event-search-facets").find("form").val();
        var path =  $($(".event-search-facets").find("form").attr("path");
        $.ajax({
            url: '/content/girlscouts-vtk/service/email.html',
            type: 'POST',
            data: {
                searchKeywords: search,
                searchPath: path
            },
            processData:false,
            contentType: false,
            success: function(result) {
               console.log("Search placeholder updated successfully");
            },
            error: function(result){
                console.log("Search placeholder updated failed");
            }
        });
    }
</script>