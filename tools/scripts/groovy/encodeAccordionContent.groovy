import javax.jcr.query.*
import javax.jcr.*
import java.util.*
import javax.jcr.Node.*
import org.apache.commons.lang.StringUtils
import org.apache.commons.codec.binary.Base64

def root = "/content/";

println "=========================================="
println "Execution started :"+ new Date();
println "=========================================="

def queryStr = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([${root}]) and s.[sling:resourceType] = 'girlscouts/components/accordion'"

Node node = null;
def result = queryResults(queryStr);
println "Number of results found: "+result.getRows().size();

try{
	if(null != result){
		def rowIter = result.getRows();
		while (rowIter.hasNext()) {
			def row = rowIter.nextRow();
			node = row.getNode();
			encodeAccordionContentNode(node.getPath());
		}
	}
}catch (Exception e) {
	println("Exception occured:"+e);
}

println "=========================================="
println "Execution Stopped :"+ new Date();
println "=========================================="

def encodeAccordionContentNode(contentPath){
	List<String> childList = new ArrayList<>();
	Resource resource = resourceResolver.getResource(contentPath);
	
	String accordionIndex = "";
	String accordionName = resource.getName();
	
	if (accordionName.contains("_") && accordionName.length() > accordionName.indexOf('_') + 1) {
		accordionIndex = accordionName.substring(accordionName.indexOf('_') + 1);
	}
	
	try {
		Node accordionNode = resource.adaptTo(Node.class);
		if (null != accordionNode) {
			javax.jcr.NodeIterator nodeItr = accordionNode.getNodes();
			while (nodeItr.hasNext()) {
				Node accChildNode = nodeItr.nextNode();
				if (accChildNode.getName() != "children") {
					javax.jcr.NodeIterator contenNode = accChildNode.getNodes();
					while (contenNode.hasNext()) {
						Node cNode = contenNode.nextNode();
						if(null != cNode) {
							if (!cNode.hasProperty("isEncoded") && cNode.hasProperty("text")) {
								String data = cNode.getProperty("text").getString();
								byte[] bytesEncoded = Base64.encodeBase64(data.getBytes());
								String encodedString = new String(bytesEncoded);
								cNode.setProperty("text", encodedString);
								cNode.setProperty("isEncoded", true);
								println "Encoded Node Path: "+cNode.getPath();
								
							}else if(!cNode.hasProperty("isEncoded") && cNode.hasProperty("tableData")) {
								String data = cNode.getProperty("tableData").getString();
								byte[] bytesEncoded = Base64.encodeBase64(data.getBytes());
								String encodedString = new String(bytesEncoded);
								cNode.setProperty("tableData", encodedString);
								cNode.setProperty("isEncoded", true);
								println "Encoded Node Path: "+cNode.getPath();
							}
						}
						
					}
					
				}
			}
		}
		
	}catch(Exception e) {
		println("Exception occured encodeAccordionContentNode :"+e);
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
				if (!StringUtils.isBlank(accordionIndex)) {
					parsys += "_" + accordionIndex;
				}
				parsys += "_parsys_";
				if (!StringUtils.isBlank(idField)) {
					parsys += idField;					
				} else {
					try {
						//resource.adaptTo(Node.class).getNode(parsys + accordion.getName());
						parsys += accordion.getName();
					} catch (PathNotFoundException pnfe) {
						println("Exception occured" + e);
					}
				}
				
				String parsysIdentifier = resource.getPath() + "/" + parsys;	
				
				try {
				    
					Resource parsysRes = resourceResolver.getResource(parsysIdentifier);
				
					if (null != parsysRes && !parsysRes.getResourceType().equals(Resource.RESOURCE_TYPE_NON_EXISTING)) {
					    
						Node parNode = parsysRes.adaptTo(Node.class);
						javax.jcr.NodeIterator parNodeItr = parNode.getNodes();
						while (parNodeItr.hasNext()) {
							Node cNode = parNodeItr.nextNode();
							if(null != cNode) {
								if (cNode.hasProperty("isEncoded") && cNode.hasProperty("text")) {
									String decodeStr = cNode.getProperty("text").getString();
									byte[] valueDecoded = Base64.decodeBase64(decodeStr);
									String decodedString = new String(valueDecoded);
									cNode.setProperty("text", decodedString);
									cNode.getProperty("isEncoded").remove();
									println "Decoded Node Path: "+cNode.getPath();
								}
								else if(cNode.hasProperty("isEncoded") && cNode.hasProperty("tableData")) {
									String decodeStr = cNode.getProperty("tableData").getString();
									byte[] valueDecoded = Base64.decodeBase64(decodeStr);
									String decodedString = new String(valueDecoded);
									cNode.setProperty("tableData", decodedString);
									cNode.getProperty("isEncoded").remove();
									println "Decoded Node Path: "+cNode.getPath();
								}
							}
							
						}
						//session.save();
					}
				} catch (Exception e) {
					println("Exception occured" + e);
				}
			}
		}
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