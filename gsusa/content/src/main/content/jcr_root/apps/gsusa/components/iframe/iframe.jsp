<%@include file="/libs/foundation/global.jsp"%>

<%
	String src = properties.get("src","");
	String width = properties.get("width", "");
	String height = properties.get("height", "");
	String scrolling = properties.get("scrolling","auto");
	boolean border = properties.get("frame-border",false);
	
	String outerCss = properties.get("outer-css","");
	String outerStyleStr = "";
	String innerCss = properties.get("inner-css","");
	String innerStyleStr = "";
	String outerClass = properties.get("outer-class","");
	String outerClassStr = "";
	String innerClass =properties.get("inner-class","");
	String innerClassStr = "";
	
	boolean outerStyle = false;
	
	if(!width.equals("")){
		outerStyle = true;
		if(!width.contains("px") && !width.contains("%")){
			width = "width: " + width + "px;";
		}
		else{
			width = "width: " + width + ";";
		}
	}
	if(!height.equals("")){
		outerStyle = true;
		if(!height.contains("px") && !height.contains("%")){
			height = "height: " + height + "px; ";
		}
		else{
			height = "height: " + height + "; ";
		}
	}
	
	if(!outerCss.equals("")){
		outerStyle = true;
	}
	
	if(outerStyle){
		outerStyleStr = "style = \"" + outerCss + " " + width + " " + height + "\"";
	}
	
	if(!innerCss.equals("")){
		innerStyleStr = "style = \"" + innerCss + "\"";
	}
	
	if(!outerClass.equals("")){
		outerClassStr = "class = \"" + outerClass + "\"";
	}
	
	if(!innerClass.equals("")){
		innerClassStr = "class = \"" + innerClass + "\"";
	}
	
	int frameBorder = border ? 1:0;
	
	if(src.equals("")){		
%>
Please enter a URL for the iframe	
<%
	} else {
		String divTag = "<div id=\"outerFrame\" " + outerClassStr + " " + outerStyleStr + ">";
		String iframeTag = "<iframe src=\"" + src + "\" id=\"innerFrame\" " + innerClassStr + "scrolling=\"" + scrolling + "\" frameBorder=\"" + frameBorder + "\" " + innerStyleStr + "></iframe>";
		String divClose = "</div>";
%>

<%= divTag %>
<%= iframeTag %>
<%= divClose %>

<% } %>