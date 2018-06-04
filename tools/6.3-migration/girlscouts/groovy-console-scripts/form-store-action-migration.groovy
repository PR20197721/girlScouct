import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import com.day.cq.wcm.api.PageManager
import com.day.cq.wcm.api.Page
import com.day.cq.replication.ReplicationStatus



String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'foundation/components/form/start';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:unstructured] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'"+
                    " AND s.[actionType]='foundation/components/form/actions/store'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)
PageManager pageManager = resourceResolver.adaptTo(PageManager);

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			if(node.hasProperty("actionType") ){
                
                String nodePath = node.getPath();
                println("Found old form action at: " + nodePath)
               
                node.setProperty("actionType", "girlscouts/components/form/actions/gsstore");
                save()
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