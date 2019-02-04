import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import com.day.cq.wcm.msm.api.LiveRelationshipManager
QueryManager queryManager = session.getWorkspace().getQueryManager();
String threeColQuery = "SELECT * FROM [nt:base] AS s where [sling:resourceType]='girlscouts/components/three-column-page'";
Query query = queryManager.createQuery(threeColQuery, "JCR-SQL2");
QueryResult resultSet = query.execute();
NodeIterator nodeItr = resultSet.getNodes();
LiveRelationshipManager lrm = resourceResolver.adaptTo(LiveRelationshipManager.class);
int nodesModified = 0;
while(nodeItr.hasNext()){
    Node node = nodeItr.nextNode();
    if(lrm.hasLiveRelationship(resourceResolver.resolve(node.getPath()))){
        try{
            if(!node.hasNode("content/right/advertisement") && node.hasNode("content")){
                Node content = node.getNode("content");
                if(!content.hasNode("right")){
                    content.addNode("right","nt:unstructured");
                }
                Node right = content.getNode("right");
                Node ad = right.addNode("advertisement","nt:unstructured");
                ad.setProperty("sling:resourceType","girlscouts/components/advertisement");
                ad.setProperty("customized","false");
                ad.addMixin("cq:LiveRelationship");
                println(ad.getPath());
                //save()
                nodesModified++
            }
        }catch(Exception e){
            println(e);
        }
    }
}
println("Number of pages modified to include advertisement Node: " + nodesModified);