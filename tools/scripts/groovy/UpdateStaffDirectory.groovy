import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*

QueryManager queryManager = session.getWorkspace().getQueryManager();

String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([/content]) AND s.[sling:resourceType]='girlscouts/components/contact-list'";
Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
QueryResult resultSet = query.execute();
NodeIterator nodeItr = resultSet.getNodes();
while(nodeItr.hasNext()) {
    Node node = nodeItr.nextNode();
    try {
        String path = node.getProperty("path").getString();
        log.info("Path: " + path);
        populateStaffDir(getPage(path).getAbsoluteParent(1));
    } catch (Exception e) {
        e.printStackTrace();
    }
}

def void populateStaffDir(homepage){
    log.info("Started parsing");

    ArrayList<String> contactList = new ArrayList<>();
    NodeIterator nodeItr = null;
    try{
        //Query for contact-list component
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String staffDirQuery = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE(["+homepage.getPath()+"]) AND s.[sling:resourceType]='girlscouts/components/contact-list'";
        Query query = queryManager.createQuery(staffDirQuery, "JCR-SQL2");
        QueryResult resultset = query.execute();
        nodeItr = resultset.getNodes();
    }catch (Exception e){
        log.error("ContactsUpdateListener threw an error", e);
    }
    while(nodeItr.hasNext()){
        Node node = nodeItr.nextNode();
        try{
            Node contacts = getResource(node.getProperty("path").getString()).adaptTo(Node.class);
            createContactString(contacts, contactList);
            log.info("Finished parsing");
            node.setProperty("contacts", contactList.toArray(new String[0]));
            node.getSession().save();
            log.info("Finished injecting contacts information to Staff-Directory's contact-list component");
        } catch (Exception e){
            log.error("ContactsUpdateListener threw an error", e);
        }

    }
}
def void createContactString(parent, contacts){
    StringBuilder sb = new StringBuilder();
    try{
        if(parent.hasNodes()){
            NodeIterator nodeItr = parent.getNodes();
            while(nodeItr.hasNext()){
                Node node = nodeItr.nextNode();
                createContactString(node, contacts);
            }
        } else{
            if(parent.hasProperty("jcr:title")){
                if(!"Contacts".equals(parent.getProperty("jcr:title").getString())){

                    if(!parent.hasProperty("email")){
                        if(!sb.toString().equals("")){
                            sb.delete(sb.length()-2, sb.length());
                            sb.append("; ");
                        }
                        sb.append(parent.getProperty("jcr:title").getString().toUpperCase());
                        sb.append(": ");
                    }
                    if(parent.hasProperty("email")){
                        sb.append(parent.getProperty("jcr:title").getString());
                        sb.append(" : ");
                    }
                }
            }
            if(parent.hasProperty("phone")){
                sb.append(parent.getProperty("phone").getString());
                if(parent.hasProperty("email") || parent.hasProperty("team") || parent.hasProperty("cq:tags"))
                    sb.append(" : ");
            }
            if(parent.hasProperty("email")){
                sb.append(parent.getProperty("email").getString());
                if(parent.hasProperty("team") || parent.hasProperty("cq:tags"))
                    sb.append(" : ");
            }
            if(parent.hasProperty("team")){
                sb.append(parent.getProperty("team").getString());
                if(parent.hasProperty("cq:tags"))
                    sb.append(" : ");
            }
            if(parent.hasProperty("cq:tags")){
                Property tags = parent.getProperty("cq:tags");
                Value[] values = tags.getValues();
                for(Value value : values){
                    sb.append(value.getString());
                    sb.append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
            }
            if(parent.hasProperty("jcr:title")){
                if(!"Contacts".equals(parent.getProperty("jcr:title").getString()))
                    contacts.add(sb.toString());
            }

        }
    } catch(Exception e){
        log.error("ContactsUpdateListener threw an error parsing contacts", e);
    }
}