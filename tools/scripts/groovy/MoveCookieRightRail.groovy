import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*
//Originally 43 with 3-col-page resource type, 66 with contact-placeholder resourceType
//109 with contact-placeholder after running script

QueryManager queryManager = session.getWorkspace().getQueryManager();
LinkedList<String> contactsList = new LinkedList<>();
String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/content/gsusa/en]) AND NAME() = \"cookieRightPath\"";
Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
QueryResult resultSet = query.execute();
NodeIterator nodeItr = resultSet.getNodes();
while(nodeItr.hasNext()) {
    Node cookieRightPath = nodeItr.nextNode();
    Node jcrContentNode = cookieRightPath.getParent();
    Node content = jcrContentNode.getNode("content");
    if (content != null){
        if (!content.hasNode("right")){
            content.addNode("right");
            save();
            move cookieRightPath.getPath() to content.getPath()+"/right/par";
        } else {
            cookieRightPath.remove();
            save();
        }
    }

    println(content.toString());
}