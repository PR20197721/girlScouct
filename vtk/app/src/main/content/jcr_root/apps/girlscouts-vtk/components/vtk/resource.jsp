<%@page
	import="
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
<%@ page
	import="java.util.*, org.girlscouts.vtk.auth.models.ApiConfig, org.girlscouts.vtk.models.*,org.girlscouts.vtk.dao.*,org.girlscouts.vtk.ejb.*"%>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="include/session.jsp"%>
<%
	final String RESOURCE_SEARCH_PROMPT = "type in a search word or term here";
	final String GLOBAL_MEETING_AID_PATH = "/content/dam/girlscouts-vtk/global/aid";
	final String LOCAL_MEETING_AID_PATH = "/content/dam/girlscouts-vtk/local/aid/meetings";
	final String TYPE_MEETING_AIDS = "meeting-aids";
	final String TYPE_MEETING_OVERVIEWS = "meeting-overviews";

	final QueryBuilder queryBuilder = sling.getService(QueryBuilder.class);
%>
<%-- VTK tab --%>
<%
    
	String activeTab = "resource";
    boolean showVtkNav = true;
    String levelMeetingsRootPath = getMeetingsRootPath(troop);
    Resource levelMeetingsRoot = resourceResolver.resolve(levelMeetingsRootPath);
    String sectionClassDefinition = "resource";
%>




<div id="modal_popup" class="reveal-modal" data-reveal=""></div>
<div id="myModal0" class="reveal-modal" data-reveal=""></div>
<div id="myModal1" class="reveal-modal" data-reveal=""></div>
<%@include file="include/bodyTop.jsp" %>



	<div class="columns large-20 large-centered" style="overflow:hidden;">
