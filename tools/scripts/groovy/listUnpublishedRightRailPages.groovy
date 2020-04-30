import javax.jcr.query.*
import javax.jcr.*
import java.util.logging.Logger
import java.util.*
import javax.jcr.Node.*
import com.day.cq.replication.*
import com.adobe.granite.timeline.*

def root = "/content/";
println "=======================================";
println "Execution Started at "+new Date();
println "=======================================";
getUnpublishedBannerPages(root);
println "=======================================";
println "Execution Ended at "+new Date();
println "=======================================";

def getUnpublishedBannerPages(String councilPath){
   def statement = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE([${councilPath}]) and s.[sling:resourceType] ='girlscouts/components/ad-page' and s.[cq:lastReplicationAction]  NOT LIKE 'Activate' "
   def results = queryResults(statement);
    try{
        if(null != results && results.getRows().size() != 0){
        def rowIterator = results.getRows();
        while(rowIterator.hasNext()){
            def row = rowIterator.nextRow();
            String pagePath = row.getNode().getParent().getPath();
            Resource resource = resourceResolver.getResource(pagePath);
            if(null != resource){
                Page page = resource.adaptTo(Page.class);
                List <TimelineEvent> list =   page.adaptTo(Timeline.class).getEvents();
                List<String> actionsList = new ArrayList<>();
                for(TimelineEvent t : list){
                    actionsList.add(t.getAction());
                }
                boolean replicated = isLastReplicated(actionsList);
                if(!replicated){
                    println "Path :" + page.getPath();
                }
            }
            
        }
    }
   }catch(Exception e){
       println "Error occured "+e;
   }  
   

}
def isLastReplicated(ArrayList list){
    boolean replicated = true;
    Collections.reverse(list);
    for(String s : list){
        if(s.equals("Deactivate")){
            replicated =false;
            break;
        }else if(s.equals("Activate")){
            replicated =true;
            break;
        }
    }
    replicated; 
}

def queryResults(String statement){
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
