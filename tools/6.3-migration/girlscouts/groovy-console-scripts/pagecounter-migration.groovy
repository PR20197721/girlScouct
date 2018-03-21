import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'girlscouts/components/pagecounter';

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
			if(node.hasProperty("paths") ){
			    Node linksNode = null;
			    if(!node.hasNode("paths")){
			        linksNode = node.addNode("paths", "nt:unstructured");
			    }else{
			        linksNode = node.getNode("paths");
			    }
			     println("**************************************************************************************")
			     println(linksNode.getPath())
			    
			     Value[] values = node.getProperty("paths").getValues()
                 println("Processing pagecounter with " + values.length + " values")
                    
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
                    String label = linkProperties[0]
                    String path = linkProperties[1]
                    String pageOnly = linkProperties[2]
                    String subDirOnly = linkProperties[3]
                    itemNode.setProperty("label", label)
                    itemNode.setProperty("pageOnly", pageOnly)
                    itemNode.setProperty("subDirOnly", subDirOnly)
                    itemNode.setProperty("path", path)
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