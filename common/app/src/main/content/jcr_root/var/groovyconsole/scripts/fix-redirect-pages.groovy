import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
property = 'cq:redirectTarget';
propertyVal = 'redirectTarget';
componentProp = 'sling:resourceType';
redirectFoundation = 'foundation/components/redirect';
redirectGsusa = 'gsusa/components/redirect';
redirectGirlscouts = 'girlscouts/components/redirect';

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

// https://author-uat.girlscouts.org/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2F*%2Fen%2Fredirects%2F*%2F%0D%0Apath.exact%3Dtrue%0D%0Atype%3Dcq%3APageContent%0D%0Agroup.p.or%3Dtrue%0D%0Agroup.1_group.1_property%3DredirectTarget%0D%0Agroup.1_group.1_property.operation%3Dexists%0D%0Agroup.1_group.2_property%3Dcq%3AredirectTarget%0D%0Agroup.1_group.2_property.operation%3Dnot%0D%0Agroup.2_group.3_property%3Dsling%3AresourceType%0D%0Agroup.2_group.3_property.value%3Dfoundation%2Fcomponents%2Fredirect%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/content/*/en/redirects/*/');
    map.put('path.exact', 'true');
    map.put('type', 'cq:PageContent');
    map.put('group.p.or', 'true');
    map.put('group.1_group.1_property', propertyVal);
    map.put('group.1_group.1_property.operation', 'exists');
    map.put('group.1_group.2_property', property);
    map.put('group.1_group.2_property.operation', 'not');
    map.put('group.2_group.3_property', componentProp);
    map.put('group.2_group.3_property.value', redirectFoundation);

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
    nodeObj.put('title', node.get('jcr:title'));
    nodeObj.put('path', node.path);

    // Change component property in node
    def component = node.get(componentProp);
    if (component != redirectGsusa && component != redirectGirlscouts) {
        nodeObj.put('old_' + componentProp, component);
        if (node.path.contains('gsusa')) {
            node.set(componentProp, redirectGsusa);
        } else {
            node.set(componentProp, redirectGirlscouts);
        }
        nodeObj.put('new_' + componentProp, node.get(componentProp));
    }

    // Change redirect property in node
    def newRedirect = node.get(property);
    def oldRedirect = node.get(propertyVal);
    if (newRedirect == null && oldRedirect != null) {
        nodeObj.put('old_' + property, newRedirect);
        node.set(property, oldRedirect);
        nodeObj.put('new_' + property, node.get(property));
    }

    // Save the node
    editedNodes.add(nodeObj);
    if (!filters.dryRun) {
        session.save();
        if (filters.publish) {
            activatePath(node.path);
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
