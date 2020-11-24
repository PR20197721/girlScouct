import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
property = 'refId';
propertyValOld = 'yearplan20\\d\\d';
propertyValNew = 'library'

// Filter
filters = [
    dryRun: true
];

// Results
buildJSON = [
    print: true, 
    nodes: true,
    hitCount: true,
    filters: true
];

//*
//*
//* QUERY

// http://34.195.250.246:4503/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fvtk2019%2F*%2Ftroops%2F*%2F%0D%0Apath.exact%3Dtrue%0D%0Atype%3Dnt%3Aunstructured%0D%0Aproperty%3DrefId%0D%0Aproperty.value%3D%25yearplan%25%0D%0Aproperty.operation%3Dlike%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/vtk2019/*/troops/*/');
    map.put('path.exact', 'true');
    map.put('type', 'nt:unstructured');
    map.put('property', property);
    map.put('property.value', '%yearplan%');
    map.put('property.operation', 'like');
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

editedNodes = [];
result.nodes.each { node ->
    // Get node data and set new property
    def nodeObj = new TreeMap();
    nodeObj.put('path', node.path);
    def oldVal = node.get(property);

    // Change property in node and save
    nodeObj.put('old_' + property, oldVal);
    node.set(property, oldVal.replaceFirst(propertyValOld, propertyValNew));
    nodeObj.put('new_' + property, node.get(property));
    editedNodes.add(nodeObj);
    if (!filters.dryRun) {
        session.save();
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.nodes) {
    jsonMap.put('editedNodes', editedNodes);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numNodesQueried', result.nodes.size());
        dataMap.put('numNodesEdited', editedNodes.size());
    }
    if (buildJSON.filters) {
        dataMap.put('filters', filters);
    }
    jsonMap.put('data', dataMap);
}
json.call(jsonMap);
if (buildJSON.print) {
    println json.toPrettyString();
}
