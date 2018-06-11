import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import javax.jcr.security.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/home/users";
String RESOURCE_TYPE = 'girlscouts-vtk/components/resources/popup-list';

String EXPRESSION = "SELECT * "+
					"FROM [rep:User] AS s "+
					"WHERE ISDESCENDANTNODE('"+PATH+"')";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		Workspace workspace = session.getWorkspace()
		RowIterator rowIter = result.getRows()
		println("**************************************************************************************")
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			String principalName = node.getProperty("rep:principalName").getString();
			if(principalName.indexOf("service") == -1 && principalName.indexOf("sling") == -1 && principalName.indexOf("communities") == -1){
				AccessControlPolicy[] policies = session.getAccessControlManager().getPolicies(node.getPath());
				if(policies.length == 0){
					println(node.getProperty("rep:principalName").getString())
					//println("**************************************************************************************")
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		println(e);
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