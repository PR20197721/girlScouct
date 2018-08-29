import Node
import JcrUtils
import javax.jcr.query.*
import java.util.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsoh/en";
String RESOURCE_TYPE = "girlscouts/components/hero-banner";

String EXPRESSION = "SELECT s.[jcr:path] "+
					"FROM [nt:unstructured] AS s "+
					"WHERE ISDESCENDANTNODE('"+PATH+"') AND"+
					" s.[sling:resourceType] = '"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			try {
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				println("Found hero node: " + node.getPath());
				int slideShowCount = Integer.parseInt(node.getProperty("slideshowcount").getString());
				
				List<String> visibleSlideList = new ArrayList<String>();
				List<String> hiddenSlideList = new ArrayList<String>();
				
				int slidesProcessed = 0;
				NodeIterator iterator = node.getNodes()
				while(iterator.hasNext()){
				    String slidestring = "";
				    Node firstChild = iterator.nextNode();
				    String sortOrder = 0;
				    if(firstChild.hasProperty("sortOrder")){
				        sortOrder = firstChild.getProperty("sortOrder").getString();
				    }
				    String newWindow = "false";
				    if(firstChild.hasProperty("newWindow")){
				        newWindow = firstChild.getProperty("newWindow").getString();
				        if(!newWindow.equals("true")){
				           newWindow = false; 
				        }
				    }
				    String linkUrl = "";
				    if(firstChild.hasProperty("linkURL")){
				       linkUrl = firstChild.getProperty("linkURL").getString();
				    }
				    String nodeName = firstChild.getName();
				    Node regularNode = firstChild.getNode("regular");
				    String regularImagePath = "";
				    if(regularNode.hasProperty("fileReference")){
				        regularImagePath = regularNode.getProperty("fileReference").getString();
				    }
				    Node mediumNode = firstChild.getNode("medium");
				    String mediumImagePath = "";
				    if(mediumNode.hasProperty("fileReference")){
				        mediumImagePath = mediumNode.getProperty("fileReference").getString();
				    }
				    Node smallNode = firstChild.getNode("small");
				    String smallImagePath = "";
				    if(smallNode.hasProperty("fileReference")){
				        smallImagePath = smallNode.getProperty("fileReference").getString();
				    }
				    slideString = sortOrder + "|||" + nodeName +"|||" + newWindow + "|||" + linkUrl + "|||" + regularImagePath + "|||" + mediumImagePath + "|||" + 
				        smallImagePath;
				    if(slidesProcessed < slideShowCount){
				        visibleSlideList.add(slideString);
				    } else{
				        hiddenSlideList.add(slideString);
				    }
				    firstChild.remove();
				    slidesProcessed ++;
				    
				}
				save();
				visibleSlideList.sort();
				hiddenSlideList.sort();
				
				println("------------------");
				println("Visible Slides");
				println("------------------");
				for(String temp : visibleSlideList){
				   String [] slideProperties = temp.split("\\|\\|\\|");
				   Node imageNode = node.addNode(slideProperties[1]);
				   imageNode.setProperty("sortOrder", slideProperties[0]);
				   imageNode.setProperty("newWindow", slideProperties[2]);
				   imageNode.setProperty("linkURL", slideProperties[3]);
				   save();
				   Node regularNode = imageNode.addNode("regular");
				   regularNode.setProperty("fileReference", slideProperties[4]);
				   
				   Node mediumNode = imageNode.addNode("medium");
				   mediumNode.setProperty("fileReference", slideProperties[5]);
				   
				   Node smallNode = imageNode.addNode("small");
				   smallNode.setProperty("fileReference", slideProperties[6]);
				   save();
				}
				println("------------------");
				println("Hidden Slides");
				println("------------------");
				for(String temp : hiddenSlideList){
				    String [] slideProperties = temp.split("\\|\\|\\|");
				   Node imageNode = node.addNode(slideProperties[1]);
				   imageNode.setProperty("sortOrder", slideProperties[0]);
				   imageNode.setProperty("newWindow", slideProperties[2]);
				   imageNode.setProperty("linkURL", slideProperties[3]);
				   save();
				   Node regularNode = imageNode.addNode("regular");
				   regularNode.setProperty("fileReference", slideProperties[4]);
				   
				   Node mediumNode = imageNode.addNode("medium");
				   mediumNode.setProperty("fileReference", slideProperties[5]);
				   
				   Node smallNode = imageNode.addNode("small");
				   smallNode.setProperty("fileReference", slideProperties[6]);
				   save();
				}
				println("====================");
				
				
			} catch (Exception e) {
				println(e.getMessage())
				e.printStackTrace()
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