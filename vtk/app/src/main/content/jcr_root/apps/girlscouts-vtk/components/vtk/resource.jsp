<%@page import="
                javax.jcr.Session,
                org.apache.sling.api.resource.ResourceResolver,
                org.apache.sling.api.resource.Resource,
                com.day.cq.wcm.api.PageManager,
                com.day.cq.wcm.api.Page,
                com.day.cq.dam.api.Asset,
                com.day.cq.search.PredicateGroup,
                com.day.cq.search.Query,
                com.day.cq.search.QueryBuilder,
                org.girlscouts.vtk.ejb.YearPlanUtil,
                com.day.cq.search.result.SearchResult,
                org.girlscouts.vtk.helpers.CouncilMapper"%>
<%@ page import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*" %>
<%@include file="/libs/foundation/global.jsp"%>
<%
	final String RESOURCE_SEARCH_PROMPT = "type in a search word or term here";
	final String MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	final String TYPE_MEETING_AIDS = "meeting-aids";
	final String TYPE_MEETING_OVERVIEWS = "meeting-overviews";

	final QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
%>

<%@include file="include/session.jsp"%>
<%-- VTK tab --%>
<%
    String activeTab = "resource";
    boolean showVtkNav = true;
%>
<%@include file="include/tab_navigation.jsp"%>
<div id="panelWrapper" class="row content">
  <div class="columns large-20 large-centered">
		<script>
			var fixVerticalSizing = false;
			$(function() {
				var cache = {};
				$( "#resourceSearchField" ).autocomplete({
					minLength: 3,
					minChar: 3,
					source: function( request, response ) {
						var term = request.term;
						$("#searchResults").html("");
						$("#resourceSearchField" ).addClass('ui-autocomplete-loading');
						$.getJSON( 
							"/content/girlscouts-vtk/controllers/vtk.getdata.html?q="+term,
							request, function() {
		                                                $( "#resourceSearchField" ).removeClass('ui-autocomplete-loading');
							}
						).done(function() {
							$("#searchResults").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
						}).fail(function() {
		                                        $("#searchResults").load("/content/girlscouts-vtk/controllers/vtk.searchR.html?rand="+Date.now());
							$( "#resourceSearchField" ).removeClass('ui-autocomplete-loading');
						});
					}
				});
			});

			//function applyAids(aid, aidDesc){  applyAids(aid,aidDesc, '<%=AssetComponentType.AID%>'); }
			function applyAids(aid, aidDesc, assetType){
				if( assetType==null || assetType==''){assetType= '<%=AssetComponentType.AID%>'; }
				var link = "/content/girlscouts-vtk/controllers/vtk.asset.html?aidId="+ aid+ "&aidName="+encodeURI(aidDesc)+"&aType="+ assetType;
				loadModalPage(link, false, null);
			}
		</script>

			<h1>Search For Resources</h1>
			<div class="ui-widget">
				<input type="text" id="resourceSearchField" name="q" placeholder="<%=RESOURCE_SEARCH_PROMPT%>" class="vtkSearchField" />
			</div>
			<div id="searchResults"></div>

			<%-- categories --%>
			<h1>Browse Resources by Category</h1>

			<%
			final PageManager manager = (PageManager)resourceResolver.adaptTo(PageManager.class);
			try {
				final String RESOURCES_PATH = "resources";
				String councilId = null;
				if (apiConfig != null) {
				    if (apiConfig.getTroops().size() > 0) {
				        councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
				    }
				}
				CouncilMapper mapper = sling.getService(CouncilMapper.class);
				String branch = mapper.getCouncilBranch(councilId);

				// TODO: language?
				String resourceRootPath = branch + "/en/" + RESOURCES_PATH;
				final Page rootPage = manager.getPage(resourceRootPath);
				
				Iterator<Page> majorIter = rootPage.listChildren();

				int majorCount = 0;
				while (majorIter.hasNext()) {
				    majorIter.next();
					majorCount++; 
				}
				majorIter = rootPage.listChildren();
			%>

			<ul class="small-block-grid-1 medium-block-grid-<%= majorCount %> large-block-grid-<%= majorCount %> browseResources">
				<% 
					while (majorIter.hasNext()) { 
					    Page currentMajor = majorIter.next();
						%><li>
					<% 
							%><div><%= currentMajor.getTitle() %></div> <%
							Iterator<Page> minorIter = currentMajor.listChildren();

							minorIter = currentMajor.listChildren();
							while (minorIter.hasNext()) {
							    Page currentMinor = minorIter.next();
							    // TODO: encode URL
							    String link = "?category=" + currentMinor.getPath();
							    String title = currentMinor.getTitle();
							    long minorCount;
							    if (currentMinor.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
							        minorCount = countAidAssets(
							                MEETING_AID_PATH,
							                queryBuilder,
							                resourceResolver.adaptTo(Session.class)
							        );
							     } else if (currentMinor.getProperties().get("type", "").equals(TYPE_MEETING_OVERVIEWS)) {
							        try {
							            String rootPath = getMeetingsRootPath(troop);
							            Resource rootRes = resourceResolver.resolve(rootPath);
							            Iterator<Resource> resIter = rootRes.listChildren();
							            int resCount = 0;
							            while (resIter.hasNext()) {
							                resCount++;
							                resIter.next();
							            }
							            minorCount = resCount;
							        } catch (Exception e) {
							            minorCount = 0;
							        }
							     } else {
							    	minorCount = countAllChildren(currentMinor) - 1;
							     } 
							    %><div>
						<a href="<%= link %>"><%= title %> (<%= minorCount %>)</a>
					</div> <%
							}
						%>
				</li>
				<%
					} 
				%>
			</ul>
			<%
			} catch (Exception e) {
				log.error("Cannot get VTK meeting aid categories: " + e.getMessage());
			}
			%>

			<%-- display aids --%>
			<%
				String categoryParam = (String)request.getParameter("category");
				Page categoryPage = manager.getPage(categoryParam);

				if (categoryPage != null) {
				    if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
					  
					    	
					   java.util.List<org.girlscouts.vtk.models.Asset> gresources = yearPlanUtil.getAllResources(user,MEETING_AID_PATH+"/"); 
					 
					    %><table width="90%" align="center" class="browseMeetingAids"><tr><th colspan="3">Meeting Aids</th></tr><% 
					    for(int i=0;i<gresources.size();i++){
						org.girlscouts.vtk.models.Asset a = gresources.get(i);
						String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
			%>
					   	<tr>

							<td width="40">
			<%
						if (assetImage != null) {
			%>	
								<img src="<%= assetImage %>" width="40" height="40" border="0"/>
			<%
						}
			%>
							</td>
					   		<td><a class="previewItem" href="<%=a.getRefId() %>" target="_blank"><%= a.getTitle() %></a> </td>
					   		<td width="40">
					   			<% if( hasPermission(troop, Permission.PERMISSION_VIEW_YEARPLAN_ID ) ){ %>
					   				<input type="button" value="Add to Meeting" onclick="applyAids('<%=a.getRefId()%>', '<%=a.getTitle()%>', '<%=AssetComponentType.AID%>' )" class="button linkButton"/>
					   			<%} %>
					   			</td>

						</tr>
					   	<%
					   }
					    %></table><%
				    } else if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_OVERVIEWS)) {
					    %><%= displayMeetingOverviews(user, troop, resourceResolver, yearPlanUtil)%><%
				    } else {
					    %><div><%= categoryPage.getTitle() %></div><%
					    %><ul><% 
						StringBuilder builder = new StringBuilder();
						Iterator<Page> resIter = categoryPage.listChildren();
						while (resIter.hasNext()) {
						    Page resPage = resIter.next();
							displayAllChildren(resPage, builder);
					    }
						%><%= builder.toString() %><%
						%></ul><%
				    }
				}
			%>
	</div>
