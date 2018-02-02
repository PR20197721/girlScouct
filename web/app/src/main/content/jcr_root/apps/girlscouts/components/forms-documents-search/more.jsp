<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch, java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo, org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.web.search.DocHit,java.util.HashSet,java.util.*"%> 
<%@ page import="javax.jcr.query.*,
                javax.jcr.*,
                com.day.cq.wcm.api.WCMMode,
				java.util.HashMap, 
				org.apache.sling.commons.json.JSONObject,
				org.girlscouts.web.search.GSSearchResult, 
				org.girlscouts.web.search.GSSearchResultManager,
				org.girlscouts.web.search.GSJcrSearchProvider" %>

<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>    
<cq:defineObjects/>
<%!
public String getValue(Row r, String column) throws ItemNotFoundException, RepositoryException {
    String toRet="";
    try{
        if( r==null) return "";
        Value val = r.getValue(column);
        if (val == null) {
            return "";
        }
        String strVal = val.getString();
        if (strVal == null) {
            return "";
        }
        return  strVal.trim();
    }catch(Exception exx){System.err.println("tata: "+column); exx.printStackTrace();}
    return "";
}
public String addTagsClause(String query, List<String> tags, boolean isPage){
	if(tags != null && tags.size() >0){
		query+=" AND (";
		for(int i=0;i<tags.size();i++){
			String tag = tags.get(i);
			//tag = tag.replace("/","\\/");
			if(i>0){
				query+=" OR";
			}
			if(isPage){
				query+=" [jcr:content/cq:tags]='"+tag+"'";
			}else{
				query+=" [jcr:content/metadata/cq:tags]='"+tag+"'";
			}
			
		}
		query+=")";
	}
	return query;
}
public String addContainsClause(String query, String searchText){
	if(searchText != null && searchText.trim().length() >0){
		query+=" AND contains(*, '"+searchText+"')";
	}
	return query;
}
%>
<%
long RESULTS_PER_PAGE = 20;
int offset = 0;
int resultCount = 0;
String q = request.getParameter("q");
String offsetParam = request.getParameter("offset");
String[] tags = request.getParameterValues("tags");
try{
	offset=Integer.parseInt(offsetParam);
}catch(Exception e){}

String PAGES_EXPRESSION = 	"SELECT [jcr:score], [/jcr:content/cq:tags], [excerpt(/renditions/original/jcr:content/jcr:data)], " +
								"[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title], "+
								"[jcr:content/jcr:title],[jcr:content/metadata/dc:description] "+
							"FROM [cq:Page] "+
							"WHERE ISDESCENDANTNODE([%s])";

String ASSETS_EXPRESSION = "SELECT [jcr:score], [/jcr:content/metadata/cq:tags], [excerpt(/renditions/original/jcr:content/jcr:data)], " +
								"[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title], "+
								"[jcr:content/jcr:title],[jcr:content/metadata/dc:description] "+
							"FROM [dam:Asset] "+
							"WHERE ISDESCENDANTNODE([%s])";

String SHARED_ASSETS_EXPRESSION = 	"SELECT [jcr:score], [/jcr:content/metadata/cq:tags], [excerpt(/renditions/original/jcr:content/jcr:data)], " +
										"[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title], "+
										"[jcr:content/jcr:title],[jcr:content/metadata/dc:description] "+
									"FROM [dam:Asset] "+
									"WHERE ISDESCENDANTNODE([%s])";


