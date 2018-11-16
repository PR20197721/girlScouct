import javax.jcr.query.*
import java.util.*

int SLIDES_PROCESS_PER_SAVE = 200


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsnetx/en/jcr:content";
String RESOURCE_TYPE = "girlscouts/components/hero-banner";

String EXPRESSION = "SELECT s.[jcr:path] "+
		"FROM [nt:unstructured] AS s "+
		"WHERE ISDESCENDANTNODE('"+PATH+"') AND"+
		" s.[sling:resourceType] = '"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

class SlideData implements Comparable<SlideData> {

	Integer sortOrder = 0
	boolean enabled = false
	String newWindow = "", linkUrl = "",
		   regularImagePath = "", mediumImagePath = "", smallImagePath = "",
		   regularImageUploadPath = "", mediumImageUploadPath = "", smallImageUploadPath = "",
		   regularFileName, mediumFileName, smallFileName

	int compareTo(SlideData other){
		return sortOrder.compareTo(other.sortOrder)
	}
}

if (result != null) {
	try {
		RowIterator rowIter = result.getRows()
		int totalSlidesProcessed = 0;
		List<String> nodesToRemove = new ArrayList<>();
		while (rowIter.hasNext()) {
			try {
				Row row = rowIter.nextRow()
				Node node = row.getNode()

				if(node.hasNode("slides")){
					// Already converted
					println("Node: " + node.getPath()) + " already converted.";
					continue;
				}


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

					String regularImagePath = "", regularImageUploadPath = "", regularFileName = ""
					if(firstChild.hasNode("regular")) {
						Node regularNode = firstChild.getNode("regular");
						if (regularNode.hasProperty("fileReference")) {
							regularImagePath = regularNode.getProperty("fileReference").getString();
						}
						if (regularNode.hasProperty("fileName")) {
							regularFileName = regularNode.getProperty("fileName").getString();
						}
						if(regularNode.hasNode("file")){
							regularImageUploadPath = regularNode.getNode("file").getPath()
						}
					}
					String mediumImagePath = "", mediumImageUploadPath = "", mediumFileName = ""
					if(firstChild.hasNode("medium")) {
						Node mediumNode = firstChild.getNode("medium");
						if (mediumNode.hasProperty("fileReference")) {
							mediumImagePath = mediumNode.getProperty("fileReference").getString();
						}
						if (mediumNode.hasProperty("fileName")) {
							mediumFileName = mediumNode.getProperty("fileName").getString();
						}
						if(mediumNode.hasNode("file")){
							mediumImageUploadPath = mediumNode.getNode("file").getPath()
						}
					}

					String smallImagePath = "", smallImageUploadPath = "", smallFileName = ""
					if(firstChild.hasNode("small")) {
						Node smallNode = firstChild.getNode("small");
						if (smallNode.hasProperty("fileReference")) {
							smallImagePath = smallNode.getProperty("fileReference").getString();
						}
						if (smallNode.hasProperty("fileName")) {
							smallFileName = smallNode.getProperty("fileName").getString();
						}
						if(smallNode.hasNode("file")){
							smallImageUploadPath = smallNode.getNode("file").getPath()
						}
					}

					SlideData slideData = new SlideData()
					slideData.sortOrder = sortOrder
					slideData.newWindow = newWindow
					slideData.linkUrl = linkUrl
					slideData.regularImagePath = regularImagePath
					slideData.regularImageUploadPath = regularImageUploadPath
					slideData.regularFileName = regularFileName
					slideData.mediumImagePath = mediumImagePath
					slideData.mediumImageUploadPath = mediumImageUploadPath
					slideData.mediumFileName = mediumFileName
					slideData.smallImagePath = smallImagePath
					slideData.smallImageUploadPath = smallImageUploadPath
					slideData.smallFileName = smallFileName
					slideData.enabled = slidesProcessed < slideShowCount
					slideList.add(slideData)

					nodesToRemove.add(firstChild.getPath());

					slidesProcessed ++;
				}
				slideList.sort();

				println("------------------");
				println("Slides");
				println("------------------");

				Node slidesNode = node.addNode("slides")
				int slideIndex = 0;
				for(SlideData slideData : slideList){

					Node imageNode = slidesNode.addNode("item" + (slideIndex++));
					imageNode.setProperty("hidden", !slideData.enabled);
					imageNode.setProperty("newWindow", slideData.newWindow);
					imageNode.setProperty("linkURL", slideData.linkUrl)

					Node regularNode = imageNode.addNode("regular");
					regularNode.setProperty("fileReference", slideData.regularImagePath);
					regularNode.setProperty("fileName", slideData.regularFileName);
					regularNode.setProperty("sling:resourceType", "foundation/components/image");
					regularNode.setProperty("imagesize", "regular")
					if(!slideData.regularImageUploadPath.equals("")){
						session.move(slideData.regularImageUploadPath, regularNode.getPath() + "/file")
					}

					Node mediumNode = imageNode.addNode("medium");
					mediumNode.setProperty("fileReference", slideData.mediumImagePath);
					mediumNode.setProperty("fileName", slideData.mediumFileName);
					mediumNode.setProperty("sling:resourceType", "foundation/components/image");
					mediumNode.setProperty("imagesize", "medium")
					if(!slideData.mediumImageUploadPath.equals("")){
						session.move(slideData.mediumImageUploadPath, mediumNode.getPath() + "/file")
					}

					Node smallNode = imageNode.addNode("small");
					smallNode.setProperty("fileReference", slideData.smallImagePath);
					smallNode.setProperty("fileName", slideData.smallFileName);
					smallNode.setProperty("sling:resourceType", "foundation/components/image");
					smallNode.setProperty("imagesize", "small")
					if(!slideData.smallImageUploadPath.equals("")){
						session.move(slideData.smallImageUploadPath, smallNode.getPath() + "/file")
					}
				}

				totalSlidesProcessed += slidesProcessed;
				if(totalSlidesProcessed > SLIDES_PROCESS_PER_SAVE){
					session.save();
					totalSlidesProcessed = 0;
				}

			} catch (Exception e) {
				println("Exception!")
				println(e.getMessage())
				e.printStackTrace()
				throw e;
			}

		}

		int nodesRemoved = 0;
		for(String nodeToRemove : nodesToRemove){
			session.removeItem(nodeToRemove)
			if(nodesRemoved % 50 == 0){
				session.save();
			}
		}

	} catch (Exception e) {
		println("Exception2!")
		println(e.getMessage())
		e.printStackTrace()
		throw e;
	}

	// Final Save
	session.save();
	println("Done");
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