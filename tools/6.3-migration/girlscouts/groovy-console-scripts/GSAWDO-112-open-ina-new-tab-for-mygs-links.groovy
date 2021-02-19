
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*


String QUERY_LANGUAGE = "JCR-SQL2";
List<String> PATH_ARRAY = new ArrayList<>();
PATH_ARRAY.add("/content/gsctx");
PATH_ARRAY.add("/content/gssjc");
String[] RESOURCE_TYPE_ARRAY = ["gsusa/components/textimage","gsusa/components/image"];
for(int i = 0 ; i < PATH_ARRAY.size();i++){
    String PATH = PATH_ARRAY.get(i);
    for(int j=0 ; j< RESOURCE_TYPE_ARRAY.length; j++){
        String RESOURCE_TYPE = RESOURCE_TYPE_ARRAY[j];
        String EXPRESSION = "SELECT s.[jcr:path] "+
                "FROM [nt:unstructured] AS s "+
                "WHERE ISDESCENDANTNODE('"+PATH+"') AND "+
                "s.[sling:resourceType]='"+RESOURCE_TYPE+"'";
        QueryResult result = search(EXPRESSION, QUERY_LANGUAGE)

        if (result != null) {
            Workspace workspace = session.getWorkspace()
            RowIterator rowIter = result.getRows()
            while (rowIter.hasNext()) {
                Row row = rowIter.nextRow()
                Node node = row.getNode()
                processData(RESOURCE_TYPE,node)
            }
        }
    }
}

def void processData(RESOURCE_TYPE,node){
    if(RESOURCE_TYPE.equals("gsusa/components/textimage")){
        if(node.hasNode("image")){
            Node imageNode = node.getNode("image");
            if(null != imageNode && imageNode.hasProperty("linkURL")){
                String linkURL =  imageNode.getProperty("linkURL").getString();
                if(linkURL.startsWith("https://mygs.girlscouts.org")){
                    if(node.hasProperty("newWindow")){
                        node.setProperty("newWindow",true);
                        println(node.path);
                    }
                }
            }
        }
    }else if(RESOURCE_TYPE.equals("gsusa/components/image")){
        if(node.hasProperty("linkURL")){
            String linkURL =  node.getProperty("linkURL").getString();
            if(linkURL.startsWith("https://mygs.girlscouts.org")){
                if(node.hasProperty("newWindow")){
                    node.setProperty("newWindow",true);
                    println(node.path);
                }
            }
        }

    }
    save()


}
def QueryResult search(EXPRESSION, QUERY_LANGUAGE) {
    //println(EXPRESSION)
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