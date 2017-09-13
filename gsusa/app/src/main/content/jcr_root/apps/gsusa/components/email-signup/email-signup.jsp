<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/gsusa/components/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode, com.day.cq.wcm.foundation.Placeholder, java.util.Random"  %>
<%@page session="false" %>

<%!
public String generateId() {
	Random rand=new Random();
	String possibleLetters = "abcdefghijklmnopqrstuvwxyz";
	StringBuilder sb = new StringBuilder(6);
	for(int i = 0; i < 4; i++)
	    sb.append(possibleLetters.charAt(rand.nextInt(possibleLetters.length())));
	return sb.toString();
}
%>

<%if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	%><cq:includeClientLib categories="apps.gsusa.authoring" /><%
}

String url = properties.get("url", "");
String councilID = properties.get("cid", "");
String dataExtensionName = properties.get("dataextensionname", "");
String dataExtensionKey = properties.get("dataextensionkey", "");

String mainText = properties.get("heading", "New article alerts!"); 
String subText = properties.get("description", "Get updates when new content is available."); 
String source = properties.get("source", "");
String fieldText = properties.get("emailplaceholder", "Enter your email address here");
String fieldTextMobile = properties.get("emailplaceholdermobile", "Enter your email!"); 
String submitButtonText = properties.get("submitbuttontext", "SUBMIT");

String mainTextError = properties.get("errorheading", "Uh-oh!"); 
String subTextError = properties.get("errordescription", "That address is not valid. Please try again!"); 

String mainTextSuccess = properties.get("successheading", "Yay! You're set!"); 
String subTextSuccess = properties.get("successdescription", "Be on the lookout for your confirmation email."); 
		
String alertEmailExists = properties.get("alertemailexists", "You are already signed up!");	

String formID = "email_" + generateId();	

boolean topBorder = properties.get("topborder", false);
boolean bottomBorder = properties.get("bottomborder", false);
		
Resource iconImage = resource.getChild("iconimage");
String iconPath = "";
if(iconImage != null) {
	iconPath = ((ValueMap)iconImage.adaptTo(ValueMap.class)).get("fileReference", "");
}
Resource thankyouImage = resource.getChild("thankyouimage");
String thankyouPath = "";
if (thankyouImage != null) {
	thankyouPath = ((ValueMap)thankyouImage.adaptTo(ValueMap.class)).get("fileReference", "");
}




%>

<script>

$(document).ready(function(){
	
	var url = '<%= url %>';
	var cid = '<%= councilID %>';
	var dename = '<%= dataExtensionName %>';
	var dekey = '<%= dataExtensionKey %>';
	var fieldTextMobile = '<%= fieldTextMobile %>';
	var source = '<%= source %>';
	var formID = '#<%= formID %>';
	
	$(formID).find('input[name="email"]').keyup(function(event) {
		var email = $(this).val();
		
		if (validateEmail(email)) {
			toDefault(formID);
		}
	});
	
	$(formID).submit(function(event){
		event.preventDefault();
		
	    var email = $(this).find('input[name="email"]').val();
	    
	    if ($.trim(email).length == 0) {
		    error(this,"Please enter an email address!");
		} else if ($.trim(email).length >= 100) {
			error(this,"Maximum 100 chars!");
		} else if (!validateEmail(email)) {
			error(this,"Please enter a valid email address!");
		} else {
			if (url.length == 0)
				alert("Error: API Endpoint is missing!");
			else if (cid.length == 0)
				alert("Error: Council ID is missing!");
			else if (dename.length == 0)
				alert("Error: DataExtension Name is missing!");
			else if (dekey.length == 0)
				alert("Error: DataExtension Key is missing!");
			else
				post(this, url, email, cid, dename, dekey, source);
		}
		
	});

	// Replace email placeholder for narrow mobile screen
	if ($(window).width() <= 415)
	{
		$(formID).find('input[name="email"]').attr('placeholder', fieldTextMobile);
	}
});

