import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsusa";
String RESOURCE_TYPE = 'gsusa/components/video-slider';

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
			if(node.hasProperty("links") ){
			    Node linksNode = null;
			    if(!node.hasNode("carouselList")){
			        linksNode = node.addNode("carouselList", "nt:unstructured");
			    }else{
			        linksNode = node.getNode("carouselList");
			    }
			     println("**************************************************************************************")
			     println(linksNode.getPath())
			    
			     Value[] values = node.getProperty("links").getValues()
                 println("Processing video-slider with " + values.length + " values")
                    
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
                    String title = linkProperties[0]
                    String imagepath = linkProperties.length >= 2 ? linkProperties[1] : ""
            
                    itemNode.setProperty("title", title)
                    itemNode.setProperty("imagepath", imagepath)
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