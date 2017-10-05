<%@ page import="com.day.cq.tagging.TagManager,com.day.cq.dam.api.Asset,java.util.ArrayList,java.util.HashSet, java.util.Locale,java.util.Map,java.util.Iterator,java.util.HashMap,java.util.List,java.util.Set,com.day.cq.search.result.SearchResult, java.util.ResourceBundle,com.day.cq.search.QueryBuilder,javax.jcr.PropertyIterator,org.girlscouts.web.events.search.SearchResultsInfo, com.day.cq.i18n.I18n,org.apache.sling.api.resource.ResourceResolver,org.girlscouts.web.events.search.EventsSrch,org.girlscouts.web.events.search.FacetsInfo,
    org.girlscouts.vtk.utils.VtkUtil,
	org.girlscouts.vtk.models.User,
	javax.servlet.http.HttpSession,
	com.day.text.Text,
	org.girlscouts.web.events.search.*,
	java.util.Collections" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
<cq:defineObjects/>
<%
	String q = request.getParameter("q");
	String[] tags = new String[]{};
	if (request.getParameterValues("tags") != null) {
		tags = request.getParameterValues("tags");
	} 
	String offset = request.getParameter("offset");
	String regions = request.getParameter("regions");
	String month = request.getParameter("month");
	String startdtRange = request.getParameter("startdtRange");
	String enddtRange = request.getParameter("enddtRange");
	String year  =request.getParameter("year");
	if(q == null && tags.length == 0 && offset == null && month == null && startdtRange == null && enddtRange == null && year == null){
		%>
		<div id="eventListWrapper"></div>
		<script>
			var resource = '<%=resource.getPath()%>';
			var eventsOffset=0;
			var monthYearLabel = "";
			$(document).ready(function() {
				loadMoreEvents();
			});
			function loadMoreEvents(){
				$.getJSON(resource+".more.json?offset="+eventsOffset, function (data) {
					eventsOffset = data.newOffset;
					$.each(data.results, function (index, result) {
						if(monthYearLabel != result.monthYearLabel){
							monthYearLabel = result.monthYearLabel;
							$("#eventListWrapper").append("<div class=\"eventsList monthSection\"><div class=\"leftCol\"><b>"+monthYearLabel.toUpperCase()+"</b></div><div class=\"rightCol horizontalRule\">&nbsp;</div></div>");
						}
						$("#eventListWrapper").append(getEventContent(result));
					});
				});
			}
			function getEventContent(event){
				var $eventDiv = $("<div>", {"class": "eventsList eventSection","itemtype":"http://schema.org/ItemList"});
				  $eventDiv.append(getEventImage(event));
				  var $rightColDiv = $("<div>", {"class": "rightCol"});
				  $rightColDiv.append(getEventTitle(event));
				  $rightColDiv.append(getEventMembershipRequired(event));
				  $rightColDiv.append(getEventDate(event));
				  $rightColDiv.append(getEventRegion(event));
				  $rightColDiv.append(getEventLocation(event));
				  $rightColDiv.append(getEventDescription(event));
				  $rightColDiv.append(getEventRegistration(event));
				  $eventDiv.append($rightColDiv);
				  var $bottomPaddingDiv = $("<div>", {"class": "eventsList bottomPadding"});
				  $eventDiv.append($rightColDiv);
				  $eventDiv.append($bottomPaddingDiv);
				  return $eventDiv;
			}
			function getEventImage(event){
				if(event.image){
					var $imgDiv = $("<div>", {"class": "leftCol", "itemprop":"image"});
					$imgDiv.append("<img src="+event.imgPath+"/>");
					return $imgDiv;
				}else{
					return "";
				}
				
			}
			function getEventTitle(event){
				return "<h6><a class=\"bold\" href=\""+event.path+".html\" itemprop=\"name\">"+event.jcr_title+"</a>";
			}
			function getEventMembershipRequired(event){
				if(event.memberOnly && memberOnly == 'true'){
					return "<p class=\"bold\">MEMBERSHIP REQUIRED</p>";
				}else{
					return "";
				}
				
			}
			function getEventDate(event){
				var $p = $("<p>", {"class":"bold"});
				$p.append("Date: ");
				$p.append("<span itemprop=\"startDate\" itemscope=\"\" itemtype=\"http://schema.org/Event\" content=\""+event.utfStartDate+"\">"+event.formattedStartDate+"</span>");
				$p.append("<span itemprop=\"stopDate\" itemscope=\"\" itemtype=\"http://schema.org/Event\" content=\""+event.utfEndDate+"\">"+event.formattedEndDate+"</span>");
				return $p;
			}
			function getEventRegion(event){
				if(event.region){
					var $p = $("<p>", {"class":"bold", "itemprop":"region", "itemscope":"", "itemptype":"http://schema.org/Place"});
					$p.append("Region: ");
					$p.append("<span itempropr=\"name\">"+event.region+"</span>");
					return $p;
				}else{
					return "";
				}
			}
			function getEventLocation(event){
				if(event.locationLabel){
					var $p = $("<p>", {"class":"bold", "itemprop":"location", "itemscope":"", "itemptype":"http://schema.org/Place"});
					$p.append("Location: ");
					$p.append("<span itemprop=\"name\">"+event.locationLabel+"</span>");
					return $p;
				}else{
					return "";
				}
			}
			function getEventDescription(event){
				if(event.srchdisp){
					var $p = $("<p>", {"itemprop":"description"});
					$p.append(event.srchdisp);
					return $p;
				}else{
					return "";
				}
			}
			function getEventRegistration(event){
				var eid = -1;
				var title = "";
				if(event.eid){
					eid = event.eid;
				}
				var $div = $("<div>", {"class":"eventDetailsRegisterLink"});
				if(event.registerLink){
					var $registerLink =  $("<a>", {"href":event.registerLink}).append("Register Now");
					$div.append($registerLink);
				}
				title = event.jcr_title;
				title = title.replace(/"\""/g, "&quot");
				title = title.replace(/"\'"/g, "\\\\'");
				var addToCartFunc = "addToCart('"+title+"','"+eid+"','"+event.path+".html'); return false;";
				var $addToCartLink =  $("<a>", {"onclick":addToCartFunc}).append("Add to MyActivities");
				$div.append($addToCartLink);
			}
			$(window).scroll(function(){
	            if  ($(window).scrollTop() == $(document).height() - $(window).height()){
	            	loadMoreEvents();
	            }
	    	});
		</script>
		<%
	}else{
		%>
		<cq:include script="events-from-search.jsp"/>
		<%
	}
%>