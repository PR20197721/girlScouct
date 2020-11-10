import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import java.util.Set
import java.util.HashSet


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'girlscouts/components/pagecounter';
int numberOfPages = 0;

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
			numberOfPages++
			//Node filterNode = row.getNode(row.getPath()+"/filters")
			if (node.hasNode("filters")) {
				Node filtersNode = node.getNode("filters")
				NodeIterator childNodes = filtersNode.getNodes()
				int count = 0
				while(childNodes.hasNext()) {
				    Node newNodes= childNodes.nextNode()
				    count++
				}
				Node itemNode = filtersNode.addNode("item"+count, "nt:unstructured")
				itemNode.setProperty("label", "Troop Leader Blueprint")
				itemNode.setProperty("pageOnly", "true")
				itemNode.setProperty("path", "/en/for-volunteers/troop-leader-blueprint")
				itemNode.setProperty("subDirOnly", "true")
				println("Item node:" + itemNode.getPath())
			}else{
				Node filtersNode = node.addNode("filters", "nt:unstructured")
				Node itemNode = filtersNode.addNode("item0", "nt:unstructured")
				itemNode.setProperty("label", "Troop Leader Blueprint")
				itemNode.setProperty("pageOnly", "true")
				itemNode.setProperty("path", "/en/for-volunteers/troop-leader-blueprint")
				itemNode.setProperty("subDirOnly", "true")
				println("item node:" + itemNode.getPath())
			}
			println("Page content node: " + node.getPath())	
		}
		save()
		println("Number Of Pages: " + numberOfPages)
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
