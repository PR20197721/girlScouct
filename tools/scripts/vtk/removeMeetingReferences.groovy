import java.util.*
import javax.jcr.query.*;

Map<String, Integer> references = new HashMap<>();


Node node = getNode("/vtk2019");
NodeIterator nodeItr = node.getNodes();
while(nodeItr.hasNext()) {
    Node council = nodeItr.nextNode();
    councilQuery(council.getPath(), references);
}

Iterator<String> itr = references.keySet().iterator();
println("References:");
while(itr.hasNext()) {
    String key = itr.next();
    println(key + ": " + references.get(key));
}


def councilQuery(councilPath, references) {
    ArrayList paths = new ArrayList();
    Set<String> planSet = new HashSet<>();
    Map<String, Integer> councilRefs = new HashMap<>();
    try{
        println(councilPath);
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        String[] meetingIds = ["D18BS03", "D18BS04", "D18BS05", "D18BS06", "D18BS07", "D18BS08", "D18BS09", "D18BS10", "D18BS11", "D18BS12", "D18TC03", "D18TC04", "D18TC05", "D18TC06", "D18TC07", "D18TC08", "D18TC09", "D18TC10", "D18TC11", "D18TC12"];
        String queryId = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([" + councilPath + "]) AND (";
        for(int i = 0; i < meetingIds.length; i++) {
            queryId = queryId + "s.[refId] like '%" + meetingIds[i] + "'";
            if(i == meetingIds.length - 1) {
                queryId = queryId + ")";
            } else {
                queryId = queryId + " OR ";
            }
        }

        javax.jcr.query.Query query = queryManager.createQuery(queryId, "JCR-SQL2");
        QueryResult resultSet = query.execute();
        NodeIterator nodeItr = resultSet.getNodes();

        while(nodeItr.hasNext()) {
            Node node = nodeItr.nextNode();
            if(node.getPath().contains("/yearPlan/")) {
                if(node.hasProperty("refId")) {
                    String ref = node.getProperty("refId").getString();
                    ref = ref.substring(ref.lastIndexOf("/") + 1)
                    if(references.containsKey(ref)) {
                        references.put(ref, references.get(ref) + 1)
                    } else {
                        references.put(ref, 1);
                    }
                }
                paths.add(node.getPath());
                planSet.add(node.getPath().substring(0, node.getPath().indexOf("/yearPlan")));
            }
        }


        println("Year plans affected in " + councilPath + ": " + planSet.size());
        for(String path : paths) {
            println(path);
        }
    }catch(Exception e) {
        print(e);
    }
}