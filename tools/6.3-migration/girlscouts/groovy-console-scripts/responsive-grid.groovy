import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsnetx";
String RESOURCE_TYPE = 'girlscouts/components/styled-parsys';
String RESOURCE_TYPE2 = 'foundation/components/parsys';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:unstructured] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND ("+
                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"' OR s.[sling:resourceType]='"+RESOURCE_TYPE2+"')";
                    
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			if(node.hasProperty("sling:resourceType") ){
			    try{
			        node.setProperty("sling:resourceType","wcm/foundation/components/responsivegrid");
			    }catch(Exception exception){
			        
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