q = q==null ? "" : q.trim();
List<String> tagSet = new ArrayList<String>();
if (tags != null) {
    for (String words : tags){
    	tagSet.add(words);
    }
}
JSONObject json = new JSONObject();
List<JSONObject> formsAndDocs = new ArrayList<JSONObject>();
GSSearchResultManager gsResultManager = new GSSearchResultManager();
String damPath = "#";
HashMap<String,String> specialCouncils = new HashMap<String,String>();
specialCouncils.put("gateway","gateway");
specialCouncils.put("girlscoutcsa","southern-appalachian");
specialCouncils.put("gsnetx","NE_Texas");
specialCouncils.put("girlscoutsnccp","nc-coastal-pines-images-");
specialCouncils.put("gswcf","wcf-images");
specialCouncils.put("gssem","gssem");
specialCouncils.put("gssjc","gssjc");
specialCouncils.put("gswestok","gswestok");
specialCouncils.put("girlscoutsaz","girlscoutsaz");
specialCouncils.put("kansasgirlscouts","kansasgirlscouts");
specialCouncils.put("gssnv","gssnv");
specialCouncils.put("gswo","gswo");
specialCouncils.put("girlscoutsosw","oregon-sw-washington-");
specialCouncils.put("gskentuckiana","gskentuckiana");
specialCouncils.put("girlscouts-dxp","dxp");
specialCouncils.put("gssn","gssn");
specialCouncils.put("gsneo","gsneo");
specialCouncils.put("usagso","usagso");
specialCouncils.put("girlscoutsofcolorado","girlscoutsofcolorado");
specialCouncils.put("girlscoutstoday","girlscoutstoday");
specialCouncils.put("gsbadgerland","gsbadgerland");
specialCouncils.put("girlscoutsoc","girlscoutsoc");
specialCouncils.put("gscsnj","gscsnj");

damPath = "/content/dam/";
Page rootPage = homepage.getAbsoluteParent(1);
String rootPagePath = rootPage.getPath();
String councilName = rootPagePath.substring(rootPagePath.lastIndexOf("/")+1,rootPagePath.length());
if(specialCouncils.containsKey(councilName)){
	damPath = damPath + specialCouncils.get(councilName);
}else{
	damPath = damPath + "girlscouts-" + councilName;
}
if((q!=null && q.length()>0) || (tagSet != null && tagSet.size()>0)){
	try{
		GSJcrSearchProvider searchProvider = new GSJcrSearchProvider(slingRequest);
		String pagesQuery = String.format(PAGES_EXPRESSION, damPath, q);
		String assetsQuery = String.format(ASSETS_EXPRESSION, damPath, q);
		String sharedAssetsQuery = String.format(SHARED_ASSETS_EXPRESSION, damPath, q); 
		pagesQuery = addTagsClause(pagesQuery, tagSet, true);
		pagesQuery = addContainsClause(pagesQuery, q);
		assetsQuery = addTagsClause(assetsQuery, tagSet, false);
		assetsQuery = addContainsClause(assetsQuery, q);
		sharedAssetsQuery = addTagsClause(sharedAssetsQuery, tagSet, false);
		sharedAssetsQuery = addContainsClause(sharedAssetsQuery, q);
		gsResultManager.add(searchProvider.search(pagesQuery));
		gsResultManager.add(searchProvider.search(assetsQuery));
		gsResultManager.add(searchProvider.search(sharedAssetsQuery));
		gsResultManager.filter();	
	}catch(Exception e){
		e.printStackTrace();
	}
}
List<GSSearchResult> queryResults = gsResultManager.getResults();
if(offset <=queryResults.size()){
	for(int i=offset; i<queryResults.size();i++){
		GSSearchResult qResult=queryResults.get(i);
		offset++;
		try{
	 		Node resultNode = qResult.getResultNode();
	 		JSONObject result = new JSONObject();
	 		String path = resultNode.getPath();
	 		result.put("title", qResult.getTitle());
	 		result.put("score", qResult.getScore());
	 		String url = path;
	 		if("cq:Page".equals(resultNode.getPrimaryNodeType())){
	 			url+=".html";
	 		}
	 		result.put("url", url);
	 		result.put("type", resultNode.getPrimaryNodeType());
	 		int idx = path.lastIndexOf('.');
	 		String extension = idx >= 0 ? path.substring(idx + 1) : "";
	 		result.put("extension", extension);
            String description = qResult.getDescription();
	 		result.put("excerpt", qResult.getExcerpt());
	 		result.put("description", qResult.getDescription());
	 		formsAndDocs.add(result);
	 		resultCount ++;
		}catch(Exception e){}
		if(resultCount == RESULTS_PER_PAGE){
			break;
		}
	}
}
json.put("results",formsAndDocs);
json.put("newOffset",offset);
json.put("resultCount",resultCount);
json.write(response.getWriter());
%>