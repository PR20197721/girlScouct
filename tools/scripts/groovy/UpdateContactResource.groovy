import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
//Originally 43 with 3-col-page resource type, 66 with contact-placeholder resourceType
//109 with contact-placeholder after running script

QueryManager queryManager = session.getWorkspace().getQueryManager();
LinkedList<String> contactsList = new LinkedList<>();
String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/content]) AND s.[sling:resourceType]='girlscouts/components/contact-list'";
Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
QueryResult resultSet = query.execute();
NodeIterator nodeItr = resultSet.getNodes();
while(nodeItr.hasNext()) {
    Node node = nodeItr.nextNode();
    try {
        String path = node.getProperty("path").getString();
        log.info("Path: " + path);
        contactsList.add(path + "/jcr:content");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
getContactPaths(contactsList);

def void getContactPaths(LinkedList<String> contacts){
    int numThreeCol = 0;
    int numContactPlaceholder = 0;
    for(String cntct : contacts){
        try{
            Node contact = getNode(cntct);
            if(contact.hasProperty("sling:resourceType")){
                if(contact.getProperty("sling:resourceType").getString().equals("girlscouts/components/three-column-page")){
                    contact.setProperty("sling:resourceType", "girlscouts/components/contact-placeholder-page");
                    save();
                    numThreeCol++;
                } else if(contact.getProperty("sling:resourceType").getString().equals("girlscouts/components/contact-placeholder-page")){
                    numContactPlaceholder++;
                }
            }

        } catch(Exception e){
            log.error("repository exception");
        }
    }


    println "3-col-page count: "+numThreeCol;
    println "Contact-Placeholder count: "+numContactPlaceholder;
}


