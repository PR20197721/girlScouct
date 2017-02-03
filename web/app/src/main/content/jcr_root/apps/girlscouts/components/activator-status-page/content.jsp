<%@page import="com.day.cq.wcm.api.WCMMode,
				org.girlscouts.web.councilupdate.PageActivator,
				org.girlscouts.web.councilupdate.DelayedPageActivator" %>
<%@include file="/libs/foundation/global.jsp" %>

<%
final WCMMode wcmMode = WCMMode.fromRequest(request);
if(wcmMode == WCMMode.EDIT){
	%><p>This page can only be viewed in author mode and should not be activated</p><%
}else{
	%>
	<h1>This is the Activator Status Page</h1>
	<%
	DelayedPageActivator dpa = sling.getService(DelayedPageActivator.class);
	PageActivator pa = null;
	String statusPath = dpa.getConfig("pagespath");
	Resource statusRes = null;
	if(statusPath != null){
		statusRes = resourceResolver.resolve(statusPath);
	}
	Node statusNode = null;
	if(statusRes.getResourceType == Resource.RESOURCE_TYPE_NON_EXISTING){
		%><p>Could not determine status of activator. It may not have been used yet.</p><%
	}else{
		statusNode = statusRes.adaptTo(Node.class);
		if(statusNode.hasProperty("inProgress") && statusNode.getProperty("inProgress").getString().equals("true")){
			%><p>The activator is now <b>active</b></p><%
		}else{
			%><p>The activator is now <b>idle</b></p><%
		}
		
		if(statusNode.hasProperty("type")){
			if(statusNode.getProperty("type").getString().equals("dpa")){
				pa = dpa;
			}
		}
		
		String groupSize = pa.getConfig("groupsize");
		if(groupSize != null){
			%><p>The number of councils to have pages activated/cache built per batch is <%= groupSize %></p><%
		}
		String minutes = pa.getConfig("minutes");
		if(minutes != null){
			%><p>Batches are staggered by a <%= minutes %> minute interval</p><%
		}
		
		TreeSet<String> toBuild = pa.getToBuild();
		if(toBuild.size() > 0){
			%><p>Pages queued for activation/cache building:</p><ul><%
			for(String p : toBuild){
				%><li>p</li><%
			}
			%></ul><%
		}
		
		TreeSet<String> currentBatch = pa.getCurrentBatch();
		if(currentBatch.size() > 0){
			%><p>Current Council Batch:</p><ul><%
			for(String p : currentBatch){
				%><li>p</li><%
			}
			%></ul><%
		}
		
		TreeSet<String> builtCouncils = pa.getBuiltCouncils();
		if(builtCouncils.size() > 0){
			%><p>Councils that have had pages activated and cache built so far</p><ul><%
			for(String p : builtCouncils){
				%><li>p</li><%
			}
			%></ul><%
		}
	}

 } %>