function success(form) {
	$(form).find('.mainText, .subText').hide();
	$(form).find('.error').hide();
	$(form).find('input[name="email"]').hide();
	$(form).find('input[type="submit"]').hide();
	$(form).find('.success').show();
	$(form).find('.success').css("display", "inline");
}

function error(form, message) {
	$(form).find('.mainText, .subText').hide();
	$(form).find('.error').show();
	$(form).find('.error').css("display", "inline");
	$(form).find('input[name="email"]').css("border-color", "red");
	$(form).find('input[name="email"]').css("color", "red");
}

function toDefault(form) {
	$(form).find('.mainText, .subText').show();
	$(form).find('.error').hide();
	$(form).find('.success').hide();
	$(form).find('input[name="email"]').css("border-color", "#cccccc");
	$(form).find('input[name="email"]').css("color", "black");
}

function post(form, url, email, cid, dename, dekey, source) {
	//var url = 'http://localhost:4502/campsapi/ajax_camp_result.asp';

	var opts = {
			  lines: 11 // The number of lines to draw
			, length: 11 // The length of each line
			, width: 7 // The line thickness
			, radius: 23 // The radius of the inner circle
			, scale: 0.5 // Scales overall size of the spinner
			, corners: 1 // Corner roundness (0..1)
			, color: '#bbb' // #rgb or #rrggbb or array of colors
			, opacity: 0.25 // Opacity of the lines
			, rotate: 0 // The rotation offset
			, direction: 1 // 1: clockwise, -1: counterclockwise
			, speed: 0.7 // Rounds per second
			, trail: 60 // Afterglow percentage
			, fps: 20 // Frames per second when using setTimeout() as a fallback for CSS
			, zIndex: 2e9 // The z-index (defaults to 2000000000)
			, className: 'spinner' // The CSS class to assign to the spinner
			, top: '50%' // Top position relative to parent
			, left: '50%' // Left position relative to parent
			, shadow: false // Whether to render a shadow
			, hwaccel: false // Whether to use hardware acceleration
			, position: 'absolute' // Element positioning
			};
	var spinner = new Spinner(opts).spin(form);

	
	$.ajax({
		type: 'POST',
		url: url,
		data: {
			email: email,
			cid: cid, 
			deName: dename,
			deKey: dekey,
			source: source
			},
		dataType: 'json', 
		success: function(data) {
			process(form, data);
			}, 
		error: function(data) {
			alert("Error: " + data.status + " " + data.statusText);
			}, 
		complete: function(data) {
			spinner.stop();		
			}
	});
}

function process(form, data) {
	/* Our own status code
    SUCC    Email was successfully added to SFMC data extension
    NAIP    IP address not found in the list of authorized IP addresses
    MVCO    Council is missing
    MVEM    Email is missing
    MVDN    DEName is missing
    MVDK    DEKey is missing
    CNFD    Could not find council from dictionary of APP ID/KEY
    MISC    Error non specified from SFMC
    EAEX    Email Already EXists   
    UNEX    Unexpected Exception
	*/
	
	if (data.status == 'SUCC') {
		success(form);
	} else if (data.status == 'NAIP') {
		alert("IP address not found in the list of authorized IP addresses");
	} else if (data.status == 'MVCO') {
		alert("Council is missing");
	} else if (data.status == 'MVEM') {
		alert("Email is missing");
	} else if (data.status == 'MVDN') {
		alert("DEName is missing");
	} else if (data.status == 'MVDK') {
		alert("DEKey is missing");
	} else if (data.status == 'CNFD') {
		alert("Could not find council from dictionary of APP ID/KEY");
	} else if (data.status == 'EAEX') {
		alert('<%= alertEmailExists %>');
	} else if (data.status == 'MISC') {
		alert("SFMC Error");
	} else if (data.status == 'UNEX') {
		alert(data.message);
	} else {
		alert(data.message);
	}	
}
	
