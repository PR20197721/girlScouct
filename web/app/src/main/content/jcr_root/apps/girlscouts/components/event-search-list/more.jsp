<%@ page import="java.util.Calendar,
java.util.TimeZone,
java.text.DateFormat,
java.text.SimpleDateFormat,
java.util.Date,
java.util.List, 
java.util.ArrayList, 
javax.jcr.Node,
javax.jcr.Property,
org.apache.sling.commons.json.JSONObject,
org.apache.sling.api.request.RequestPathInfo,
org.apache.sling.api.resource.ResourceUtil,
org.girlscouts.common.search.GSSearchResult, 
org.girlscouts.common.search.GSSearchResultManager,
org.girlscouts.common.search.GSJcrSearchProvider,

org.apache.commons.collections.IteratorUtils,
javax.jcr.NodeIterator,
javax.jcr.query.*,
javax.jcr.Session" 

%>    
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>      
<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%!
GSDateTimeFormatter dtfIn = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
GSDateTimeFormatter dtfOutDate = GSDateTimeFormat.forPattern("EEE MMM dd");
GSDateTimeFormatter dtfOutTime = GSDateTimeFormat.forPattern("h:mm aa");
GSDateTimeFormatter dtfOutMY = GSDateTimeFormat.forPattern("MMMM yyyy");
GSDateTimeFormatter dtUTF = GSDateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm");

public void setImage(JSONObject event, Node node, ResourceResolver rr){
    try {
        String imgPath="";
        if(node.hasProperty("jcr:content/data/thumbImage")){
            imgPath = node.getProperty("jcr:content/data/thumbImage").getString();
        } else if(node.hasNode("jcr:content/data/image")){
            imgPath = node.hasProperty("jcr:content/data/image/fileReference") ? node.getProperty("jcr:content/data/image/fileReference").getString() : "";
        }
        if(!ResourceUtil.isNonExistingResource(rr.resolve(imgPath))){
            imgPath = gsImagePathProvider.getImagePath(imgPath, "cq5dam.web.240.240");
        }
        event.put("imgPath", imgPath);
    }catch(Exception e){}
}

public void setRegistrationLink(JSONObject event, Node node, boolean includeCart, ResourceResolver rr){
    try {
        String register = "";
        String eventID = "-1";
        
        if(node.hasProperty("jcr:content/data/eid")){
            eventID = node.getProperty("jcr:content/data/eid").getString();
        }
        if(node.hasProperty("jcr:content/data/register") && !node.getProperty("jcr:content/data/register").getString().equals("")){
            register = node.getProperty("jcr:content/data/register").getString();
        }
        if(includeCart && register != null && !register.isEmpty() && !eventID.equals("-1")){
            event.put("registerLink", genLink(rr, register));
        }
        
    }catch(Exception e){}
}

