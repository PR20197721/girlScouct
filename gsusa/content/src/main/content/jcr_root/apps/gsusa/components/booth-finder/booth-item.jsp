<%@page import="org.girlscouts.web.gsusa.component.boothfinder.BoothFinder.BoothBasic,
                java.text.DateFormat, java.util.Comparator,org.codehaus.jackson.map.ObjectMapper,
                java.text.SimpleDateFormat" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page session="false" %>
<%
BoothBasic booth = (BoothBasic)request.getAttribute("gsusa-booth-list-item");
if (booth != null) {
    DateFormat inputFormat = new SimpleDateFormat("M/d/yyyy");
    DateFormat outputFormat = new SimpleDateFormat("EEEE, MMMM d");
    String startDate = outputFormat.format(inputFormat.parse(booth.dateStart));
    String uid= (new java.util.Date().getTime()+"_"+ Math.random() ).replace(".","-");
    ObjectMapper mapper = new ObjectMapper();
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
            <a class='viewMapA<%=uid%>'>View Map</a>
            <script>
                $('a.viewMapA<%=uid%>').on('click', function() {
                    $('#modal_booth_item_map').foundation('reveal', 'open', {
                        url: '<%= resource.getPath() %>.booth-detail.html',
                        cache:false,
                        data: 
                                <%= mapper.writeValueAsString(booth) %>

                    });
                 });
            </script>
    </div>
</div>

<%
}
%>