function validateEmail(email) {
	  var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	  return regex.test(email);
}
</script>

<script>
//http://spin.js.org/#v2.3.2
!function(a,b){"object"==typeof module&&module.exports?module.exports=b():"function"==typeof define&&define.amd?define(b):a.Spinner=b()}(this,function(){"use strict";function a(a,b){var c,d=document.createElement(a||"div");for(c in b)d[c]=b[c];return d}function b(a){for(var b=1,c=arguments.length;c>b;b++)a.appendChild(arguments[b]);return a}function c(a,b,c,d){var e=["opacity",b,~~(100*a),c,d].join("-"),f=.01+c/d*100,g=Math.max(1-(1-a)/b*(100-f),a),h=j.substring(0,j.indexOf("Animation")).toLowerCase(),i=h&&"-"+h+"-"||"";return m[e]||(k.insertRule("@"+i+"keyframes "+e+"{0%{opacity:"+g+"}"+f+"%{opacity:"+a+"}"+(f+.01)+"%{opacity:1}"+(f+b)%100+"%{opacity:"+a+"}100%{opacity:"+g+"}}",k.cssRules.length),m[e]=1),e}function d(a,b){var c,d,e=a.style;if(b=b.charAt(0).toUpperCase()+b.slice(1),void 0!==e[b])return b;for(d=0;d<l.length;d++)if(c=l[d]+b,void 0!==e[c])return c}function e(a,b){for(var c in b)a.style[d(a,c)||c]=b[c];return a}function f(a){for(var b=1;b<arguments.length;b++){var c=arguments[b];for(var d in c)void 0===a[d]&&(a[d]=c[d])}return a}function g(a,b){return"string"==typeof a?a:a[b%a.length]}function h(a){this.opts=f(a||{},h.defaults,n)}function i(){function c(b,c){return a("<"+b+' xmlns="urn:schemas-microsoft.com:vml" class="spin-vml">',c)}k.addRule(".spin-vml","behavior:url(#default#VML)"),h.prototype.lines=function(a,d){function f(){return e(c("group",{coordsize:k+" "+k,coordorigin:-j+" "+-j}),{width:k,height:k})}function h(a,h,i){b(m,b(e(f(),{rotation:360/d.lines*a+"deg",left:~~h}),b(e(c("roundrect",{arcsize:d.corners}),{width:j,height:d.scale*d.width,left:d.scale*d.radius,top:-d.scale*d.width>>1,filter:i}),c("fill",{color:g(d.color,a),opacity:d.opacity}),c("stroke",{opacity:0}))))}var i,j=d.scale*(d.length+d.width),k=2*d.scale*j,l=-(d.width+d.length)*d.scale*2+"px",m=e(f(),{position:"absolute",top:l,left:l});if(d.shadow)for(i=1;i<=d.lines;i++)h(i,-2,"progid:DXImageTransform.Microsoft.Blur(pixelradius=2,makeshadow=1,shadowopacity=.3)");for(i=1;i<=d.lines;i++)h(i);return b(a,m)},h.prototype.opacity=function(a,b,c,d){var e=a.firstChild;d=d.shadow&&d.lines||0,e&&b+d<e.childNodes.length&&(e=e.childNodes[b+d],e=e&&e.firstChild,e=e&&e.firstChild,e&&(e.opacity=c))}}var j,k,l=["webkit","Moz","ms","O"],m={},n={lines:12,length:7,width:5,radius:10,scale:1,corners:1,color:"#000",opacity:.25,rotate:0,direction:1,speed:1,trail:100,fps:20,zIndex:2e9,className:"spinner",top:"50%",left:"50%",shadow:!1,hwaccel:!1,position:"absolute"};if(h.defaults={},f(h.prototype,{spin:function(b){this.stop();var c=this,d=c.opts,f=c.el=a(null,{className:d.className});if(e(f,{position:d.position,width:0,zIndex:d.zIndex,left:d.left,top:d.top}),b&&b.insertBefore(f,b.firstChild||null),f.setAttribute("role","progressbar"),c.lines(f,c.opts),!j){var g,h=0,i=(d.lines-1)*(1-d.direction)/2,k=d.fps,l=k/d.speed,m=(1-d.opacity)/(l*d.trail/100),n=l/d.lines;!function o(){h++;for(var a=0;a<d.lines;a++)g=Math.max(1-(h+(d.lines-a)*n)%l*m,d.opacity),c.opacity(f,a*d.direction+i,g,d);c.timeout=c.el&&setTimeout(o,~~(1e3/k))}()}return c},stop:function(){var a=this.el;return a&&(clearTimeout(this.timeout),a.parentNode&&a.parentNode.removeChild(a),this.el=void 0),this},lines:function(d,f){function h(b,c){return e(a(),{position:"absolute",width:f.scale*(f.length+f.width)+"px",height:f.scale*f.width+"px",background:b,boxShadow:c,transformOrigin:"left",transform:"rotate("+~~(360/f.lines*k+f.rotate)+"deg) translate("+f.scale*f.radius+"px,0)",borderRadius:(f.corners*f.scale*f.width>>1)+"px"})}for(var i,k=0,l=(f.lines-1)*(1-f.direction)/2;k<f.lines;k++)i=e(a(),{position:"absolute",top:1+~(f.scale*f.width/2)+"px",transform:f.hwaccel?"translate3d(0,0,0)":"",opacity:f.opacity,animation:j&&c(f.opacity,f.trail,l+k*f.direction,f.lines)+" "+1/f.speed+"s linear infinite"}),f.shadow&&b(i,e(h("#000","0 0 4px #000"),{top:"2px"})),b(d,b(i,h(g(f.color,k),"0 0 1px rgba(0,0,0,.1)")));return d},opacity:function(a,b,c){b<a.childNodes.length&&(a.childNodes[b].style.opacity=c)}}),"undefined"!=typeof document){k=function(){var c=a("style",{type:"text/css"});return b(document.getElementsByTagName("head")[0],c),c.sheet||c.styleSheet}();var o=e(a("group"),{behavior:"url(#default#VML)"});!d(o,"transform")&&o.adj?i():j=d(o,"animation")}return h});
</script>

