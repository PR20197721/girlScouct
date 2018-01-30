<%@ page import="org.girlscouts.web.search.formsdocuments.FormsDocumentsSearch, java.util.Map,java.util.List,org.girlscouts.web.events.search.SearchResultsInfo, org.girlscouts.web.events.search.FacetsInfo,com.day.cq.search.result.Hit, org.girlscouts.web.search.DocHit,java.util.HashSet,java.util.*"%> 
<%@page import="javax.jcr.query.*,
                javax.jcr.*,
                com.day.cq.wcm.api.WCMMode,
				java.util.HashMap" %>


<%@include file="/libs/foundation/global.jsp"%>
<%@include file="/apps/girlscouts/components/global.jsp"%>
<cq:includeClientLib categories="apps.girlscouts" />
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
%>
<%
String q = request.getParameter("q");
q = q==null ? "" : q.trim();
String[] tags = request.getParameterValues("tags");
if((q!=null && !q.isEmpty()) ||  tags != null){
	String path = properties.get("./srchLocation", "");
	if(path.isEmpty()){  
	    path = "/content/dam/girlscouts-shared/en/documents";
	}
	String formDocumentContentPath = properties.get("./form-document-path","");
	if(formDocumentContentPath.isEmpty()){
	    //formDocumentContentPath = "/content/gateway/en/about-our-council/forms-documents";
	    //change default content path to current page.
	    formDocumentContentPath = currentPage.getPath();
	}
	// xss escaped query string
	final String escapedQueryForAttr = xssAPI.encodeForHTMLAttr(q != null ? q : "");
	// user selected tags
	Set<String> set = new HashSet<String>();
	if (request.getParameterValues("tags") != null) {
	    String tagParams="";
	    for (String words : tags){
	        set.add(words);
	    }
	}
	List<Hit> hits = new ArrayList();
	String damPath = "#";
	if(WCMMode.fromRequest(request) == WCMMode.EDIT){
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
	}
	%>
	<div class="search">
	<%
	try{
 		boolean isTag=false;
 		if( set!=null && set.size()>0){isTag= true;} 
		Session session = resourceResolver.adaptTo(Session.class);
		QueryManager qm = session.getWorkspace().getQueryManager();
		if( q.length()>2  ||  (set!=null && set.size()>0))
 			for(int i=0;i<2;i++){  
				String query = "";
			    if( i==1 )
			        query ="SELECT "+ (isTag ? "[/jcr:content/metadata/cq:tags]," : "") +" [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [dam:Asset] WHERE ISDESCENDANTNODE(["+path +"]) "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");
	    		else if( i==0 )
	        		query ="SELECT "+ (isTag ? "[/jcr:content/cq:tags]," : "") +" [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [cq:Page] WHERE ISDESCENDANTNODE(["+formDocumentContentPath+"])  "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");
	    		else if( i==2 )
	        		query ="SELECT  [excerpt(/renditions/original/jcr:content/jcr:data)],[jcr:content/renditions/original/jcr:content/jcr:data], [jcr:path],[jcr:content/metadata/dc:title],[jcr:content/jcr:title],[jcr:content/metadata/dc:description] from [dam:Asset] WHERE ISDESCENDANTNODE([/content/dam/girlscouts-shared/documents])  "+ ((q.length() > 0 ) ? " AND contains(*, '"+q+"')" : "");
	    		Query q1 = qm.createQuery(query, Query.JCR_SQL2);
	    		// q1.setLimit(100); 
				QueryResult result = q1.execute();
    			alex:for (RowIterator it = result.getRows(); it.hasNext(); ) {
        			try{       
   						Row r = it.nextRow();
						Node rowNode = resourceResolver.resolve(r.getPath()).adaptTo(Node.class);
  						if( i!=2 && isTag ){
						   boolean isFound= false; 
						   Property tagProp;
						   if( i!=0 ){	   
							   tagProp = rowNode.getProperty("jcr:content/metadata/cq:tags");
						   }else{
						   		tagProp = rowNode.getProperty("jcr:content/cq:tags");
						   }
							Value[] tagProps = tagProp.getValues();
					       for(Value v : tagProps){
					            String _tag= v.getString();
					            if( set.contains( _tag ) ){ 
					            	isFound= true; 
					            }
					        }
    						if( !isFound ){continue alex;}
  						}//edn if
					    String pth = getValue(r,"jcr:path");
					    String title = "";
					    title = getValue(r, "jcr:content/metadata/dc:title");
					    if (title.isEmpty()) {
					        title = getValue(r, "jcr:content/jcr:title");
					    }
					    if (title.isEmpty()) {
					        title = path.substring(path.lastIndexOf("/") + 1);
					    }
						String description = getValue(r, "jcr:content/metadata/dc:description");
						int idx = pth.lastIndexOf('.');
				        String extension = idx >= 0 ? pth.substring(idx + 1) : "";
				        String newWindow = "";
						%>
        				<br/>
						<%
        				if(!extension.isEmpty() && !extension.equals("html")){
            				newWindow = "target=_blank";
							%>
        					<span class="icon type_<%=extension%>"><img src="/etc/designs/default/0.gif" alt="*"></span>
							<%
        				}
						%>
    					<a href="<%=(i==0) ? (pth+".html") : pth%>" <%=newWindow %>><%=title %></a>
						<%
        				if ( (set!=null && set.size()>0 ) || (q != null && !q.isEmpty()) ) {
				            String excerpt = description;
				            if (description ==null || "".equals(description)) {
				                excerpt = title;
				            }
							%>
    						<div><%= excerpt %></div>
						<%
						}
						%>
    					<br/>
    				<%
        			}catch(Exception ex){ex.printStackTrace();}
				}//end for
			}//end i
		}catch(Exception e){ e.printStackTrace();System.err.println(e.getMessage()); }
		%>
	</div>
<%}%>
