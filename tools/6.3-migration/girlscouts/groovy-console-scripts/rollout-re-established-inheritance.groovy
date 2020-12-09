import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import java.util.Set
import java.util.HashSet


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String PAGE_NAME = "troop-leader-blueprint";
String property = "jcr:mixinTypes";
String[] propertyValue = ["cq:LiveRelationship"];
int numberOfNodes = 0;

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [cq:Page] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                    "NAME()='"+PAGE_NAME+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)
 
if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			if(!node.getPath().contains("/content/girlscouts-template")){
			    String rowsNodePath = node.getPath() + "/jcr:content/content/middle/par/row_copy";
			    Resource rowsNode = resourceResolver.resolve(rowsNodePath);
			    Iterator<Resource> pars = rowsNode.listChildren();
			    while(pars.hasNext()){
			        Resource par = pars.next();
			        Iterator<Resource> textImageNodes = par.listChildren();
			        while(textImageNodes.hasNext()){
			            try{
			                numberOfNodes++
                            Resource textImageNode = textImageNodes.next();
                            Node txtImageNode = textImageNode.adaptTo(Node.class); 
                            if(!txtImageNode.hasProperty("cq:isCancelledForChildren")){
                                println("Page content node: " + txtImageNode.getPath())
                                try{txtImageNode.removeMixin("cq:ReplicationStatus");}catch(Exception e){
                                    println("ReplicationStatus value Exception with message: " + e.getMessage());
                                    }
                                try{txtImageNode.removeMixin("cq:LiveSyncCancelled");}catch(Exception e){
                                    println("LiveSyncCancelled value Exception with message: " + e.getMessage());
                                    }
                                
                                txtImageNode.addMixin("cq:LiveRelationship");
                                
                                println("mixinTypes: " + txtImageNode.getProperty("jcr:mixinTypes"))
                                
                            }
                            save()
                        } catch(Exception e){
                        println("Node Update Exception message: " + e.getMessage());
                        }
			        }
			    }
			}
			
		}
		
		println("Number Of Nodes: " + numberOfNodes)
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
