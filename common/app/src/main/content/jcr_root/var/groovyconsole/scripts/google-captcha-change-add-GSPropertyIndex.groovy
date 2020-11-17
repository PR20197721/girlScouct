import javax.jcr.Node
import org.apache.jackrabbit.commons.JcrUtils
import javax.jcr.query.*
import groovy.json.JsonBuilder
import java.util.regex.Pattern
import java.util.regex.Matcher

//*
//*
//* USER VARIABLES
oldSlingResourceType = 'girlscouts/components/form/captcha';
newSlingResourceType = 'girlscouts/components/form/recaptcha-v2';
submitSlingResourceType = 'foundation/components/form/submit';
actionType = 'girlscouts/components/form/actions/web-to-case';
propertyMap = [
    'constraintMessage': 'Invalid captcha.',
    'required': 'true',
    'sling:resourceSuperType': 'foundation/components/form/defaults/field',
    'sling:resourceType': newSlingResourceType
];

// *** NOTE ***
// This query can only run in environments that use GSPropertyIndex or a similar index that aggregates only "actionType"
// Please test environment applicability using the querydebug links below to see if the query returns results
// and Explain Query from /libs/granite/operations/content/diagnosistools/queryPerformance.html to determine which index is being used

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
//* QUERY FORM NODES

// http://3.230.103.184:4502/libs/cq/search/content/querydebug.html?_charset_=UTF-8&query=path%3D%2Fcontent%2F%0D%0Atype%3Dnt%3Aunstructured%0D%0A1_property%3D*%2FactionType%0D%0A1_property.value%3Dgirlscouts%2Fcomponents%2Fform%2Factions%2Fweb-to-case%0D%0Ap.limit%3D-1%0D%0Ap.hits%3Dfull%0D%0Aorderby%3Dpath
def buildQuery(data) {
    Map map = [:];

    map.put('path', '/content/');
    map.put('type', 'nt:unstructured');
    map.put('property', '*/actionType'); // Parent of a node with actionType property, form start
    map.put('property.value', actionType);
    map.put('p.limit', '-1'); // Get all results
    map.put('p.hits', 'full'); // Get all properties
    map.put('orderby', 'path'); // Sort results

    return queryBuilder.createQuery(PredicateGroup.create(map), session);
}

query = buildQuery(filters);
result = query.execute();

def getProp(node, prop) {
    return node.hasProperty(prop) ? node.getProperty(prop).getString() : null
}

editedNodes = [];
createdNodes = [];
result.nodes.each { parent ->
    submitNode = null;
    captchaNode = null;

    parent.nodes.each { node ->
        def resourceType = getProp(node, 'sling:resourceType');

        // Fix existing captcha node properties
        if (resourceType == oldSlingResourceType || resourceType == newSlingResourceType) {
            captchaNode = node;

            // Get node data and set new property
            def nodeObj = new TreeMap();
            nodeObj.put('path', node.path);

            // Change property in node and save
            propertyMap.each { property, value ->
                def oldVal = getProp(node, property);
                if (oldVal == value) return;
                nodeObj.put('old_' + property, oldVal);
                node.setProperty(property, value);
                nodeObj.put('new_' + property, getProp(node, property));
            }
            if (nodeObj.size() > 1) {
                editedNodes.add(nodeObj);
                if (!filters.dryRun) {
                    session.save();
                }
            }
        } else if (resourceType == submitSlingResourceType) {
            submitNode = node;
        }
    }

    // Add captcha node to parent if it doesn't already exist
    if (submitNode != null && captchaNode == null) {
        // Add new captcha node to parent
        def node = parent.addNode('recaptcha_v2', 'nt:unstructured');

        // Reorder node
        // https://docs.adobe.com/docs/en/spec/jsr170/javadocs/jcr-2.0/javax/jcr/Node.html#orderBefore(java.lang.String,%20java.lang.String)
        // https://www.codota.com/code/java/methods/javax.jcr.Node/orderBefore?snippet=5ce6bef837d9ff0004ebb811
        parent.orderBefore(node.name, submitNode.name);

        // Add properties to node
        def nodeObj = new TreeMap();
        nodeObj.put('path', node.path);
        propertyMap.each { property, value ->
            node.setProperty(property, value);
            nodeObj.put(property, value);
        }

        // Save
        createdNodes.add(nodeObj);
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
    jsonMap.put('createdNodes', createdNodes);
}
if (buildJSON.filters) {
    def dataMap = new TreeMap();
    if (buildJSON.hitCount) {
        dataMap.put('numFormsQueried', result.nodes.size());
        dataMap.put('numNodesEdited', editedNodes.size());
        dataMap.put('numNodesCreated', createdNodes.size());
    }
    if (buildJSON.filters) {
        dataMap.put('filters', filters);
    }
    jsonMap.put('_data', dataMap);
}
json.call(jsonMap);
if (buildJSON.print) {
    println json.toPrettyString();
}
