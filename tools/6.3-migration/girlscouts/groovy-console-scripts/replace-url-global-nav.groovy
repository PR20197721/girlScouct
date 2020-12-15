import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
String PATH = "/content";
String RESOURCE_TYPE = 'girlscouts/components/global-navigation';

String EXPRESSION = "SELECT s.[jcr:path] "+
                    "FROM [nt:unstructured] AS s "+
                    "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                    "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

if (result != null) {
    try {
        RowIterator rowIter = result.getRows()
        while (rowIter.hasNext()) {
            Row row = rowIter.nextRow()
            Node node = row.getNode()
            if(node.hasNode("links")){
                Node linksNode = node.getNode("links");
                println("**************************************************************************************")
                println(linksNode.getPath());
                NodeIterator itr = linksNode.getNodes()
                while(itr.hasNext()){
                    Node itemNode = itr.nextNode()
                    String url = itemNode.hasProperty("url") ? itemNode.getProperty("url").getString() : null;
                    if (null != url && url.contains("my.girlscouts.org")) {
                        println("URL : " + url);
                        if (url.contains("vtk")) {
                            url = url.replaceAll("my", "mygs");
                            url = url.replaceAll(".content.*.?refererCouncil=.*", "/")
                        } else {
                            url = url.replaceAll("my", "mygs");
                            url = url.replaceAll(".?refererCouncil=.*", "")
                        }                       
                        println("updated url : " + url);
                        itemNode.setProperty("url", url)
                    }
                }
                save()
                println("saved converted content")
            }   
        }
    } catch (Exception e) {
        println(e.getMessage())
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
        println(e.getMessage());
    }
    return result;
}
