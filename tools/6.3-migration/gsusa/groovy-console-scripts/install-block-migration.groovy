import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'gsusa/components/install-block';

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
		    
			if(node.hasProperty("apple") ){
			    Node iconNode = null;
			    if(!node.hasNode("appleicon")){
			        iconNode = node.addNode("appleicon", "nt:unstructured");
			    }else{
			        iconNode = node.getNode("appleicon");
			    }
			    iconNode.setProperty("sling:resourceType", "foundation/components/image");
			    
			    String appleValue = node.getProperty("apple").getString();
		        String[] appleValues = appleValue.split("\\|\\|\\|");
		        
				if(appleValues.length > 0 && appleValues[0] != null){
					iconNode.setProperty("fileReference", appleValues[0]);
				}
				if(appleValues.length > 1 && appleValues[1] != null){
		        		node.setProperty("applelink", appleValues[1]);
				}
				if(appleValues.length > 2 && appleValues[2] != null){
					node.setProperty("applenewwindow", appleValues[2]);
				}
	        		
		        node.getProperty("apple").remove();
			}
			
			if(node.hasProperty("google") ){
			    Node iconNode = null;
			    if(!node.hasNode("googleicon")){
			        iconNode = node.addNode("googleicon", "nt:unstructured");
			    }else{
			        iconNode = node.getNode("googleicon");
			    }
			    iconNode.setProperty("sling:resourceType", "foundation/components/image");
			    
			    String googleValue = node.getProperty("google").getString();
		        String[] googleValues = googleValue.split("\\|\\|\\|");
		        
				if(googleValues.length > 0 && googleValues[0] != null){
					iconNode.setProperty("fileReference", googleValues[0]);
				}
				if(googleValues.length > 1 && googleValues[1] != null){
		        		node.setProperty("googlelink", googleValues[1]);
				}
				if(googleValues.length > 2 && googleValues[2] != null){
					node.setProperty("googlenewwindow", googleValues[2]);
				}
		        
		        node.getProperty("google").remove();
			}
			save();
	        println("saved converted content");
			
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
