import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsusa";
String RESOURCE_TYPE = "foundation/components/textimage";

String EXPRESSION = "SELECT s.[jcr:path] "+
					"FROM [nt:unstructured] AS s "+
					"WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
					"CONTAINS(s.[sling:resourceType],'"+RESOURCE_TYPE+"')";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			node.setProperty("sling:resourceType","girlscouts/components/textimage")
			save()
			println(node.getPath())
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