import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'gsusa/components/footer-share';

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
			if(node.hasProperty("socialIcons") ){
			    Node linksNode = null;
			    if(!node.hasNode("socialIcons")){
			        linksNode = node.addNode("socialIcons", "nt:unstructured");
			    }else{
			        linksNode = node.getNode("socialIcons");
			    }
			     println("**************************************************************************************")
			     println(linksNode.getPath())
			     try{
				     Value[] values = node.getProperty("socialIcons").getValues()
	                 println("Processing footer-share with " + values.length + " values")
	                    
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
	                    String name = linkProperties[0]
	                    String url = linkProperties.length >= 2 ? linkProperties[1] : ""
	                    String iconPath = linkProperties.length >= 3 ? linkProperties[2] : ""
	                    itemNode.setProperty("name", name)
	                    itemNode.setProperty("url", url)
	                    itemNode.setProperty("icon-path", iconPath)
	                        
	                        
	                }
			     }catch(Exception e1){
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
						String name = linkProperties[0]
	                    String url = linkProperties.length >= 2 ? linkProperties[1] : ""
	                    String iconPath = linkProperties.length >= 3 ? linkProperties[2] : ""
	                    itemNode.setProperty("name", name)
	                    itemNode.setProperty("url", url)
	                    itemNode.setProperty("icon-path", iconPath)
					 }catch(Exception e2){}
				 }			        
			    save()
			        println("saved converted content")			    
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