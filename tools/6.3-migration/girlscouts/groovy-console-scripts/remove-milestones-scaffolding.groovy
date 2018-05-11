import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content/gsusa";
String RESOURCE_TYPE = 'gsusa/components/video-slider';

Resource content = resourceResolver.resolve("/etc/scaffolding");
Iterator<Resource> sites = content.listChildren();
while(sites.hasNext()){
    try{
    Resource res = sites.next();
		if(res.isResourceType("nt:folder")){
		    Node node = res.adaptTo(Node.class); 
		    if(node.hasNode("milestones")){
    		    Node milestonesNode = node.getNode("milestones");
    		    String mileStonesPath = milestonesNode.getPath();
    		    println("Removing scaffolding: " + mileStonesPath)
    		    milestonesNode.remove()
    		    save()
		    }
		}
    } catch(Exception e){
        println("Exception with message: " + e.getMessage());
    }
}


