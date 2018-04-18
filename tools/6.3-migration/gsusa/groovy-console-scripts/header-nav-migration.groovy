import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsusa";
String RESOURCE_TYPE = 'gsusa/components/header-nav';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:unstructured] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			if(node.hasProperty("navs") ){
			    Node linksNode = null;
			    if(!node.hasNode("navs")){
			        linksNode = node.addNode("navs", "nt:unstructured");
			    }else{
			        linksNode = node.getNode("navs");
			    }
			     println("**************************************************************************************")
			     println(linksNode.getPath())
				 try{
				     Value[] values = node.getProperty("navs").getValues()
	                 println("Processing header-nav with " + values.length + " values")
	                    
	                 for(int i = 0; i < values.length; i++){
	                    String linkSet = values[i].getString()
	                    println(linkSet);
	                    String[] linkProperties = linkSet.split("\\|\\|\\|");
	                    Node itemNode = null
	                    if(!linksNode.hasNode("item" + i)){
	                        itemNode = linksNode.addNode("item" + i, "nt:unstructured")
	                    } else{
	                        itemNode = linksNode.getNode("item" + i);
	                    }
						String lLabel = linkProperties[0];
						String path = linkProperties.length >= 2 ? linkProperties[1] : ""
						String clazz = linkProperties.length >= 3 ? linkProperties[2] : ""
						String mLabel = linkProperties.length >=4 ? linkProperties[3] : ""
						String sLabel = linkProperties.length >=5 ? linkProperties[4] : ""
						Boolean hideInDesktop = linkProperties.length >=6 ? Boolean.parseBoolean(linkProperties[5]) : false
						Boolean hideInMobile = linkProperties.length >=7 ? Boolean.parseBoolean(linkProperties[6]) : false
						Boolean rootLandingPage = linkProperties.length >=8 ? Boolean.parseBoolean(linkProperties[7]) : false
						Boolean newWindow = linkProperties.length >=9 ? Boolean.parseBoolean(linkProperties[8]) : false
	                    
	                    itemNode.setProperty("large-label", lLabel)
						 itemNode.setProperty("medium-label", mLabel)
						 itemNode.setProperty("small-label", sLabel)
						 itemNode.setProperty("class", clazz)
						 itemNode.setProperty("path", path)
						 itemNode.setProperty("hide-in-desktop", hideInDesktop)
						 itemNode.setProperty("hide-in-mobile", hideInMobile)
						 itemNode.setProperty("root-landing-page", rootLandingPage)
						 itemNode.setProperty("new-window", newWindow)
	                }				        
				    save()
				    println("saved converted content")
				 } catch(Exception e1){
					 try{
						 Value value = node.getProperty("navs").getValue()
						 String linkSet = value.getString()
						 println(linkSet);
						 String[] linkProperties = linkSet.split("\\|\\|\\|");
						 Node itemNode = null
						 if(!linksNode.hasNode("item" + i)){
							 itemNode = linksNode.addNode("item" + i, "nt:unstructured")
						 } else{
							 itemNode = linksNode.getNode("item" + i);
						 }
						 String lLabel = linkProperties[0];
						 String path = linkProperties.length >= 2 ? linkProperties[1] : ""
						 String clazz = linkProperties.length >= 3 ? linkProperties[2] : ""
						 String mLabel = linkProperties.length >=4 ? linkProperties[3] : ""
						 String sLabel = linkProperties.length >=5 ? linkProperties[4] : ""
						 Boolean hideInDesktop = linkProperties.length >=6 ? Boolean.parseBoolean(linkProperties[5]) : false
						 Boolean hideInMobile = linkProperties.length >=7 ? Boolean.parseBoolean(linkProperties[6]) : false
						 Boolean rootLandingPage = linkProperties.length >=8 ? Boolean.parseBoolean(linkProperties[7]) : false
						 Boolean newWindow = linkProperties.length >=9 ? Boolean.parseBoolean(linkProperties[8]) : false
						 
						 itemNode.setProperty("large-label", lLabel)
						 itemNode.setProperty("medium-label", mLabel)
						 itemNode.setProperty("small-label", sLabel)
						 itemNode.setProperty("class", clazz)
						 itemNode.setProperty("path", path)
						 itemNode.setProperty("hide-in-desktop", hideInDesktop)
						 itemNode.setProperty("hide-in-mobile", hideInMobile)
						 itemNode.setProperty("root-landing-page", rootLandingPage)
						 itemNode.setProperty("new-window", newWindow)
					 save()
					 println("saved converted content")
					 }catch(Exception e2){
					 
					 }
				 }		    
			}			
		}
	} catch (Exception e) {
	    println(e.getMessage())
		e.printStackTrace()
	}
}

def QueryResult search(EXPRESSION, QUERY_LANGUAGE) {
    println(EXPRESSION)
	QueryResult result = null;
	try {
		QueryManager queryManager = session.getWorkspace().getQueryManager();
		Query sql2Query = queryManager.createQuery(EXPRESSION, QUERY_LANGUAGE);
		return sql2Query.execute();
	} catch (Exception e) {
		e.printStackTrace();
	}
	return result;
}