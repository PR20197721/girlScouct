<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.text.DateFormat, java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.text.SimpleDateFormat,
                java.util.Map,
                java.util.HashMap" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>

<%!
public String replaceBoothInfo(String input, BoothBasic b, String name) {
	if (b == null) {
		return input;
	} else {
		String output = input.replaceAll("\\{\\{location\\}\\}", b.location)
	             .replaceAll("\\{\\{addr1\\}\\}", b.address1)
	             .replaceAll("\\{\\{addr2\\}\\}", !b.address2.equals("")? " " + b.address2 : "")
	             .replaceAll("\\{\\{city\\}\\}", b.city)
	             .replaceAll("\\{\\{state\\}\\}", b.state)
	             .replaceAll("\\{\\{zip\\}\\}", b.zipCode)
	             .replaceAll("\\{\\{startDate\\}\\}", b.dateStart)
	             .replaceAll("\\{\\{open\\}\\}", b.timeOpen)
	             .replaceAll("\\{\\{close\\}\\}", b.timeClose)
	             .replaceAll("\\{\\{name\\}\\}", name);
		return output;
	}
}
%>

<%
BoothBasic booth = (BoothBasic)request.getAttribute("gsusa-booth-list-item");
String zip = (String)request.getAttribute("gsusa_booth_list_zip");

if (booth != null) {
    DateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
    DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
    String startDate = outputFormat.format(inputFormat.parse(booth.dateStart));
    String uid= (new java.util.Date().getTime()+"_"+ Math.random() ).replace(".","-");
    ObjectMapper mapper = new ObjectMapper();
    String boothBasic_json_data= mapper.writeValueAsString(booth);
    if( boothBasic_json_data!=null && boothBasic_json_data.trim().startsWith("{")){
    	boothBasic_json_data= boothBasic_json_data.replace("{", "{"+ "\"zip\":\"" + zip + "\", ");
    }
    Council council = (Council)request.getAttribute("gsusa_council_info");
    String councilName = "";
    if( council!=null ){
    	boothBasic_json_data= boothBasic_json_data.replace("{", "{"+ "\"councilName\":\"" + council.name + "\", ");
    	councilName = boothBasic_json_data.replace("{", "{"+ "\"councilName\":\"" + council.name + "\", ");
    }
    String fbTitle = replaceBoothInfo(properties.get("mapFBTitle", ""), booth, council.name).replace("\"", "\\\"");
    String fbDesc = replaceBoothInfo(properties.get("mapFBDesc", ""), booth, council.name).replace("\"", "\\\"");
    String tweet = replaceBoothInfo(properties.get("mapTweet", ""), booth, council.name).replace("\"", "\\\"");
    String shareImgPath = properties.get("mapFBImgPath","");
    boothBasic_json_data = boothBasic_json_data.replace("{", "{"+"\"fbTitle\":\"" + fbTitle + "\", ");
    boothBasic_json_data = boothBasic_json_data.replace("{", "{"+"\"fbDesc\":\"" + fbDesc + "\", ");
    boothBasic_json_data = boothBasic_json_data.replace("{", "{"+"\"tweet\":\"" + tweet + "\", ");
    boothBasic_json_data = boothBasic_json_data.replace("{", "{"+"\"shareImgPath\":\"" + shareImgPath + "\", ");
%>
<div class="row details">
    <div class="detail clearfix">
        <section>
            <p><%= booth.location %></p>
            <p><%= booth.address1 %></p>
            <p><%= booth.address2 %></p>
            <p><%= booth.city %>, <%= booth.state %> <%= booth.zipCode %></p>
        </section>
        <section>
            <p><%= startDate %></p>
            <p><%= booth.timeOpen %> - <%= booth.timeClose %></p>
        </section>
        <section>
            <p><%= booth.distance %> Miles</p>
        </section>
    </div>
    <div class="clearfix right">
            <a class='viewMapA<%=uid%> button'>View Details</a>
            <script>
                $('a.viewMapA<%=uid%>').on('click', function() {
                    $('#modal_booth_item_map').foundation('reveal', 'open', {
                        url: '<%= resource.getPath() %>.booth-detail.html',
                        cache:false,
                        data:<%= boothBasic_json_data %>
                    });
                    $(".off-canvas-wrap").addClass('noprint');
                 });
            </script>
    </div>
</div>

<%
}
%>