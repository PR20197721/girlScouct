<%--

  Accordion component.

  Generates an HTML accordion object

--%>
<%@page import="com.day.cq.wcm.api.WCMMode" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@page import="java.util.Date,
				com.day.cq.wcm.api.WCMMode" %>
<%
    if (WCMMode.fromRequest(request) == WCMMode.EDIT) {
	   %><cq:includeClientLib categories="apps.gsusa.authoring" /><%
	}
	String[] namesAndAnchors = properties.get("children", String[].class);
	String[] children = null;
	String[] anchors = null;
	if(namesAndAnchors != null && namesAndAnchors.length > 0){
		children = new String[namesAndAnchors.length];
		anchors = new String[namesAndAnchors.length];
		for(int i =0; i < namesAndAnchors.length; i++){
			String[] split = namesAndAnchors[i].split("\\|\\|\\|");
			children[i] = split.length >= 1 ? split[0] : "";
            anchors[i] = split.length >= 2 ? split[1] : "";
		}
	}
	if(children == null){
        %><p>**Edit this component to add accordions**</p><%
	}
	else{
		String[] ids = new String[children.length];
        %>
            <dl class="accordion" data-accordion><%
            for (int i=0; i<children.length; i++){
            	String parsys = resource.getName() + "_parsys_" + i;
            	ids[i] = parsys;
            	%><dt style="clear:both" id="<%=anchors[i]%>" data-target="<%=parsys%>"><h6><%=children[i]%></dt>
            	<dd class="accordion-navigation">
            		<div class="content" id="<%=parsys%>">
            			<cq:include path="<%=parsys%>" resourceType="foundation/components/parsys" />
            		</div>
            	</dd>
            	<%
            }
            %></dl>
            <%
            if(WCMMode.fromRequest(request) == WCMMode.EDIT){
            %>
        <script>
        	CQ.WCM.on("editablesready", function(){
        	<%
        		for(int i=0; i<ids.length; i++){
        	%>
    			window.<%= ids[i] %> = new toggleParsys("<%= resource.getPath() + "/" + ids[i] %>");
    			window.<%= ids[i] %>.hideParsys();
        	<%
				}
			%>
        	});
        	</script><%
            }
	}
%>