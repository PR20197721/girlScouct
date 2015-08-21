<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
	String path = properties.get("path","");
	if(path.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT) {
%>
**PLEASE ENTER A FILE PATH
<%
	} else if(!path.equals("")) {
		path = path + "/";
%>
<script type="text/javascript">
function openPDF() {
	var program = document.curriculumForm.program.value;
	var grade = document.curriculumForm.grade.value;
	var state = document.curriculumForm.state.value;

	var filePath = encodeURI("<%= path %>" + grade + "_" + program + "/" +
	state + "_" + grade + "_" + program + ".pdf");

	$.ajax({
		type: "POST",
   		url: "<%= currentNode.getPath() + ".html" %>",
   		async: false,
        data: { path: filePath },
	})
		.done(function( msg ) {
			var json = JSON.parse(msg);
	   		//console.log( json );
	   		if(json.key == "found"){
	   			window.open(filePath);
	   		}
	   		else{
	   			window.location.href="/content/gsusa/en/404.html";
	   		}
		})
		.fail(function(msg){
			console.log( msg );
	});
}
</script>

<!-- Form Content -->
<cq:include script="form-content.jsp" />
<%
	}
%>
