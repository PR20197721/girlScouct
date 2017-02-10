<%@page import="com.day.cq.wcm.api.WCMMode,
				org.girlscouts.web.councilupdate.DelayedPageActivator,
				org.girlscouts.web.councilupdate.PageActivator,
				java.util.TreeSet,
				java.util.HashMap" %>
<%@include file="/libs/foundation/global.jsp" %>
<div id="main" class="row collapse inner-wrapper">
<%
final WCMMode wcmMode = WCMMode.fromRequest(request);
if(wcmMode != WCMMode.EDIT){
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
	if(statusRes.getResourceType() == Resource.RESOURCE_TYPE_NON_EXISTING){
		%><p>Could not determine status of activator. It may not have been used yet.</p><%
	}else{
		statusNode = statusRes.adaptTo(Node.class);
		if(statusNode.hasProperty("inProgress") && statusNode.getProperty("inProgress").getString().equals("true")){
			%><p>The activator is now <b>active</b></p><%
		
			if(statusNode.hasProperty("type")){
				if(statusNode.getProperty("type").getString().equals("dpa")){
					pa = dpa;
					%><p>The current activation scheme is "Delayed Activation with Site Crawl"</p><%
				}else if(statusNode.getProperty("type").getString().equals("ipa-c")){
					pa = dpa;
					%><p>The current activation scheme is "Immediate Activation with Site Crawl"</p><%
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
			
			HashMap<String,TreeSet<String>> toBuild = pa.getToBuild();
			if(toBuild != null && toBuild.keySet().size() > 0){
				%><p>Pages queued for activation/cache building:</p><ul><%
				for(String key : toBuild.keySet()){
					%><li><%= key %></li><ul><%
					for(String val : toBuild.get(key)){
						%><li><%= val %></li><%
					}
					%></ul><%
				}
				%></ul><%
			}
			
			HashMap<String,TreeSet<String>> currentBatch = pa.getCurrentBatch();
			if(currentBatch != null && currentBatch.keySet().size() > 0){
				%><p>Current Council Batch:</p><ul><%
				for(String key : currentBatch.keySet()){
					%><li><%= key %></li><ul><%
						for(String val : currentBatch.get(key)){
							%><li><%= val %></li><%
						}
					%></ul><%
				}
				%></ul><%
			}
			
			HashMap<String, TreeSet<String>> builtCouncils = pa.getBuiltCouncils();
			long lastBatchTime = pa.getLastBatchTime();
			if(builtCouncils != null && builtCouncils.keySet().size() > 0){
				%><p>Councils that have had pages activated and cache built so far</p><ul><%
				for(String key : builtCouncils.keySet()){
					%><li><%= key %></li><ul><%
							for(String val : builtCouncils.get(key)){
								%><li><%= val %></li><%
							}
					%></ul><%
				}
				%></ul><%
				%><p>The last batch took <%= "" + lastBatchTime %> seconds to process</p><%
			}
			
			TreeSet<String> unmapped = pa.getUnmapped();
			if(unmapped.size() > 0){
				%><p>Not all pages could be processed. The activator was unable to map the following pages to their council websites</p><ul><%
				for(String p : unmapped){
					%><li><%= p %></li><%
				}
				%></ul><%
			}
			
		}else{
			%><p>The activator is now <b>idle</b></p><%
			String expressionStr = dpa.getConfig("scheduler.expression");
			String[] expression = expressionStr.split(" ");
			String amPm = "AM";
			int hours = Integer.parseInt(expression[2]);
			if(expression.length >= 3){
				if(hours > 11){
					amPm = "PM";
				}
				int minutes = Integer.parseInt(expression[1]);
				hours = (hours % 12);
				if(hours == 0){
					hours = 12;
				}
				%><p>The activator will run automatically at <%= String.format("%02d", hours) + ":" + String.format("%02d", minutes) + amPm %></p><%
			}
			if(statusNode.hasProperty("pages") && statusNode.getProperty("pages").getValues().length > 0){
				Value[] pages = statusNode.getProperty("pages").getValues();
				%><p>Pages queued for activation/cache building on next run (note that these will be grouped by council when the process takes place):</p><ul><%
				for(Value v : pages){
					%><li><%= v.getString()%></li><%
				}
				%></ul><%
			}
		}
	}
 } %>
</div>