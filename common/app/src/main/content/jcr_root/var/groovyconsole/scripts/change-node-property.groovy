import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
property = 'vtype';
propertyVal = 'vanityPathValidator';

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

// https://author-uat.girlscouts.org/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fetc%2Fscaffolding%2F%0D%0Atype%3Dcq%3AWidget%0D%0Aproperty%3Dname%0D%0Aproperty.value%3D.%2Fjcr%3Acontent%2Fsling%3AvanityPath%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/etc/scaffolding/');
    map.put('type', 'cq:Widget');
    map.put('property', 'name');
    map.put('property.value', './jcr:content/sling:vanityPath');
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
    nodeObj.put('title', node.get('jcr:title'));
    nodeObj.put('path', node.path);

    // Change property in node and save
    nodeObj.put('old_' + property, node.get(property));
    node.set(property, propertyVal);
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
