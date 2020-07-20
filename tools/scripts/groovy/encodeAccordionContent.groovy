import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.Base64

def root = "/content/";

List<String> councilsList = new ArrayList<>();
println "=========================================="
println "Execution started :"+ new Date();
println "=========================================="

councilsList = getcouncilsList(root);
for (String item: councilsList) {
encodeAccordionContent(item);
}

println "=========================================="
println "Execution Stopped :"+ new Date();
println "=========================================="


def encodeAccordionContent(council){
	println "Encoding accordion content for " + council;
	def statement = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([${council}]) and s.[sling:resourceType] = 'girlscouts/components/accordion'"
	def result = queryResults(statement);
	println "Total Number of Accordion componenst configured in "+council +": "+result.getRows().size();

	try {
		if(null != result && result.getRows().size() != 0) {
			def rowIter = result.getRows();
			while (rowIter.hasNext()) {
				def row = rowIter.nextRow();
				node = row.getNode();
				checkForUnusedAccordions(node.getPath());
			}
		}
	} catch(Exception e) {
		println "Error occured "+e;
	}
}

def checkForUnusedAccordions(accordionPath) {
	//println "Checking Node "+accordionPath;

	List<String> childList = new ArrayList<>();
	Resource resource = resourceResolver.getResource(accordionPath);
	String accordionIndex = "";
	String accordionName = resource.getName();
	if (accordionName.contains("_") && accordionName.length() > accordionName.indexOf('_') + 1) {
		accordionIndex = accordionName.substring(accordionName.indexOf('_') + 1);
	}
	Resource children = resource.getChild("children");
	if(null != children && !children.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
		Iterator<Resource> items = children.listChildren();
		if(null != items && items.hasNext()) {
			while(items.hasNext()) {
				Node accordion = items.next().adaptTo(Node.class);
				String idField = "";
				if (accordion.hasProperty("idField")) {
					idField = accordion.getProperty("idField").getString();
				}
				String parsys = "accordion";
				if (StringUtils.isNotBlank(accordionIndex)) {
					parsys += "_" + accordionIndex;
				}
				parsys += "_parsys_";
				if (!StringUtils.isBlank(idField)) {
					parsys += idField;
				} else {
					try {
						parsys += accordion.getName();
					} catch (PathNotFoundException pnfe) {
						println("Exception occured" + e);
					}
				}
				childList.add(parsys);
			}
		}
	}

	try {
		Node accordionNode = resource.adaptTo(Node.class);
		if (null != accordionNode) {
			javax.jcr.NodeIterator nodeItr = accordionNode.getNodes();
			while (nodeItr.hasNext()) {
				Node accChildNode = nodeItr.nextNode();
				if (!accChildNode.getName().equals("children")) {
					if (!childList.contains(accChildNode.getName())) {
						javax.jcr.NodeIterator contenNode = accChildNode.getNodes();
						while (contenNode.hasNext()) {
							Node cNode = contenNode.nextNode();
							encodeAccordionContentNode(cNode);
						}
					}
				}
			}
		}
	} catch (Exception e) {
		println ("Exception occured " + e);

	}
}

def encodeAccordionContentNode(cnode) {
	String[] properties =["text", "tableData", "jcr:title", "title", "alt", "jcr:description"];
	try{
		if(null != cnode) {
			if(!cnode.hasProperty('isEncoded')) {
				for(String prop:properties) {
					if(cnode.hasProperty(prop)) {
						if(!cnode.isCheckedOut()){
							println"Node is checkedin: "+cnode.getPath();
						}
						String data = cnode.getProperty(prop).getString();
						byte[] bytesEncoded = Base64.encodeBase64(data.getBytes());
						String encodedString = new String(bytesEncoded);
						cnode.setProperty(prop, encodedString);
					}
				}
				cnode.setProperty("isEncoded", true);
				println "path "+ cnode.getPath();
				session.save();
			}

			javax.jcr.NodeIterator nodeIt = cnode.getNodes();
			while(nodeIt.hasNext()) {
				encodeAccordionContentNode(nodeIt.next());
			}
		}
	}catch(Exception e){
		println "Error occured "+e;
		println "Exception Path :"+cnode.getPath();
	}
	

}


def queryResults(statement) {
	def result = null;
	try {
		def queryManager = session.workspace.queryManager;
		def query = queryManager.createQuery(statement, "JCR-SQL2");
		result = query.execute();
	} catch (Exception e) {
		println "Exception occured at queryResults() "+ e;
	}
	return result;
}

def getcouncilsList(String rootPath) {
	Resource parentResource = resourceResolver.getResource(rootPath);
	Iterator <Resource> resource = parentResource.listChildren();
	List <String> councils = new ArrayList <String> ();
	while (resource.hasNext()) {
		Resource res = resource.next();
		if (res.adaptTo(Page.class) != null) {
			councils.add(res.getPath());
		}
	}
	println "List of Councils :" + councils
	councils;
}