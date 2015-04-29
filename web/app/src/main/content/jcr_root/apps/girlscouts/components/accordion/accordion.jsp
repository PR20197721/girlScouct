<%--

  Accordion component.

  Generates an HTML accordion object

--%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Date" %>
<%
	String[] children = properties.get("children", String[].class);
	if(children == null){
        %><p>**Edit this component to add accordions**</p><%
	}
	else{
        %>
        <dl class="accordion" data-accordion><%
        for (int i=0; i<children.length; i++){
        	String parsys = resource.getName() + "_parsys_" + i;
        	%><dt data-target="<%=parsys%>"><h6 class = "on"><%=children[i]%></dt>
        	<dd class="accordion-navigation">
        		<div class="content" id="<%=parsys%>">
        			<cq:include path="<%=parsys%>" resourceType="foundation/components/parsys" />
        		</div>     		
        	</dd>
        	<%
        }
        %></dl>
        <script> CQ.WCM.on("editablesready", function(){
        	$('dd .content').each(function(){
        			window[$( this ).attr('id')] = new toggleParsys("<%= resource.getPath() + "/"%>" + $( this ).attr('id'));
        			window[$( this ).attr('id')].hideParsys();
        		});
        	});</script><%
	}
%>