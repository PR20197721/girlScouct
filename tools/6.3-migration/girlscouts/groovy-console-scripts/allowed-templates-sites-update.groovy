import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsusa";
String RESOURCE_TYPE = 'gsusa/components/video-slider';

Resource content = resourceResolver.resolve("/content");
Iterator<Resource> sites = content.listChildren();
while(sites.hasNext()){
    try{
    Resource res = sites.next();
		if(res.isResourceType("cq:Page")){
		    Node node = res.adaptTo(Node.class); 
		    if(node.hasNode("jcr:content")){
    		    Node jcrContent = node.getNode("jcr:content");
    		    String siteName = node.getName();
    		    println("Updating site: " + siteName)
    		    if(siteName.equals("gsusa")){
    		        jcrContent.setProperty("cq:allowedTemplates", "/apps/gsusa/templates/.*");
    		    } else{
    		        jcrContent.setProperty("cq:allowedTemplates", "/apps/girlscouts/templates/.*");
    		    }
    		    save()
		    }
		}
    } catch(Exception e){
        println("Exception with message: " + e.getMessage());
    }
}


