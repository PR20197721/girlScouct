<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<%
	String path = properties.get("path","");
	if(path.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>
**PLEASE ENTER A FILE PATH
<%
	} else if(!path.equals("")) {
		path = path + "/";
		try{
			resourceResolver.resolve(path);
		} catch(Exception e){
			//TODO: dynamically get the domain
			%>
			<script>
			window.open("/content/gsusa/en/404.html");
			</script>
			<%
		}
%>
<script>
function openPDF()
{
     var program = document.curriculumForm.program.value;
	 var grade = document.curriculumForm.grade.value;
	 var state = document.curriculumForm.state.value;
	
	 var filePath = escape("<%= path %>" + grade + "_" + program + "/" + 
	              state + "_" + grade + "_" + program + ".pdf");
				  
	 console.log(filePath);
	 window.open(filePath,"PDF");
}
</script>
	
<!-- Form Content -->
<cq:include script="form-content.jsp" />
<%
		
	}
%>