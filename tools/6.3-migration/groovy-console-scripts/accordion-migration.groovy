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
	    Workspace workspace = session.getWorkspace()
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			Row row = rowIter.nextRow()
			Node node = row.getNode()
			if(node.hasProperty("children") ){
			    Node childrenNode = null;
			    if(!node.hasNode("children")){
			        childrenNode = node.addNode("children", "nt:unstructured");
			    }else{
			        childrenNode = node.getNode("children");
			    }
			    println("**************************************************************************************")
			    println(childrenNode.getPath())
			    try {
			        String accordion = node.getProperty("children").getString()
			        println("Processing single accordion")
			        println(accordion)
			        Node itemNode = null
			        if(!childrenNode.hasNode("item0")){
    			        itemNode = childrenNode.addNode("item0", "nt:unstructured")
    			    }else{
    			        itemNode = childrenNode.getNode("item0")
    			    }
			        println("creating "+itemNode.getPath())
			        if(accordion.contains("|||")){
			            String[] childValues = accordion.split("|||")
			            itemNode.setProperty("nameField",childValues[0])
			            itemNode.setProperty("anchorField",childValues[1])
			            itemNode.setProperty("idField",childValues[2])
			        }else{
			            itemNode.setProperty("nameField",accordion)
			        }
			        String accordionNodeName = node.getName()+"_parsys_0"
			        if(node.hasNode(accordionNodeName)){
			            Node accordionContentNode = node.getNode(accordionNodeName);
			            println("old accordion content node: "+accordionContentNode.getPath())
                      	String srcPath   = accordionContentNode.getPath();
                      	String path    = accordionContentNode.getPath().replace(accordionNodeName,node.getName()+"_parsys_"+itemNode.getName());
                      	println("new accordion content node: "+path)
                      	if(node.hasNode(node.getName()+"_parsys_"+itemNode.getName())){
                      	    println(path+" exists")
                      	    node.getNode(node.getName()+"_parsys_"+itemNode.getName()).remove()
                      	    println(path+" removed")
                      	    save()
                      	}
                      	workspace.copy(srcPath, path);
                      	println("created "+path)
			        }
			        save()
			        println("saved workspace")
			    } catch (Exception e) {
            		Value[] children = node.getProperty("children").getValues()
            		println("Processing multiple accordions")
            		for(int i=0; i<children.length; i++){
            		    println("---------------------------------------------------------------------------------")
    			        println(children[i])
    			        Node itemNode = null
    			         if(!childrenNode.hasNode("item"+i)){
        			        itemNode = childrenNode.addNode("item"+i, "nt:unstructured")
        			     }else{
        			        itemNode = childrenNode.getNode("item"+i)
        			     }
    			        println("creating "+itemNode.getPath())
    			        String accordion = children[i].getString()
    			        if(accordion.contains("|||")){
    			            String[] childValues = accordion.split("|||")
    			            itemNode.setProperty("nameField",childValues[0])
    			            itemNode.setProperty("anchorField",childValues[1])
    			            itemNode.setProperty("idField",childValues[2])
    			        }else{
    			            itemNode.setProperty("nameField",accordion)
    			        }
    			        String accordionNodeName = node.getName()+"_parsys_"+i;
    			        if(node.hasNode(accordionNodeName)){
    			            Node accordionContentNode = node.getNode(accordionNodeName);
    			            println("old accordion content node: "+accordionContentNode.getPath())
                          	String srcPath   = accordionContentNode.getPath();
                          	String path    = accordionContentNode.getPath().replace(accordionNodeName,node.getName()+"_parsys_"+itemNode.getName());
                          	println("new accordion content node: "+path)
                          	if(node.hasNode(node.getName()+"_parsys_"+itemNode.getName())){
                          	    println(path+" exists")
                          	    node.getNode(node.getName()+"_parsys_"+itemNode.getName()).remove();
                          	    println(path+" removed")
                          	    save()
                          	}
                          	println("created: "+path)
                          	workspace.copy(srcPath, path);
                          	println("saved workspace")
    			        }
            		}
            	}
			}
			save()
			println("saved")
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