<%
	if (topBorder) { %>
		<hr style="border-top: solid 1px #dddddd">
<% } %>


<%
  if (currentPage.getPath().equals(currentPage.getAbsoluteParent(2).getPath())){
%>
	<!-- HOMEPAGE VERSION -->
	<div class="row homepage">
<%
  } else {
%>
	<!-- NON-HOMEPAGE VERSION -->
	<div class="row">
<%
  }
%>
	  <div class="wrapper clearfix">
	    <div class="wrapper-inner clearfix">
	      <form class="email-form" name="submit-email" id="<%= formID %>">
	        <div class="left-image">
	          <img src="<%= iconPath %>">
	        </div>
	        <div class="right-form">
	          <div class="text">
	            <div class="mainText" for="email"><%= mainText %></div>
	            <div class="subText" for="email"><%= subText %></div>
	            <div class="mainText error" for="email"><%= mainTextError %></div>
	            <div class="subText error" for="email"><%= subTextError %></div>
	            <div class="mainText success" for="email"><%= mainTextSuccess %></div>
	            <div class="subText success" for="email"><%= subTextSuccess %></div>
	          </div>            
	          <div class="form-wrapper clearfix">
	            <input type="text" placeholder="<%= fieldText %>" maxlength="100" title="email address" class="email" name="email">
	            <input type="submit" class="submit-button" value="<%= submitButtonText %>"/>
	            <img class="success" src="<%= thankyouPath %>"> 
	          </div>	          
	        </div>            
	      </form>
	    </div>
	  </div>
	</div>

<%
	if (bottomBorder) { %>
		<hr style="border-top: solid 1px #dddddd">
<% } %>



