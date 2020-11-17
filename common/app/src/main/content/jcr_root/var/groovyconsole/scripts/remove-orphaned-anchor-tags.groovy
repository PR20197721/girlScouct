import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
properties = [
    'activityDescription',
    'materials',
    'req',
    'str'
];

// Filter
filters = [
    dryRun: true,
    publish: true
];

// Results
buildJSON = [
    print: true,
    nodes: true,
    errors: true,
    hitCount: true,
    filters: true
];

//*
//*
//* QUERY

// http://34.236.224.243:4502/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2Fgirlscouts-vtk%2Fmeetings%2Flibrary%0D%0Atype%3Dnt%3Aunstructured%0D%0Agroup.p.or%3Dtrue%0D%0Agroup.1_property%3DactivityDescription%0D%0Agroup.1_property.operation%3Dexists%0D%0Agroup.2_property%3Dmaterials%0D%0Agroup.2_property.operation%3Dexists%0D%0Agroup.3_property%3Dreq%0D%0Agroup.3_property.operation%3Dexists%0D%0Agroup.4_property%3Dstr%0D%0Agroup.4_property.operation%3Dexists%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/content/girlscouts-vtk/meetings/library');
    map.put('type', 'nt:unstructured');
    map.put('group.p.or', 'true');
    properties.eachWithIndex { property, i ->
        map.put('group.' + i + '_property', property);
        map.put('group.' + i + '_property.operation', 'exists');
    };
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

errors = new TreeMap();
def activatePath(path) {
    try {
        activate(path);
    } catch (Exception ex) {
        errors.put(path, ex.message);
    }
    sleep(1000);
}

editedNodes = [];
result.nodes.each { node ->
    // Get node data and set new property
    def nodeObj = new TreeMap();
    //nodeObj.put('title', node.get('jcr:title'));
    nodeObj.put('path', node.path);

    // Check and clean properties
    properties.each { property ->
        def value = node.get(property);
        if (value == null) return;

        // https://regexr.com/58ohn
        def cleaned = value.replaceAll('<a>(.*?)<\\/a>', '$1');
        if (value == cleaned) return;

        nodeObj.put('old_' + property, value);
        node.set(property, cleaned);
        nodeObj.put('new_' + property, node.get(property));
    }

    // Add obj and save if edits were made
    if (nodeObj.size() > 1) {
        editedNodes.add(nodeObj);
        if (!filters.dryRun) {
            session.save();
            if (filters.publish) {
                activatePath(node.path);
            }
        }
    }
}

// https://www.programcreek.com/java-api-examples/?api=groovy.json.JsonBuilder
def json = new JsonBuilder();
def jsonMap = new TreeMap();

if (buildJSON.nodes) {
    jsonMap.put('editedNodes', editedNodes);
}
if (buildJSON.errors && !errors.empty) {
    jsonMap.put('errors', errors);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numNodesQueried', result.nodes.size());
        dataMap.put('numNodesCleaned', editedNodes.size());
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
