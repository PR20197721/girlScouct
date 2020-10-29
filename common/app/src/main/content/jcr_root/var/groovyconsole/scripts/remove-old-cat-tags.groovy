import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
property = 'catTags';

// Filter
filters = [
    dryRun: true,
    currentYear: 2020
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

// http://34.195.250.246:4503/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2Fgirlscouts-vtk%2Fmeetings%2Flibrary%0D%0Atype%3Dnt%3Aunstructured%0D%0A1_property%3Docm_classname%0D%0A1_property.value%3Dorg.girlscouts.vtk.ocm.MeetingNode%0D%0A2_property%3DisArchived%0D%0A2_property.operation%3Dnot%0D%0A3_property%3DcatTags%0D%0A3_property.value%3D%25Badges_for_%25%0D%0A3_property.operation%3Dlike%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/content/girlscouts-vtk/meetings/library');
    map.put('type', 'nt:unstructured');
    map.put('1_property', 'ocm_classname');
    map.put('1_property.value', 'org.girlscouts.vtk.ocm.MeetingNode');
    map.put('2_property', 'isArchived');
    map.put('2_property.operation', 'not');
    map.put('3_property', property);
    map.put('3_property.value', '%Badges_for_%');
    map.put('3_property.operation', 'like');
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
    nodeObj.put('name', node.get('name'));
    nodeObj.put('path', node.path);

    // https://regexr.com/5avum
    def oldCatTags = node.get(property);
    def newCatTags = oldCatTags.replaceAll('Badges_for_((?!' + filters.currentYear + ')\\d){4}-((?!' + (filters.currentYear + 1) + ')\\d){4},', '');
    if (newCatTags.isEmpty()) newCatTags = null;

    // Change property in node and save
    nodeObj.put('old_' + property, oldCatTags);
    node.set(property, newCatTags);
    nodeObj.put('new_' + property, node.get(property));
    if (oldCatTags != newCatTags) {
        editedNodes.add(nodeObj);
        if (!filters.dryRun) {
            session.save();
        }
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