public void setDates(JSONObject event, Node node){
    try {
        GSDateTime today = new GSDateTime();
        GSDateTimeZone dtz = null;
        GSDateTime startDate = null;
        GSDateTime endDate =null;
        GSLocalDateTime localStartDate = null;
        GSLocalDateTime localEndDate = null;
        
        String startDateStr = "";
        String startTimeStr = "";
        String endDateStr = "";
        String endTimeStr = "";
        String formatedStartDateStr="";
        String formatedEndDateStr="";
        String timeZoneShortLabel = "";
    
        if(node.hasProperty("jcr:content/data/start")){
            startDate = GSDateTime.parse(node.getProperty("jcr:content/data/start").getString(), dtfIn);
            localStartDate = GSLocalDateTime.parse(node.getProperty("jcr:content/data/start").getString(), dtfIn);
        }
        event.put("monthYearLabel",dtfOutMY.print(startDate));
        if(node.hasProperty("jcr:content/data/end")){
            endDate = GSDateTime.parse(node.getProperty("jcr:content/data/end").getString(), dtfIn);
            localEndDate = GSLocalDateTime.parse(node.getProperty("jcr:content/data/end").getString(), dtfIn);
        }
        
        String timeZoneLabel = node.hasProperty("jcr:content/data/timezone") ? node.getProperty("jcr:content/data/timezone").getString() : "";
        Boolean useRaw = timeZoneLabel.length() < 4;
        try{
            if(!timeZoneLabel.isEmpty() && !useRaw){
                int openParen1 = timeZoneLabel.indexOf("(");
                int openParen2 = timeZoneLabel.indexOf("(",openParen1+1);
                int closeParen = timeZoneLabel.indexOf(")",openParen2);
                if(closeParen != -1 && openParen2 != -1 && timeZoneLabel.length() > openParen2){
                    timeZoneLabel = timeZoneLabel.substring(openParen2+1,closeParen);
                }
                
                    dtz = GSDateTimeZone.forID(timeZoneLabel);
                    startDate = startDate.withZone(dtz);
                    timeZoneShortLabel = dtz.getShortName(startDate.getMillis());
                    startDateStr = dtfOutDate.print(startDate);
                    startTimeStr = dtfOutTime.print(startDate);
                
            } 
            if(timeZoneLabel.isEmpty() || useRaw){
                timeZoneShortLabel = timeZoneLabel;
                startTimeStr = dtfOutTime.print(localStartDate);
                startDateStr = dtfOutDate.print(localStartDate);
            }
            formatedStartDateStr = startDateStr + ", " +startTimeStr;
            event.put("formattedStartDate", formatedStartDateStr);
            event.put("utfStartDate", (startDate == null ? "" : dtUTF.withZone(GSDateTimeZone.UTC).print(startDate)));
        }catch(Exception e){
            useRaw = true;
            e.printStackTrace();
        }
        try{
        if(dtz != null){
            endDate = endDate.withZone(dtz);
            endDateStr = dtfOutDate.print(endDate);
            endTimeStr = dtfOutTime.print(endDate);
        } else{
            endDateStr = dtfOutDate.print(localEndDate);
            endTimeStr = dtfOutTime.print(localEndDate);
        }
        boolean sameDay = startDate.year() == endDate.year() && startDate.dayOfYear() == endDate.dayOfYear();
        if (!sameDay) {
            formatedEndDateStr= " - " + endDateStr +", " + endTimeStr;
        }else {
            formatedEndDateStr= " - " + endTimeStr;
        }
        int month = startDate.monthOfYear();
        formatedEndDateStr = formatedEndDateStr + " " + timeZoneShortLabel;
        event.put("formattedEndDate", formatedEndDateStr);
        event.put("utfEndDate", (endDate == null ? "" : dtUTF.withZone(GSDateTimeZone.UTC).print(endDate)));
        }catch(Exception e){
            useRaw = true;
            e.printStackTrace();
        }
        
    } catch (Exception e){}
}
%>
<%
    //Refactoring for 6.5 Upgrade
    final Session session = slingRequest.getResourceResolver().adaptTo(Session.class);
    final List<Node> eventNodesList = new ArrayList<>(0);

    int RESULTS_PER_PAGE = 10;
    int offset = 0;
    int resultCount = 0;
    
    String[] propertyPaths = new String[]{
            "jcr:content/jcr:title",
            "jcr:content/data/locationLabel",
            "jcr:content/data/address",
            "jcr:content/data/end",
            "jcr:content/data/color",
            "jcr:content/data/start",
            "jcr:content/data/srchdisp",
            "jcr:content/data/eid",
            "jcr:content/data/memberOnly",
            "jcr:content/data/priceRange",
            "jcr:content/data/timezone",
            "jcr:content/data/visibleDate"
    };
    String EVENTS_RESOURCE_TYPE = "girlscouts/components/event-page";
    Calendar cal = Calendar.getInstance();
    cal.setTimeZone(TimeZone.getDefault());
    DateFormat sql2DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    DateFormat printDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    sql2DateFormat.setTimeZone(TimeZone.getDefault());
    Boolean includeCart = false;
    if(homepage.getContentResource().adaptTo(Node.class).hasProperty("event-cart")){
        if(homepage.getContentResource().adaptTo(Node.class).getProperty("event-cart").getString().equals("true")){
            includeCart = true;
        }
    }
    
    
    try {
        JSONObject json = new JSONObject();
        List<JSONObject> events = new ArrayList<JSONObject>();
        
        try{
            final RequestPathInfo requestPathInfo = slingRequest.getRequestPathInfo();
            String[] selectors = requestPathInfo.getSelectors();
            if(selectors.length == 3){
                offset = Integer.parseInt(selectors[2]);
            }
        }catch(Exception e){}
        
        
        
        String EXPRESSION = "SELECT [jcr:score], [jcr:path], [jcr:primaryType] "
                + "FROM [cq:Page] AS s "
                + "WHERE ISDESCENDANTNODE([%s]) AND "
                    + "(s.[jcr:content/data/visibleDate] IS NULL OR s.[jcr:content/data/visibleDate] <= CAST('%s' AS DATE)) AND "
                    + "(s.[jcr:content/data/start] >= CAST('%s' AS DATE) OR s.[jcr:content/data/end] >= CAST('%s' AS DATE)) "
                + "ORDER BY s.[jcr:content/data/start] ASC";
        String path = currentSite.get("eventPath",String.class);
        String dateAsString = sql2DateFormat.format(cal.getTime());
        String query = String.format(EXPRESSION, path, dateAsString, dateAsString, dateAsString);
        try {
            boolean searchMore = true;
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query sql2Query = queryManager.createQuery(query, "JCR-SQL2");
            
            //If eventNodesList is empty execute query and cache results
            if(eventNodesList.isEmpty()){
                QueryResult result = sql2Query.execute();
                NodeIterator nodeIter = result.getNodes();
                //System.out.println(">>> event query: " + sql2Query.getStatement());
                while ( nodeIter.hasNext() ) {
                    eventNodesList.add(nodeIter.nextNode());
                }
            }
            
            int limit = (offset + RESULTS_PER_PAGE <= eventNodesList.size()) ? offset + RESULTS_PER_PAGE : eventNodesList.size();
            
            //Build json response while searchmore is true
            while(searchMore){
                if(offset >= eventNodesList.size()){
                    searchMore = false;
                    break;
                }
                
                for (int counter = offset; counter < limit; counter++) {    
                    offset++;   
                    try{
                        Node resultNode = eventNodesList.get(counter);
                        JSONObject event = new JSONObject();
                        String eventPath = resultNode.getPath();
                        try{ eventPath = resourceResolver.map(eventPath);}catch(Exception e){}
                        event.put("path", eventPath);
                        for(String prop:propertyPaths){
                            if(resultNode.hasProperty(prop)){
                                Property property = resultNode.getProperty(prop);
                                event.put(property.getName().replaceAll(":", "_"), property.getString());
                            }
                        }
                        setDates(event, resultNode);
                        setImage(event, resultNode, resourceResolver);
                        event.put("includeCart",includeCart);
                        setRegistrationLink(event, resultNode, includeCart, resourceResolver);
                        events.add(event);
                        resultCount++;
                    }catch(Exception e){}
                    if(resultCount == RESULTS_PER_PAGE){
                        searchMore = false;
                        break;
                    }
                }   
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        json.put("results",events);
        json.put("newOffset",offset);
        json.put("resultCount",resultCount);
        json.write(response.getWriter());
    } catch(Exception e){
        e.printStackTrace();
    }
    
    //out.println("<div>offset:"+offset+"</div>");
%>
