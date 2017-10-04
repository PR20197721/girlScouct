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
//System.err.println("testerX path levelMeetingsRootPath: "+ levelMeetingsRootPath);    

    Resource levelMeetingsRoot = resourceResolver.resolve(levelMeetingsRootPath);
    String sectionClassDefinition ="";

    /*
    int meetingAidCount = yearPlanUtil.getCountLocalMeetingAidsByLevel(user, troop, levelMeetingsRootPath);
    meetingAidCount += yearPlanUtil.getAssetCount(user, troop, GLOBAL_MEETING_AID_PATH);
    */
 
    int meetingAidCount = yearPlanUtil.getVtkAssetCount(user, troop, GLOBAL_MEETING_AID_PATH);
   
    //int countLocalMeetingsAidsByLevel = yearPlanUtil.getAllResourcesCount(user, troop, LOCAL_MEETING_AID_PATH+"/"); 
                                
   String path = getMeetingsRootPath(troop);
    int meeting_overviews = 0;
    if( path!=null )
    	meeting_overviews= yearPlanUtil.getMeetingCount(user, troop, path+"/");
%>




<div id="modal_popup" class="reveal-modal" data-reveal=""></div>
<div id="myModal0" class="reveal-modal" data-reveal=""></div>
<div id="myModal1" class="reveal-modal" data-reveal=""></div>
<%@include file="include/bodyTop.jsp" %>



<div id="modal_popup_activity_MOTHERFIDE" class="reveal-modal" data-reveal aria-labelledby="modalTitle" aria-hidden="true" role="dialog">
  <h2 id="modalTitle">Awesome. I have it.</h2>
  <p class="lead">Your couch.  It is mine.</p>
  <p>I'm a cool paragraph that lives inside of an even cooler modal. Wins!</p>
  <a class="close-reveal-modal" aria-label="Close">&#215;</a>
</div>





	<div class="columns large-20 large-centered" style="overflow:hidden;">
<!-- a style="border:0; z-index: 100000000; position: absolute;height: 911px;width: 100%;" data-reveal-id="modal_popup_activity_MOTHERFIDE"></a -->
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


    final PageManager manager = (PageManager) resourceResolver.adaptTo(PageManager.class);
    final String RESOURCES_PATH = "resources";
    String councilId = null;
    if (apiConfig != null) {
        if (apiConfig.getTroops().size() > 0) {
            councilId = Integer.toString(apiConfig.getTroops().get(0).getCouncilCode());
        }
    }
    CouncilMapper mapper = sling.getService(CouncilMapper.class);
    String branch = mapper.getCouncilBranch(councilId);
    String resourceRootPath = branch + "/en/" + RESOURCES_PATH;
    java.util.Collection<bean_resource> resources= yearPlanUtil.getResourceData(user, troop, resourceRootPath);
    java.util.List<String> categories = VtkUtil.countResourseCategories(resources);
	%>
	
	<ul class="small-block-grid-1 medium-block-grid-<%=categories.size()%> large-block-grid-<%=categories.size()%> browseResources">
	    <%
	    for(int i=0;i<categories.size();i++){ 
	        String category= categories.get(i);
	    %>
		    <li>
		         <div><%=category %></div>
		         <%
		         java.util.Iterator <bean_resource>itr = resources.iterator();
		         while( itr.hasNext()){
		        	  bean_resource bresource = itr.next();
		        	  if( !bresource.getCategoryDisplay().equals( category ) ) continue;
                     if( bresource.getTitle().equals("Meeting Aids") )// && bresource.getItemCount()==0)
                          bresource.setItemCount(18);//meetingAidCount ) ;
                     else if(bresource.getTitle().equals("Meeting Overviews") )// && bresource.getItemCount()==0 )
                          bresource.setItemCount(85);//meeting_overviews);

                      if( "VTK Tutorial Videos".equals( bresource.getTitle() )){bresource.setItemCount(5);}
                      if( "Staying Safe on Adventures (Safety Activity Checkpoints)".equals( bresource.getTitle() )){bresource.setItemCount(1);}
                      if( "vtkvideos".equals( bresource.getTitle() ) ){continue;}
		         %>
			         <div>
			            
			             <a href="/content/girlscouts-vtk/en/myvtk/<%= troop.getSfCouncil() %>/vtk.resource.<%=(bresource.getPath() ==null || bresource.getPath().length()<=0) ? "" : bresource.getPath().substring(1).replaceAll("/","___")%>.html"><%=bresource.getTitle()%> (<%=bresource.getItemCount()%>) </a>
			             
			         </div> 
			     <%} %>
		    </li>
	    <%} %>
	</ul>
	<%
	//String categoryParam = (String)request.getParameter("category");
	String[] selectors = slingRequest.getRequestPathInfo().getSelectors();
	String categoryParam = null;
	for (String selector : selectors) {
		if (!"resource".equals(selector)) {
			categoryParam = "/" + selector.replaceAll("___", "/");
			break;
		}
	}
	if( categoryParam!=null && !categoryParam.trim().equals("")){
	%>
	   <%@include file="resource_display_aids.jsp" %>
	<%} %>
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


	private String getMeetingsRootPath(Troop troop) {
		if( troop==null || troop.getTroop()==null || troop.getTroop().getGradeLevel()==null) return "";
        String level = troop.getTroop().getGradeLevel().toLowerCase();
		// The field in SF is 1-Brownie, we need brownie
		if (level.contains("-")) {
			 level= level.substring( level.indexOf("-")+1 ); //exception: 7-Multi-level
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
