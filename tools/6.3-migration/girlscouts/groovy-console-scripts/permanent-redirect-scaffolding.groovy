import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import java.util.Iterator

String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/etc/scaffolding";
String FIELD_DESCRIPION = 'Choose the target redirect page.';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:base] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND CONTAINS "+
                    "(s.[fieldDescription],'"+FIELD_DESCRIPION+"')";
                    
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			String nodePath = node.getPath();
			println("Node Path::"+nodePath)
			node.setProperty("fieldDescription", "Choose the target redirect page. For PDF, please use absolute path.")
			println("Field Description New::"+node.getProperty("fieldDescription").getString())
		
		}
		save()
	}catch (Exception e) {
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