</div>

<%!
	private long countAllChildren(Page page) {
		// TODO: Need an effecient way.
		if (page == null) {
		    return 0;
		}

		long count = 1;
		Iterator<Page> iter = page.listChildren();
		while (iter.hasNext()) {
		    Page currentPage = iter.next();
		    count += countAllChildren(currentPage);
		}
		return count;
	}

	private void displayAllChildren(Page rootPage, StringBuilder builder) {
	    builder.append("<li>");
	    String path = rootPage.getPath();
	    // TODO: Get the link back once the dialog is styled
	    builder.append("<a href=\"javascript:void(0)\" onclick=\"displayResource('web', '");
	    builder.append(path);
	    builder.append("')\">");
	    builder.append(rootPage.getTitle());
	    builder.append("</a>");
	    Iterator<Page> iter = rootPage.listChildren();
	    while (iter.hasNext()) {
	        Page childPage = iter.next();
		    builder.append("<ul>");
		    displayAllChildren(childPage, builder);
		    builder.append("</ul>");
	    }
	    builder.append("</li>");
	}
	
	private long countAidAssets(String path, QueryBuilder builder, Session session) {
	    Map<String, String> map = new HashMap<String, String>();
	    map.put("path", path);
	    map.put("type", "dam:Asset");
	    map.put("p.limit", "1");
	    PredicateGroup predicate = PredicateGroup.create(map);
	    Query query = builder.createQuery(predicate, session);
	    
	    SearchResult result = query.getResult();
	    return result.getTotalMatches();
	}
	
	private String getMeetingsRootPath(Troop troop) {
		String level = troop.getTroop().getGradeLevel().toLowerCase();
	    // The field in SF is 1-Brownie, we need brownie
	    if (level.contains("-")) {
	        level = level.split("-")[1];
	    }
	    
	    // TODO: Move this to a constant? Or we need a DAO to get all meetings of a certain level.
	    final String MEETING_ROOT = "/content/girlscouts-vtk/meetings/myyearplan";
	    String levelMeetingsRootPath = MEETING_ROOT + "/" + level;    
	    
	    return levelMeetingsRootPath;
	}
	
	private String displayMeetingOverviews(User user, Troop troop, ResourceResolver rr, YearPlanUtil yearPlanUtil) {
	    try {
		    StringBuilder builder = new StringBuilder("<ul>");
		    String levelMeetingsRootPath = getMeetingsRootPath(troop);
		    Resource levelMeetingsRoot = rr.resolve(levelMeetingsRootPath);
		    Iterator<Resource> iter = levelMeetingsRoot.listChildren();
		    while (iter.hasNext()) {
		        Resource resource = iter.next();
		        Meeting meeting = yearPlanUtil.getMeeting(user,resource.getPath());
			    builder.append("<li><a href=\"javascript:void(0)\" onclick=\"displayResource('overview', '");
			    builder.append(meeting.getPath());
			    builder.append("')\">");
			    builder.append(meeting.getName());
			    builder.append("</a> - "+ meeting.getBlurb()+"</li>");
		    }
	
	        builder.append("</ul>");
	        return builder.toString();
	    } catch (Exception e) {}
	    return "";
	}
%>
