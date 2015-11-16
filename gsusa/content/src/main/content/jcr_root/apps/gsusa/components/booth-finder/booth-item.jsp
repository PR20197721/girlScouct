<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.text.DateFormat, java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,
                org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.Council,
                java.text.SimpleDateFormat" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
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
    if( council!=null ){
    	boothBasic_json_data= boothBasic_json_data.replace("{", "{"+ "\"councilName\":\"" + council.name + "\", ");
    }
%>
<div class="row details">
    <div class="detail clearfix">
        <section>
            <p><%= booth.location %></p>
            <p><%= booth.address1 %></p>
            <p><%= booth.address2 %></p>
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
            <a class='viewMapA<%=uid%> button'>View Map</a>
            <script>
                $('a.viewMapA<%=uid%>').on('click', function() {
                    $('#modal_booth_item_map').foundation('reveal', 'open', {
                        url: '<%= resource.getPath() %>.booth-detail.html',
                        cache:false,
                        data:<%= boothBasic_json_data %>
                    });
                 });
            </script>
    </div>
</div>

<%
}
%>