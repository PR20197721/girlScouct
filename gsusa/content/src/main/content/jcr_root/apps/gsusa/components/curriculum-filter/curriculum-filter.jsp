<%@include file="/libs/foundation/global.jsp"%>

<%
	String path = properties.get("path","");
	if(path.equals("") && WCMMode.fromRequest(request) == WCMMode.EDIT){
		%>
**PLEASE ENTER A FILE PATH
		<%
	} else {
	
	}
%>