import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'girlscouts/components/accordion';

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
			if(node.hasProperty("children") && (!node.hasNode("children") || (node.hasNode("children") && !node.getNode("children").hasNodes() ))){
				if(!node.hasNode("children")){
					node.addNode("children", "nt:unstructured");
				}
				Node childrenNode = node.getNode("children");
				println("**************************************************************************************")
				println(node)
				try {
					String accordion = node.getProperty("children").getString()
					Node newNode = childrenNode.addNode("item0")
					if(accordion.contains("|||")){
						String[] childValues = accordion.split("|||")
						newNode.setProperty("nameField",childValues[0])
						newNode.setProperty("anchorField",childValues[1])
						newNode.setProperty("idField",childValues[2])
					}else{
						newNode.setProperty("nameField",accordion)
					}
					println("newNode: "+newNode)
					save()
				} catch (Exception e) {
					Value[] children = node.getProperty("children").getValues()
					for(int i=0; i<children.length; i++){
						println(children[i])
						Node newNode = childrenNode.addNode("item"+i)
						String accordion = children[i].getString();
						if(accordion.contains("|||")){
							String[] childValues = accordion.split("|||")
							newNode.setProperty("nameField",childValues[0])
							newNode.setProperty("anchorField",childValues[1])
							newNode.setProperty("idField",childValues[2])
						}else{
							newNode.setProperty("nameField",accordion)
						}
						println("newNode: "+newNode)
						save()
					}
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
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