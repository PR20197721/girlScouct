<%@include file="/libs/foundation/global.jsp" %>
<%@page import="com.day.cq.wcm.api.WCMMode" %>

<% 
String path = properties.get("path","");
String imgPath = properties.get("imgPath","/content/dam/girlscouts-gsusa/images/misc/council-map.gif");

if((path.equals("") || imgPath.equals("")) && WCMMode.fromRequest(request) == WCMMode.EDIT){
%>
<p>Please select the results page</p>
<%
}else if(!path.equals("") && !imgPath.equals("")){
%>
	<img id="council-map-img" src="<%= imgPath %>" alt="United States map" height="320" style="width:494px" border="0" usemap="#Map" /><map id="council-map" name="Map">
    <area shape="rect" coords="16,232,80,245" href="<%= path %>.html?state=AK" alt="Alaska">
    <area shape="rect" coords="16,255,80,267" href="<%= path %>.html?state=HI" alt="Hawaii">
    <area shape="rect" coords="16,274,90,286" href="<%= path %>.html?state=PR" alt="Puerto Rico">
	
    <area shape="poly" coords="63,18,102,29,95,62,56,55,47,45,47,23,62,31" href="<%= path %>.html?state=WA" alt="Washington">
    <area shape="poly" coords="45,49,54,57,92,65,85,103,31,87" href="<%= path %>.html?state=OR" alt="Oregon">   
    <area shape="poly" coords="35,89,63,99,57,131,92,186,84,206,65,204,40,174,25,104" href="<%= path %>.html?state=CA" alt="California">
    <area shape="poly" coords="104,30,109,31,115,61,123,84,137,85,132,112,87,104" href="<%= path %>.html?state=ID" alt="Idaho">
    <area shape="poly" coords="64,99,108,110,97,171,92,167,91,181,58,131" href="<%= path %>.html?state=NV" alt="Nevada">
    <area shape="poly" coords="111,31,195,44,191,86,139,80,138,85,125,83,116,63" href="<%= path %>.html?state=MT" alt="Montana">
    <area shape="poly" coords="139,81,190,89,186,129,131,122" href="<%= path %>.html?state=WY" alt="Wyoming">
    <area shape="poly" coords="109,110,131,113,130,122,147,126,141,167,100,160" href="<%= path %>.html?state=UT" alt="Utah">
    <area shape="poly" coords="101,161,141,169,131,230,117,227,85,208,92,184,93,171,96,174" href="<%= path %>.html?state=AZ" alt="Arizona">
    <area shape="poly" coords="148,126,203,131,201,175,142,169" href="<%= path %>.html?state=CO" alt="Colorado">
    <area shape="poly" coords="142,171,191,176,188,226,158,224,134,229" href="<%= path %>.html?state=NM" alt="New Mexico">
    <area shape="poly" coords="196,44,243,46,248,78,193,76" href="<%= path %>.html?state=ND" alt="North Dakota">
    <area shape="poly" coords="193,78,247,79,248,112,227,108,190,107" href="<%= path %>.html?state=SD" alt="South Dakota">
    <area shape="poly" coords="189,108,224,109,247,114,252,124,253,134,257,142,204,141,205,130,187,128" href="<%= path %>.html?state=NE" alt="Nebraska">
    <area shape="poly" coords="205,143,256,144,264,152,264,175,204,176" href="<%= path %>.html?state=KS" alt="Kansas">
    <area shape="poly" coords="193,177,261,177,267,192,265,211,246,209,217,201,218,181,192,180" href="<%= path %>.html?state=OK" alt="Oklahoma">
    <area shape="poly" coords="192,182,216,182,215,202,232,207,267,214,268,229,275,239,271,254,246,270,236,284,242,296,223,289,202,253,193,249,185,260,171,251,171,241,159,227,188,228" href="<%= path %>.html?state=TX" alt="Texas">
    <area shape="poly" coords="245,46,259,42,268,51,295,55,280,68,276,80,277,90,288,100,288,104,249,104" href="<%= path %>.html?state=MN" alt="Minnesota">
    <area shape="poly" coords="250,105,288,105,291,113,298,118,292,127,290,135,255,135,254,124,248,115" href="<%= path %>.html?state=IA" alt="Iowa">
    <area shape="poly" coords="255,136,289,136,300,154,298,160,310,173,305,186,301,180,264,180,266,151,256,143" href="<%= path %>.html?state=MO" alt="Missouri">
    <area shape="poly" coords="267,181,297,181,305,187,295,207,298,219,268,218" href="<%= path %>.html?state=AR" alt="Arkansas">
    <area shape="poly" coords="269,220,296,219,297,227,291,240,310,239,313,247,317,259,299,259,272,254,277,239,269,229" href="<%= path %>.html?state=LA" alt="Louisiana">
    <area shape="poly" coords="279,70,290,68,311,78,317,88,314,104,317,113,298,116,290,106,289,99,279,89,277,81" href="<%= path %>.html?state=WI" alt="Wisconsin">
    <area shape="poly" coords="296,69,310,60,316,69,333,66,339,72,321,76,317,83" href="<%= path %>.html?state=MI" alt="Michigan">
    <area shape="poly" coords="337,75,346,78,357,103,351,118,326,120,329,110,324,91" href="<%= path %>.html?state=MI" alt="Michigan">
    <area shape="poly" coords="299,117,313,114,320,120,322,154,319,162,313,173,300,160,303,153,289,136" href="<%= path %>.html?state=IL" alt="Illinois">
    <area shape="poly" coords="323,120,341,120,343,147,332,162,321,163,323,154" href="<%= path %>.html?state=IN" alt="Indiana">
    <area shape="poly" coords="318,164,332,162,345,147,363,149,367,161,356,173,309,180" href="<%= path %>.html?state=KY" alt="Kentucky">
    <area shape="poly" coords="309,180,371,171,355,191,302,197" href="<%= path %>.html?state=TN" alt="Tennessee">
    <area shape="poly" coords="301,199,320,197,319,236,325,244,315,246,312,239,293,239,300,222,295,208" href="<%= path %>.html?state=MS" alt="Mississippi">
    <area shape="poly" coords="322,196,342,193,350,219,353,235,328,236,332,242,328,245,320,236" href="<%= path %>.html?state=AL" alt="Alabama">
    <area shape="poly" coords="344,121,352,117,359,120,371,112,376,127,375,137,364,149,345,146" href="<%= path %>.html?state=OH" alt="Ohio">
    <area shape="poly" coords="377,127,382,136,387,134,391,139,399,133,403,137,387,149,385,161,369,165,364,148,375,138" href="<%= path %>.html?state=WV" alt="West Virginia">
    <area shape="poly" coords="402,139,413,145,422,162,359,173,366,163,373,166,386,163,387,150" href="<%= path %>.html?state=VA" alt="Virginia">
    <area shape="poly" coords="371,172,422,163,429,177,407,196,394,188,372,187,358,191" href="<%= path %>.html?state=NC" alt="North Carolina">
    <area shape="poly" coords="363,191,393,188,405,196,388,216" href="<%= path %>.html?state=SC" alt="South Carolina">
    <area shape="poly" coords="345,194,362,192,387,218,386,235,354,236" href="<%= path %>.html?state=GA" alt="Georgia">
    <area shape="poly" coords="332,237,385,237,397,256,408,275,407,292,395,299,395,284,382,275,378,258,365,244,354,249,334,244" href="<%= path %>.html?state=FL" alt="Florida">
    <area shape="poly" coords="447,34,455,32,461,48,470,57,459,64,452,74,448,84,438,62,443,43" href="<%= path %>.html?state=ME" alt="Maine">
    <area shape="poly" coords="437,63,447,83,444,87,434,88" href="<%= path %>.html?state=NH" alt="New Hampshire">
    <area shape="poly" coords="422,67,437,63,434,86,429,90" href="<%= path %>.html?state=VT" alt="Vermont">
    <area shape="poly" coords="383,95,396,94,405,87,404,80,411,70,420,69,429,91,428,111,420,110,412,104,378,110" href="<%= path %>.html?state=NY" alt="New York">
    <area shape="poly" coords="372,111,412,106,420,111,419,119,422,122,417,127,398,131,390,137,389,134,380,133" href="<%= path %>.html?state=PA" alt="Pennsylvania">
    <area shape="poly" coords="431,90,436,89,446,87,456,94,458,99,449,100,444,92,429,97" href="<%= path %>.html?state=MA" alt="Massachusetts">
    <area shape="poly" coords="442,93,450,101,445,104,442,95" href="<%= path %>.html?state=RI" alt="Rhode Island">
    <area shape="poly" coords="429,100,439,95,442,103,430,109" href="<%= path %>.html?state=CT" alt="Connecticut">
    <area shape="poly" coords="429,111,445,104,430,117" href="<%= path %>.html?state=NY" alt="New York">
    <area shape="poly" coords="420,111,427,112,430,119,426,134,418,128,422,120" href="<%= path %>.html?state=NJ" alt="New Jersey">
    <area shape="poly" coords="417,129,426,139,420,140" href="<%= path %>.html?state=DE" alt="Delaware">
    <area shape="poly" coords="400,132,415,128,418,142,425,140,426,147,420,154,412,142" href="<%= path %>.html?state=MD" alt="Maryland">
	</map>

<%
}
%>