import javax.jcr.query.*
import java.util.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsnetx/en";
String RESOURCE_TYPE = "girlscouts/components/hero-banner";

String EXPRESSION = "SELECT s.[jcr:path] "+
					"FROM [nt:unstructured] AS s "+
					"WHERE ISDESCENDANTNODE('"+PATH+"') AND"+
					" s.[sling:resourceType] = '"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

class SlideData implements Comparable<SlideData> {

	Integer sortOrder = 0
	boolean enabled = false
	String newWindow = "", linkUrl = "", regularImagePath = "", mediumImagePath = "", smallImagePath = "", uploadImagePath = ""

	int compareTo(SlideData other){
		return sortOrder.compareTo(other.sortOrder)
	}
}

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		while (rowIter.hasNext()) {
			try {
				Row row = rowIter.nextRow()
				Node node = row.getNode()
				println("Found hero node: " + node.getPath());
				int slideShowCount = 99

				if(node.hasProperty("slideshowcount")) {
					slideShowCount = Integer.parseInt(node.getProperty("slideshowcount").getString());
				}
				
				List<SlideData> slideList = new ArrayList<>();
				
				int slidesProcessed = 0;
				NodeIterator iterator = node.getNodes()
				while(iterator.hasNext()){
				    Node firstChild = iterator.nextNode();
				    String sortOrder = 0;
				    if(firstChild.hasProperty("sortOrder")){
				        sortOrder = firstChild.getProperty("sortOrder").getString();
				    }
				    String newWindow = "false";
				    if(firstChild.hasProperty("newWindow")){
				        newWindow = firstChild.getProperty("newWindow").getString();
				        if(!newWindow.equals("true")){
				           newWindow = "false";
				        }
				    }
				    String linkUrl = "";
				    if(firstChild.hasProperty("linkURL")){
				       linkUrl = firstChild.getProperty("linkURL").getString();
				    }
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

					SlideData slideData = new SlideData()
					slideData.sortOrder = sortOrder
					slideData.newWindow = newWindow
					slideData.linkUrl = linkUrl
					slideData.regularImagePath = regularImagePath
					slideData.mediumImagePath = mediumImagePath
					slideData.smallImagePath = smallImagePath
					slideData.enabled = slidesProcessed < slideShowCount
					slideList.add(slideData)

				    firstChild.remove();
				    slidesProcessed ++;
				}
				save();
				slideList.sort();
				
				println("------------------");
				println("Slides");
				println("------------------");

				Node slidesNode = node.addNode("slides")
				save();
				int slideIndex = 0;
				for(SlideData slideData : slideList){

					Node imageNode = slidesNode.addNode("item" + (slideIndex++));
					save();

					imageNode.setProperty("enabled", slideData.enabled);
					imageNode.setProperty("newWindow", slideData.newWindow);
					imageNode.setProperty("linkURL", slideData.linkUrl)
					save();

					Node regularNode = imageNode.addNode("regular");
					regularNode.setProperty("fileReference", slideData.regularImagePath);
					regularNode.setProperty("sling:resourceType", "foundation/components/image");
					regularNode.setProperty("imagesize", "regular")

					Node mediumNode = imageNode.addNode("medium");
					mediumNode.setProperty("fileReference", slideData.mediumImagePath);
					regularNode.setProperty("sling:resourceType", "foundation/components/image");
					regularNode.setProperty("imagesize", "medium")

					Node smallNode = imageNode.addNode("small");
					smallNode.setProperty("fileReference", slideData.smallImagePath);
					regularNode.setProperty("sling:resourceType", "foundation/components/image");
					regularNode.setProperty("imagesize", "small")
					save();
				}
				modal_view_sent_emails
				
			} catch (Exception e) {
				println("Exception!")
				println(e.getMessage())
				e.printStackTrace()
				throw e;
			}
			
		}
	} catch (Exception e) {
		println("Exception2!")
		println(e.getMessage())
		e.printStackTrace()
		throw e;
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