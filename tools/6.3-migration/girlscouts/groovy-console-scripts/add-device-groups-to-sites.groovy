import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import java.util.Iterator

String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [cq:Page] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"')";
                    
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)
println("resourceResolver "+resourceResolver)
Resource content = resourceResolver.resolve("/content")
Iterator<Resource> sites = content.listChildren()
String[] arr = {"/etc/mobile/groups/responsive"}
while(sites.hasNext()){
	try{
		Resource res = sites.next();
		if(res.isResourceType("cq:Page")){
    		Node node = res.adaptTo(Node.class);
    		if(node.hasNode("jcr:content")){
    		    Node jcrContent = node.getNode("jcr:content");
        		if(!jcrContent.hasProperty("cq:deviceGroups") ){
        		    println("will set property on "+jcrContent.getPath())
        			jcrContent.setProperty("cq:deviceGroups",arr);
        			save()
        			println("saved converted content")
        		}
    		}
		}
	}catch(Exception exception){
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