<div style="z-index: 100000000; position: absolute;height: 911px;width: 100%;" onclick="alert('MOTHERFIDE')"></div>
		<script>
			var fixVerticalSizing = false;
			$(function() {
				var cache = {};
				$( "#resourceSearchField" ).autocomplete({
					minLength: 4,
					minChar: 4,
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

			var x=0;		
			function applyAids(aid, aidDesc, assetType){
	
				if( assetType==null || assetType==''){assetType= '<%=AssetComponentType.AID%>';
				}
				var link = "/content/girlscouts-vtk/controllers/vtk.asset1.html?aidId="
						+ aid
						+ "&aidName="
						+ encodeURI(aidDesc)
						+ "&aType="
						+ assetType;

				$('#myModal' + x).foundation('reveal', 'open', {
					url : link
				});
				if (x == 0)
					x = 1;
				else
					x = 0;
			}
		</script>




		<h1>Search For Resources</h1>
	
		<div class="ui-widget">
			<input type="text" id="resourceSearchField" name="q"
				placeholder="<%=RESOURCE_SEARCH_PROMPT%>" class="vtkSearchField" />
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

		<ul
			class="small-block-grid-1 medium-block-grid-<%=majorCount%> large-block-grid-<%=majorCount%> browseResources">
			<%
				while (majorIter.hasNext()) { 
						    Page currentMajor = majorIter.next();
						    long minorCount = 0;
			    %><li><%					
				%><div><%=currentMajor.getTitle()%></div> <%
					Iterator<Page> minorIter = currentMajor.listChildren();
 					minorIter = currentMajor.listChildren();
 					while (minorIter.hasNext()) {
 					    Page currentMinor = minorIter.next();
 					    String link = "?category=" + currentMinor.getPath();
 					    String title = currentMinor.getTitle();
 					   if (currentMinor.getProperties().get("type", "").equals(TYPE_MEETING_AIDS)) {
 						  minorCount=0;
 						    try{
 							    Iterator<Resource> iter = levelMeetingsRoot.listChildren();
 								while (iter.hasNext()) {
 								    	Resource meetingResource = iter.next();
 								    	
 								        String meetingId= meetingResource.getPath().substring( meetingResource.getPath().lastIndexOf("/"));
 								        meetingId= meetingId.replace("/","");
 								        minorCount+= yearPlanUtil.getAllResourcesCount(user, troop, LOCAL_MEETING_AID_PATH+"/"+meetingId); //lresources.size();
 								
 								}
 						    }catch(Exception e){}
 					    
 					        minorCount += countAidAssets(
 					                GLOBAL_MEETING_AID_PATH,
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
 					        	e.printStackTrace();
 					            minorCount = 0;
 					        }
 					     } else {	    	 
 					    	minorCount = countAllChildren(currentMinor) - 1;
 					     }				   
 			    %>             
                <div>
					<a href="<%=link%>"><%=title%> (<%=minorCount%>)</a>
				</div> 
				<%
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
		%><table width="90%" align="center" class="browseMeetingAids">
			<tr>
				<th colspan="3">Meeting Aids</th>
			</tr>
			<%
				//LOCAL AIDS
					    	try {
							    Iterator<Resource> iter = levelMeetingsRoot.listChildren();
							    while (iter.hasNext()) {
							        Resource meetingResource = iter.next();
							        
							        String meetingId= meetingResource.getPath().substring( meetingResource.getPath().lastIndexOf("/"));
                                    meetingId= meetingId.replace("/","");
							        java.util.List<org.girlscouts.vtk.models.Asset> lresources = yearPlanUtil.getAllResources(user, troop, LOCAL_MEETING_AID_PATH+"/"+meetingId);//meeting.getId()); 
							        for(int i=0;i<lresources.size();i++){
									    org.girlscouts.vtk.models.Asset la = lresources.get(i);
										String lAssetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(la.getDocType());

			%>
			<tr>

				<td width="40">
					<%
						if (lAssetImage != null) {
					%> <img src="<%=lAssetImage%>" width="40" height="40" border="0" />
					<%
						}
					%>
				</td>
				<td><a class="previewItem" href="<%=la.getRefId()%>"
					target="_blank"><%=la.getTitle()%></a></td>
				<td width="40">
					<%
						if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){
					%>
					<input type="button" value="Add to Meeting"
					onclick="applyAids('<%=la.getRefId()%>', '<%=la.getTitle()%>', '<%=AssetComponentType.AID%>' )"
					class="button linkButton" /> <%
 	}
 %>
				</td>

			</tr>
			<%
				}
								}
						    } catch (Exception e) {e.printStackTrace();}
						  
						   	java.util.List<org.girlscouts.vtk.models.Asset> gresources = yearPlanUtil.getAllResources(user, troop, GLOBAL_MEETING_AID_PATH+"/"); 
						    for(int i=0;i<gresources.size();i++){
							org.girlscouts.vtk.models.Asset a = gresources.get(i);
							String assetImage = org.girlscouts.vtk.utils.GSUtils.getDocTypeImageFromString(a.getDocType());
			%>
			<tr>

				<td width="40">
					<%
						if (assetImage != null) {
					%> <img src="<%=assetImage%>" width="40" height="40" border="0" />
					<%
						}
					%>
				</td>
				<td><a class="previewItem" href="<%=a.getRefId()%>"
					target="_blank"><%=a.getTitle()%></a></td>
				<td width="40">
					<%
						if( VtkUtil.hasPermission(troop, Permission.PERMISSION_EDIT_MEETING_ID ) ){
					%>
					<input type="button" value="Add to Meeting"
					onclick="applyAids('<%=a.getRefId()%>', '<%=a.getTitle()%>', '<%=AssetComponentType.AID%>' )"
					class="button linkButton" /> <%
 	}
 %>
				</td>

			</tr>
			<%
			
				}
			%>
		</table>
		<%
			} else if (categoryPage.getProperties().get("type", "").equals(TYPE_MEETING_OVERVIEWS)) {
		%><%=displayMeetingOverviews(user, troop, resourceResolver, yearPlanUtil, levelMeetingsRoot)%>
		<%
			} else {
		%><div><%=categoryPage.getTitle()%></div>
		<%
			
		%><ul>
			<%
				StringBuilder builder = new StringBuilder();
							Iterator<Page> resIter = categoryPage.listChildren();
							while (resIter.hasNext()) {
							    Page resPage = resIter.next();
								displayAllChildren(resPage, builder, xssAPI);
						    }
			%><%=builder.toString()%>
			<%
				
			%>
		</ul>
		<%
			}
				}
		%>
	</div>
	
<%@include file="include/bodyBottom.jsp" %>
<script>
	loadNav('resource');
</script>


<%!private long countAllChildren(Page page) {   
		// TODO: Need an efficient way.
		long count = 1;
		Iterator<Page> iter = page.listChildren();
			while (iter.hasNext()) {

				Page currentPage = iter.next();
				count += countAllChildren(currentPage);
			}
		return count;
	}

	private void displayAllChildren(Page rootPage, StringBuilder builder,
			com.adobe.granite.xss.XSSAPI xssAPI) {
		builder.append("<li>");
		String path = rootPage.getPath();
		// TODO: Get the link back once the dialog is styled
		String href = "\"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_resource.html?resource="
				+ path + "\">";
		String modalUrl = "<a data-reveal-id=\"modal_popup\" data-reveal-ajax=\"true\" href="
				+ href + rootPage.getTitle() + "</a>";
		builder.append(modalUrl);
		Iterator<Page> iter = rootPage.listChildren();
		while (iter.hasNext()) {
			Page childPage = iter.next();
			builder.append("<ul>");
			displayAllChildren(childPage, builder, xssAPI);
			builder.append("</ul>");
		}
		builder.append("</li>");
	}

	private long countAidAssets(String path, QueryBuilder builder,
			Session session) {
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
		if( troop==null || troop.getTroop()==null || troop.getTroop().getGradeLevel()==null) return "";
        String level = troop.getTroop().getGradeLevel().toLowerCase();
		// The field in SF is 1-Brownie, we need brownie
		if (level.contains("-")) {
			level = level.split("-")[1];
		}

		// TODO: Move this to a constant? Or we need a DAO to get all meetings of a certain level.
		final String MEETING_ROOT = "/content/girlscouts-vtk/meetings/myyearplan"
				+ VtkUtil.getCurrentGSYear();
		String levelMeetingsRootPath = MEETING_ROOT + "/" + level;

		return levelMeetingsRootPath;

	}

	private String displayMeetingOverviews(User user, Troop troop,
			ResourceResolver rr, YearPlanUtil yearPlanUtil, Resource levelMeetingsRoot ) {
		try {
			StringBuilder builder = new StringBuilder("<ul>");
			Iterator<Resource> iter = levelMeetingsRoot.listChildren();
			while (iter.hasNext()) {
				Resource resource = iter.next();
				Meeting meeting = yearPlanUtil.getMeeting(user,troop,
						resource.getPath());
				String path = meeting.getPath();
				builder.append("<li>");
				String href = "\"/content/girlscouts-vtk/controllers/vtk.include.modals.modal_volunteer.html?resource="
						+ path + "\">";
				String modalUrl = "<a data-reveal-id=\"modal_popup\" data-reveal-ajax=\"true\" href="
						+ href + meeting.getName() + "</a>";
				builder.append(modalUrl);
				builder.append(" - " + meeting.getBlurb() + "</li>");
			}

			builder.append("</ul>");
			return builder.toString();
		} catch (Exception e) {
		}
		return "";
	}%>
